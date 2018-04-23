package com.example.vikkram.placessearch;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import java.util.Collections;
import java.util.Comparator;

public class ReviewFragment extends android.support.v4.app.Fragment implements AdapterView.OnItemSelectedListener{

    private static final String TAG = "ReviewFragment";
    private static ReviewAdapter adapter;
    ArrayList<JSONObject> google_reviews;
    ArrayList<JSONObject> default_google_reviews;

    RecyclerView recyclerView;
    View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.review_fragment, container, false);

        JSONObject json= ((PlacesInfo)this.getActivity()).js;
        google_reviews = new ArrayList<>();
        default_google_reviews= new ArrayList<>();
        getDefaultReviews(true);
        default_google_reviews.addAll(google_reviews);
        /*
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
        */
        
        Spinner reviewType = view.findViewById(R.id.reviewCategory);
        Log.d(TAG, "onCreateView: "+google_reviews);
        Spinner sortType = view.findViewById(R.id.sortingCategory);
        Log.d(TAG, "Google reviews: "+google_reviews);
        reviewType.setOnItemSelectedListener(this);
        sortType.setOnItemSelectedListener(this);
        recyclerView = view.findViewById(R.id.revcontainer);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ReviewAdapter(getContext(), google_reviews, true );
        //adapter.setClickListener(this);

        //recyclerView.invalidate();
        recyclerView.setAdapter(adapter);
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


    public void getDefaultReviews(boolean type)
    {
        JSONObject json= ((PlacesInfo)this.getActivity()).js;
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
    }

    @Override
    public void onResume() {

        super.onResume();


    }

    public void customsort_timings(final boolean asc)
    {
        Collections.sort( google_reviews, new Comparator<JSONObject>() {
            //You can change "Name" with "ID" if you want to sort by ID
            private static final String KEY_NAME = "time";

            @Override
            public int compare(JSONObject a, JSONObject b) {
                long valA=0;
                long valB=0;
                try {
                    valA =  a.getLong(KEY_NAME);
                    valB =  b.getLong(KEY_NAME);
                }
                catch (JSONException e) {
                    //do something
                }
                if(asc)
                    return (int)(valA-valB);
                else
                    return (int)(valB-valA);
                //if you want to change the sort order, simply use the following:
                //return -valA.compareTo(valB);
            }
        });

    }
    public void customsort_ratings(final boolean asc)
    {
        Collections.sort( google_reviews, new Comparator<JSONObject>() {
            //You can change "Name" with "ID" if you want to sort by ID
            private static final String KEY_NAME = "rating";

            @Override
            public int compare(JSONObject a, JSONObject b) {
                int valA=0;
                int valB=0;
                try {
                    valA =  a.getInt(KEY_NAME);
                    valB =  b.getInt(KEY_NAME);
                }
                catch (JSONException e) {
                    //do something
                }
                if(asc)
                    return valA-valB;
                else
                    return valB-valA;
                //if you want to change the sort order, simply use the following:
                //return -valA.compareTo(valB);
            }
        });
    }
    public void customsort(String sorting, int choice)
    {
        Log.d(TAG, "customsort called "+choice);
        if(choice == 0)
        {
            //Log.d(TAG, "Default reviews: "+default_google_reviews);
            //google_reviews= new ArrayList<>();
            //google_reviews.addAll(default_google_reviews);
            adapter = new ReviewAdapter(getContext(), default_google_reviews, true );

            recyclerView.setAdapter(adapter);


        }
        else if(choice == 1 || choice==2)
        {
            if(choice==2)
                customsort_ratings(true);
            else
                customsort_ratings(false);

            //Log.d(TAG, "customsort result:  "+google_reviews);
            adapter = new ReviewAdapter(getContext(), google_reviews, true );
            //adapter.setClickListener(this);

            //recyclerView.invalidate();
            recyclerView.setAdapter(adapter);
        }

        else
        {
            if(choice==4)
                customsort_timings(true);
            else
                customsort_timings(false);
            adapter = new ReviewAdapter(getContext(), google_reviews, true );

            recyclerView.setAdapter(adapter);

        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        //Log.d(TAG, "onItemSelected: "+parent.getItemAtPosition(position).getClass());

        String item= parent.getItemAtPosition(position).toString();

        if(item.equals("Gooogle Reviews") || item.equals("Yelp Reviews"))
        {
            Log.d(TAG, "Either google or yelp was selected");
        }

        else
        {
            Log.d(TAG, "Sorting type selection detected");
            customsort(item,parent.getSelectedItemPosition());
        }
       // Log.d(TAG, "onItemSelected: "+parent.getSelectedItemPosition());

        //if(item.equals("Google Reviews"))
        //Log.d(TAG, "onItemSelected: "+parent.getItemAtPosition(position).toString());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
