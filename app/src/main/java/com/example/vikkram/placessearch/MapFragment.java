package com.example.vikkram.placessearch;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.widget.Toast.*;

import java.net.URLEncoder;
import java.util.Map;

public class MapFragment extends android.support.v4.app.Fragment implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, AdapterView.OnItemSelectedListener{
    private static final String TAG = "MapFragment";

    private GoogleApiClient mGoogleApiClient;
    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136));

    private double lat = 0.0;
    private double lng=0.0;


    private double src_lat =0.0;
    private double src_lng=0.0;

    String destination_place="";
    GoogleMap mainMap;
    View view;
    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.map_fragment, container, false);
        JSONObject json= ((PlacesInfo)this.getActivity()).js;
        try {
            Log.d(TAG, "Map data: "+json.getJSONObject("result").get("name"));
            destination_place = json.getJSONObject("result").getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        double ne_lat=0,ne_lng=0,sw_lat=0,sw_lng=0;


        try {
            //Log.d(TAG, "Bias ports: "+json.getJSONObject("result").getJSONObject("geometry").getJSONObject("location").get("lat"));

            ne_lat= json.getJSONObject("result").getJSONObject("geometry").getJSONObject("viewport").getJSONObject("northeast").getDouble("lat");
            Log.d(TAG, "North east latitude: "+ne_lat);
            ne_lng = json.getJSONObject("result").getJSONObject("geometry").getJSONObject("viewport").getJSONObject("northeast").getDouble("lng");

            sw_lat = json.getJSONObject("result").getJSONObject("geometry").getJSONObject("viewport").getJSONObject("southwest").getDouble("lat");

            sw_lng= json.getJSONObject("result").getJSONObject("geometry").getJSONObject("viewport").getJSONObject("southwest").getDouble("lng");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        LatLng north_east = new LatLng(ne_lat,ne_lng);
        LatLng south_west = new LatLng(sw_lat,sw_lng);

        LatLngBounds bound= new LatLngBounds(south_west,north_east);
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

        try {
            Log.d(TAG, "Latitude obtained: "+json.getJSONObject("result").getJSONObject("geometry").getJSONObject("location").get("lat").toString());
            lat= json.getJSONObject("result").getJSONObject("geometry").getJSONObject("location").getDouble("lat");
            lng = json.getJSONObject("result").getJSONObject("geometry").getJSONObject("location").getDouble("lng");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(getContext(), mGoogleApiClient, bound, null);


        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        final AutoCompleteTextView atv = (AutoCompleteTextView)view.findViewById(R.id.map_atv_destination);
        atv.setAdapter(mPlaceAutocompleteAdapter);

        final Spinner spn = (Spinner)view.findViewById(R.id.spinnerCategory);

        spn.setOnItemSelectedListener(this);
        atv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "Item clicked: "+atv.getText().toString());
                Log.d(TAG, "Calling directions api");

                String travel_mode = spn.getSelectedItem().toString();
                getDirections(travel_mode.toLowerCase());
                //String url = "http://my-cloned-env-vasuki.us-west-2.elasticbeanstalk.com/placedetails?placeid=ChIJN1t_tDeuEmsRUsoyG83frY4";


            }
        } );


        return view;
    }


    public void getDirections(String mode)
    {

        Log.d(TAG, "Making google directions api request");

        AutoCompleteTextView dst = (AutoCompleteTextView)view.findViewById(R.id.map_atv_destination);


        String source = dst.getText().toString().trim();
        Log.d(TAG, "Original location:  "+source);
        if(source.equals(""))
            return;
        final String destination= String.valueOf(lat)+","+String.valueOf(lng);
        Log.d(TAG, "Mode of travel: "+mode);
        String url = "https://maps.googleapis.com/maps/api/directions/json?origin="+ URLEncoder.encode(source)+"&destination="+destination+"&mode="+mode+"&key=AIzaSyAYKYTAwBVMKXjmcfhD-EGLkhbL-9Yxayg";

        mainMap.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                //Log.d(TAG, "On the inside");


                try {
                    Log.d(TAG, "Submitted results"+response);
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps");
                    Log.d(TAG, "onResponse: "+jsonArray);

                   // Log.d(TAG, "Source location: "+jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("start_location").getString("lat"));
                   // Log.d(TAG, "Source location: "+jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("start_location").getString("lng"));
                    src_lat= jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("start_location").getDouble("lat");
                    src_lng= jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("start_location").getDouble("lng");

                    String temp_dst = destination;


                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    //builder.include(position(new LatLng(lat,lng)));
                    MarkerOptions m1 = new MarkerOptions().position(new LatLng(lat,lng));
                    MarkerOptions m2 = new MarkerOptions().position(new LatLng(src_lat, src_lng));
                    builder.include(m1.getPosition());
                    builder.include(m2.getPosition());

                    mainMap.addMarker(new MarkerOptions().position(new LatLng(lat,lng))
                            .title(destination_place)).showInfoWindow();

                    mainMap.addMarker(new MarkerOptions().position(new LatLng(src_lat,src_lng))
                            .title("Marker in Sydney"));
                    int width = getResources().getDisplayMetrics().widthPixels;
                    int height = getResources().getDisplayMetrics().heightPixels;
                    int padding = (int) (width * 0.15);
                    LatLngBounds updated_bounds = builder.build();

                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(updated_bounds, width, height, padding);

                    mainMap.animateCamera(cu);
                    String[] points;
                    points= getPaths(jsonArray);
                    displayDirection(points);
                    //Log.d(TAG, "Polyline points obtained: "+points);



                } catch (JSONException e) {
                    Toast.makeText(getContext(),"No routes found:", Toast.LENGTH_SHORT).show();
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


        //Log.d(TAG, "Polyline point list: "+points);
    }

    public String[] getPaths(JSONArray googlesteps)
    {
        int count = googlesteps.length();
        String[] polylines = new String[count];

        for(int i = 0;i<count;i++)
        {
            try {
                polylines[i] = getPath(googlesteps.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return polylines;
    }


    public void displayDirection(String[] directionsList)
    {

        int count = directionsList.length;
        for(int i = 0;i<count;i++)
        {
            PolylineOptions options = new PolylineOptions();
            options.color(Color.BLUE);
            options.width(10);
            options.addAll(PolyUtil.decode(directionsList[i]));

            mainMap.addPolyline(options);
        }
    }
    public String getPath(JSONObject googlePathJson)
    {
        String polyline = "";
        try {
            polyline = googlePathJson.getJSONObject("polyline").getString("points");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return polyline;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng dest = new LatLng(lat, lng);


        mainMap = googleMap;

        googleMap.addMarker(new MarkerOptions().position(dest)
                .title(destination_place)).showInfoWindow();

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(dest));
        googleMap.animateCamera( CameraUpdateFactory.zoomTo( 17.0f ) );

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        String item= parent.getItemAtPosition(position).toString();

        Log.d(TAG, "onItemSelected: "+item);
        getDirections(item.toLowerCase());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
