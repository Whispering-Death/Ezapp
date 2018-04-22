package com.example.vikkram.placessearch;



import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PlacesActivity extends AppCompatActivity implements PlaceSearchAdapter.ItemClickListener {
    PlaceSearchAdapter adapter;
    private static final String TAG = "PlacesActivity";
    ArrayList<JSONObject> jsonList;
    private static String placeid="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);

        ArrayList<String> animalNames = new ArrayList<>();
        jsonList = new ArrayList<>();
        String jsonData = getIntent().getStringExtra("places");
        //Log.d(TAG, jsonData);

        JSONObject jsonResults = null;
        try {
            jsonResults = new JSONObject(jsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            JSONArray places = jsonResults.getJSONArray("results");
            for(int index=0;index<places.length();++index)
            {
                JSONObject js = places.getJSONObject(index);
                jsonList.add(js);

                animalNames.add(js.getString("name"));

            }
            Log.d(TAG, places.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }



        //JSONArray jsonData =
        /*
        animalNames.add("Horse");
        animalNames.add("Cow");
        animalNames.add("Camel");
        animalNames.add("Sheep");
        animalNames.add("Goat");
        */
        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PlaceSearchAdapter(this, jsonList);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(View view, int position) {
        //Log.d(TAG, "Item from place results has been clicked ");
        //Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Current data: "+String.valueOf(adapter.getItem(position)));

        String placeid="";

        try {
            placeid = adapter.getItem(position).get("place_id").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "Place id found: "+placeid);
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching place details");
        //String url = "http://my-cloned-env-vasuki.us-west-2.elasticbeanstalk.com/placedetails?placeid=ChIJN1t_tDeuEmsRUsoyG83frY4";
        String url = "http://my-cloned-env-vasuki.us-west-2.elasticbeanstalk.com/placedetails?placeid="+placeid;
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                progressDialog.dismiss();

                try {
                    //Log.d(TAG, "Submitted results"+response);
                    JSONObject jsonObject = new JSONObject(response);

                    Intent intent = new Intent(PlacesActivity.this, PlacesInfo.class);
                    intent.putExtra("placeinfo", jsonObject.toString());
                    startActivity(intent);




                } catch (JSONException e) {

                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                //Toast.makeText(this, "Error" + error.toString(), Toast.LENGTH_SHORT).show();

            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

}