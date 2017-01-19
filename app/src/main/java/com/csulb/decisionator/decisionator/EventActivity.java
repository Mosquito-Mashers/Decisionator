package com.csulb.decisionator.decisionator;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class EventActivity extends AppCompatActivity  implements OnMapReadyCallback {
    protected final static String PERSONALITY_DATA = "com.csulb.decisionator.PERSONALITY_DATA";
    protected final static String CURRENT_USER_DATA = "com.csulb.decisionator.PERSONALITY_DATA";
    protected final static String FRIEND_DATA = "com.csulb.decisionator.PERSONALITY_DATA";
    protected final static String WORD_CLOUD_DATA = "com.csulb.decisionator.WORD_CLOUD_DATA";
    protected final static String TOP_VENUE_DATA = "com.csulb.decisionator.TOP_VENUE_DATA";
    private FriendAdapter friendAdapter;

    private Intent enterEvent;
    private Intent logoutIntent;
    private Intent lobbyIntent;
    private Intent profileIntent;
    private Map<String, String> intentPairs = new HashMap<String, String>();
    private CognitoCachingCredentialsProvider credentialsProvider;

    private String eTopic;
    private String eHost;
    private String eInvites;
    private String eCategory;
    private String eID;
    private String eHostID;
    private String poolID;
    private String uID;
    private String uName;
    private String strForCloud = "";
    private String topVenues = "";
    private String globalCloud;
    private boolean isHost = false;
    private String allTagsStrForCloud = "";
    private String allTagsStrForMe = "";
    private String allTagsStrForFriend = "";
    private String allCommonTags = "";
    private User currUser;
    private uProfile currProfile;



    private Event currEvent;
    private String venue;
    private Location mid;
    private LatLng finalLoc;

    private ArrayList<String> placesCloud = new ArrayList<String>();
    private ArrayList<User> allUsers = new ArrayList<User>();
    private ArrayList<uProfile> allProfiles = new ArrayList<uProfile>();
    private ArrayList<Bitmap> userPics = new ArrayList<Bitmap>();
    private ArrayList<String> invited = new ArrayList<String>();
    private ArrayList<String> rsvped = new ArrayList<String>();
    private ArrayList<String> personalityCloud = new ArrayList<String>();
    private SpannableString wordCloud;

    private ListView invitedList;
    private Button rsvp;
    private Button share;
    private Button joinEvent;
    private ImageButton clearFragment;
    private RelativeLayout fragContainer;
    private GoogleMap map;
    private ShareDialog shareDialog;

    //Ron testing
    private RelativeLayout fragContainer2;
    private ImageButton clearFragment2;
    private RelativeLayout fragContainer3;

    //5/7 Personality Pie
    private RelativeLayout ppFragContainer;
    private ImageButton clearFragment3;

    private checkUpdates updateRefresh = new checkUpdates();
    private Intent notificationIntent;
    private static final int notifyID = 111;

    private ResultGraphFragment frag;
    private ResultGraphFragment2 frag2;
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
                updateRefresh.cancel(true);
                startActivity(logoutIntent);
                return true;
            case R.id.lobby:
                updateRefresh.cancel(true);
                startActivity(lobbyIntent);
                return true;
            case R.id.profile:
                updateRefresh.cancel(true);
                startActivity(profileIntent);
                return true;
            case R.id.chart:

                Bundle fragArgs = new Bundle();
                fragArgs.putString(WORD_CLOUD_DATA, strForCloud);
                fragArgs.putString(TOP_VENUE_DATA, topVenues);
                ResultGraphFragment fragInfo = ResultGraphFragment.newInstance(fragArgs);
                getSupportFragmentManager().beginTransaction().replace(R.id.resultGraphFragmentContainer, fragInfo).commit();
                fragContainer.setVisibility(View.VISIBLE);
                enableDisableView(findViewById(R.id.event_main_container), false);
                return true;
            case R.id.chart2:

                Bundle fragArgs2 = new Bundle();
                fragArgs2.putString(WORD_CLOUD_DATA, strForCloud);
                fragArgs2.putString(TOP_VENUE_DATA, topVenues);
                ResultGraphFragment2 fragInfo2 = ResultGraphFragment2.newInstance(fragArgs2);
                getSupportFragmentManager().beginTransaction().replace(R.id.resultGraphFragmentContainer2, fragInfo2).commit();
                fragContainer2.setVisibility(View.VISIBLE);
                enableDisableView(findViewById(R.id.event_main_container), false);
                return true;
            //case R.id.ppChart:
            //    Bundle ppArgs = new Bundle;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int temp = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        setContentView(R.layout.activity_event);


        if(GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext()) == 1)
        {
            MapsInitializer.initialize(getApplicationContext());
        }


        initializeGlobals();

        initializeListeners();
    }


    public ArrayList<String> getPlacesCloud() {
        return placesCloud;
    }

    public void setPlacesCloud(ArrayList<String> placesCloud) {
        this.placesCloud = placesCloud;
    }

    private void initializeGlobals()
    {
        FacebookSdk.sdkInitialize(this);
        frag = new ResultGraphFragment();
        frag2 = new ResultGraphFragment2();
        PersonalityPieFragment ppFrag = new PersonalityPieFragment();

        logoutIntent = new Intent(this, FacebookLogin.class);
        lobbyIntent = new Intent(this, LobbyActivity.class);
        profileIntent = new Intent(this, MyProfile.class);

        enterEvent = getIntent();
        eTopic = enterEvent.getStringExtra(EventCreationActivity.EVENT_TOPIC);
        eHost = enterEvent.getStringExtra(EventCreationActivity.EVENT_HOST_NAME);
        eInvites = enterEvent.getStringExtra(EventCreationActivity.EVENT_INVITES);
        eCategory = enterEvent.getStringExtra(EventCreationActivity.EVENT_CATEGORY);
        eID = enterEvent.getStringExtra(EventCreationActivity.EVENT_ID);
        poolID = enterEvent.getStringExtra(FacebookLogin.POOL_ID);
        uID = enterEvent.getStringExtra(FacebookLogin.USER_ID);
        uName = enterEvent.getStringExtra(FacebookLogin.USER_F_NAME);

        credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),    /* get the context for the application */
                poolID, // Identity Pool ID
                Regions.US_EAST_1           /* Region for your identity pool--US_EAST_1 or EU_WEST_1*/
        );

        this.setTitle(eTopic);

        lobbyIntent.putExtra(FacebookLogin.USER_ID, uID);
        lobbyIntent.putExtra(FacebookLogin.POOL_ID, poolID);
        lobbyIntent.putExtra(FacebookLogin.USER_F_NAME, uName);
        profileIntent.putExtra(FacebookLogin.USER_ID, uID);
        profileIntent.putExtra(FacebookLogin.POOL_ID, poolID);
        profileIntent.putExtra(FacebookLogin.USER_F_NAME, uName);

        intentPairs.put(FacebookLogin.POOL_ID, poolID);
        intentPairs.put(FacebookLogin.USER_ID, uID);
        intentPairs.put(FacebookLogin.USER_F_NAME, uName);

        new getCurrUser().execute(uID);
        new getAllFriends().execute(eID);

        invitedList = (ListView) findViewById(R.id.invitedList);
        rsvp = (Button) findViewById(R.id.rsvpButton);
        share = (Button) findViewById(R.id.shareButton);
        joinEvent = (Button) findViewById(R.id.joinEvent);
        clearFragment = (ImageButton) findViewById(R.id.clear_Fragment);
        fragContainer = (RelativeLayout) findViewById(R.id.fragment_Conatiner);
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

        //Ron Test
        clearFragment2 = (ImageButton) findViewById(R.id.clear_Fragment2);
        clearFragment3 = (ImageButton) findViewById(R.id.clear_personality_Fragment);
        fragContainer2 = (RelativeLayout) findViewById(R.id.fragment_Conatiner2);
        ppFragContainer = (RelativeLayout) findViewById(R.id.fragment_Conatiner3);

        clearFragment.setImageResource(R.mipmap.clear_icon);
        clearFragment2.setImageResource(R.mipmap.clear_icon);
        clearFragment3.setImageResource(R.mipmap.clear_icon);

        Bundle fragArgs = new Bundle();
        fragArgs.putString(WORD_CLOUD_DATA, strForCloud);
        fragArgs.putString(TOP_VENUE_DATA, topVenues);
        ResultGraphFragment fragInfo = ResultGraphFragment.newInstance(fragArgs);
        getSupportFragmentManager().beginTransaction().replace(R.id.resultGraphFragmentContainer, fragInfo).commit();

        //MORE RON
        Bundle fragArgs2 = new Bundle();
        fragArgs2.putString(WORD_CLOUD_DATA, strForCloud);
        fragArgs2.putString(TOP_VENUE_DATA, topVenues);
        ResultGraphFragment2 fragInfo2 = ResultGraphFragment2.newInstance(fragArgs2);
        getSupportFragmentManager().beginTransaction().replace(R.id.resultGraphFragmentContainer2, fragInfo2).commit();


        ppFragContainer.setVisibility(View.GONE);
        fragContainer2.setVisibility(View.GONE);
        fragContainer.setVisibility(View.GONE);

        shareDialog = new ShareDialog(this);

        updateRefresh.execute();
    }

    public void enableDisableView(View view, boolean enabled) {
        view.setEnabled(enabled);
        if ( view instanceof ViewGroup ) {
            ViewGroup group = (ViewGroup)view;

            for ( int idx = 0 ; idx < group.getChildCount() ; idx++ ) {
                enableDisableView(group.getChildAt(idx), enabled);
            }
        }
    }

    private void initializeListeners() {

        rsvp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(getApplicationContext(), "You have RSVP'ed!", Toast.LENGTH_SHORT);
                toast.show();
                rsvp.setVisibility(View.GONE);
                new updateEvent().execute(eID);
                new updateRsvp().execute(eID);
            }
        });

        clearFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle fragArgs = new Bundle();
                fragArgs.putString(WORD_CLOUD_DATA, strForCloud);
                fragArgs.putString(TOP_VENUE_DATA, topVenues);
                ResultGraphFragment fragInfo = ResultGraphFragment.newInstance(fragArgs);
                getSupportFragmentManager().beginTransaction().replace(R.id.resultGraphFragmentContainer, fragInfo).commit();
                fragContainer.setVisibility(View.GONE);
                enableDisableView(findViewById(R.id.event_main_container), true);
            }
        });
        clearFragment2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle fragArgs2 = new Bundle();
                fragArgs2.putString(WORD_CLOUD_DATA, strForCloud);
                fragArgs2.putString(TOP_VENUE_DATA, topVenues);
                ResultGraphFragment2 fragInfo2 = ResultGraphFragment2.newInstance(fragArgs2);
                getSupportFragmentManager().beginTransaction().replace(R.id.resultGraphFragmentContainer2, fragInfo2).commit();
                fragContainer2.setVisibility(View.GONE);
                enableDisableView(findViewById(R.id.event_main_container), true);
            }
        });
        clearFragment3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle ppFragArgs = new Bundle();
                ppFragArgs.putString(PERSONALITY_DATA, allCommonTags);
                PersonalityPieFragment fragInfo3 = PersonalityPieFragment.newInstance(ppFragArgs);
                getSupportFragmentManager().beginTransaction().replace(R.id.personality_frag_container, fragInfo3).commit();
                ppFragContainer.setVisibility(View.GONE);
                enableDisableView(findViewById(R.id.event_main_container), true);
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String description = "";
                String end = finalLoc.latitude+","+finalLoc.longitude;
                String uri = "https://www.google.com/maps/dir//"+end+"/";
                ShareLinkContent linkContent = new ShareLinkContent.Builder()
                        .setContentTitle("Lets go to..." + venue)
                        .setContentDescription(description)
                        .setContentUrl(Uri.parse(uri))
                        .build();

                shareDialog.show(linkContent);
            }
        });

        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                String start = currUser.getLatitude() + "," + currUser.getLongitude();
                String end = finalLoc.latitude + "," + finalLoc.longitude;
                String uri = "https://www.google.com/maps/dir/" + start + "/" + end + "/";
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));

                if (marker.getSnippet() != null && marker.getSnippet().contentEquals("Tap for directions!")) {
                    updateRefresh.cancel(true);
                    startActivity(browserIntent);
                }
            }
        });

        joinEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Event temp = currEvent;
                temp.setAttendees(temp.getAttendees() + "," + uID);

                joinEvent.setVisibility(View.GONE);
                new addUserToEvent().execute(temp);

            }
        });
    }

    private boolean checkExists(String item, ArrayList<User> list)
    {
        boolean found = false;
        int k;
        for(k = 0; k < list.size(); k++)
        {
            if(list.get(k).getUserID().contentEquals(item))
            {
                found = true;
            }
        }
        return found;
    }

    private void generateFriendMap(ArrayList<User> allUsers, GoogleMap mp)
    {
        int k;
        ArrayList<Location> locs = new ArrayList<Location>();
        ArrayList<Marker> markers = new ArrayList<Marker>();
        Location temp;

        for(k = 0; k < allUsers.size(); k++)
        {
            temp = new Location("");
            User user = allUsers.get(k);

            if(user.getLatitude() == 0 || user.getLongitude() == 0)
            {
                user.setLatitude(33.784091);
                user.setLongitude( -118.114090);
            }

            temp.setLatitude(user.getLatitude());
            temp.setLongitude(user.getLongitude());
            locs.add(temp);

            LatLng loc = new LatLng(user.getLatitude(),user.getLongitude());
            if(user.getUserID().contentEquals(uID))
            {
                map.addMarker(new MarkerOptions()
                        .position(loc)
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.host_icon))
                        .title(user.getfName() + " " + user.getlName()));
            }
            else
            {
                map.addMarker(new MarkerOptions()
                        .position(loc)
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.person_icon))
                        .title(user.getfName() + " " + user.getlName()));
            }
        }

        mid = getMidLocation(locs);

        map.addMarker(new MarkerOptions()
                .position(new LatLng(mid.getLatitude(),mid.getLongitude()))
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.final_loc_icon))
                .title("midpoint"));

        currEvent.setLatitude(mid.getLatitude());
        currEvent.setLongitude(mid.getLongitude());

        new getFinalLocation(map).execute();

        CameraUpdate center= CameraUpdateFactory.newLatLng(new LatLng(mid.getLatitude(), mid.getLongitude()));
        CameraUpdate zoom= CameraUpdateFactory.zoomTo(10);

        map.moveCamera(center);
        map.animateCamera(zoom);
    }
    public Address getAddress(Location location)
    {
        Address address = null;
        double lat = location.getLatitude();
        double longt = location.getLongitude();

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try
        {
            List<Address> listAddresses = geocoder.getFromLocation(lat, longt, 1);
            if(listAddresses.size() > 0)
            {
                address = listAddresses.get(0);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return address;
    }

    public Location getMidLocation(ArrayList<Location> locations)
    {
        Location midLocation = new Location("");

        double sumX = 0;
        double sumY = 0;

        int k = 0;
        int locCount = locations.size();

        for(k = 0; k < locCount; k++)
        {
            sumX += locations.get(k).getLatitude();
            sumY += locations.get(k).getLongitude();
        }

        midLocation.setLatitude( sumX / locCount);
        midLocation.setLongitude(sumY / locCount);

        if(midLocation.getLatitude() == 0 || midLocation.getLongitude() == 0)
        {
            midLocation.setLatitude(33.760605);
            midLocation.setLongitude(-118.156446);
        }

        return midLocation;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        generateFriendMap(allUsers,googleMap);
    }

    public void setUID(String UID) {
        this.uID = UID;
    }

    public void setCurrEvent(Event currEvent) {
        this.currEvent = currEvent;
    }

    public Button getJoinButton() {
        return joinEvent;
    }

    public String getAllTagData(uProfile prof)
    {
        String temp = "";
        if(prof != null) {

            temp += prof.getLikeTags();
            temp += prof.getMovieLikeTags();
            temp += prof.getTextTags();
            temp += prof.getPlacesTags();
            //temp += prof.getImageTags();
        }
        return temp;
    }

    private class FriendAdapter extends ArrayAdapter<User>
    {
        private ArrayList<User> friends;

        public FriendAdapter(Context context, int profilePictureResourceID, ArrayList<User> friendList)
        {
            super(context, profilePictureResourceID, friendList);
            this.friends = new ArrayList<User>();
            this.friends.addAll(friendList);
        }

        private class ViewHolder
        {
            RelativeLayout friendContainer;
            ImageView rsvpStatus;
            ImageView profilePic;
            TextView name;
            ImageButton interestChart;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.list_item_user_display, null);

                holder = new ViewHolder();
                holder.friendContainer = (RelativeLayout) convertView.findViewById(R.id.invFriendContainer);
                holder.rsvpStatus = (ImageView) convertView.findViewById(R.id.rsvpStatus);
                holder.profilePic = (ImageView) convertView.findViewById(R.id.invUserProfilePicture);
                holder.name = (TextView) convertView.findViewById(R.id.invUserName);
                holder.interestChart = (ImageButton) convertView.findViewById(R.id.interestChart);

                convertView.setTag(holder);
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }

            User user = friends.get(position);

            holder.interestChart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    User friend = friends.get(position);
                    uProfile friendProf = new uProfile();
                    int k;
                    for(k = 0; k < allProfiles.size(); k++)
                    {

                        if(friend.getUserID().contentEquals(allProfiles.get(k).getUserID()) && allProfiles.get(k) != null)
                        {
                            friendProf = allProfiles.get(k);

                            break;
                        }
                    }
                    String currentProfData = getAllTagData(currProfile);
                    //allTagsStrForMe += currentProfData;
                    String friendProfData = getAllTagData(friendProf);
                    //allTagsStrForFriend += friendProfData;
                    //Alternate method to only pass 1 string to fragment below
                    List<String> myTagList = new ArrayList<String> (Arrays.asList(currentProfData.split(",")));
                    List<String> friendTagList = new ArrayList<String> (Arrays.asList(friendProfData.split(",")));
                    HashMap<String, Integer> myTagMap = new HashMap<String, Integer>();
                    for (String s : myTagList)
                    {
                        myTagMap.put(s, 0);
                    }
                    // then we can iterate over the map entries, count word frequency and put it as entry value
                    for (Map.Entry<String, Integer> me : myTagMap.entrySet())
                    {
                        int f = Collections.frequency(friendTagList, me.getKey());
                        me.setValue(f);
                    }
                    Iterator mapIter = myTagMap.entrySet().iterator();
                    while(mapIter.hasNext())
                    {
                        Map.Entry<String, Integer> me = (Map.Entry<String, Integer>)mapIter.next();
                        String word = me.getKey();
                        int freq = me.getValue();
                        allCommonTags += word.replace(","," ") + "," + freq + "|";
                    }

                    //Toast.makeText(getApplicationContext(), "you clicked on the interest chart for " + uName + " vs " + friends.get(position).getfName(), Toast.LENGTH_SHORT).show();
                    //Toast.makeText(getApplicationContext(), uName, Toast.LENGTH_SHORT).show();
                    //Toast.makeText(getApplicationContext(), currentProfData, Toast.LENGTH_LONG).show();
                    //Toast.makeText(getApplicationContext(), friend.getfName(), Toast.LENGTH_SHORT).show();
                   // Toast.makeText(getApplicationContext(), friendProfData, Toast.LENGTH_LONG).show();
                    //Toast.makeText(getApplicationContext(), allTagsStrForCloud, Toast.LENGTH_LONG).show();
                    Bundle fragArgs = new Bundle();
                    fragArgs.putString(PERSONALITY_DATA, allCommonTags);
                    PersonalityPieFragment fragInfo = PersonalityPieFragment.newInstance(fragArgs);
                    getSupportFragmentManager().beginTransaction().replace(R.id.personality_frag_container, fragInfo).commit();
                    ppFragContainer.setVisibility(View.VISIBLE);
                }
            });

            if(user.getProfilePic() == null) {
                holder.profilePic.setImageResource(R.mipmap.ic_launcher);
            }
            else
            {
                new DownloadImageTask(holder.profilePic).execute(user.getProfilePic());
            }


            int k;
            int m;
            boolean assignedImage = false;

            if(user.getUserID().contentEquals(uID))
            {
                holder.rsvpStatus.setImageResource(R.mipmap.host_icon);
                holder.interestChart.setVisibility(View.GONE);
                assignedImage = true;
            }
            else
            {
                holder.interestChart.setVisibility(View.VISIBLE);
            }

            for(k = 0; k < rsvped.size(); k++)
            {
                if(user.getUserID().contentEquals(rsvped.get(k)) && !assignedImage)
                {
                    holder.rsvpStatus.setImageResource(R.mipmap.rsvp_icon);
                    assignedImage = true;
                    break;
                }
            }
            if(!assignedImage) {
                for (m = 0; m < invited.size(); m++) {
                    if (user.getUserID().contentEquals(invited.get(m))) {
                        holder.rsvpStatus.setImageResource(R.mipmap.unread_icon);
                        assignedImage = true;
                        break;
                    }
                }
            }
            if(!assignedImage)
            {
                holder.rsvpStatus.setImageResource(R.mipmap.star_icon);
            }

            holder.name.setText(user.getfName() + " " + user.getlName());

            return convertView;
        }
    }

    class updateEvent extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
            DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);

            Event event = mapper.load(Event.class, params[0]);
            String currID = uID;

            String rsvps = event.getRsvpList();
            String rsvpList[];
            if(rsvps != null) {
                rsvpList = rsvps.split(",");
                int k;

                for (k = 0; k < rsvpList.length; k++) {
                    if (!rsvpList[k].contentEquals(currID)) {
                        rsvps += "," + currID;
                        break;
                    }
                }
            }
            else
            {
                rsvps = currID;
            }

            event.setRsvpList(rsvps);

            mapper.save(event);
            return null;
        }
    }

    class updateRsvp extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
            DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);
            Event event = mapper.load(Event.class, params[0]);
            String eHostID = event.getHostID();
            User user = mapper.load(User.class, eHostID);
            int rsvpCount = user.getRsvpCount();
            int newRsvp = rsvpCount + 1;
            user.setRsvpCount(newRsvp);
            mapper.save(user);
            return null;
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
            userPics.add(result);
        }
    }

    class getCurrUser extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
            DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);

            currUser = mapper.load(User.class, params[0]);

            if(currUser == null)
            {
                currUser = new User();
                currUser.setLatitude(33.760605);
                currUser.setLongitude(-118.156446);
            }

            return null;
        }
    }

    class populatePlaces extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
            DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);

            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
            PaginatedScanList<uProfile> profileResult = mapper.scan(uProfile.class, scanExpression);

            int k;
            for (k = 0; k < profileResult.size(); k++)
            {
                uProfile item = profileResult.get(k);
                for(int i = 0; i < allUsers.size(); i++)
                {
                    if(item.getUserID().contentEquals(uID) && currProfile == null)
                    {
                        currProfile = item;
                    }
                    allProfiles.add(item);
                    if(item.getPlacesTags() != null) {
                        User usr = allUsers.get(i);
                        if (item.getUserID().contentEquals(usr.getUserID())) {
                            Collections.addAll(placesCloud, item.getPlacesTags().split(","));
                            strForCloud += item.getPlacesTags();
                        }
                    }
                }
            }

            return strForCloud;
        }


        @Override
        protected void onPostExecute(String val)
        {
            Bundle fragArgs = new Bundle();
            fragArgs.putString(WORD_CLOUD_DATA,val);
            globalCloud = val;
            ResultGraphFragment fragInfo = ResultGraphFragment.newInstance(fragArgs);
            getSupportFragmentManager().beginTransaction().replace(R.id.resultGraphFragmentContainer, fragInfo).commit();
            //Ron Test Below for Places Pie Chart
            Bundle fragArgs2 = new Bundle();
            fragArgs2.putString(WORD_CLOUD_DATA, val);
            ResultGraphFragment2 fragInfo2 = ResultGraphFragment2.newInstance(fragArgs2);
            getSupportFragmentManager().beginTransaction().replace(R.id.resultGraphFragmentContainer2, fragInfo2).commit();
        }
    }

    class getAllFriends extends AsyncTask<String, Void, ArrayList<User>> {
        @Override
        protected ArrayList<User> doInBackground(String... params) {
            AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
            DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);

            boolean peopleInvited = false;

            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
            PaginatedScanList<User> userResult = mapper.scan(User.class, scanExpression);
            Event eventResult = mapper.load(Event.class, params[0]);
            currEvent = eventResult;
            String invitedArray[] = new String[1];
            if(eventResult.getAttendees() != null)
            {
                peopleInvited = true;
                invitedArray= eventResult.getAttendees().split(",");
            }

            String rsvpList = eventResult.getRsvpList();

            if(currUser.getUserID().contentEquals(eventResult.getHostID()))
            {
                isHost = true;
            }
            int k;
            for (k = 0; k < userResult.size(); k++)
            {
                User item = userResult.get(k);
                if(item.getUserID().contentEquals(eventResult.getHostID()))
                {
                    allUsers.add(item);
                }
                String itemID = item.getUserID();

                if(peopleInvited) {
                    for (int i = 0; i < invitedArray.length; i++) {
                        if (invitedArray[i].replaceAll("\\s+$", "").contentEquals(itemID)) {
                            if (rsvpList != null && rsvpList.contains(item.getUserID())) {
                                rsvped.add(item.getUserID());
                                //item.setlName(item.getlName() + "R");
                            } else {
                                invited.add(item.getUserID());
                                //item.setlName(item.getlName() + "?");
                            }
                            allUsers.add(item);

                            continue;
                        }
                    }
                }
            }
            return allUsers;
        }

        @Override
        protected void onPostExecute(ArrayList<User> res)
        {
            if(isHost)
            {
                rsvp.setVisibility(View.GONE);
                Toast toast = Toast.makeText(getApplicationContext(), "You are the host!", Toast.LENGTH_SHORT);
                toast.show();
            }

            friendAdapter = new FriendAdapter(getApplicationContext(), R.layout.list_item_user_display,res);
            boolean partOfEvent = false;
            for(int k = 0; k < allUsers.size(); k++)
            {
                if(allUsers.get(k).getUserID().contentEquals(uID))
                {
                    partOfEvent = true;
                    break;
                }
            }
            if(!partOfEvent)
            {
                joinEvent.setVisibility(View.VISIBLE);
            }
            if(!isHost) {
                for (int k = 0; k < rsvped.size(); k++) {
                    if (rsvped.get(k).contentEquals(uID)) {
                        rsvp.setVisibility(View.GONE);
                        Toast toast = Toast.makeText(getApplicationContext(), "You have already RSVP'ed!", Toast.LENGTH_SHORT);
                        toast.show();
                        break;
                    }
                }
            }

            invitedList = (ListView) findViewById(R.id.invitedList);
            invitedList.setAdapter(friendAdapter);
            new populatePlaces().execute();
            //new populatePersonality().execute();
            generateFriendMap(res, map);
        }
    }

    class getFinalLocation extends AsyncTask<Void, Void, ArrayList<JSONObject>> {
        private GoogleMap map;
        ArrayList<JSONObject> places;

        getFinalLocation(GoogleMap gMap)
        {
            map = gMap;
        }

        @Override
        protected ArrayList<JSONObject> doInBackground(Void... params) {
            String type = currEvent.getCategory();
            String query = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
            query += "keyword=" + currEvent.getTopic().replace(' ','+');
            query += "&location="+currEvent.getLatitude() + "," + currEvent.getLongitude();
            query += "&rankby=distance";
            query += "&key="+getString(R.string.places_api_key);
            places = getJSON(query);

            return places;
        }

        @Override
        protected void onPostExecute(ArrayList<JSONObject> places)
        {
            ArrayList<String> resultingPlaces = new ArrayList<String>();
            int k;
            int i;
            if(places.size() > 0) {
                finalLoc = new LatLng(mid.getLatitude(), mid.getLongitude());
                venue = "Could not find " + eTopic;
                JSONObject firstResult = places.get(0);
                try {
                    JSONArray finalResultList = firstResult.getJSONArray("results");
                    JSONObject firstRes = finalResultList.getJSONObject(0);
                    venue = firstRes.getString("name");
                    JSONObject location = firstRes.getJSONObject("geometry").getJSONObject("location");
                    String lat = location.getString("lat");
                    String lng = location.getString("lng");
                    finalLoc = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));

                    for(k = 0; k < finalResultList.length(); k++)
                    {

                        JSONObject Res = finalResultList.getJSONObject(k);
                        String bName = Res.getString("name");
                        resultingPlaces.add(bName);
                    }
                    String finalDec = makeFinalDecision(resultingPlaces);
                    for(k = 0; k < finalResultList.length(); k++) {

                        JSONObject Res = finalResultList.getJSONObject(k);
                        if(Res.getString("name").contentEquals(finalDec))
                        {
                            venue = finalDec;
                            location = Res.getJSONObject("geometry").getJSONObject("location");
                            lat = location.getString("lat");
                            lng = location.getString("lng");
                            finalLoc = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                makeFinalDecision(resultingPlaces);
                if(finalLoc.latitude == 0 || finalLoc.longitude == 0)
                {
                    finalLoc = new LatLng(33.78705292, -118.1564652);
                    venue = "Result not found";
                }
                MarkerOptions finalMark = new MarkerOptions()
                        .position(finalLoc)
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.final_resolved_loc_icon))
                        .snippet("Tap for directions!")
                        .title(venue);
                map.addMarker(finalMark).showInfoWindow();

                CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(finalMark.getPosition().latitude, finalMark.getPosition().longitude));
                map.moveCamera(center);
            }
            CameraUpdate zoom= CameraUpdateFactory.zoomTo(10);
            map.animateCamera(zoom);
        }
    }


    public String makeFinalDecision(ArrayList<String> choices)
    {
        WordCloudGenerator gen = new WordCloudGenerator(strForCloud);
        String cleaned = gen.removeStopWords(strForCloud);
        String trimmed[] = gen.splitAndTrimText();
        ArrayList<String> cloudItems = new ArrayList<String>();
        cloudItems.addAll(Arrays.asList(trimmed));

        Decisionate terminator = new Decisionate(cloudItems,choices);
        Map<String,Integer> sortedPlaces = terminator.accumulatePoints();

        String decisionatedChoice = sortedPlaces.entrySet().iterator().next().getKey();
        int decisionatedWeight = sortedPlaces.entrySet().iterator().next().getValue();

        if(decisionatedWeight >= 1)
        {
            Iterator mapIter = sortedPlaces.entrySet().iterator();
            int k = 0;
            while(mapIter.hasNext() && k < 5)
            {
                Map.Entry<String, Integer> me = (Map.Entry<String,Integer>)mapIter.next();
                String place = me.getKey();
                int weight = me.getValue();
                topVenues += place.replace(","," ") + "," + weight + "|";
                k++;
            }

            return decisionatedChoice;
        }
        String choice = "Could not find a location!";

        int k;
        int m;
        TreeMap<String,Integer> weight = new TreeMap<String, Integer>();

        for(m = 0; m < choices.size(); m++)
        {
            weight.put(choices.get(m),0);
        }

        for(k = 0; k < placesCloud.size(); k++)
        {
            for(m = 0; m < choices.size(); m++)
            {
                String place = choices.get(m);
                int freq = weight.get(place);

                if(place.contains(placesCloud.get(k)))
                {
                    freq += 1;
                    weight.put(place,freq);
                }
            }
        }

        int biggestWeight = 0;
        for(m = 0; m < choices.size(); m++)
        {
            int value = weight.get(choices.get(m));
            if(value > biggestWeight)
            {
                choice = choices.get(m);
                biggestWeight = value;
            }
        }

        return choice;
    }

    public ArrayList<JSONObject> getJSON(String inUrl) {
        HttpURLConnection urlConnection = null;
        URL url = null;
        JSONObject object = null;
        ArrayList<JSONObject> objs = new ArrayList<JSONObject>();
        InputStream inStream = null;
        try {
            url = new URL(inUrl.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.connect();
            inStream = urlConnection.getInputStream();
            BufferedReader bReader = new BufferedReader(new InputStreamReader(inStream));
            String temp, response = "";
            while ((temp = bReader.readLine()) != null) {
                response += temp;
            }
            object = (JSONObject) new JSONTokener(response).nextValue();
            objs.add(object);
        } catch (Exception e) {

        } finally {
            if (inStream != null) {
                try {
                    // this will close the bReader as well
                    inStream.close();
                } catch (IOException ignored) {
                }
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return objs;
    }

    class addUserToEvent extends AsyncTask<Event,Void,Void>
    {

        @Override
        protected Void doInBackground(Event... params) {
            AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
            DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);

            Event dbEvent = mapper.load(Event.class,params[0].getEventID());

            dbEvent.setAttendees(params[0].getAttendees());

            mapper.save(dbEvent);

            return null;
        }

        @Override
        protected void onPostExecute(Void var)
        {
            allUsers.clear();
            new getAllFriends().execute(eID);
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
}