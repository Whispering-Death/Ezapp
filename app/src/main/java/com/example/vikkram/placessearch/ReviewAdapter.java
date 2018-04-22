package com.example.vikkram.placessearch;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private List<JSONObject> mData;
    private LayoutInflater mInflater;
    private FavAdapter.ItemClickListener mClickListener;

    private boolean isgoogle =true;
    View view;

    ReviewAdapter(Context context, List<JSONObject> data, boolean reviewType) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.isgoogle= reviewType;

    }

    // inflates the row layout from xml when needed
    @Override
    public ReviewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = mInflater.inflate(R.layout.recycler_favorites, parent, false);

        return new ReviewAdapter.ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ReviewAdapter.ViewHolder holder, final int position) {

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
    private void toastMessage(String message)
    {

        Toast t = Toast.makeText(view.getContext(), message, Toast.LENGTH_SHORT);
        //View v1 = t.getView();
        //v1.setBackgroundColor(Integer.parseInt("#cccccc"));
        t.show();

        //Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }
    // allows clicks events to be caught
    void setClickListener(FavAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

}
