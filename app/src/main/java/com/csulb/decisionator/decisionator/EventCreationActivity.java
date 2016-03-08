package com.csulb.decisionator.decisionator;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

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
    private String uID;
    private String poolID;
    private String uFname;
    private String topic;
    private UUID eventID;
    private static final Map<String, String> intentPairs = new HashMap<String, String>();

    private EditText eventTopic;
    private Button inviteFriends;
    private RadioGroup categories;
    private RadioButton selectedCategory;

    private Intent fromLobby;
    private Intent logoutIntent;
    private Intent moveToInvite;
    private Context context;

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
        setContentView(R.layout.activity_event_creation);

        //Initialize the global variables for:
        //Android objects
        //Api objects
        initializeGlobals();

        //Create and assign the appropriate listeners for each gui object
        initializeListeners();

        prepareIntent(moveToInvite, intentPairs);
    }

    private void prepareIntent(Intent moveToInvite, Map<String, String> intentPairs) {
        Iterator mapIter = intentPairs.entrySet().iterator();

        while (mapIter.hasNext())
        {
            Map.Entry kvPair = (Map.Entry) mapIter.next();
            moveToInvite.putExtra(kvPair.getKey().toString(), kvPair.getValue().toString());
        }
    }

    private void initializeGlobals() {
        moveToInvite = new Intent(this, InviteFriendsActivity.class);
        logoutIntent = new Intent(this, FacebookLogin.class);
        fromLobby = getIntent();

        uID = fromLobby.getStringExtra(FacebookLogin.USER_ID);
        poolID = fromLobby.getStringExtra(FacebookLogin.POOL_ID);
        uFname = fromLobby.getStringExtra(FacebookLogin.USER_F_NAME);

        credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),    /* get the context for the application */
                poolID, // Identity Pool ID
                Regions.US_EAST_1           /* Region for your identity pool--US_EAST_1 or EU_WEST_1*/
        );

        eventID = UUID.randomUUID();
        eventTopic = (EditText) findViewById(R.id.eventTopic);
        inviteFriends = (Button) findViewById(R.id.inviteFriendsBtn);
        categories = (RadioGroup) findViewById(R.id.eventCategories);

        context = getApplicationContext();
    }

    private void initializeListeners() {
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

                Date date = new Date();

                intentPairs.put(EVENT_TOPIC, topic);
                intentPairs.put(FacebookLogin.POOL_ID, poolID);
                intentPairs.put(FacebookLogin.USER_ID, uID);
                intentPairs.put(FacebookLogin.USER_F_NAME, uFname);
                intentPairs.put(EVENT_ID, eventID.toString());

                prepareIntent(moveToInvite, intentPairs);

                Event evnt = new Event();
                evnt.setEventID(eventID.toString());
                evnt.setHostID(uID);
                evnt.setHostName(uFname);
                evnt.setTopic(topic);
                evnt.setDateCreated(date.toString());



                if(categories.getCheckedRadioButtonId() > 0)
                {
                    selectedCategory = (RadioButton) findViewById(categories.getCheckedRadioButtonId());
                }
                else
                {
                    selectedCategory = (RadioButton) findViewById(R.id.radioLocation);
                }
                evnt.setCategory(selectedCategory.getText().toString());

                new addEventToDB().execute(evnt);

                startActivity(moveToInvite);
            }
        });
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
}
