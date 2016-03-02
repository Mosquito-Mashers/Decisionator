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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import java.util.Iterator;
import java.util.Map;

public class EventActivity extends AppCompatActivity {

    private FriendAdapter friendAdapter;

    private Intent enterEvent;
    private Intent goToLobby;
    private Map<String, String> intentPairs = new HashMap<String, String>();

    private String eTopic;
    private String eHost;
    private String eInvites;
    private String eCategory;
    private String eID;
    private String poolID;
    private String uID;
    private String uName;
    private CognitoCachingCredentialsProvider credentialsProvider;

    private TextView eventTitle;
    private TextView mapsContainer;
    private TextView eventHost;
    private ListView invitedList;
    private Button returnToLobby;
    private Button rsvp;
    private ImageView eventCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        initializeGlobals();

        initializeListeners();

        prepareIntent(goToLobby,intentPairs);
    }



    private void initializeGlobals()
    {
        goToLobby = new Intent(this, LobbyActivity.class);

        enterEvent = getIntent();
        eTopic = enterEvent.getStringExtra(EventCreationActivity.EVENT_TOPIC);
        eHost = enterEvent.getStringExtra(EventCreationActivity.EVENT_HOST_NAME);
        eInvites = enterEvent.getStringExtra(EventCreationActivity.EVENT_INVITES);
        eCategory = enterEvent.getStringExtra(EventCreationActivity.EVENT_CATEGORY);
        eID = enterEvent.getStringExtra(EventCreationActivity.EVENT_ID);
        poolID = enterEvent.getStringExtra(FacebookLogin.POOL_ID);
        uID = enterEvent.getStringExtra(FacebookLogin.USER_ID);
        uName = enterEvent.getStringExtra(FacebookLogin.USER_F_NAME);

        intentPairs.put(FacebookLogin.POOL_ID, poolID);
        intentPairs.put(FacebookLogin.USER_ID, uID);
        intentPairs.put(FacebookLogin.USER_F_NAME, uName);

        credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),    /* get the context for the application */
                poolID, // Identity Pool ID
                Regions.US_EAST_1           /* Region for your identity pool--US_EAST_1 or EU_WEST_1*/
        );

        eventTitle = (TextView) findViewById(R.id.eventTitle);
        mapsContainer = (TextView) findViewById(R.id.gMapsContainer);
        eventHost = (TextView) findViewById(R.id.eventHost);
        invitedList = (ListView) findViewById(R.id.invitedList);
        returnToLobby = (Button) findViewById(R.id.returnToLobby);
        rsvp = (Button) findViewById(R.id.rsvpButton);
        eventCategory = (ImageView) findViewById(R.id.eventDescPic);

        eventTitle.setText(eTopic);
        eventHost.setText(eHost);

        new getAllFriends().execute(eID);
    }

    private void initializeListeners() {

        returnToLobby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(goToLobby);
            }
        });
        rsvp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new updateEvent().execute(eID);

            }
        });
    }

    private void prepareIntent(Intent moveToLobby, Map<String, String> intentPairs) {
        Iterator mapIter = intentPairs.entrySet().iterator();

        while (mapIter.hasNext())
        {
            Map.Entry kvPair = (Map.Entry) mapIter.next();
            moveToLobby.putExtra(kvPair.getKey().toString(), kvPair.getValue().toString());
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
            RelativeLayout friendContainer;
            ImageView profilePic;
            TextView name;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.list_item_user_display, null);

                holder = new ViewHolder();
                holder.friendContainer = (RelativeLayout) convertView.findViewById(R.id.invFriendContainer);
                holder.profilePic = (ImageView) convertView.findViewById(R.id.invUserProfilePicture);
                holder.name = (TextView) convertView.findViewById(R.id.invUserName);

                convertView.setTag(holder);
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }

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

    class updateEvent extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            ArrayList<Event> temp = new ArrayList<Event>();
            AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
            DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);

            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
            Event event = mapper.load(Event.class, params[0]);
            User currUser = mapper.load(User.class, uID);
            String currName = currUser.getfName() + " " + currUser.getlName();

            String rsvps = event.getRsvpList();
            String rsvpList[];
            if(rsvps != null) {
                rsvpList = rsvps.split(", ");
                int k;

                for (k = 0; k < rsvpList.length; k++) {
                    if (!rsvpList[k].contentEquals(currName)) {
                        rsvps += ", " + currName;
                        break;
                    }
                }
            }
            else
            {
                rsvps = currName;
            }

            event.setRsvpList(rsvps);

            mapper.save(event);
            return null;
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

    class getAllFriends extends AsyncTask<String, Void, ArrayList<User>> {
        @Override
        protected ArrayList<User> doInBackground(String... params) {
            ArrayList<User> temp = new ArrayList<User>();
            AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
            DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);

            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
            PaginatedScanList<User> userResult = mapper.scan(User.class, scanExpression);
            Event eventResult = mapper.load(Event.class, params[0]);

            String invitedArray[] = eventResult.getAttendees().split(", ");

            int k;
            for (k = 0; k < userResult.size(); k++)
            {
                User item = userResult.get(k);
                String name = item.getfName() + " " + item.getlName();

                for(int i = 0; i < invitedArray.length; i++)
                {
                    if (invitedArray[i].replaceAll("\\s+$", "").contentEquals(name))
                    {
                        temp.add(item);
                        continue;
                    }
                }
            }
            return temp;
        }

        @Override
        protected void onPostExecute(ArrayList<User> res)
        {
            friendAdapter = new FriendAdapter(getApplicationContext(), R.layout.list_item_user_display,res);

            invitedList = (ListView) findViewById(R.id.invitedList);
            invitedList.setAdapter(friendAdapter);
        }
    }
}
