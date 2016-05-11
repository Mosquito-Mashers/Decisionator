package com.csulb.decisionator.decisionator;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

import java.io.InputStream;
import java.util.ArrayList;

public class InviteFriendsActivity extends AppCompatActivity {

    protected final static String ATTENDEES = "com.decisionator.decisionator.invitefriendsactivity.ATTENDEES";

    private FriendAdapter friendAdapter;
    private CognitoCachingCredentialsProvider credentialsProvider;

    private String poolID;
    private String uFName;
    String topic;
    String uID;

    private ArrayList<User> fbFriends;
    private ArrayList<User> invitedFriends;

    private Intent inEvent;
    private Intent logoutIntent;
    private Intent lobbyIntent;
    private Intent profileIntent;
    private Intent startEvent;
    private Event event;

    private ListView friendList;
    private Button inviteButton;

    private checkUpdates updateRefresh = new checkUpdates();
    private Intent notificationIntent;
    private static final int notifyID = 111;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_resources, menu);
        MenuItem itemChart = menu.findItem(R.id.chart);
        itemChart.setVisible(false);
        MenuItem itemProfile = menu.findItem(R.id.profile);
        itemProfile.setVisible(false);
        MenuItem itemChart2 = menu.findItem(R.id.chart2);
        itemChart2.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                updateRefresh.cancel(true);
                startActivity(logoutIntent);
                return true;
            case R.id.lobby:
                updateRefresh.cancel(true);
                startActivity(lobbyIntent);
                return true;
            case R.id.profile:
                updateRefresh.cancel(true);
                startActivity(profileIntent);
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_friends);

        initializeGlobals();

        initializeListeners();
    }

    private void initializeGlobals() {
        startEvent = new Intent(this, EventActivity.class);
        logoutIntent = new Intent(this, FacebookLogin.class);
        lobbyIntent = new Intent(this, LobbyActivity.class);
        profileIntent = new Intent(this, MyProfile.class);
        fbFriends = new ArrayList<User>();
        invitedFriends = new ArrayList<User>();

        inEvent = getIntent();

        poolID = inEvent.getStringExtra(FacebookLogin.POOL_ID);
        uFName = inEvent.getStringExtra(FacebookLogin.USER_F_NAME);
        topic = inEvent.getStringExtra(EventCreationActivity.EVENT_TOPIC);
        uID = inEvent.getStringExtra(FacebookLogin.USER_ID);

        credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),    /* get the context for the application */
                poolID, // Identity Pool ID
                Regions.US_EAST_1           /* Region for your identity pool--US_EAST_1 or EU_WEST_1*/
        );

        lobbyIntent.putExtra(FacebookLogin.USER_ID,uID);
        lobbyIntent.putExtra(FacebookLogin.POOL_ID,poolID);
        lobbyIntent.putExtra(FacebookLogin.USER_F_NAME,uFName);
        profileIntent.putExtra(FacebookLogin.USER_ID, uID);
        profileIntent.putExtra(FacebookLogin.POOL_ID,poolID);
        profileIntent.putExtra(FacebookLogin.USER_F_NAME,uFName);

        inviteButton = (Button) findViewById(R.id.inviteButton);

        new getAllFriends().execute();
        updateRefresh.execute();
    }

    private void initializeListeners() {
        inviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                StringBuffer attendeeList = new StringBuffer();
                String attendees;
                for (int i = 0; i < invitedFriends.size(); i++) {
                    User user = invitedFriends.get(i);
                    attendeeList.append(user.getUserID()+",");
                }
                if (attendeeList.length() > 0) {
                    attendees = attendeeList.substring(0, attendeeList.length() - 1);
                } else {
                    attendees = "None";
                }
                event = new Event();
                event.setTopic(topic);
                event.setHostID(inEvent.getStringExtra(FacebookLogin.USER_ID));
                event.setEventID(inEvent.getStringExtra(EventCreationActivity.EVENT_ID));
                event.setAttendees(attendees);
                event.setCategory(inEvent.getStringExtra(EventCreationActivity.EVENT_CATEGORY));

                new updateEvent().execute(event);

                startEvent.putExtra(FacebookLogin.POOL_ID, poolID);
                startEvent.putExtra(FacebookLogin.USER_ID, uID);
                startEvent.putExtra(FacebookLogin.USER_F_NAME, uFName);
                startEvent.putExtra(EventCreationActivity.EVENT_ID,event.getEventID());
                startEvent.putExtra(EventCreationActivity.EVENT_TOPIC, event.getTopic());
                startEvent.putExtra(EventCreationActivity.EVENT_INVITES, event.getAttendees());
                startEvent.putExtra(EventCreationActivity.EVENT_HOST_NAME, uFName);
                startEvent.putExtra(EventCreationActivity.EVENT_CATEGORY, event.getCategory());

                updateRefresh.cancel(true);
                startActivity(startEvent);
            }
        });
    }

    private class FriendAdapter extends ArrayAdapter<User>
    {
        private ArrayList<User> friends;

        public FriendAdapter(Context context, int profilePictureResourceID, ArrayList<User> friendList)
        {
            super(context, profilePictureResourceID, friendList);
            this.friends = new ArrayList<User>();
            this.friends.addAll(friendList);
        }

        private class ViewHolder
        {
            RelativeLayout friendContainer;
            ImageView profilePic;
            CheckBox name;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.list_item_user_info, null);

                holder = new ViewHolder();
                holder.friendContainer = (RelativeLayout) convertView.findViewById(R.id.friendContainer);
                holder.profilePic = (ImageView) convertView.findViewById(R.id.userProfilePicture);
                holder.name = (CheckBox) convertView.findViewById(R.id.userCheckbox);

                convertView.setTag(holder);
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.friendContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RelativeLayout rtl = (RelativeLayout) v;
                    CheckBox cb = (CheckBox) rtl.getChildAt(0);
                    User user = friends.get(position);

                    cb.performClick();
                    if(invitedFriends.contains(user))
                    {
                        invitedFriends.remove(user);
                    }
                    else
                    {
                        invitedFriends.add(user);
                    }
                }
            });

            User user = friends.get(position);

            if(user.getProfilePic() == null) {
                holder.profilePic.setImageResource(R.mipmap.ic_launcher);
            }
            else
            {
                new DownloadImageTask(holder.profilePic).execute(user.getProfilePic());
            }
            holder.name.setText(user.getfName() + " " + user.getlName());

            return convertView;
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    class getAllFriends extends AsyncTask<Void, Void, ArrayList<User>> {
        @Override
        protected ArrayList<User> doInBackground(Void... params) {
            ArrayList<User> temp = new ArrayList<User>();
            AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
            DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);

            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
            PaginatedScanList<User> result = mapper.scan(User.class, scanExpression);

            int k;
            for (k = 0; k < result.size(); k++)
            {
                User item = result.get(k);
                if (!item.getUserID().contentEquals(uID))
                {
                    temp.add(item);
                }
            }
            return temp;
        }

        @Override
        protected void onPostExecute(ArrayList<User> res)
        {
            friendAdapter = new FriendAdapter(getApplicationContext(), R.layout.list_item_user_info,res);

            friendList = (ListView) findViewById(R.id.friendList);
            friendList.setAdapter(friendAdapter);
        }
    }

    class updateEvent extends AsyncTask<Event, Void, Void> {
        @Override
        protected Void doInBackground(Event... arg0) {
            AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
            DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);

            Event temp = mapper.load(Event.class, arg0[0].getEventID());
            temp.setEventID(arg0[0].getEventID());
            temp.setHostID(arg0[0].getHostID());
            temp.setLongitude(arg0[0].getLongitude());
            temp.setLatitude(arg0[0].getLatitude());
            temp.setAttendees(arg0[0].getAttendees());

            mapper.save(temp);

            return null;
        }
    }

    class checkUpdates extends AsyncTask<Void, Void, PaginatedScanList<Event>> {

        private boolean isRunning;

        @Override
        protected void onPreExecute()
        {
            isRunning = true;
        }

        @Override
        protected PaginatedScanList<Event> doInBackground(Void... params) {
/*
            try{
                Thread.sleep(15000); //sleep for 15 seconds
            }
            catch(InterruptedException e){
                e.getMessage();
            }
            */
            AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
            DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);

            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
            PaginatedScanList<Event> result = mapper.scan(Event.class, scanExpression);

            notificationIntent = new Intent(getApplicationContext(), LobbyActivity.class);
            notificationIntent.putExtra(FacebookLogin.POOL_ID, poolID);
            notificationIntent.putExtra(FacebookLogin.USER_ID, uID);
            notificationIntent.putExtra(FacebookLogin.USER_F_NAME, uFName);

            if (isCancelled()) return null;

            if (result != null) {
                return result;
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(PaginatedScanList<Event> res) {
            isRunning = false;
            PendingIntent pendIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            Notification nb =
                    new Notification.Builder(getApplicationContext())
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle("Decisionator")
                            .setContentText("You have new events on Decisionator!")
                            .setAutoCancel(true)
                            .setContentIntent(pendIntent).build();

            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            //nm.notify(notifyID, nb);

            //execute every 30s

            int k;
            int m;
            int j;
            String[] attendees;
            String[] viewed;
            boolean notViewed = false;
            boolean isAttendee = false;
            if (res != null) {
                for (k = 0; k < res.size(); k++) {

                    if(notViewed)
                    {
                        break;
                    }
                    Event item = res.get(k);
                    if(item.getViewedList() != null)
                    {
                        viewed = item.getViewedList().split(",");
                    }
                    else
                    {
                        viewed = null;
                    }

                    if(item.getAttendees() != null)
                    {
                        attendees = item.getAttendees().split(",");
                    }
                    else
                    {
                        attendees = null;
                    }


                    if (viewed != null && attendees != null) {
                        for (m = 0; m < attendees.length; m++) {
                            if (uID.contentEquals(attendees[m])) {

                                isAttendee = true;
                                notViewed = true;
                                break;
                            }
                        }

                        for (j = 0; j < viewed.length; j++) {
                            if (uID.contentEquals(viewed[j]) && isAttendee) {

                                notViewed = false;
                                break;
                            }
                        }
                    } else if (viewed == null && attendees != null) {
                        for (m = 0; m < attendees.length; m++) {
                            if (uID.contentEquals(attendees[m])) {

                                notViewed = true;
                                break;
                                //Send notification
                            }
                        }
                    }
                }

                if (notViewed) {
                    nm.notify(notifyID, nb);
                    return;
                }
            }
        }
    }


}