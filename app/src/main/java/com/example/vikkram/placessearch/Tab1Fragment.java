package com.example.vikkram.placessearch;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RadioButton;
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
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.net.URLEncoder;
import java.util.concurrent.Executor;

public class Tab1Fragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener, LocationListener,Filterable {

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
    Location location;
    private String provider;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tab1_fragment, container, false);
        //btnTEST = (Button) view.findViewById(R.id.btnTEST);
        locationManager = (LocationManager) getActivity().getSystemService(getContext().LOCATION_SERVICE);

        boolean enabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

// check if enabled and if not send user to the GSP settings
// Better solution would be to display a dialog and suggesting to
// go to the settings
        if (!enabled) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);

        getDeviceLocation();

        Button searchBtn = view.findViewById(R.id.btn_search);
        Button clearBtn = view.findViewById(R.id.btn_clear);

        //searchBtn.setOnClickListener(this);
        final EditText keyword = view.findViewById(R.id.et_keyword);
        final TextView error_keyword = (TextView) view.findViewById(R.id.ifNullKeyword);
        Context c1 = getActivity().getApplicationContext();
        destination = (AutoCompleteTextView) view.findViewById(R.id.atv_destination);


        if (mGoogleApiClient == null || !mGoogleApiClient.isConnected()) {
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
        RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.location);
        //mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());


        searchBtn.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                             Log.d(TAG, "Search results queried");

                                             if (validateForm()) {
                                                 getJSONData();
                                             }
                                         }
                                     }



        );

        clearBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clearform();
                    }
                }
        );

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected

                switch (checkedId) {
                    case R.id.first:

                        Log.d(TAG, "radcheck1: ");
                        destination.setText("");
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


        destination.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "Item clicked: "+destination.getText().toString());

            }
        } );

        return view;
    }


    private void clearform()
    {
        EditText et_keyword = (EditText) view.findViewById(R.id.et_keyword);
        et_keyword.setText("");
        EditText et_distance = (EditText) view.findViewById(R.id.et_distance);
        et_distance.setText("");
        RadioButton btn1= (RadioButton) view.findViewById(R.id.first);
        Spinner mSpinner = (Spinner) view.findViewById(R.id.spinnerCategory);
        String compareValue = "Default";

        TextView er_keyword = view.findViewById(R.id.ifNullKeyword);
        er_keyword.setVisibility(View.GONE);
        TextView er_location = view.findViewById(R.id.ifNullDestination);
        er_location.setVisibility(View.GONE);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.categories_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
        if (compareValue != null) {
            int spinnerPosition = adapter.getPosition(compareValue);
            mSpinner.setSelection(spinnerPosition);
        }

        btn1.setChecked(true);
        destination.setText("");
        destination.setEnabled(false);

    }
    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
                //initMap();
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        permissions,
                        LOC_REQ_CODE);
            }


        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    permissions,
                    LOC_REQ_CODE);
        }

        if (mLocationPermissionsGranted) {
            location = locationManager.getLastKnownLocation(provider);

        }

    }


/*
    private void requestLocationAccessPermission() {
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOC_REQ_CODE);
    }
    */

    @Override
    public void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if(locationManager!=null)
            locationManager.requestLocationUpdates(provider, 400, 1, this);
    }

    private void getJSONData() {

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Fetching results");
        progressDialog.show();
        EditText et_keyword = (EditText) view.findViewById(R.id.et_keyword);
        String keyword = et_keyword.getText().toString().trim();
        Spinner sp_category = (Spinner) view.findViewById(R.id.spinnerCategory);
        EditText et_distance = (EditText) view.findViewById(R.id.et_distance);

        Integer distance;

        try{
            distance = Integer.parseInt(et_distance.getText().toString().trim());
        }
        catch (NumberFormatException nfe)
        {
            distance =10;
        }

        RadioButton second = (RadioButton) view.findViewById(R.id.second);
        String category = sp_category.getSelectedItem().toString().toLowerCase();
        category = category.replaceAll(" ","_");
        Log.d(TAG, keyword);
        Log.d(TAG, destination.getText().toString());
        Log.d(TAG, category);
        Log.d(TAG, String.valueOf(distance));
        Log.d(TAG, "Latitude: "+lat);
        Log.d(TAG, "Longitude: "+lon);
        String url="";

        if(second.isChecked())
        {
            Log.d(TAG, "getJSONData: "+destination.getText().toString());
            url="http://vasuki-travel-env.hhtzymbd2i.us-west-2.elasticbeanstalk.com/geocoding?keyword="+URLEncoder.encode(keyword)+"&category="+category+"&distance="+distance+"&place="+ URLEncoder.encode(destination.getText().toString());
            Log.d(TAG,url);

        }
        else
        {
            url="http://vasuki-travel-env.hhtzymbd2i.us-west-2.elasticbeanstalk.com/geocoding?keyword="+URLEncoder.encode(keyword)+"&category="+category+"&distance="+distance+"&lat="+lat+"&lon="+lon;
            Log.d(TAG, url);

        }

        Log.d(TAG, "URL: "+url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressDialog.dismiss();

                try {
                    Log.d(TAG, "Submitted results"+response);
                    JSONObject jsonObject = new JSONObject(response);
                    //Toast.makeText(getContext(), "No Error", Toast.LENGTH_SHORT).show();

                    JSONArray array = jsonObject.getJSONArray("results");
                    String pagetoken = "";

                    try{
                        pagetoken= jsonObject.getString("next_page_token");
                    }
                    catch(JSONException e)
                    {
                        ;
                    }
                    for (int i = 0; i < array.length(); ++i) {
                        JSONObject res = array.getJSONObject(i);

                    }

                    Intent intent = new Intent(getActivity(), PlacesActivity.class);

                    if(pagetoken!="")
                        intent.putExtra("token",pagetoken);
                    intent.putExtra("places", jsonObject.toString());
                    startActivity(intent);




                } catch (JSONException e) {

                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Error" + error.toString(), Toast.LENGTH_SHORT).show();

            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);


    }

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
    public void onLocationChanged(Location location1) {
        lat= location1.getLatitude();
        lon= location1.getLongitude();
        Log.d(TAG, "Location changed again");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        locationManager.removeUpdates(this);
        Log.d(TAG, "Location update initiated ");
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public Filter getFilter() {
        return null;
    }
}
