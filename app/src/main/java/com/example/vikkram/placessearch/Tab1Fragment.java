package com.example.vikkram.placessearch;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Executor;

public class Tab1Fragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String TAG = "Tab1Fragment";
    private FusedLocationProviderClient mFusedLocationClient;
    private Button btnTEST;
    private static final int LOC_REQ_CODE = 1;
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private GoogleApiClient mGoogleApiClient;
    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136));

    private static LocationManager locationManager;

    private AutoCompleteTextView destination;
    private static double lat = 0;
    private static double lon = 0;
    private static View view;
    private Boolean mLocationPermissionsGranted = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tab1_fragment,container,false);
        //btnTEST = (Button) view.findViewById(R.id.btnTEST);

        Button searchBtn = view.findViewById(R.id.btn_search);

        //searchBtn.setOnClickListener(this);
        final EditText keyword =  view.findViewById(R.id.et_keyword);
        final TextView error_keyword = (TextView)  view.findViewById(R.id.ifNullKeyword);
        Context c1 = getActivity().getApplicationContext();
        destination = (AutoCompleteTextView)  view.findViewById(R.id.atv_destination);
        locationManager = (LocationManager) c1.getSystemService(c1.LOCATION_SERVICE);

        if(mGoogleApiClient == null || !mGoogleApiClient.isConnected()){
            try {
                mGoogleApiClient = new GoogleApiClient
                        .Builder(getContext())
                        .addApi(Places.GEO_DATA_API)
                        .addApi(Places.PLACE_DETECTION_API)
                        .enableAutoManage(getActivity(), this)
                        .build();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }



        mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(getContext(), mGoogleApiClient, LAT_LNG_BOUNDS, null);

        destination.setAdapter(mPlaceAutocompleteAdapter);
        //getCurrentPlaceItems();
        //RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.location);
        RadioGroup radioGroup = (RadioGroup) view .findViewById(R.id.location);
        //mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());


        searchBtn.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                             Log.d(TAG, "Search results queried");

                                             if(validateForm())
                                             {
                                                 getJSONData();
                                             }
                                         }
                                     }


        );
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected

                switch(checkedId) {
                    case R.id.first:

                        Log.d(TAG, "radcheck1: ");
                        destination.setEnabled(false);
                        break;
                    case R.id.second:
                        // Fragment 2
                        Log.d(TAG, "radcheck2: ");
                        destination.setEnabled(true);
                        break;
                }
            }
        });

/*        btnTEST.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "TESTING BUTTON CLICK 1",Toast.LENGTH_SHORT).show();
            }
        });
*/
        return view;
    }


    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        /*
        try{
            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if(mLocationPermissionsGranted){

                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();

                        }else{
                            Log.d(TAG, "onComplete: current location is null");

                        }
                    }
                });
            }
        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
        }
        */
    }

    private void getLocationPermission(){
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
                //initMap();
            }else{
                ActivityCompat.requestPermissions(getActivity(),
                        permissions,
                        LOC_REQ_CODE);
            }
            getDeviceLocation();

        }else{
            ActivityCompat.requestPermissions(getActivity(),
                    permissions,
                    LOC_REQ_CODE);
        }
    }


    private void getCurrentPlaceItems() {

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED /*&& ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED*/)
        {

            //requestLocationAccessPermission();
            return;


        } else {
            Location location1 = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0, (LocationListener) location1);
            //Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            //Location locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Criteria criteria = new Criteria();

            String mprovider = locationManager.getBestProvider(criteria, false);
            //locationManager.requestLocationUpdates(mprovider, 15000, 1, (LocationListener) this);
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener((Executor) this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object
                                lat= location.getLatitude();
                                lon = location.getLongitude();
                            }
                        }
                    });


           // double lat = location1.getLatitude();
            //double lon = location1.getLongitude();
            Log.d(TAG,"Latitude: "+lat);
            Log.d(TAG, "Longitude: "+lon);

        }
        return;
    }

/*
    private void requestLocationAccessPermission() {
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOC_REQ_CODE);
    }
    */

    private void getJSONData() {
        EditText et_keyword = (EditText) view.findViewById(R.id.et_keyword);
        String keyword = et_keyword.getText().toString();
        Spinner sp_category = (Spinner) view.findViewById(R.id.spinnerCategory);
        EditText et_distance = (EditText) view.findViewById(R.id.et_distance);

        Integer distance;
        if(et_distance.getText().toString().trim()=="")
            distance= 10;
        else
            distance = Integer.parseInt(et_distance.getText().toString().trim());

        String category = sp_category.getSelectedItem().toString();
        Log.d(TAG, keyword);
        Log.d(TAG, destination.getText().toString());
        Log.d(TAG, category);
        Log.d(TAG, String.valueOf(distance));

        String url = "http://vasuki-travel-env.hhtzymbd2i.us-west-2.elasticbeanstalk.com/geocoding?keyword=USC&category=Default&distance=10&place=Boston";

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //progressDialog.dismiss();

                try {
                    Log.d(TAG, "Submitted results"+response);
                    JSONObject jsonObject = new JSONObject(response);
                    Toast.makeText(getContext(), "No Error", Toast.LENGTH_SHORT).show();
                    JSONArray array = jsonObject.getJSONArray("results");

                    for (int i = 0; i < array.length(); ++i) {
                        JSONObject res = array.getJSONObject(i);
                       // Nearby place = new Nearby(res.getString("icon"), res.getString("name"),"http://cs-server.usc.edu:45678/hw/hw9/images/android/heart_outline_black.xml");
                        //Log.d(TAG, res.getString("name"));
                        //place_list.add(place);
                    }

                    Intent intent = new Intent(getActivity(), PlacesActivity.class);
                    intent.putExtra("places", jsonObject.toString());
                    startActivity(intent);


                    //recycler_adapter = new PlaceSearchAdapter( getApplicationContext(),place_list);
                    //recyclerView.setAdapter(recycler_adapter);

                    //Log.d(TAG, jsonObject.getString("results"));

                } catch (JSONException e) {

                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getContext(), "Error" + error.toString(), Toast.LENGTH_SHORT).show();

            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);


    }
   /* public void radcheck(View view1) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view1).isChecked();

        // Check which radio button was clicked
        switch (view1.getId()) {
            case R.id.first:
                if (checked) {
                    Log.d(TAG, "radcheck1: ");
                    destination.setEnabled(false);
                }
                break;
            case R.id.second:
                if (checked) {
                    Log.d(TAG, "radcheck2: ");
                    destination.setEnabled(true);

                }
                // Ninjas rule
                break;
        }
    }
*/
    private boolean validate()
    {
        final EditText keyword = view.findViewById(R.id.et_keyword);
        final TextView error_keyword = (TextView) view.findViewById(R.id.ifNullKeyword);
        TextView error_destination = (TextView) view.findViewById((R.id.ifNullDestination));

        boolean flag = true;
        if(keyword.getText().toString().trim().length()<=0) {
            //user name is not inserted
            error_keyword.setVisibility(View.VISIBLE);
            flag=false;
        }

        RadioGroup locpref = (RadioGroup) view.findViewById(R.id.location);

        destination = view.findViewById(R.id.atv_destination);
        int checkedRadioButton = locpref.getCheckedRadioButtonId();

        if(checkedRadioButton== R.id.second)
        {
            if(destination.getText().toString().trim().length()<=0)
            {
                error_destination.setVisibility(View.VISIBLE);
                flag=false;
            }
        }

        if(!flag)
            toastMessage("Please fix all fields with errors");

        return flag;
    }


    private boolean validateForm()
    {
        final EditText keyword = view.findViewById(R.id.et_keyword);
        final TextView error_keyword = (TextView) view.findViewById(R.id.ifNullKeyword);
        TextView error_destination = (TextView) view.findViewById((R.id.ifNullDestination));

        boolean flag = true;
        if(keyword.getText().toString().trim().length()<=0) {
            //user name is not inserted
            error_keyword.setVisibility(View.VISIBLE);
            flag=false;
        }

        RadioGroup locpref = (RadioGroup) view.findViewById(R.id.location);

        destination = view.findViewById(R.id.atv_destination);
        int checkedRadioButton = locpref.getCheckedRadioButtonId();

        if(checkedRadioButton== R.id.second)
        {
            if(destination.getText().toString().trim().length()<=0)
            {
                error_destination.setVisibility(View.VISIBLE);
                flag=false;
            }
        }

        if(!flag)
            toastMessage("Please fix all fields with errors");

        return flag;
    }



    private void toastMessage(String message)
    {
        Toast t = Toast.makeText(view.getContext().getApplicationContext(), message, Toast.LENGTH_SHORT);
        //View v1 = t.getView();
        //v1.setBackgroundColor(Integer.parseInt("#cccccc"));
        t.show();

        //Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
