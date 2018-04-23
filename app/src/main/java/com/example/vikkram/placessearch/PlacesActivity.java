package com.example.vikkram.placessearch;



import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

    ArrayList<JSONObject> jsonList1;
    ArrayList<JSONObject> jsonList2;

    ArrayList<String> pageTokens;

    private static int cur_page=0;
    private static String placeid="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);
      
        pageTokens = new ArrayList<>();
        ArrayList<String> animalNames = new ArrayList<>();
        jsonList1= new ArrayList<>();
        jsonList2= new ArrayList<>();
        jsonList = new ArrayList<>();
        String jsonData = getIntent().getStringExtra("places");



        if(getIntent().getStringExtra("token")!= null)
        {
            pageTokens.add(getIntent().getStringExtra("token"));
        }

        //Log.d(TAG, "Next page token found: "+pageTokens.get(0));
        Log.d(TAG, "Data obtained from places activity: "+jsonData);

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



        Button prevBtn = (Button) findViewById(R.id.prev);

        Button nextBtn= (Button) findViewById(R.id.next);
        if(cur_page == 0)
        {
            prevBtn.setEnabled(false);
        }

        if(pageTokens.size()>= cur_page+1)
        {
            nextBtn.setEnabled(true);
        }
        else
        {
            nextBtn.setEnabled(false);
        }


        prevBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "Previous page requested ");

                        cur_page-=1;
                        parseData();

                    }
                }
        );


        nextBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "Next page requested ");

                        String token = pageTokens.get(cur_page);


                        //cur_page+=1;
                        if(cur_page==0 && jsonList1.size()!=0)
                        {
                            cur_page+=1;
                            parseData();
                        }

                        else if(cur_page==1 && jsonList2.size()!=0)
                        {
                            cur_page+=1;
                            parseData();
                        }

                        else
                            getNextPage(token);



                    }
                }
        );


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

    public void setButtons()
    {
        Button prevBtn = (Button) findViewById(R.id.prev);

        Button nextBtn= (Button) findViewById(R.id.next);
        Log.d(TAG, "Current page number: "+cur_page);
        if(cur_page!=0)
            prevBtn.setEnabled(true);
        else
            prevBtn.setEnabled(false);

        if(pageTokens.size()>=cur_page+1)
            nextBtn.setEnabled(true);
        else
            nextBtn.setEnabled(false);
    }

    public void parseData()
    {

        ArrayList<JSONObject> tempList= new ArrayList<>();
        if(cur_page==0)
            tempList.addAll(jsonList);
        else if(cur_page==1)
            tempList.addAll(jsonList1);
        else
            tempList.addAll(jsonList2);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Log.d(TAG, "Page1 size: "+jsonList1.size());

        adapter = new PlaceSearchAdapter(this, tempList);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
        setButtons();

        Log.d(TAG, "Populated JSON data: "+jsonList1.toString());
    }


    public void getNextPage(String token)
    {

        String url = "http://vasuki-travel-env.hhtzymbd2i.us-west-2.elasticbeanstalk.com/nextPageSearch?pagetoken="+token;
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                ArrayList<JSONObject> tempList = new ArrayList<>();
                try {
                    Log.d(TAG, "Next page results:"+response);
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

                    if(pagetoken!="")
                        pageTokens.add(pagetoken);

                    for (int i = 0; i < array.length(); ++i) {
                        JSONObject res = array.getJSONObject(i);
                        tempList.add(res);

                    }

                    if(cur_page==0)
                    {
                        jsonList1.addAll(tempList);
                    }
                    else
                    {
                        jsonList2.addAll(tempList);
                    }
                    cur_page+=1;
                    parseData();
                } catch (JSONException e) {

                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                //Toast.makeText(this, "Error" + error.toString(), Toast.LENGTH_SHORT).show();

            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);



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
        progressDialog.show();
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