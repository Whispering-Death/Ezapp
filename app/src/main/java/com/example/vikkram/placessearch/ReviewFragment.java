package com.example.vikkram.placessearch;

import android.app.Fragment;
import android.content.Intent;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ReviewFragment extends android.support.v4.app.Fragment implements AdapterView.OnItemSelectedListener{

    private static final String TAG = "ReviewFragment";
    private static ReviewAdapter adapter;
    ArrayList<JSONObject> google_reviews;
    ArrayList<JSONObject> default_google_reviews;
    ArrayList<JSONObject> yelp_reviews;
    ArrayList<JSONObject> default_yelp_reviews;

    RecyclerView recyclerView;
    View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.review_fragment, container, false);

        JSONObject json= ((PlacesInfo)this.getActivity()).js;
        google_reviews = new ArrayList<>();
        default_google_reviews= new ArrayList<>();
        yelp_reviews = new ArrayList<>();
        default_yelp_reviews= new ArrayList<>();
        getDefaultReviews(true);
        default_google_reviews.addAll(google_reviews);
        getYelpReviews();
        default_yelp_reviews.addAll(yelp_reviews);


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


    public void getYelpReviews()
    {

        //String url="";
        String country="";
        String state= "";
        String city="";


        JSONObject json= ((PlacesInfo)this.getActivity()).js;
        Log.d(TAG, "getYelpReviews: "+json);
        JSONObject results = null;
        try {
            results = json.getJSONObject("result");
           // Log.d(TAG, "getYelpReviews: "+results);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONArray address_components = null;
        ArrayList<JSONObject> address;
        if(results != null)
        {
            //Log.d(TAG, "getYelpReviews: "+results);
            try {
                Log.d(TAG, "get Address components: "+ results.get("address_components"));
                address_components= results.getJSONArray("address_components");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        for(int i=0;i<address_components.length();++i)
        {
            JSONObject innerDet= null;
            JSONArray types= null;
            try {
                innerDet = address_components.getJSONObject(i);

                types= innerDet.getJSONArray("types");
                Log.d(TAG, "Types of places:  "+types);

                for(int j=0;j<types.length();++j)
                {
                    if(types.getString(j).equals("country"))
                    {
                        country = innerDet.getString("short_name");
                    }
                    else if(types.getString(j).equals("administrative_area_level_1"))
                    {
                        state = innerDet.getString("short_name");
                    }
                    else if(types.getString(j).equals("locality"))
                    {
                        city= innerDet.getString("short_name");
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }



        }

        Log.d(TAG, "Country: "+country+" State: "+state+" City: "+city);


        //Log.d(TAG, "getYelpReviews: ");

        String vicinity="";
        String name="";
        try {
            vicinity = results.getString("vicinity");
            name = results.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Log.d(TAG, "Parameters: "+vicinity+" "+name);

        String url="http://vasuki-travel-env.hhtzymbd2i.us-west-2.elasticbeanstalk.com/yelpSearch?country="+ URLEncoder.encode(country)+
                "&state="+URLEncoder.encode(state)
                +"&address="+URLEncoder.encode(vicinity)+"&city="+URLEncoder.encode(city)+
                "&name="+URLEncoder.encode(name);

        Log.d(TAG, "URL request: "+url);


        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    Log.d(TAG, "Submitted results"+response);
                    JSONArray yelpJSON = new JSONArray(response);

                    for(int i=0;i<yelpJSON.length();++i)
                    {
                        yelp_reviews.add(yelpJSON.getJSONObject(i));

                    }


                    //Log.d(TAG, "Final Yelp Reviews: "+yelp_reviews);


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
        int sel_pos = parent.getSelectedItemPosition();
        if(item.equals("Gooogle Reviews") || item.equals("Yelp Reviews"))
        {
            Log.d(TAG, "Either google or yelp was selected");
            if(sel_pos==1)
            {
                adapter = new ReviewAdapter(getContext(), yelp_reviews, false );

                recyclerView.setAdapter(adapter);
            }

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
