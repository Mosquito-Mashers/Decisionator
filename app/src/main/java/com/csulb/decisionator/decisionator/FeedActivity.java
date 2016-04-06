package com.csulb.decisionator.decisionator;

import android.app.Activity;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FeedActivity extends AppCompatActivity {

    private Intent enterFriendFeed;
    private Intent lobbyIntent;
    private Intent fromLobby;
    private Intent toFriendFeedIntent;

    private String eID;
    private String uID;
    private String uName;
    private String poolID;
    private String friendID;
    private static final Map<String, String> intentPairs = new HashMap<String, String>();
    private Context context;
    private CognitoCachingCredentialsProvider credentialsProvider;

    private ListView feedList;
    private Button view;
    private FriendAdapter friendAdapter;
    private ArrayList<User> peepFriends;
    private ArrayList<User> allUsers = new ArrayList<User>();
    private User currUser;
    private static final int notifyID = 111;
    private checkUpdates updateRefresh = new checkUpdates();
    private Intent notificationIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        initializeGlobals();

    }

    private void initializeGlobals() {
        fromLobby = getIntent();
        lobbyIntent = new Intent(this, LobbyActivity.class);
        toFriendFeedIntent = new Intent(this, friendEventActivity.class);
        uID = fromLobby.getStringExtra(FacebookLogin.USER_ID);
        poolID = fromLobby.getStringExtra(FacebookLogin.POOL_ID);
        uName = fromLobby.getStringExtra(FacebookLogin.USER_F_NAME);


        peepFriends = new ArrayList<User>();
        feedList = (ListView) findViewById(R.id.feedList);
        credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),    /* get the context for the application */
                poolID, // Identity Pool ID
                Regions.US_EAST_1           /* Region for your identity pool--US_EAST_1 or EU_WEST_1*/
        );

        lobbyIntent.putExtra(FacebookLogin.USER_ID,uID);
        lobbyIntent.putExtra(FacebookLogin.POOL_ID,poolID);
        lobbyIntent.putExtra(FacebookLogin.USER_F_NAME,uName);
        toFriendFeedIntent.putExtra(FacebookLogin.POOL_ID, poolID);
        toFriendFeedIntent.putExtra(FacebookLogin.USER_ID, friendID);
        toFriendFeedIntent.putExtra(FacebookLogin.USER_F_NAME, uName);

        //viewButton = (Button) findViewById(R.id.viewButton);
        new getAllFriends().execute();
        updateRefresh.execute();
    }

    private void initializeListenters() {

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
            //RelativeLayout feedContainer;

            ImageView profilePic;
            TextView name;
            Button viewButton;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.list_item_feed_info, null);

                holder = new ViewHolder();
                //holder.feedContainer = (RelativeLayout) convertView.findViewById(R.id.feedContainer);
                holder.profilePic = (ImageView) convertView.findViewById(R.id.userProfilePicture);
                holder.viewButton = (Button) convertView.findViewById(R.id.goToFriendFeed);
                holder.name = (TextView) convertView.findViewById(R.id.userName);
                convertView.setTag(holder);
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }

            User user = friends.get(position);
            user.getUserID();
            holder.name.setText(user.getfName()+ " "+ user.getlName());

            toFriendFeedIntent.putExtra(user.getUserID(), friendID);
            holder.viewButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(toFriendFeedIntent);
                }
            });
            if(user.getProfilePic() == null) {
                holder.profilePic.setImageResource(R.mipmap.ic_launcher);
            }
            else
            {
                new DownloadImageTask(holder.profilePic).execute(user.getProfilePic());
            }
            holder.viewButton.setText("View Their Feed");

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
            friendAdapter = new FriendAdapter(getApplicationContext(), R.layout.list_item_feed_info,res);

            feedList = (ListView) findViewById(R.id.feedList);
            feedList.setAdapter(friendAdapter);
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
            notificationIntent.putExtra(FacebookLogin.USER_F_NAME, uName);

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
                            .setSmallIcon(R.drawable.notification_icon)
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


