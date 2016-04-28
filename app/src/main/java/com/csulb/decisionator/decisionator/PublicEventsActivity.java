package com.csulb.decisionator.decisionator;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class PublicEventsActivity extends AppCompatActivity {

    private String uID;
    private String poolID;
    private String uName;
    private CognitoCachingCredentialsProvider credentialsProvider;
    private TextView welcomeText;
    public ListView historyList;
    private ProgressBar historyFeedProg;
    private Intent fromLobby;
    private Intent notificationIntent;
    private Intent lobbyIntent;
    private Intent logoutIntent;
    private Intent profileIntent;
    private EventAdapter eventAdapter;
    private checkUpdates updateRefresh = new checkUpdates();

    private static final int notifyID = 111;

    SimpleDateFormat date = new SimpleDateFormat("dd-MM-yyy HH:mm:ss z");

    private ArrayList<Event> events = new ArrayList<Event>();
    private ArrayList<User> users = new ArrayList<User>();

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
            case R.id.profile:
                startActivity(profileIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_events);

        initializeGlobals();
    }


    public void initializeGlobals() {

        fromLobby = getIntent();
        lobbyIntent = new Intent(this, LobbyActivity.class);
        logoutIntent = new Intent(this, FacebookLogin.class);
        profileIntent = new Intent(this, MyProfile.class);

        uID = fromLobby.getStringExtra(FacebookLogin.USER_ID);
        poolID = fromLobby.getStringExtra(FacebookLogin.POOL_ID);
        uName = fromLobby.getStringExtra(FacebookLogin.USER_F_NAME);
        credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),    /* get the context for the application */
                "us-east-1:a74e3f8c-6c2b-40b6-89d5-46d4f870a6f2", // Identity Pool ID
                Regions.US_EAST_1           /* Region for your identity pool--US_EAST_1 or EU_WEST_1*/
        );
        welcomeText = (TextView) findViewById(R.id.publicEventWelcome);
        historyFeedProg = (ProgressBar) findViewById(R.id.PublicEventFeedProg);
        historyFeedProg.setVisibility(View.VISIBLE);

        lobbyIntent.putExtra(FacebookLogin.USER_ID, uID);
        lobbyIntent.putExtra(FacebookLogin.POOL_ID, poolID);
        lobbyIntent.putExtra(FacebookLogin.USER_F_NAME, uName);
        profileIntent.putExtra(FacebookLogin.USER_ID, uID);
        profileIntent.putExtra(FacebookLogin.POOL_ID, poolID);
        profileIntent.putExtra(FacebookLogin.USER_F_NAME, uName);

        welcomeText.setText("Join a public event!");
        new getAllUsers().execute();
        new getEvents().execute();
    }


    private class EventAdapter extends ArrayAdapter<Event> {
        private ArrayList<Event> events;

        @Override
        public int getCount(){
            return this.events!=null ? this.events.size() : 0;
        }

        public EventAdapter(Context context, int profilePictureResourceID, ArrayList<Event> eventList) {
            super(context, profilePictureResourceID, eventList);
            this.events = new ArrayList<Event>();
            this.events.addAll(eventList);
        }

        public void removeEvent(int position) {
            this.events.remove(position);
            this.notifyDataSetChanged();
        }

        private class ViewHolder {
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
                LayoutInflater vi = (LayoutInflater) getSystemService(
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
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final Event event = events.get(position);
            holder.newEvent.setVisibility(View.GONE);
            holder.newEvText.setVisibility(View.GONE);

            if (event.getViewedList() == null) {
                holder.newEvent.setVisibility(View.VISIBLE);
                holder.newEvText.setVisibility(View.VISIBLE);
                holder.newEvent.setImageResource(R.mipmap.new_event_icon);
                holder.newEvText.setText("NEW!");
            } else {

                String viewed[] = event.getViewedList().split(",");
                boolean alreadyViewed = false;
                for (int k = 0; k < viewed.length; k++) {

                    if (viewed[k].contentEquals(uID)) {
                        alreadyViewed = true;
                        break;
                    }
                }
                if (!alreadyViewed) {
                    holder.newEvent.setVisibility(View.VISIBLE);
                    holder.newEvText.setVisibility(View.VISIBLE);
                    holder.newEvent.setImageResource(R.mipmap.new_event_icon);
                    holder.newEvText.setText("NEW!");
                }
            }
            holder.eventButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new updateViewedList().execute(event.getEventID());
                    Intent gotoEvent = new Intent(getApplicationContext(), EventActivity.class);

                    gotoEvent.putExtra(EventCreationActivity.EVENT_ID, event.getEventID());
                    gotoEvent.putExtra(EventCreationActivity.EVENT_TOPIC, event.getTopic());
                    gotoEvent.putExtra(EventCreationActivity.EVENT_INVITES, event.getAttendees());
                    gotoEvent.putExtra(EventCreationActivity.EVENT_HOST_NAME, event.getHostName());
                    gotoEvent.putExtra(EventCreationActivity.EVENT_CATEGORY, event.getCategory());
                    gotoEvent.putExtra(FacebookLogin.POOL_ID, poolID);
                    gotoEvent.putExtra(FacebookLogin.USER_ID, uID);
                    gotoEvent.putExtra(FacebookLogin.USER_F_NAME, uName);
                    updateRefresh.cancel(true);
                    startActivity(gotoEvent);
                }
            });

            String cat = event.getCategory();

            holder.eventTopic.setText("The topic is: " + event.getTopic());

            String attenName = "";

            if(event.getAttendees() != null) {
                String attenList[] = event.getAttendees().split(",");
                int count = 0;

                for (int m = 0; m < users.size(); m++) {
                    for (int j = 0; j < attenList.length; j++) {
                        if (users.get(m).getUserID().contentEquals(attenList[j])) {
                            if (count < 3) {
                                if (count != 2) {
                                    attenName += users.get(m).getfName() + " " + users.get(m).getlName() + ", ";
                                } else {
                                    attenName += users.get(m).getfName() + " " + users.get(m).getlName() + " ";
                                }
                            }
                            count++;
                        }
                    }
                }

                if (count > 3) {
                    attenName += "+ " + (count - 2) + " more";
                }
                if (count == 0) {
                    attenName = "No one";
                }
            }
            else
            {
                attenName = "No one";
            }
            holder.attendeeList.setText(attenName);

            holder.hostName.setText(event.getHostName());

            if (cat == null) {
                holder.eventPic.setImageResource(R.mipmap.gps_icon);
            } else {
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


    class getEvents extends AsyncTask<Void, Void, ArrayList<Event>> {
        protected ArrayList<Event> doInBackground(Void... params) {
            ArrayList<Event> temp = new ArrayList<Event>();
            AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
            DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);

            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
            PaginatedScanList<Event> result = mapper.scan(Event.class, scanExpression);
            int k;
            int m;
            int count = 0;
            for (k = 0; k < result.size(); k++) {
                Event item = result.get(k);
                if(!item.getIsPrivate())
                {
                    temp.add(item);
                }
            }
            return temp;
        }

        @Override
        protected void onPostExecute(ArrayList<Event> res) {
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
            historyList = (ListView) findViewById(R.id.PublicEventlist);
            historyList.setAdapter(eventAdapter);
            SwipeDismissListViewTouchListener touchListener = new SwipeDismissListViewTouchListener(historyList, new SwipeDismissListViewTouchListener.OnDismissCallback() {
                @Override
                public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                    for (int position : reverseSortedPositions) {
                        eventAdapter.removeEvent(position);
                    }
                    eventAdapter.notifyDataSetChanged();
                }
            });

            historyList.setOnTouchListener(touchListener);
            historyFeedProg.setVisibility(View.GONE);

            updateRefresh = new checkUpdates();

            updateRefresh.execute();

            historyList = (ListView) findViewById(R.id.PublicEventlist);
            historyList.setAdapter(eventAdapter);
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

    class updateViewedList extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
            DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);

            Event result = mapper.load(Event.class, params[0]);
            Event temp;
            String[] existingViews;
            int k;
            boolean existsInList = false;

            if (result != null) {
                temp = result;
                if (result.getViewedList() == null) {
                    temp.setViewedList(uID + ",");
                } else {
                    existingViews = temp.getViewedList().split(",");
                    for (k = 0; k < existingViews.length; k++) {
                        if (existingViews[k].contentEquals(uID)) {
                            existsInList = true;
                            break;
                        }
                    }
                    if (!existsInList) {
                        temp.setViewedList(temp.getViewedList() + uID + ",");
                    }
                }
                mapper.save(temp);
            }
            return null;
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
        protected void onPostExecute(PaginatedScanList<User> res) {
            users.addAll(res);
        }
    }

    //accessors
    public ArrayList<Event> getEvents() {
        return events;
    }
    public ArrayList<User> getUsers() {
        return users;
    }


}
