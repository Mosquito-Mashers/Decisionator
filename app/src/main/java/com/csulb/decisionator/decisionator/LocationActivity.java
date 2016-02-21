package com.csulb.decisionator.decisionator;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.camera2.params.Face;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.tv.TvContract;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.login.LoginManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LocationActivity extends AppCompatActivity implements LocationListener {

    protected LocationManager locationManager;
    protected LoginManager loginManager;
    protected LocationListener locationListener;
    protected Context context;
    protected String latitude, longitude;
    protected boolean gps_enabled, network_enabled;

    TextView currentCoords;
    TextView relativeAddress;
    Button returnHomebtn;
    Button decisionate;
    ProgressBar coordProg;
    ProgressBar addrProg;

    private SharedPreferences prefs;

    Intent eventInitiated;
    Intent returnHome;

    Location userLoc;
    /////////////////////////////////////////////
    //Debug//////////////////////////////////////
    Location loc1;
    Location loc2;
    Location loc3;
    Location loc4;
    /////////////////////////////////////////////


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        currentCoords = (TextView) findViewById(R.id.currentLocation);
        relativeAddress = (TextView) findViewById(R.id.relativeAddress);
        returnHomebtn = (Button) findViewById(R.id.returnHome);
        decisionate = (Button) findViewById(R.id.makeDecision);
        coordProg = (ProgressBar) findViewById(R.id.coordLoading);
        addrProg = (ProgressBar) findViewById(R.id.addrLoading);

        returnHome = new Intent(this, FacebookLogin.class);
        eventInitiated = getIntent();

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        loginManager = LoginManager.getInstance();

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
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        setDummyLocations();

        returnHomebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginManager.logOut();
                prefs.edit().putBoolean("isLoggedIn", false);
                startActivity(returnHome);
            }
        });

        decisionate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Location midLocation = new Location("");
                Address midAddr;

                Location finalLocation = new Location("");
                Address finalAddr;

                ArrayList<Location> debugLocations = new ArrayList<Location>();

                debugLocations.add(loc1);
                debugLocations.add(loc2);
                debugLocations.add(loc3);
                debugLocations.add(loc4);
                debugLocations.add(userLoc);

                midLocation = getMidLocation(debugLocations);
                midAddr = getAddress(midLocation);

                String topic = eventInitiated.getStringExtra(EventCreationActivity.EVENT_TOPIC);

                // Search for restaurants nearby
                Uri gmmIntentUri = Uri.parse("geo:"+midLocation.getLatitude()+","+midLocation.getLongitude()+"?q="+topic);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);


                finalAddr = getAddress(finalLocation);
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        userLoc = location;

        coordProg.setVisibility(View.GONE);
        addrProg.setVisibility(View.GONE);

        currentCoords.setText("Latitude:" + location.getLatitude() + ", Longitude:" + location.getLongitude());

        Address relativeAddr = getAddress(location);

        relativeAddress.setText(relativeAddr.getAddressLine(0));
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

    private Address getAddress(Location location)
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

    private Location getMidLocation(ArrayList<Location> locations)
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
}
