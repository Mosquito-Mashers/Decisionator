package com.csulb.decisionator.decisionator;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutionException;

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
    private EventAdapter eventAdapter;

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
    }

    private void initializeGlobals() {
        //Intent Initialization
        loginSuccess = getIntent();
        createEventIntent = new Intent(this, EventCreationActivity.class);

        //GUI assignments
        welcomeMessage = (TextView) findViewById(R.id.welcomeText);
        createEvent = (Button) findViewById(R.id.createEvent);

        //Global string values
        uName = loginSuccess.getStringExtra(FacebookLogin.USER_F_NAME);
        uID = loginSuccess.getStringExtra(FacebookLogin.USER_ID);
        poolID = loginSuccess.getStringExtra(FacebookLogin.POOL_ID);

        //Prepare outgoing intent
        intentPairs.put(FacebookLogin.POOL_ID, poolID);
        intentPairs.put(FacebookLogin.USER_ID, uID);
        intentPairs.put(FacebookLogin.USER_F_NAME, uName);

        //GUI Update based on intent
        welcomeString = welcomeMessage.getText() + " " + uName + "!";
        welcomeMessage.setText(welcomeString);
    }

    private class EventAdapter extends ArrayAdapter<Event>
    {
        private ArrayList<Event> events;

        public EventAdapter(Context context, int profilePictureResourceID, ArrayList<Event> eventList)
        {
            super(context, profilePictureResourceID, eventList);
            this.events = new ArrayList<Event>();
            this.events.addAll(eventList);
        }

        private class ViewHolder
        {
            ImageView eventPic;
            ImageView hostPic;
            TextView eventTopic;
            TextView attendeeList;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.list_item_user_info, null);

                holder = new ViewHolder();

                convertView.setTag(holder);


            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.friendContainer = (RelativeLayout) convertView.findViewById(R.id.friendContainer);
            holder.profilePic = (ImageView) convertView.findViewById(R.id.userProfilePicture);
            holder.name = (CheckBox) convertView.findViewById(R.id.userCheckbox);
            holder.friendContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RelativeLayout rtl = (RelativeLayout) v;
                    CheckBox cb = (CheckBox) rtl.getChildAt(0);
                    User user = (User)cb.getTag();

                    cb.performClick();
                    user.setSelected(cb.isSelected());
                }
            });

            Event event = events.get(position);

            if(event.getCategory() == null) {
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
}
