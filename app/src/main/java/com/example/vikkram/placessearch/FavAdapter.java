package com.example.vikkram.placessearch;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class FavAdapter extends RecyclerView.Adapter<FavAdapter.ViewHolder>{

    private List<JSONObject> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private  Context context;
    private static final String TAG = "FavAdapter";
    private Tab2Fragment fragment;
    View view;
    SharedPreferences myprefs;
    // data is passed into the constructor
    FavAdapter(Context context, List<JSONObject> data, Tab2Fragment fragment) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.fragment = fragment;

    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = mInflater.inflate(R.layout.recycler_favorites, parent, false);
        myprefs = view.getContext().getSharedPreferences("MyPrefs", MODE_PRIVATE);

        //this.fragment.printdummy();
        //setHasStableIds(true);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final JSONObject js = mData.get(position);

        //String animal = mData.get(position);
        String place_name = null;
        String place_icon = null;
        String place_vic = "";
        String placeid = "";

        try {
            place_name = js.get("name").toString();

            place_icon = js.get("icon").toString();
            place_vic = js.getString("vicinity");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(place_name!= null)
         holder.myName.setText(place_name);

        Picasso.get().load(place_icon).into(holder.myIcon);
        holder.myDel.setImageResource(R.drawable.ic_resfav);
        holder.myAdr.setText(place_vic);
        holder.itemView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String placeid="";
                        try {
                            placeid= js.getString("place_id");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Log.d(TAG, "Place id found: "+placeid);


                        final ProgressDialog progressDialog = new ProgressDialog(context);
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

                                    Intent intent = new Intent(context, PlacesInfo.class);
                                    intent.putExtra("placeinfo", jsonObject.toString());
                                    context.startActivity(intent);




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

                        RequestQueue requestQueue = Volley.newRequestQueue(context);
                        requestQueue.add(stringRequest);
                    }
                }
        );
        holder.myDel.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String placeid = null;
                        try {
                            placeid = js.get("place_id").toString();
                            String name = js.get("name").toString();
                            SharedPreferences.Editor editor = myprefs.edit();
                            editor.remove(placeid);
                            mData.remove(position);

                            if(mData.isEmpty())
                                fragment.check();
                            toastMessage(name+" removed from favorites");
                            editor.commit();
                            notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }
        );
    }

    // total number of rows
    @Override
    public int getItemCount() {



        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myName;
        ImageView myIcon;
        ImageView myDel;

        TextView myAdr;
        ViewHolder(View itemView) {
            super(itemView);
            myName = itemView.findViewById(R.id.fav_name);
            myIcon = itemView.findViewById(R.id.fav_icon);
            myDel = itemView.findViewById(R.id.fav_del);
            myAdr = itemView.findViewById(R.id.fav_vic);


            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position

    JSONObject getItem(int id) {
        return mData.get(id);
    }
    private void toastMessage(String message)
    {

        Toast t = Toast.makeText(view.getContext(), message, Toast.LENGTH_SHORT);
        //View v1 = t.getView();
        //v1.setBackgroundColor(Integer.parseInt("#cccccc"));
        t.show();

        //Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }
    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
