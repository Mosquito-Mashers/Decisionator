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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class InviteFriendsActivity extends AppCompatActivity {

    protected final static String ATTENDEES = "com.decisionator.decisionator.invitefriendsactivity.ATTENDEES";

    private FriendAdapter friendAdapter;
    private CognitoCachingCredentialsProvider credentialsProvider;

    private String poolID;
    String topic;
    String uID;
    private Intent inEvent;
    private Intent startEvent;
    private Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_friends);

        startEvent = new Intent(this, LocationActivity.class);

        inEvent = getIntent();

        poolID = inEvent.getStringExtra(FacebookLogin.POOL_ID);

        credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),    /* get the context for the application */
                poolID, // Identity Pool ID
                Regions.US_EAST_1           /* Region for your identity pool--US_EAST_1 or EU_WEST_1*/
        );

        displayFriendsList();

        checkButtonClick();
    }

    private void displayFriendsList()
    {
        ArrayList<User> fbFriends = new ArrayList<User>();

        try {
            fbFriends = new getAllFriends().execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        friendAdapter = new FriendAdapter(this, R.layout.list_item_user_info,fbFriends);
        ListView friendList = (ListView) findViewById(R.id.friendList);
        friendList.setAdapter(friendAdapter);

        friendList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User user = (User) parent.getItemAtPosition(position);
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
            ImageView profilePic;
            CheckBox name;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            Log.v("ConvertView", String.valueOf(position));

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.list_item_user_info, null);

                holder = new ViewHolder();
                holder.profilePic = (ImageView) convertView.findViewById(R.id.userProfilePicture);
                holder.name = (CheckBox) convertView.findViewById(R.id.userCheckbox);
                convertView.setTag(holder);

                holder.name.setOnClickListener( new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v ;
                        User user = (User) cb.getTag();
                        user.setSelected(cb.isChecked());
                    }
                });
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
                try {
                    Bitmap profile = new DownloadImageTask(holder.profilePic).execute(user.getProfilePic()).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
            holder.name.setText(user.getfName() + " " + user.getlName());
            holder.name.setChecked(user.isSelected());
            holder.name.setTag(user);

            return convertView;
        }
    }

    private void checkButtonClick() {


        Button myButton = (Button) findViewById(R.id.inviteButton);
        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                StringBuffer responseText = new StringBuffer();
                ArrayList<User> userList = friendAdapter.friends;
                for (int i = 0; i < userList.size(); i++) {
                    User user = userList.get(i);
                    if (user.isSelected()) {
                        responseText.append(user.getUserID()+",");
                    }
                }

                topic = inEvent.getStringExtra(EventCreationActivity.EVENT_TOPIC);
                uID = inEvent.getStringExtra(FacebookLogin.USER_ID);
                event = new Event();
                event.setTopic(topic);
                event.setHostID(inEvent.getStringExtra(FacebookLogin.USER_ID));
                event.setEventID(inEvent.getStringExtra(EventCreationActivity.EVENT_ID));
                event.setAttendees(responseText.toString());
                new updateEvent().execute(event);

                startEvent.putExtra(EventCreationActivity.EVENT_TOPIC, topic);
                startEvent.putExtra(FacebookLogin.POOL_ID, poolID);
                startEvent.putExtra(FacebookLogin.USER_ID, uID);
                startEvent.putExtra(ATTENDEES, responseText.toString());
            }
        });
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
                temp.add(result.get(k));
            }

            return temp;
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
}
