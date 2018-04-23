package com.example.vikkram.placessearch;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import com.squareup.picasso.Picasso;
import java.util.Date;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private List<JSONObject> mData;
    private LayoutInflater mInflater;
    private ReviewAdapter.ItemClickListener mClickListener;
    private static final String TAG = "ReviewAdapter";
    private boolean isgoogle =true;
    View view;

    ReviewAdapter(Context context, List<JSONObject> data, boolean reviewType) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        Log.d(TAG, "ReviewAdapter: "+this.mData);
        this.isgoogle= reviewType;

    }

    // inflates the row layout from xml when needed
    @Override
    public ReviewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = mInflater.inflate(R.layout.review_layout, parent, false);

        return new ViewHolder(view);
    }


    public void set(final JSONObject js, String field, TextView text)
    {
        try {

            text.setText(js.get(field).toString());

        } catch (JSONException e) {
           text.setText("No XXX");
        }

    }
    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ReviewAdapter.ViewHolder holder, final int position) {
        final JSONObject js = mData.get(position);
        Log.d(TAG, "onBindViewHolder: ");
        String name= "";
        String url="";
        String date="";
        String photo_url = "";
        String rating="";
        String text = "";

        set(js,"author_name",holder.name);



        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
        //long time =0;
        try {
            Date revData = new Date(js.getLong("time")*1000L);
            Log.d(TAG, "Time: " +simpleDateFormat.format(revData));
            holder.date.setText(simpleDateFormat.format(revData));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        set(js,"rating",holder.rating);
        set(js,"text",holder.review);

        try {
            photo_url = js.getString("profile_photo_url");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(photo_url!="")
        {
            Log.d(TAG, "Photo found ");
            Picasso.get().load(photo_url).into(holder.photo);

        }
        // holder.name.setText("afridi");

        //set(js,"author_name",holder.name);




    }

    // total number of rows
    @Override
    public int getItemCount() {

        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name;
        TextView date;
        TextView rating;
        ImageView photo;
        TextView review;
        ViewHolder(View itemView) {
            super(itemView);
            name= itemView.findViewById(R.id.author_name);
            rating = itemView.findViewById(R.id.rating);
            review=  itemView.findViewById(R.id.review_text);
            date= itemView.findViewById(R.id.date);
            photo= itemView.findViewById(R.id.photo);



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

    void setClickListener(ReviewAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

}
