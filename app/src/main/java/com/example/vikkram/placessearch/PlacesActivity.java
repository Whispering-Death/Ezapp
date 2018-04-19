package com.example.vikkram.placessearch;



import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PlacesActivity extends AppCompatActivity implements PlaceSearchAdapter.ItemClickListener {
    PlaceSearchAdapter adapter;
    private static final String TAG = "PlacesActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);

        ArrayList<String> animalNames = new ArrayList<>();
        ArrayList<JSONObject> jsonList = new ArrayList<>();
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
        Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
    }

}