package com.csulb.decisionator.decisionator;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

import java.io.InputStream;

public class MyProfile extends AppCompatActivity {

    private Intent lobbyIntent;
    private Intent logoutIntent;
    private Intent enterProfile;


    private String poolID;
    private String uFName;
    String uID;

    private TextView profileWelcome;
    private TextView picAnalysis;
    private TextView locationAnalysis;
    private TextView likesAnalysis;
    private ImageView profilePic;
    private ProgressBar profileLoading;
    private String [] achieveString;
    private LinearLayout gallery;

    private uProfile currProfile;
    private User currUser;

    private CognitoCachingCredentialsProvider credentialsProvider;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_resources, menu);
        MenuItem itemChart = menu.findItem(R.id.chart);
        itemChart.setVisible(false);
        MenuItem itemProfile = menu.findItem(R.id.profile);
        itemProfile.setVisible(false);
        MenuItem itemChart2 = menu.findItem(R.id.chart2);
        itemChart2.setVisible(false);
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        initializeGlobals();


    }

    public void initializeGlobals()
    {
        logoutIntent = new Intent(this, FacebookLogin.class);
        lobbyIntent = new Intent(this, LobbyActivity.class);

        enterProfile = getIntent();

        poolID = enterProfile.getStringExtra(FacebookLogin.POOL_ID);
        uFName = enterProfile.getStringExtra(FacebookLogin.USER_F_NAME);
        uID = enterProfile.getStringExtra(FacebookLogin.USER_ID);

        credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),    /* get the context for the application */
                poolID, // Identity Pool ID
                Regions.US_EAST_1           /* Region for your identity pool--US_EAST_1 or EU_WEST_1*/
        );

        lobbyIntent.putExtra(FacebookLogin.USER_ID,uID);
        lobbyIntent.putExtra(FacebookLogin.POOL_ID,poolID);
        lobbyIntent.putExtra(FacebookLogin.USER_F_NAME,uFName);

        profileWelcome = (TextView) findViewById(R.id.userProfileWelcome);
        picAnalysis = (TextView) findViewById(R.id.profilePicAnalysis);
        locationAnalysis = (TextView) findViewById(R.id.locationAnalysis);
        likesAnalysis = (TextView) findViewById(R.id.publicLikesAnalysis);
        profilePic = (ImageView) findViewById(R.id.myProfilePic);
        profileLoading = (ProgressBar) findViewById(R.id.profileLoading);

        gallery = (LinearLayout) findViewById(R.id.Gallery);

        new getUserProfile().execute(uID);
        new SetAchievements().execute(uID);

        //imageView.setImageResource(R.mipmap.star_icon);
    }
    private void uiChange(){
        ImageView achieve1 = new ImageView(getApplicationContext());
        achieve1.setImageResource(R.mipmap.clear_icon);
        achieve1.setLayoutParams(new ViewGroup.LayoutParams(220, ViewGroup.LayoutParams.MATCH_PARENT));
        gallery.addView(achieve1);

        ImageView achieve2 = new ImageView(getApplicationContext());
        achieve2.setImageResource(R.mipmap.clear_icon);
        achieve2.setLayoutParams(new ViewGroup.LayoutParams(220, ViewGroup.LayoutParams.MATCH_PARENT));
        gallery.addView(achieve2);

        ImageView achieve3 = new ImageView(getApplicationContext());
        achieve3.setImageResource(R.mipmap.clear_icon);
        achieve3.setLayoutParams(new ViewGroup.LayoutParams(220, ViewGroup.LayoutParams.MATCH_PARENT));
        gallery.addView(achieve3);

        ImageView achieve4 = new ImageView(getApplicationContext());
        achieve4.setImageResource(R.mipmap.clear_icon);
        achieve4.setLayoutParams(new ViewGroup.LayoutParams(220, ViewGroup.LayoutParams.MATCH_PARENT));
        gallery.addView(achieve4);

        ImageView achieve5 = new ImageView(getApplicationContext());
        achieve5.setImageResource(R.mipmap.clear_icon);
        achieve5.setLayoutParams(new ViewGroup.LayoutParams(220, ViewGroup.LayoutParams.MATCH_PARENT));
        gallery.addView(achieve5);

        for(int i = 0; i < achieveString.length; i++){
            if(achieveString[i].equals("1")){
                achieve1.setImageResource(R.mipmap.trophy_sample);
            }
            if(achieveString[i].equals("2")){
                achieve2.setImageResource(R.mipmap.trophy_sample2);
            }
            if(achieveString[i].equals("3")){
                achieve3.setImageResource(R.mipmap.trophy_sample3);
            }
            if(achieveString[i].equals("4")){
                achieve3.setImageResource(R.mipmap.trophy_sample4);
            }
            if(achieveString[i].equals("5")){
                achieve3.setImageResource(R.mipmap.trophy_sample5);
            }

        }
    }

    class getUserProfile extends AsyncTask<String,Void,Void>{

        @Override
        protected Void doInBackground(String... params) {
            AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
            DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);

            currProfile = mapper.load(uProfile.class,params[0]);
            currUser = mapper.load(User.class,params[0]);

            return null;
        }

        @Override
        protected void onPostExecute(Void param)
        {
            profileWelcome.setText(currUser.getfName() + " " + currUser.getlName());
            new DownloadImageTask(profilePic).execute(currUser.getProfilePic());
            profileLoading.setVisibility(View.GONE);
            WordCloudGenerator gen;
            if(currProfile.getPlacesTags() != null) {
                gen = new WordCloudGenerator(currProfile.getPlacesTags());

                gen.createFrequencyMap();

                locationAnalysis.setText(gen.buildSmallMap());
            }

            if(currProfile.getImageTags() != null) {
                gen = new WordCloudGenerator(currProfile.getImageTags());

                gen.createFrequencyMap();

                picAnalysis.setText(gen.buildSmallMap());
            }

            if(currProfile.getLikeTags() != null) {
                gen = new WordCloudGenerator(currProfile.getLikeTags());

                gen.createFrequencyMap();

                likesAnalysis.setText(gen.buildSmallMap());
            }
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

    private class SetAchievements extends  AsyncTask<String, Void, Void> {

        @Override
        public Void doInBackground(String... params){
            AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
            DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);

            User user = mapper.load(User.class, params[0]);
            achieveString = user.getAchievements().split(",");
            return null;
        }

        @Override
        public void onPostExecute(Void v) {
            uiChange();
        }

    }
}
