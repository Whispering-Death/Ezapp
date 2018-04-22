package com.example.vikkram.placessearch;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ReviewFragment extends android.support.v4.app.Fragment implements AdapterView.OnItemSelectedListener{

    private static final String TAG = "ReviewFragment";
    View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.review_fragment, container, false);

        JSONObject json= ((PlacesInfo)this.getActivity()).js;
        ArrayList<JSONObject> google_reviews = new ArrayList<>();
        JSONArray reviewData= new JSONArray();
        try {
            Log.d(TAG, "Reviews: "+json.getJSONObject("result"));
            reviewData= json.getJSONObject("result").getJSONArray("reviews");
            Log.d(TAG, "Review data:  "+reviewData);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        for(int i=0;i<reviewData.length();++i)
        {
            try {
                google_reviews.add(reviewData.getJSONObject(i));
            } catch (JSONException e) {
                break;
            }
        }
        
        Spinner reviewType = view.findViewById(R.id.reviewCategory);
        
        Spinner sortType = view.findViewById(R.id.sortingCategory);
        Log.d(TAG, "Google reviews: "+google_reviews);
        reviewType.setOnItemSelectedListener(this);
        sortType.setOnItemSelectedListener(this);

        /*
        reviewType.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        Toast.makeText(getActivity(),"Long text",Toast.LENGTH_SHORT);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                }
        );
        */
        
        return view;
    }

    @Override
    public void onResume() {

        super.onResume();


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "onItemSelected: "+parent.getItemAtPosition(position).toString());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
