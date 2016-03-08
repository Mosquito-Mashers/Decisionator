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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
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

public class LobbyActivity extends AppCompatActivity {

    //Values retrieved from intent
    private String uName;
    private String uID;
    private String welcomeString;
    private String poolID;
    private static final Map<String, String> intentPairs = new HashMap<String, String>();

    //Gui items
    private Intent loginSuccess;
    private Intent createEventIntent;
    private TextView welcomeMessage;
    private Button createEvent;
    private ImageButton refreshEvents;
    private ProgressBar feedProg;
    private EventAdapter eventAdapter;
    private ArrayList<Event> events;
    private ListView eventList;
    private CognitoCachingCredentialsProvider credentialsProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        initializeGlobals();

        initializeListeners();

        prepareIntent(createEventIntent, intentPairs);
    }

    private void prepareIntent(Intent createEventIntent, Map<String, String> intentPairs) {
        Iterator mapIter = intentPairs.entrySet().iterator();

        while (mapIter.hasNext())
        {
            Map.Entry kvPair = (Map.Entry) mapIter.next();
            createEventIntent.putExtra(kvPair.getKey().toString(), kvPair.getValue().toString());
        }
    }

    private void initializeListeners() {
        createEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(createEventIntent);
            }
        });

        refreshEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new getEvents().execute();


            }
        });
    }

    private void initializeGlobals() {
        //Intent Initialization
        loginSuccess = getIntent();
        createEventIntent = new Intent(this, EventCreationActivity.class);
        events = new ArrayList<Event>();

        //GUI assignments
        welcomeMessage = (TextView) findViewById(R.id.welcomeText);
        createEvent = (Button) findViewById(R.id.createEvent);
        refreshEvents = (ImageButton) findViewById(R.id.refreshEvents);
        feedProg = (ProgressBar) findViewById(R.id.feedLoading);

        //Global string values
        uName = loginSuccess.getStringExtra(FacebookLogin.USER_F_NAME);
        uID = loginSuccess.getStringExtra(FacebookLogin.USER_ID);
        poolID = loginSuccess.getStringExtra(FacebookLogin.POOL_ID);

        credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),    /* get the context for the application */
                poolID, // Identity Pool ID
                Regions.US_EAST_1           /* Region for your identity pool--US_EAST_1 or EU_WEST_1*/
        );

        //Prepare outgoing intent
        intentPairs.put(FacebookLogin.POOL_ID, poolID);
        intentPairs.put(FacebookLogin.USER_ID, uID);
        intentPairs.put(FacebookLogin.USER_F_NAME, uName);


        //GUI Update based on intent
        welcomeString = welcomeMessage.getText() + " " + uName + "!";
        welcomeMessage.setText(welcomeString);
        new getEvents().execute();
    }

    private class EventAdapter extends ArrayAdapter<Event>
    {
        private ArrayList<Event> events;
        private ViewHolder holder;

        public void addEvent(Event ev)
        {
            events.add(ev);
        }

        public EventAdapter(Context context, int profilePictureResourceID, ArrayList<Event> eventList)
        {
            super(context, profilePictureResourceID, eventList);
            this.events = new ArrayList<Event>();
            this.events.addAll(eventList);
        }

        private class ViewHolder
        {
            ImageView eventPic;
            TextView hostName;
            TextView eventTopic;
            TextView attendeeList;
            Button eventButton;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.list_item_event_info, null);

                holder = new ViewHolder();

                holder.eventPic = (ImageView) convertView.findViewById(R.id.eventPicture);
                holder.hostName = (TextView) convertView.findViewById(R.id.hostName);
                holder.eventTopic = (TextView) convertView.findViewById(R.id.eventTopic);
                holder.attendeeList = (TextView) convertView.findViewById(R.id.attendeeList);
                holder.eventButton = (Button) convertView.findViewById(R.id.goToEvent);


                convertView.setTag(holder);


            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }

            final Event event = events.get(position);

            holder.eventButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent gotoEvent = new Intent(getApplicationContext(), EventActivity.class);

                    gotoEvent.putExtra(EventCreationActivity.EVENT_ID,event.getEventID());
                    gotoEvent.putExtra(EventCreationActivity.EVENT_TOPIC, event.getTopic());
                    gotoEvent.putExtra(EventCreationActivity.EVENT_INVITES, event.getAttendees());
                    gotoEvent.putExtra(EventCreationActivity.EVENT_HOST_NAME, event.getHostName());
                    gotoEvent.putExtra(EventCreationActivity.EVENT_CATEGORY, event.getCategory());
                    gotoEvent.putExtra(FacebookLogin.POOL_ID, poolID);
                    gotoEvent.putExtra(FacebookLogin.USER_ID, uID);
                    gotoEvent.putExtra(FacebookLogin.USER_F_NAME, uName);


                    startActivity(gotoEvent);
                }
            });

            String cat = event.getCategory();

            holder.eventTopic.setText("The topic is: " + event.getTopic());
            holder.attendeeList.setText(event.getAttendees());

            //new getHost(holder.hostName).execute(event.getHostID());
            holder.hostName.setText(event.getHostName());

            //new DownloadImageTask(holder.hostPic).execute(host.getProfilePic());

            if( cat == null )
            {
                holder.eventPic.setImageResource(R.mipmap.gps_icon);
            }
            else {
                cat = cat.toLowerCase();

                if (cat.contains("location")) {
                    holder.eventPic.setImageResource(R.mipmap.gps_icon);
                } else if (cat.contains("food")) {
                    holder.eventPic.setImageResource(R.mipmap.food_icon);
                } else if (cat.contains("entertainment")) {
                    holder.eventPic.setImageResource(R.mipmap.entertainment_icon);
                } else if (cat.contains("random")) {
                    holder.eventPic.setImageResource(R.mipmap.rand_q_icon);
                } else {
                    holder.eventPic.setImageResource(R.mipmap.gps_icon);
                }
            }

            holder.eventTopic.setTag(event);

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

    class getEvents extends AsyncTask<Void, Void, ArrayList<Event>> {

        @Override
        protected ArrayList<Event> doInBackground(Void... params) {
            ArrayList<Event> temp = new ArrayList<Event>();
            AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
            DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);

            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
            PaginatedScanList<Event> result = mapper.scan(Event.class, scanExpression);

            int k;
            for (k = 0; k < result.size(); k++)
            {
                Event item = result.get(k);
                if(item.getAttendees() != null) {
                    if (item.getHostID().contentEquals(uID) || item.getAttendees().contains(uName)) {
                        temp.add(item);
                    }
                }
            }
            return temp;
        }

        @Override
        protected void onPostExecute(ArrayList<Event> res)
        {
            eventAdapter = new EventAdapter(getApplicationContext(), R.layout.list_item_event_info, res);
            eventList = (ListView) findViewById(R.id.eventList);
            eventList.setAdapter(eventAdapter);
            feedProg.setVisibility(View.GONE);
        }


    }

    class getHost extends AsyncTask<String, Void, User> {

        private TextView hostName;

        getHost(TextView hstName)
        {
            this.hostName = hstName;
        }
        @Override
        protected User doInBackground(String... params) {
            AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
            DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);

            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
            PaginatedScanList<User> result = mapper.scan(User.class, scanExpression);

            int k;
            for (k = 0; k < result.size(); k++)
            {
                User item = result.get(k);
                if (item.getUserID().contentEquals(uID))
                {
                    return item;
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(User hst)
        {
            hostName.setText(hst.getfName() + " " + hst.getlName());
        }
    }
}
