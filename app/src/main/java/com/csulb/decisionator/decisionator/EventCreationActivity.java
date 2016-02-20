package com.csulb.decisionator.decisionator;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class EventCreationActivity extends AppCompatActivity {

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

        context = getApplicationContext();

        inviteFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedCategory = (RadioButton) findViewById(categories.getCheckedRadioButtonId());
                showPopup("The topic is " + eventTopic.getText(),context);
                if(selectedCategory.getText() != null) {
                    showPopup("The category is " + selectedCategory.getText().toString(), context);
                }
                else
                {
                    showPopup("The category is null", context);
                }
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
