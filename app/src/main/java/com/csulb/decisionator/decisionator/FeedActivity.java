package com.csulb.decisionator.decisionator;

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
import android.widget.ImageView;
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
import java.util.HashMap;
import java.util.Map;

public class FeedActivity extends AppCompatActivity {
    protected final static String FRIEND_ID = "com.csulb.decisionator.FRIEND_ID";
    protected final static String FRIEND_F_NAME = "com.csulb.decisionator.FRIEND_F_NAME";

    private Intent lobbyIntent;
    private Intent logoutIntent;
    private Intent fromLobby;
    private Intent toFriendFeedIntent;

    private String eID;
    private String uID;
    private String uName;
    private String poolID;
    private String fID;
    private String friendName;
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
   // private checkUpdates updateRefresh = new checkUpdates();
    private Intent notificationIntent;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_resources, menu);
        MenuItem itemChart = menu.findItem(R.id.chart);
        itemChart.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                startActivity(logoutIntent);
                return true;
            case R.id.lobby:
                startActivity(lobbyIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        initializeGlobals();

    }

    private void initializeGlobals() {
        fromLobby = getIntent();
        logoutIntent = new Intent(this, FacebookLogin.class);
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
        toFriendFeedIntent.putExtra(FacebookLogin.USER_ID, uID);
        toFriendFeedIntent.putExtra(FacebookLogin.USER_F_NAME, uName);

        //viewButton = (Button) findViewById(R.id.viewButton);
        new getAllFriends().execute();

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


            final String usersID = user.getUserID();
            final String usersFirstName = user.getfName();
            holder.name.setText(user.getfName() + " " + user.getlName());

            holder.viewButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toFriendFeedIntent.putExtra(FRIEND_ID, usersID);
                    toFriendFeedIntent.putExtra(FRIEND_F_NAME, usersFirstName);

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

}


