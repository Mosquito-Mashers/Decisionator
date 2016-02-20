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
    ProgressBar coordProg;
    ProgressBar addrProg;

    private SharedPreferences prefs;

    Intent eventInitiated;
    Intent returnHome;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        currentCoords = (TextView) findViewById(R.id.currentLocation);
        relativeAddress = (TextView) findViewById(R.id.relativeAddress);
        returnHomebtn = (Button) findViewById(R.id.returnHome);
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

        returnHomebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginManager.logOut();
                prefs.edit().putBoolean("isLoggedIn",false);
                startActivity(returnHome);
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
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
}
