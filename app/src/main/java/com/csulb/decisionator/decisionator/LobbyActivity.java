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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class LobbyActivity extends AppCompatActivity {

    //Values retrieved from intent
    private String uName;
    private String uID;
    private String welcomeString;
    private String lastLogin;
    private String poolID;
    private static final Map<String, String> intentPairs = new HashMap<String, String>();

    //Gui items
    private Intent loginSuccess;
    private Intent logoutIntent;
    private Intent createEventIntent;
    private TextView welcomeMessage;
    private Button createEvent;
    private ImageButton refreshEvents;
    private ProgressBar feedProg;
    private EventAdapter eventAdapter;
    private ArrayList<Event> events;
    private ArrayList<User> users = new ArrayList<User>();
    private ListView eventList;
    private CognitoCachingCredentialsProvider credentialsProvider;
    private DateFormat format = new SimpleDateFormat("EEE MMM dd kk:mm:ss zzz yyyy");
    SimpleDateFormat date = new SimpleDateFormat("dd-MM-yyy HH:mm:ss z");

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_resources, menu);
        MenuItem item = menu.findItem(R.id.lobby);
        item.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                startActivity(logoutIntent);
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
        logoutIntent = new Intent(this, FacebookLogin.class);
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
        new getAllUsers().execute();
        new getCurrUser().execute(uID);
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
            ImageView newEvent;
            TextView newEvText;
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
                holder.newEvent = (ImageView) convertView.findViewById(R.id.newEvent);
                holder.newEvText = (TextView) convertView.findViewById(R.id.newEventText);
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
            Date lastLogDate = new Date();
            Date eventCreateDate = new Date();
            try {
                lastLogDate = date.parse(lastLogin);
                eventCreateDate = date.parse(event.getDateCreated());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if(lastLogDate.before(eventCreateDate))
            {
                holder.newEvent.setImageResource(R.mipmap.new_event_icon);
                holder.newEvText.setText("NEW!");
            }

            holder.eventButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent gotoEvent = new Intent(getApplicationContext(), EventActivity.class);

                    gotoEvent.putExtra(EventCreationActivity.EVENT_ID, event.getEventID());
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

            String attenList[] = event.getAttendees().split(", ");
            String attenName = "";

            for(int m = 0; m < users.size(); m++)
            {
                for(int j = 0; j < attenList.length; j++)
                {
                    if(users.get(m).getUserID().contentEquals(attenList[j]))
                    {
                        attenName += users.get(j).getfName() + " " +users.get(j).getlName() + ", ";
                    }
                }
            }
            holder.attendeeList.setText(attenName);

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
            ArrayList<Event> beforeRes = res;

            Collections.sort(res, new Comparator<Event>() {
                @Override
                public int compare(Event lhs, Event rhs) {

                    Date left = new Date();
                    Date right = new Date();
                    int result = 0;

                    try {
                        left = date.parse(lhs.getDateCreated());
                        right = date.parse(rhs.getDateCreated());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    result = right.after(left) ? 1 : -1;

                    return result;
                }
            });
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

    class getAllUsers extends AsyncTask<Void, Void, PaginatedScanList<User>> {

        @Override
        protected PaginatedScanList<User> doInBackground(Void... params) {
            AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
            DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);

            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
            PaginatedScanList<User> result = mapper.scan(User.class, scanExpression);


            return result;
        }

        @Override
        protected void onPostExecute(PaginatedScanList<User> res)
        {
            users.addAll(res);
        }
    }

    class getCurrUser extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
            DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);

            User result = mapper.load(User.class, params[0]);

            lastLogin = result.getLastLogin();

            return null;
        }
    }
}
