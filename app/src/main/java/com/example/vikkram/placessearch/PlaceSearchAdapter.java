package com.example.vikkram.placessearch;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class PlaceSearchAdapter extends RecyclerView.Adapter<PlaceSearchAdapter.ViewHolder> {

    private List<JSONObject> mData;
    private List<String> placeIcon;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    SharedPreferences myprefs;
    // data is passed into the constructor
    PlaceSearchAdapter(Context context, List<JSONObject> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recycler_view, parent, false);
        myprefs = view.getContext().getSharedPreferences("MyPrefs", MODE_PRIVATE);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final JSONObject js = mData.get(position);

        String animal = null;
        String placeIcon = null;
        //final String placeid= null;
        try {
            animal = js.get("name").toString();
            placeIcon= js.get("icon").toString();
            String placeid= js.get("place_id").toString();
            if(myprefs.contains(placeid))
            {
                holder.myPlaceFav.setImageResource(R.drawable.ic_resfav);
                holder.myPlaceFav.setTag("fav");
            }
            //placeid = js.get("place_id").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        holder.myTextView.setText(animal);
        Picasso.get().load(placeIcon).into(holder.myPlaceIcon);
        holder.myPlaceFav.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String placeid = null;

                        try{
                           placeid= js.get("place_id").toString();
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                        if(holder.myPlaceFav.getTag() == "nofav")
                        {
                            SharedPreferences.Editor editor = myprefs.edit();
                            editor.putString(placeid,js.toString());
                            editor.commit();
                            holder.myPlaceFav.setImageResource(R.drawable.ic_resfav);
                            holder.myPlaceFav.setTag("fav");


                        }
                        else
                        {
                            SharedPreferences.Editor editor = myprefs.edit();
                            editor.remove(placeid);
                            editor.commit();
                            holder.myPlaceFav.setImageResource(R.drawable.ic_resnofav);
                            holder.myPlaceFav.setTag("nofav");

                        }

                    }
                }
        );
        //Picasso.get().load(R.drawable.ic_search).into(holder.myPlaceFav);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView;

        ImageView myPlaceIcon;
        ImageView myPlaceFav;
        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.place_name);
            myPlaceIcon = itemView.findViewById(R.id.place_icon);
            myPlaceFav = itemView.findViewById(R.id.place_nofav);

            //itemView.setOnClickListener(this);


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

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}