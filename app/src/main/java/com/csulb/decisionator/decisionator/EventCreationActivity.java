package com.csulb.decisionator.decisionator;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class EventCreationActivity extends AppCompatActivity {
    protected final static String EVENT_TOPIC = "com.decisionator.decisionator.evenetcreationactivity.EVENT_TOPIC";
    protected final static String EVENT_ID = "com.decisionator.decisionator.evenetcreationactivity.EVENT_ID";

    private CognitoCachingCredentialsProvider credentialsProvider;
    private String uID;
    private String poolID;
    private String uFname;

    EditText eventTopic;
    Button inviteFriends;
    RadioGroup categories;
    RadioButton selectedCategory;
    Intent fromLobby;
    Intent inviteClicked;
    Intent moveToInvite;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_creation);

        fromLobby = getIntent();

        uID = fromLobby.getStringExtra(FacebookLogin.USER_ID);
        poolID = fromLobby.getStringExtra(FacebookLogin.POOL_ID);
        uFname = fromLobby.getStringExtra(FacebookLogin.USER_F_NAME);

        credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),    /* get the context for the application */
                poolID, // Identity Pool ID
                Regions.US_EAST_1           /* Region for your identity pool--US_EAST_1 or EU_WEST_1*/
        );
        eventTopic = (EditText) findViewById(R.id.eventTopic);
        inviteFriends = (Button) findViewById(R.id.inviteFriendsBtn);
        categories = (RadioGroup) findViewById(R.id.eventCategories);

        inviteClicked = new Intent(this, LocationActivity.class);
        moveToInvite = new Intent(this, InviteFriendsActivity.class);
        context = getApplicationContext();


        inviteFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String topic = eventTopic.getText().toString();
                moveToInvite.putExtra(EVENT_TOPIC,topic);
                moveToInvite.putExtra(FacebookLogin.POOL_ID,poolID);
                moveToInvite.putExtra(FacebookLogin.USER_ID,uID);
                moveToInvite.putExtra(FacebookLogin.USER_F_NAME,uFname);

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();

                Event evnt = new Event();

                UUID eventID = UUID.randomUUID();
                moveToInvite.putExtra(EVENT_ID,eventID.toString());

                evnt.setEventID(eventID.toString());
                evnt.setHostID(uID);
                evnt.setTopic(topic);
                evnt.setDateCreated(date.toString());

                new addEventToDB().execute(evnt);

                showPopup("The topic is " + topic,context);
                if(categories.getCheckedRadioButtonId() > 0)
                {
                    selectedCategory = (RadioButton) findViewById(categories.getCheckedRadioButtonId());
                    showPopup("The category is " + selectedCategory.getText().toString(), context);
                }
                else
                {
                    showPopup("The category is null", context);
                }
                startActivity(moveToInvite);
            }
        });
        /*
        inviteFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String topic = eventTopic.getText().toString();
                inviteClicked.putExtra(EVENT_TOPIC,topic);
                inviteClicked.putExtra(FacebookLogin.POOL_ID,poolID);
                inviteClicked.putExtra(FacebookLogin.USER_ID,uID);

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();

                Event evnt = new Event();

                UUID eventID = UUID.randomUUID();
                inviteClicked.putExtra(EVENT_ID,eventID.toString());

                evnt.setEventID(eventID.toString());
                evnt.setHostID(uID);
                evnt.setTopic(topic);
                evnt.setDateCreated(date.toString());

                new addEventToDB().execute(evnt);

                showPopup("The topic is " + topic,context);
                if(categories.getCheckedRadioButtonId() > 0)
                {
                    selectedCategory = (RadioButton) findViewById(categories.getCheckedRadioButtonId());
                    showPopup("The category is " + selectedCategory.getText().toString(), context);
                }
                else
                {
                    showPopup("The category is null", context);
                }
                startActivity(inviteClicked);
            }
        });
        */
    }

    public void showPopup(CharSequence text, Context ctx)
    {
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(ctx, text, duration);
        toast.show();
    }

    private CognitoCachingCredentialsProvider validateCredentials()
    {
        FacebookLogin fb = new FacebookLogin();


        return fb.getCredentials();
    }

    class addEventToDB extends AsyncTask<Event, Void, Void> {

        protected Void doInBackground(Event... arg0) {
            AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
            DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);
            mapper.save(arg0[0]);

            return null;
        }
    }
    /*
    class getLatestEvent extends AsyncTask<Void, Void, Integer> {

        protected Integer doInBackground(Void... arg0) {
            AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
            DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);

            ddbClient.getTa

            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
            scanExpression.

            Event temp = mapper.load(Event.class, arg0[0].getEventID());

            if(temp == null)
            {
                mapper.save(arg0[0]);
            }
            return null;
        }
    }
    */
}
