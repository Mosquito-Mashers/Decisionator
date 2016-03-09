package com.csulb.decisionator.decisionator;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.facebook.login.LoginManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class LocationActivity extends AppCompatActivity implements LocationListener {

    protected LocationManager locationManager;
    protected LoginManager loginManager;
    protected LocationListener locationListener;
    protected Context context;
    protected String latitude, longitude;
    protected boolean gps_enabled, network_enabled;


    private String uID;
    private String poolID;
    private String eventID;
    private String uFName;

    private CognitoCachingCredentialsProvider credentialsProvider;

    private TextView currentCoords;
    private TextView relativeAddress;
    private TextView midLoc;
    private TextView midAdr;
    private Button returnHomebtn;
    private Button decisionate;
    private ProgressBar coordProg;
    private ProgressBar addrProg;


    private SharedPreferences prefs;
    private Event evnt;

    private Intent eventInitiated;
    private Intent logoutIntent;
    private Intent returnHome;
    private static final Map<String, String> intentPairs = new HashMap<String, String>();

    private Location userLoc;
    /////////////////////////////////////////////
    //Debug//////////////////////////////////////
    private Location loc1;
    private Location loc2;
    private Location loc3;
    private Location loc4;
    private Location midLocation = new Location("");
    private Address midAddr;
    private ArrayList<Location> debugLocations = new ArrayList<Location>();
    /////////////////////////////////////////////


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
        setContentView(R.layout.activity_location);

        initializeGlobals();

        initializeListeners();

        setInitialLoc();

        prepareIntent(returnHome, intentPairs);
    }

    private void setInitialLoc() {
        Address relativeAddr = getAddress(userLoc);

        currentCoords.setText("Latitude:" + userLoc.getLatitude() + ", Longitude:" + userLoc.getLongitude());
        relativeAddress.setText(relativeAddr.getAddressLine(0));
    }

    private void prepareIntent(Intent returnHome, Map<String, String> intentPairs) {
        Iterator mapIter = intentPairs.entrySet().iterator();

        while (mapIter.hasNext())
        {
            Map.Entry kvPair = (Map.Entry) mapIter.next();
            returnHome.putExtra(kvPair.getKey().toString(), kvPair.getValue().toString());
        }
    }

    private void initializeListeners() {
        //userLoc = CSULB Coordinates
        userLoc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if(userLoc == null)
        {
            userLoc.setLatitude(33.760605);
            userLoc.setLongitude(-118.156446);
        }
        //Timeout
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 300, this);

        //If onLocationChanged not fired in 10s
        this.onLocationChanged(userLoc);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        //userLoc = CSULB Coordinates
        userLoc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if(userLoc == null)
        {
            userLoc = new Location("No providers");
            userLoc.setLatitude(33.760605);
            userLoc.setLongitude(-118.156446);
        }
        User lastKnown = new User();
        lastKnown.setUserID(uID);
        lastKnown.setLatitude(userLoc.getLatitude());
        lastKnown.setLongitude(userLoc.getLongitude());
        new updateUserLoc().execute(lastKnown);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 300, this);


        returnHomebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(returnHome);
            }
        });

        decisionate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Location finalLocation = new Location("");
                Address finalAddr;

                String topic = eventInitiated.getStringExtra(EventCreationActivity.EVENT_TOPIC);

                // Search for restaurants nearby
                Uri gmmIntentUri = Uri.parse("geo:" + midLocation.getLatitude() + "," + midLocation.getLongitude() + "?q=" + topic+"&num=1");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);

                finalAddr = getAddress(finalLocation);
            }
        });
    }

    private void initializeGlobals()
    {
        currentCoords = (TextView) findViewById(R.id.currentLocation);
        relativeAddress = (TextView) findViewById(R.id.relativeAddress);
        midLoc = (TextView) findViewById(R.id.midLocation);
        midAdr = (TextView) findViewById(R.id.midAddr);
        returnHomebtn = (Button) findViewById(R.id.returnHome);
        decisionate = (Button) findViewById(R.id.makeDecision);
        coordProg = (ProgressBar) findViewById(R.id.coordLoading);
        addrProg = (ProgressBar) findViewById(R.id.addrLoading);

        returnHome = new Intent(this, LobbyActivity.class);
        logoutIntent = new Intent(this, FacebookLogin.class);
        eventInitiated = getIntent();
        uID = eventInitiated.getStringExtra(FacebookLogin.USER_ID);
        poolID = eventInitiated.getStringExtra(FacebookLogin.POOL_ID);
        eventID = eventInitiated.getStringExtra(EventCreationActivity.EVENT_ID);
        uFName = eventInitiated.getStringExtra(FacebookLogin.USER_F_NAME);

        intentPairs.put(FacebookLogin.USER_ID, uID);
        intentPairs.put(FacebookLogin.POOL_ID, poolID);
        intentPairs.put(EventCreationActivity.EVENT_ID, eventID);
        intentPairs.put(FacebookLogin.USER_F_NAME, uFName);


        credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),    /* get the context for the application */
                poolID, // Identity Pool ID
                Regions.US_EAST_1           /* Region for your identity pool--US_EAST_1 or EU_WEST_1*/
        );

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        loginManager = LoginManager.getInstance();

        userLoc = new Location("No providers");


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }


        setDummyLocations();
    }

    @Override
    //If has not changed in 10 seconds
    public void onLocationChanged(Location location) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.removeUpdates(this);
        userLoc = location;

        coordProg.setVisibility(View.GONE);
        addrProg.setVisibility(View.GONE);

        currentCoords.setText("Latitude:" + location.getLatitude() + ", Longitude:" + location.getLongitude());

        Address relativeAddr = getAddress(location);

        User currUser = new User();

        currUser.setUserID(uID);
        currUser.setLatitude(location.getLatitude());
        currUser.setLongitude(location.getLongitude());

        evnt = new Event();
        evnt.setEventID(eventID);
        evnt.setLatitude(location.getLatitude());
        evnt.setLongitude(location.getLongitude());

        new updateUserLoc().execute(currUser);
        new updateEventLoc().execute(evnt);

        relativeAddress.setText(relativeAddr.getAddressLine(0));


        debugLocations.add(loc1);
        debugLocations.add(loc2);
        debugLocations.add(loc3);
        debugLocations.add(loc4);
        debugLocations.add(userLoc);

        midLocation = getMidLocation(debugLocations);
        midAddr = getAddress(midLocation);


        midLoc.setText("Latitude:" + midLocation.getLatitude() + ", Longitude:" + midLocation.getLongitude());
        midAdr.setText(getAddress(midLocation).getAddressLine(0));
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude", "disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude", "enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude","status");
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

        return midLocation;
    }

    private void setDummyLocations()
    {
        TextView friend1Lat = (TextView) findViewById(R.id.friend1Lat);
        TextView friend1Long = (TextView) findViewById(R.id.friend1Long);
        TextView friend1Addr = (TextView) findViewById(R.id.friend1Addr);

        TextView friend2Lat = (TextView) findViewById(R.id.friend2Lat);
        TextView friend2Long = (TextView) findViewById(R.id.friend2Long);
        TextView friend2Addr = (TextView) findViewById(R.id.friend2Addr);

        TextView friend3Lat = (TextView) findViewById(R.id.friend3Lat);
        TextView friend3Long = (TextView) findViewById(R.id.friend3Long);
        TextView friend3Addr = (TextView) findViewById(R.id.friend3Addr);

        TextView friend4Lat = (TextView) findViewById(R.id.friend4Lat);
        TextView friend4Long = (TextView) findViewById(R.id.friend4Long);
        TextView friend4Addr = (TextView) findViewById(R.id.friend4Addr);

        loc1 = new Location("Dummy");
        loc2 = new Location("Dummy");
        loc3 = new Location("Dummy");
        loc4 = new Location("Dummy");

        loc1.setLatitude(33.787154);
        loc1.setLongitude(-118.156446);

        friend1Lat.setText("" + loc1.getLatitude() + ", ");
        friend1Long.setText("" + loc1.getLongitude());
        //friend1Addr.setText(getAddress(loc1).getAddressLine(0));

        loc2.setLatitude(33.791149);
        loc2.setLongitude(-118.136737);

        friend2Lat.setText("" + loc2.getLatitude() + ", ");
        friend2Long.setText("" + loc2.getLongitude());
        //friend2Addr.setText(getAddress(loc2).getAddressLine(0));

        loc3.setLatitude(33.808249);
        loc3.setLongitude(-118.072546);

        friend3Lat.setText("" + loc3.getLatitude() + ", ");
        friend3Long.setText("" + loc3.getLongitude());
        //friend3Addr.setText(getAddress(loc3).getAddressLine(0));

        loc4.setLatitude(33.760605);
        loc4.setLongitude(-118.133185);

        friend4Lat.setText("" + loc4.getLatitude() + ", ");
        friend4Long.setText("" + loc4.getLongitude());
        //friend4Addr.setText(getAddress(loc4).getAddressLine(0));
    }

    private void setLocations()
    {
        try {
            ArrayList<User> peopleInvolved = new getPeopleInvolved().execute(evnt).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        TextView friend1Lat = (TextView) findViewById(R.id.friend1Lat);
        TextView friend1Long = (TextView) findViewById(R.id.friend1Long);
        TextView friend1Addr = (TextView) findViewById(R.id.friend1Addr);

        TextView friend2Lat = (TextView) findViewById(R.id.friend2Lat);
        TextView friend2Long = (TextView) findViewById(R.id.friend2Long);
        TextView friend2Addr = (TextView) findViewById(R.id.friend2Addr);

        TextView friend3Lat = (TextView) findViewById(R.id.friend3Lat);
        TextView friend3Long = (TextView) findViewById(R.id.friend3Long);
        TextView friend3Addr = (TextView) findViewById(R.id.friend3Addr);

        TextView friend4Lat = (TextView) findViewById(R.id.friend4Lat);
        TextView friend4Long = (TextView) findViewById(R.id.friend4Long);
        TextView friend4Addr = (TextView) findViewById(R.id.friend4Addr);

        loc1 = new Location("Dummy");
        loc2 = new Location("Dummy");
        loc3 = new Location("Dummy");
        loc4 = new Location("Dummy");

        loc1.setLatitude(33.787154);
        loc1.setLongitude(-118.156446);

        friend1Lat.setText("" + loc1.getLatitude() + ", ");
        friend1Long.setText("" + loc1.getLongitude());
        friend1Addr.setText(getAddress(loc1).getAddressLine(0));

        loc2.setLatitude(33.791149);
        loc2.setLongitude(-118.136737);

        friend2Lat.setText("" + loc2.getLatitude() + ", ");
        friend2Long.setText("" + loc2.getLongitude());
        friend2Addr.setText(getAddress(loc2).getAddressLine(0));

        loc3.setLatitude(33.808249);
        loc3.setLongitude(-118.072546);

        friend3Lat.setText("" + loc3.getLatitude() + ", ");
        friend3Long.setText("" + loc3.getLongitude());
        friend3Addr.setText(getAddress(loc3).getAddressLine(0));

        loc4.setLatitude(33.760605);
        loc4.setLongitude(-118.133185);

        friend4Lat.setText("" + loc4.getLatitude() + ", ");
        friend4Long.setText("" + loc4.getLongitude());
        friend4Addr.setText(getAddress(loc4).getAddressLine(0));
    }

    class updateUserLoc extends AsyncTask<User, Void, Void> {

        protected Void doInBackground(User... arg0) {
            AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
            DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);
            User temp = mapper.load(User.class, arg0[0].getUserID());

            temp.setLatitude(arg0[0].getLatitude());
            temp.setLongitude(arg0[0].getLongitude());

            mapper.save(temp);

            return null;
        }
    }

    class updateEventLoc extends AsyncTask<Event, Void, Void> {

        protected Void doInBackground(Event... arg0) {
            AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
            DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);
            Event temp = mapper.load(Event.class, arg0[0].getEventID());

            temp.setLatitude(arg0[0].getLatitude());
            temp.setLongitude(arg0[0].getLongitude());

            mapper.save(temp);

            return null;
        }
    }

    class getPeopleInvolved extends AsyncTask<Event, Void, ArrayList<User>> {
        @Override
        protected ArrayList<User> doInBackground(Event... params) {
            ArrayList<User> temp = new ArrayList<User>();
            AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
            DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);
            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
            Event result = mapper.load(Event.class, params[0].getEventID());


            List<String> peopleInvolved = Arrays.asList(result.getAttendees().split("\\s*,\\s*"));
            peopleInvolved.add(result.getHostID());

            int k;
            for (k = 0; k < peopleInvolved.size(); k++)
            {
                temp.add(mapper.load(User.class, peopleInvolved.get(k)));
            }

            return temp;
        }
    }
}
