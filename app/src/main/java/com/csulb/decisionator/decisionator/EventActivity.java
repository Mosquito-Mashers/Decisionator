package com.csulb.decisionator.decisionator;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class EventActivity extends AppCompatActivity  implements OnMapReadyCallback {

    private FriendAdapter friendAdapter;

    private Intent enterEvent;
    private Intent logoutIntent;
    private Intent lobbyIntent;
    private Map<String, String> intentPairs = new HashMap<String, String>();

    private String eTopic;
    private String eHost;
    private String eInvites;
    private String eCategory;
    private String eID;
    private String poolID;
    private String uID;
    private String uName;
    private String venue;
    private String placesCloud;
    private Event currEvent;
    private ArrayList<User> allUsers = new ArrayList<User>();
    private ArrayList<uProfile> allProfiles = new ArrayList<uProfile>();
    private ArrayList<Bitmap> userPics = new ArrayList<Bitmap>();
    private CognitoCachingCredentialsProvider credentialsProvider;

    private ListView invitedList;
    private Button rsvp;
    private Button share;

    private Location mid;
    private LatLng finalLoc;

    private GoogleMap map;

    private ShareDialog shareDialog;
    private User currUser;
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
            case R.id.lobby:
                startActivity(lobbyIntent);
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
        setContentView(R.layout.activity_event);
        MapsInitializer.initialize(getApplicationContext());

        initializeGlobals();

        initializeListeners();


    }

    private void initializeGlobals()
    {
        logoutIntent = new Intent(this, FacebookLogin.class);
        lobbyIntent = new Intent(this, LobbyActivity.class);

        shareDialog = new ShareDialog(this);
        enterEvent = getIntent();
        eTopic = enterEvent.getStringExtra(EventCreationActivity.EVENT_TOPIC);
        eHost = enterEvent.getStringExtra(EventCreationActivity.EVENT_HOST_NAME);
        eInvites = enterEvent.getStringExtra(EventCreationActivity.EVENT_INVITES);
        eCategory = enterEvent.getStringExtra(EventCreationActivity.EVENT_CATEGORY);
        eID = enterEvent.getStringExtra(EventCreationActivity.EVENT_ID);
        poolID = enterEvent.getStringExtra(FacebookLogin.POOL_ID);
        uID = enterEvent.getStringExtra(FacebookLogin.USER_ID);
        uName = enterEvent.getStringExtra(FacebookLogin.USER_F_NAME);

        this.setTitle(eTopic);

        lobbyIntent.putExtra(FacebookLogin.USER_ID, uID);
        lobbyIntent.putExtra(FacebookLogin.POOL_ID, poolID);
        lobbyIntent.putExtra(FacebookLogin.USER_F_NAME, uName);

        intentPairs.put(FacebookLogin.POOL_ID, poolID);
        intentPairs.put(FacebookLogin.USER_ID, uID);
        intentPairs.put(FacebookLogin.USER_F_NAME, uName);

        credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),    /* get the context for the application */
                poolID, // Identity Pool ID
                Regions.US_EAST_1           /* Region for your identity pool--US_EAST_1 or EU_WEST_1*/
        );
        new getCurrUser().execute(uID);

        invitedList = (ListView) findViewById(R.id.invitedList);
        rsvp = (Button) findViewById(R.id.rsvpButton);
        share = (Button) findViewById(R.id.shareButton);

        new getAllFriends().execute(eID);

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
    }


    private void initializeListeners() {
        rsvp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new updateEvent().execute(eID);

            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String description = "";
                String start = currUser.getLatitude()+","+currUser.getLongitude();
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

    private void prepareIntent(Intent moveToLobby, Map<String, String> intentPairs) {
        Iterator mapIter = intentPairs.entrySet().iterator();

        while (mapIter.hasNext())
        {
            Map.Entry kvPair = (Map.Entry) mapIter.next();
            moveToLobby.putExtra(kvPair.getKey().toString(), kvPair.getValue().toString());
        }
    }

    private void generateFriendMap(ArrayList<User> allUsers, GoogleMap mp)
    {
        int k;
        int i;
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
                Marker mark = map.addMarker(new MarkerOptions()
                        .position(loc)
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.host_icon))
                        .title(user.getfName() + " " + user.getlName()));
            }
            else
            {
                Marker mark = map.addMarker(new MarkerOptions()
                        .position(loc)
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.person_icon))
                        .title(user.getfName() + " " + user.getlName()));
            }

        }

        mid = getMidLocation(locs);

        Marker mark = map.addMarker(new MarkerOptions()
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
            ImageView profilePic;
            TextView name;
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
                holder.profilePic = (ImageView) convertView.findViewById(R.id.invUserProfilePicture);
                holder.name = (TextView) convertView.findViewById(R.id.invUserName);

                convertView.setTag(holder);
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }

            User user = friends.get(position);

            if(user.getProfilePic() == null) {
                holder.profilePic.setImageResource(R.mipmap.ic_launcher);
            }
            else
            {
                new DownloadImageTask(holder.profilePic).execute(user.getProfilePic());
            }
            holder.name.setText(user.getfName() + " " + user.getlName());

            return convertView;
        }
    }

    class updateEvent extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            ArrayList<Event> temp = new ArrayList<Event>();
            AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
            DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);

            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
            Event event = mapper.load(Event.class, params[0]);
            String currName = currUser.getfName() + " " + currUser.getlName();

            String userViewed = event.getViewedList();
            String viewList[];
            if(userViewed != null){
                viewList = userViewed.split(", ");
                int r;

            }

            String rsvps = event.getRsvpList();
            String rsvpList[];
            if(rsvps != null) {
                rsvpList = rsvps.split(", ");
                int k;

                for (k = 0; k < rsvpList.length; k++) {
                    if (!rsvpList[k].contentEquals(currName)) {
                        rsvps += ", " + currName;
                        break;
                    }
                }
            }
            else
            {
                rsvps = currName;
            }

            event.setRsvpList(rsvps);

            mapper.save(event);
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
                currUser.setLatitude(33.760605);
                currUser.setLongitude(-118.156446);
            }

            return null;
        }
    }

    class getAllFriends extends AsyncTask<String, Void, ArrayList<User>> {
        @Override
        protected ArrayList<User> doInBackground(String... params) {
            AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
            DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);

            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
            PaginatedScanList<User> userResult = mapper.scan(User.class, scanExpression);
            Event eventResult = mapper.load(Event.class, params[0]);
            currEvent = eventResult;

            String invitedArray[] = eventResult.getAttendees().split(", ");
            String rsvpList = eventResult.getRsvpList();

            int k;
            for (k = 0; k < userResult.size(); k++)
            {
                User item = userResult.get(k);
                if(item.getUserID().contentEquals(eventResult.getHostID()))
                {
                    allUsers.add(item);
                }
                String name = item.getfName() + " " + item.getlName();

                for(int i = 0; i < invitedArray.length; i++)
                {
                    if (invitedArray[i].replaceAll("\\s+$", "").contentEquals(name))
                    {
                        if(rsvpList != null && rsvpList.contains(item.getfName() + " " + item.getlName()))
                        {
                            item.setlName(item.getlName() + " RSVP'ed!");
                        }
                        else
                        {
                            item.setlName(item.getlName() + "?");
                        }
                        allUsers.add(item);

                        continue;
                    }
                }
            }
            return allUsers;
        }

        @Override
        protected void onPostExecute(ArrayList<User> res)
        {
            new getAllProfiles().execute(res);
            friendAdapter = new FriendAdapter(getApplicationContext(), R.layout.list_item_user_display,res);

            invitedList = (ListView) findViewById(R.id.invitedList);
            invitedList.setAdapter(friendAdapter);
            generateFriendMap(res, map);
        }
    }

    class getAllProfiles extends AsyncTask<ArrayList<User>, Void, ArrayList<uProfile>> {
        @Override
        protected ArrayList<uProfile> doInBackground(ArrayList<User>... params) {
            AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
            DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);

            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
            PaginatedScanList<uProfile> profileResult = mapper.scan(uProfile.class, scanExpression);




            if(profileResult != null) {

                boolean existsInList;
                int k;
                for (k = 0; k < profileResult.size(); k++) {
                    uProfile item = profileResult.get(k);
                    existsInList = checkExists(item.getUserID(),params[0]);
                    if(item != null && existsInList) {
                        allProfiles.add(item);
                    }
                }
            }

            return allProfiles;
        }

        @Override
        protected void onPostExecute(ArrayList<uProfile> res)
        {
            if(res != null) {
                int i;
                for (i = 0; i < res.size(); i++) {
                    placesCloud += res.get(i).getPlacesTags() + ",";
                }
            }
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
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(finalLoc.latitude == 0 || finalLoc.longitude == 0)
                {
                    finalLoc = new LatLng(33.78705292, -118.1564652);
                    venue = "Result not found";
                }
                MarkerOptions finalMark = new MarkerOptions()
                        .position(finalLoc)
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.final_resolved_loc_icon))
                        .title(venue);
                map.addMarker(finalMark).showInfoWindow();

                CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(finalMark.getPosition().latitude, finalMark.getPosition().longitude));
                map.moveCamera(center);
            }
            CameraUpdate zoom= CameraUpdateFactory.zoomTo(10);
            map.animateCamera(zoom);
        }
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
}
