package com.csulb.decisionator.decisionator;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class EventCreationActivity extends AppCompatActivity {
    protected final static String EVENT_TOPIC = "com.decisionator.decisionator.evenetcreationactivity.EVENT_TOPIC";

    EditText eventTopic;
    Button inviteFriends;
    RadioGroup categories;
    RadioButton selectedCategory;
    Intent inviteClicked;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_creation);
        eventTopic = (EditText) findViewById(R.id.eventTopic);
        inviteFriends = (Button) findViewById(R.id.inviteFriendsBtn);
        categories = (RadioGroup) findViewById(R.id.eventCategories);

        inviteClicked = new Intent(this, LocationActivity.class);
        context = getApplicationContext();

        inviteFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String topic = eventTopic.getText().toString();
                inviteClicked.putExtra(EVENT_TOPIC,topic);
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
    }

    private void showPopup(CharSequence text, Context ctx)
    {
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(ctx, text, duration);
        toast.show();
    }
}
