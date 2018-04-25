package com.example.vikkram.placessearch;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements LocationListener {

    private static final String TAG = "MainActivity";

    private SectionsPageAdapter mSectionsPageAdapter;

    private ViewPager mViewPager;

    private LocationManager locationManager;
    private String provider;
    private Location location;
    private static final int LOC_REQ_CODE = 1234;
    private double lat = 0.0;
    private double lon = 0.0;
    String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: Starting.");
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean enabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

// check if enabled and if not send user to the GSP settings
// Better solution would be to display a dialog and suggesting to
// go to the settings
        if (!enabled) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);
        //Criteria criteria = new Criteria();
        //provider = locationManager.getBestProvider(criteria, false);

        //getLocation();

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_search);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_placefav);
        /*
        if (location != null) {
            Log.d(TAG, "Provider has been set");
            onLocationChanged(location);
        } else {
            lat = 0.0;
            lon = 0.0;
            //locationManager.requestLocationUpdates(provider, 400, 1, this);

            Log.d(TAG, "No location updates");
        }
        */

    }


    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOC_REQ_CODE);
            return;
        } else {

            Location location = locationManager.getLastKnownLocation(provider);
        }
    }


    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new Tab1Fragment(), "Search");
        adapter.addFragment(new Tab2Fragment(), "Favorites");
        //adapter.addFragment(new Tab3Fragment(), "TAB3");
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(1);
    }

    @Override
    public void onLocationChanged(Location location1) {
        /*
        lat = location1.getLatitude();
        lon = location1.getLongitude();
        Log.d(TAG, "Latitude: "+lat);
        Log.d(TAG, "Longitude:  "+lon);
        locationManager.removeUpdates(this);
        */

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

        Log.d(TAG, "onStatusChanged: ");

        //Bundle args = new Bundle();
        //args.putString("latitude", String.valueOf(location.getLatitude()));
        //args.putString("longitude", String.valueOf(location.getLongitude()));





    }

    @Override
    protected void onPause() {
        super.onPause();
        /*
        locationManager.removeUpdates(this);
        */
    }

    @Override
    protected void onResume() {
        super.onResume();

        /*
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.d(TAG, "Permissions not available ");
            return;
        }
        else
        {
            //locationManager.requestLocationUpdates(provider, 400, 1, this);
            locationManager.requestLocationUpdates(provider, 400, 1, this);
            Log.d(TAG, "Request location updates");
        }
        */

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
