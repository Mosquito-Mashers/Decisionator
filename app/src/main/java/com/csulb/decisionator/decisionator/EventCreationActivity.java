package com.csulb.decisionator.decisionator;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class EventCreationActivity extends AppCompatActivity {
    protected final static String EVENT_TOPIC = "com.decisionator.decisionator.evenetcreationactivity.EVENT_TOPIC";
    protected final static String EVENT_ID = "com.decisionator.decisionator.evenetcreationactivity.EVENT_ID";
    protected final static String EVENT_INVITES = "com.decisionator.decisionator.evenetcreationactivity.EVENT_INVITES";
    protected final static String EVENT_HOST_NAME = "com.decisionator.decisionator.evenetcreationactivity.EVENT_HOST_NAME";
    protected final static String EVENT_CATEGORY = "com.decisionator.decisionator.evenetcreationactivity.EVENT_CATEGORY";

    private CognitoCachingCredentialsProvider credentialsProvider;
    private SimpleDateFormat date = new SimpleDateFormat("dd-MM-yyy HH:mm:ss z");

    private Intent fromLobby;
    private Intent logoutIntent;
    private Intent lobbyIntent;
    private Intent moveToInvite;

    private static final Map<String, String> intentPairs = new HashMap<String, String>();
    private Context context;

    private String uID;
    private String poolID;
    private String uFname;
    public String topic;
    private UUID eventID;

    private TextView eventPredicate;
    private EditText eventTopic;
    private Button inviteFriends;
    private RadioGroup categories;
    private RadioButton locCategory;
    private RadioButton foodCategory;
    private RadioButton entertainCategory;
    private RadioButton randomCategory;
    private RadioButton selectedCategory;

    private checkUpdates updateRefresh = new checkUpdates();
    private Intent notificationIntent;
    private static final int notifyID = 111;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_resources, menu);
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_creation);

        //Initialize the global variables for:
        //Android objects
        //Api objects
        initializeGlobals();

        //Create and assign the appropriate listeners for each gui object
        initializeListeners();

        prepareIntent(moveToInvite, intentPairs);
    }

    private void initializeGlobals() {
        moveToInvite = new Intent(this, InviteFriendsActivity.class);
        logoutIntent = new Intent(this, FacebookLogin.class);
        lobbyIntent = new Intent(this, LobbyActivity.class);
        fromLobby = getIntent();

        uID = fromLobby.getStringExtra(FacebookLogin.USER_ID);
        poolID = fromLobby.getStringExtra(FacebookLogin.POOL_ID);
        uFname = fromLobby.getStringExtra(FacebookLogin.USER_F_NAME);

        credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),    /* get the context for the application */
                poolID, // Identity Pool ID
                Regions.US_EAST_1           /* Region for your identity pool--US_EAST_1 or EU_WEST_1*/
        );

        lobbyIntent.putExtra(FacebookLogin.USER_ID,uID);
        lobbyIntent.putExtra(FacebookLogin.POOL_ID,poolID);
        lobbyIntent.putExtra(FacebookLogin.USER_F_NAME,uFname);

        eventID = UUID.randomUUID();
        eventPredicate = (TextView) findViewById(R.id.topicPredicate);
        eventTopic = (EditText) findViewById(R.id.eventTopic);
        inviteFriends = (Button) findViewById(R.id.inviteFriendsBtn);
        locCategory = (RadioButton)findViewById(R.id.radioLocation);
        foodCategory = (RadioButton)findViewById(R.id.radioFood);
        entertainCategory = (RadioButton)findViewById(R.id.radioEntertainment);
        randomCategory = (RadioButton)findViewById(R.id.radioRandom);
        categories = (RadioGroup) findViewById(R.id.eventCategories);


        context = getApplicationContext();
        updateRefresh.execute();
    }

    public void initializeListeners() {

        locCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventPredicate.setText("Lets go to a...");
                eventTopic.setHint("Bar, Zoo, Park,...");
            }
        });
        foodCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventPredicate.setText("I'm feeling...");
                eventTopic.setHint("Thai, Mexican, Italian,...");
            }
        });
        entertainCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventPredicate.setText("Lets go to a...");
                eventTopic.setHint("Movie, Theme Park, Club,...");
            }
        });
        randomCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
            }
        });

        eventTopic.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard();
                }
            }
        });

        inviteFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                topic = eventTopic.getText().toString();
                if(TextUtils.isEmpty(topic) || topic.trim().equals(""))
                {
                    eventTopic.setError("Please enter your selection");
                    return;
                }
                Date currDate = new Date();

                intentPairs.put(EVENT_TOPIC, topic);
                intentPairs.put(FacebookLogin.POOL_ID, poolID);
                intentPairs.put(FacebookLogin.USER_ID, uID);
                intentPairs.put(FacebookLogin.USER_F_NAME, uFname);
                intentPairs.put(EVENT_ID, eventID.toString());

                Event evnt = new Event();
                evnt.setEventID(eventID.toString());
                evnt.setHostID(uID);
                evnt.setHostName(uFname);
                evnt.setTopic(topic);
                evnt.setLatitude(33.760605);
                evnt.setLongitude(-118.156446);
                evnt.setDateCreated(date.format(currDate));

                if(categories.getCheckedRadioButtonId() > 0)
                {
                    selectedCategory = (RadioButton) findViewById(categories.getCheckedRadioButtonId());
                }
                else
                {
                    selectedCategory = (RadioButton) findViewById(R.id.radioLocation);
                }
                evnt.setCategory(selectedCategory.getText().toString());
                intentPairs.put(EVENT_CATEGORY, evnt.getCategory());
                prepareIntent(moveToInvite, intentPairs);

                addToDB(evnt);
                //new addEventToDB().execute(evnt);

                updateRefresh.cancel(true);
                startActivity(moveToInvite);
            }
        });
    }

    public void addToDB(Event ev)
    {
        new addEventToDB().execute(ev);
    }

    private void prepareIntent(Intent moveToInvite, Map<String, String> intentPairs) {
        Iterator mapIter = intentPairs.entrySet().iterator();

        while (mapIter.hasNext())
        {
            Map.Entry kvPair = (Map.Entry) mapIter.next();
            moveToInvite.putExtra(kvPair.getKey().toString(), kvPair.getValue().toString());
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(eventTopic.getWindowToken(), 0);
    }

    class addEventToDB extends AsyncTask<Event, Void, Void> {

        protected Void doInBackground(Event... arg0) {
            AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
            DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);
            mapper.save(arg0[0]);

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
            notificationIntent.putExtra(FacebookLogin.USER_F_NAME, uFname);

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