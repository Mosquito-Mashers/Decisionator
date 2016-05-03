package com.csulb.decisionator.decisionator;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
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
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ListView;
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
    private ListView achieve;
    private AchievementAdapter achievementAdaptor1;

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
        achieve = (ListView) findViewById(R.id.Achievements);

        new getUserProfile().execute(uID);
        new SetAchievements().execute(uID);
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
                gen = new WordCloudGenerator(currProfile.getPlacesTags(), null);

                gen.createFrequencyMap();

                locationAnalysis.setText(gen.buildSmallMap());
            }

            if(currProfile.getImageTags() != null) {
                gen = new WordCloudGenerator(currProfile.getImageTags(), null);

                gen.createFrequencyMap();

                picAnalysis.setText(gen.buildSmallMap());
            }

            if(currProfile.getLikeTags() != null) {
                gen = new WordCloudGenerator(currProfile.getLikeTags(), null);

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
            String [] achieve1 = user.getAchievements().split(",");
            achievementAdaptor1 = new AchievementAdapter(getApplicationContext(), achieve1);
            return null;
        }

        public void onPostExecute() {
            achieve.setAdapter(achievementAdaptor1);
        }

    }
    private class AchievementAdapter extends ArrayAdapter<String>{

        private String[] values;

        public AchievementAdapter(Context context, String[] values){
            super(context, 0 ,values);
            this.values = values;
        }

        public View getView(int position, View convertView, ViewGroup Parent){
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(android.R.layout.activity_list_item, null);
            ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
            if(values[position].equals(1)){
                imageView.setImageResource(R.mipmap.star_icon);
            }
            else {
                imageView.setImageResource(R.mipmap.clear_icon);
            }
            if(values[position].equals(2)){
                imageView.setImageResource(R.mipmap.star_icon);
            }
            else {
                imageView.setImageResource(R.mipmap.clear_icon);
            }
            if(values[position].equals(3)){
                imageView.setImageResource(R.mipmap.star_icon);
            }
            else {
                imageView.setImageResource(R.mipmap.clear_icon);
            }
            return rowView;
        }
    }
}
