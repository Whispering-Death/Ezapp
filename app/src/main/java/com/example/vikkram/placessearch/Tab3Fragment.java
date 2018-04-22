package com.example.vikkram.placessearch;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class Tab3Fragment extends Fragment{
    private static final String TAG = "Tab3Fragment";

    private Button btnTEST;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab3_fragment,container,false);
        btnTEST = (Button) view.findViewById(R.id.btnTEST3);

        btnTEST.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getActivity(), "TESTING BUTTON CLICK 3",Toast.LENGTH_SHORT).show();
                final ProgressDialog progressDialog = new ProgressDialog(getContext());
                progressDialog.setMessage("Fetching results");
                String url = "http://my-cloned-env-vasuki.us-west-2.elasticbeanstalk.com/placedetails?placeid=ChIJN1t_tDeuEmsRUsoyG83frY4";
                StringRequest stringRequest = new StringRequest(Request.Method.GET,
                        url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        progressDialog.dismiss();

                        try {
                            //Log.d(TAG, "Submitted results"+response);
                            JSONObject jsonObject = new JSONObject(response);

                          //Toast.makeText(getContext(), "No Error", Toast.LENGTH_SHORT).show();
                            /*JSONArray array = jsonObject.getJSONArray("results");

                            for (int i = 0; i < array.length(); ++i) {
                                JSONObject res = array.getJSONObject(i);

                            }*/
                            /*
                            Intent intent = new Intent(getActivity(), PlacesInfo.class);
                            intent.putExtra("placeinfo", jsonObject.toString());
                            startActivity(intent);
                            */



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
                //Intent intent = new Intent(getActivity(), PlacesInfo.class);
                //intent.putExtra("places", jsonObject.toString());
                //startActivity(intent);

            }
        });

        return view;
    }

}
