package com.csulb.decisionator.decisionator;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Jose on 4/6/2016.
 */
public class UsersHistory extends AppCompatActivity {

    private String uID;
    private String poolID;
    private String uName;
    private CognitoCachingCredentialsProvider credentialsProvider;
    private TextView test;
    private ListView list;
    private Intent fromLobby;
    private EventAdapter eventAdapter;

    private ArrayList<Event> events = new ArrayList<Event>();


    //@Override
    protected void OnCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_history);
        initializeGlobals();

    }

    private void initializeGlobals() {

        fromLobby = getIntent();

        uID = fromLobby.getStringExtra(FacebookLogin.USER_ID);
        poolID = fromLobby.getStringExtra(FacebookLogin.POOL_ID);
        uName = fromLobby.getStringExtra(FacebookLogin.USER_F_NAME);
        credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),    /* get the context for the application */
                "us-east-1:a74e3f8c-6c2b-40b6-89d5-46d4f870a6f2", // Identity Pool ID
                Regions.US_EAST_1           /* Region for your identity pool--US_EAST_1 or EU_WEST_1*/
        );
        test = (TextView) findViewById(R.id.test);
        test.setText("Welcome");
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
                if (item.getAttendees() != null) {

                    String[] attens = item.getAttendees().split(",");
                    for (m = 0; m < attens.length; m++) {
                        if (attens[m].contentEquals(uID)) {
                            temp.add(item);
                            count++;
                            break;
                        }
                    }

                    if (item.getHostID().contentEquals(uID)) {
                        temp.add(item);
                        count++;
                    }
                }
            }
            return temp;
        }

        @Override
        protected void onPostExecute(ArrayList<Event> res) {
            list = (ListView) findViewById(R.id.list);
            list.setAdapter(eventAdapter);
        }

    }
}
