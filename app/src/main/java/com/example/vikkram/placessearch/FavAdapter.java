package com.example.vikkram.placessearch;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.Image;
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

public class FavAdapter extends RecyclerView.Adapter<FavAdapter.ViewHolder>{

    private List<JSONObject> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    SharedPreferences myprefs;
    // data is passed into the constructor
    FavAdapter(Context context, List<JSONObject> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recycler_favorites, parent, false);
        myprefs = view.getContext().getSharedPreferences("MyPrefs", MODE_PRIVATE);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final JSONObject js = mData.get(position);

        //String animal = mData.get(position);
        String place_name = null;
        String place_icon = null;

        try {
            place_name = js.get("name").toString();
            place_icon = js.get("icon").toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(place_name!= null)
         holder.myName.setText(place_name);

        Picasso.get().load(place_icon).into(holder.myIcon);
        holder.myDel.setImageResource(R.drawable.ic_resfav);

        holder.myDel.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String placeid = null;
                        try {
                            placeid = js.get("place_id").toString();
                            SharedPreferences.Editor editor = myprefs.edit();
                            editor.remove(placeid);
                            mData.remove(position);

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
        ViewHolder(View itemView) {
            super(itemView);
            myName = itemView.findViewById(R.id.fav_name);
            myIcon = itemView.findViewById(R.id.fav_icon);
            myDel = itemView.findViewById(R.id.fav_del);


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

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
