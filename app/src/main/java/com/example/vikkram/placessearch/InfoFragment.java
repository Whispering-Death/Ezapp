package com.example.vikkram.placessearch;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class InfoFragment extends Fragment{
    private static final String TAG = "InfoFragment";
    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.info_fragment, container, false);
        //String url = "http://my-cloned-env-vasuki.us-west-2.elasticbeanstalk.com/placedetails?placeid=ChIJN1t_tDeuEmsRUsoyG83frY4";
        JSONObject json= ((PlacesInfo)this.getActivity()).js;
        //JSONObject jsonData= null;
        /*
        if(getArguments()!= null)
        {
            Log.d(TAG, "Got data: "+getArguments().getString("placedetails"));
            String data = getArguments().getString("placedetails");
            try {
                jsonData = new JSONObject(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        */
        //Log.d(TAG, jsonData[0].toString());
        TextView address = (TextView) view.findViewById(R.id.address);
        TextView number = (TextView) view.findViewById(R.id.number);
        TextView price = (TextView) view.findViewById(R.id.price);
        RatingBar rating = (RatingBar) view.findViewById(R.id.ratingBar);
        TextView google = (TextView) view.findViewById(R.id.googlepage);
        TextView website = (TextView) view.findViewById(R.id.site);
        JSONObject jsonData = null;
        try {
            jsonData = json.getJSONObject("result");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        address.setText("No data");
        number.setText("No data");
        price.setText("No data");
        //rating.setText("No data");
        google.setText("No data");
        website.setText("No data");


        if(jsonData != null)
        {

            try {
                address.setText(jsonData.get("vicinity").toString());
            } catch (JSONException e) {

                //e.printStackTrace();
            }

            try {
                number.setText(jsonData.getString("international_phone_number"));
            } catch (JSONException e) {
                //e.printStackTrace();
            }


            try {

                int price_number = jsonData.getInt("price_level");
                String dollars="";
                for(int i=0;i<price_number;++i)
                {
                    dollars+="$";
                }

                price.setText(dollars);
            } catch (JSONException e) {

                //e.printStackTrace();
            }
            try {
                rating.setRating(Float.parseFloat(jsonData.getString("rating")));
            } catch (JSONException e) {
                //e.printStackTrace();
            }

            try {
                google.setText(jsonData.getString("url"));
            } catch (JSONException e) {
                //e.printStackTrace();
            }

            try {
                website.setText(jsonData.getString("website"));
            } catch (JSONException e) {
               // e.printStackTrace();
            }


        }

        return view;
    }
}
