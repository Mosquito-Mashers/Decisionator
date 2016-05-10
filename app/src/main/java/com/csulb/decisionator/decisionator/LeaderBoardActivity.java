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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class LeaderBoardActivity extends AppCompatActivity {

    private Intent fromLobby;
    private Intent lobbyIntent;
    private Intent logoutIntent;
    private Intent profileIntent;
    private Intent notificationIntent;

    private ListView friendLadder;
    private ArrayAdapter adapter;
    private FriendAdapter friendAdapter;
    private String [] achieveString;
    private LinearLayout gallery;

    private String poolID;
    private String uFName;
    String uID;
    private User currUser;

    private CognitoCachingCredentialsProvider credentialsProvider;

    private checkUpdates updateRefresh = new checkUpdates();
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
        setContentView(R.layout.activity_leader_board);
        initializeGlobals();
        initializeListeners();
    }

    private void initializeGlobals() {
        //intents
        lobbyIntent = new Intent(this, LobbyActivity.class);
        logoutIntent = new Intent(this, FacebookLogin.class);
        profileIntent = new Intent(this, MyProfile.class);

        fromLobby = getIntent();

        poolID = fromLobby.getStringExtra(FacebookLogin.POOL_ID);
        uFName = fromLobby.getStringExtra(FacebookLogin.USER_F_NAME);
        uID = fromLobby.getStringExtra(FacebookLogin.USER_ID);

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

        friendLadder = (ListView) findViewById(R.id.ladder);

        new getAllFriendsRank().execute();
    }

    private void initializeListeners() {

    }


    class getAllFriendsRank extends AsyncTask<Void, Void, ArrayList<User>> {
        @Override
        protected ArrayList<User> doInBackground(Void... params) {
            ArrayList<User> friendsInRankedOrder = new ArrayList<User>();
            AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
            DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);

            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
            PaginatedScanList<User> result = mapper.scan(User.class, scanExpression);

            int k;
            for (k = 0; k < result.size(); k++)
            {
                User item = result.get(k);
                friendsInRankedOrder.add(item);
            }
            Collections.sort(friendsInRankedOrder, new CompareUserRank());
            return friendsInRankedOrder;
        }

        @Override
        protected void onPostExecute(ArrayList<User> res) {
            friendLadder = (ListView) findViewById(R.id.ladder);
            //adapter = new ArrayAdapter<User>(getApplicationContext(),android.R.layout.simple_list_item_1, res);
            friendAdapter = new FriendAdapter(getApplicationContext(), R.layout.list_item_leaderboard_info,res);
            friendLadder.setAdapter(friendAdapter);
        }
    }

    //used to sort User() arrayList in order of RsvpCount()
    class CompareUserRank implements Comparator<User> {
        @Override
        public int compare(User u1, User u2)
        {
            return u2.getRsvpCount() - u1.getRsvpCount();
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

            //Button viewButton;
            TextView rsvpScoreView;
            LinearLayout achievements;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.list_item_leaderboard_info, null);

                holder = new ViewHolder();
                //holder.feedContainer = (RelativeLayout) convertView.findViewById(R.id.feedContainer);

                convertView.setTag(holder);
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.profilePic = (ImageView) convertView.findViewById(R.id.userProfilePicture);
            //holder.viewButton = (Button) convertView.findViewById(R.id.goToFriendFeed);
            holder.name = (TextView) convertView.findViewById(R.id.userName);
            holder.rsvpScoreView = (TextView) convertView.findViewById(R.id.rsvpScore);
            holder.achievements = (LinearLayout) convertView.findViewById(R.id.UserGallery);


            User user = friends.get(position);

            if(user.getAchievements() != null)
            {
                achieveString = user.getAchievements().split(",");
            }
            else
            {
                achieveString = new String[1];
                achieveString[0] = "0";
            }
            uiChange(holder.achievements);

            final String usersID = user.getUserID();
            final String usersFirstName = user.getfName();
            holder.name.setText(user.getfName() + " " + user.getlName());
            holder.rsvpScoreView.setText("No Score");
            //holder.achievements.setText("Achievements Earned: " + user.getAchievements());
            if(user.getRsvpCount() > 0) {
                holder.rsvpScoreView.setText("Score:" + user.getRsvpCount());
            }
            else
            {
                holder.rsvpScoreView.setText("No Score");
            }

            if (user.getProfilePic() == null)
                holder.profilePic.setImageResource(R.mipmap.ic_launcher);
            else
                new DownloadImageTask(holder.profilePic).execute(user.getProfilePic());

                return convertView;
        }
    }

    private void uiChange(LinearLayout gal){
        gal.removeAllViewsInLayout();
        ImageView achieve1 = new ImageView(getApplicationContext());
        achieve1.setImageResource(R.mipmap.clear_icon);
        achieve1.setLayoutParams(new ViewGroup.LayoutParams(220, ViewGroup.LayoutParams.MATCH_PARENT));
        gal.addView(achieve1);

        ImageView achieve2 = new ImageView(getApplicationContext());
        achieve2.setImageResource(R.mipmap.clear_icon);
        achieve2.setLayoutParams(new ViewGroup.LayoutParams(220, ViewGroup.LayoutParams.MATCH_PARENT));
        gal.addView(achieve2);

        ImageView achieve3 = new ImageView(getApplicationContext());
        achieve3.setImageResource(R.mipmap.clear_icon);
        achieve3.setLayoutParams(new ViewGroup.LayoutParams(220, ViewGroup.LayoutParams.MATCH_PARENT));
        gal.addView(achieve3);

        for(int i = 0; i < achieveString.length; i++){
            if(achieveString[i].equals("1")){
                achieve1.setImageResource(R.mipmap.star_icon);
            }
            if(achieveString[i].equals("2")){
                achieve2.setImageResource(R.mipmap.star_icon);
            }
            if(achieveString[i].equals("3")){
                achieve3.setImageResource(R.mipmap.star_icon);
            }

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
            //new SetAchievements().execute(uID);
        }
    }

    private class SetAchievements extends  AsyncTask<String, Void, Void> {

        @Override
        public Void doInBackground(String... params){
            AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
            DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);

            User user = mapper.load(User.class, params[0]);
            return null;
        }

        @Override
        public void onPostExecute(Void v) {
            //uiChange();
        }

    }

}
