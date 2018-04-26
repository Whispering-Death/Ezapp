package com.example.vikkram.placessearch;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONObject;

import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {

    private List<PlacePhotoMetadata> mData;
    private LayoutInflater mInflater;
    private GeoDataClient geoDataClient;
    private PhotoAdapter.ItemClickListener mClickListener;
    private static final String TAG = "PhotoAdapter";
    View view;
    SharedPreferences myprefs;
    // data is passed into the constructor
    PhotoAdapter(Context context, List<PlacePhotoMetadata> data, PhotoFragment fragment) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        Log.d(TAG, "PhotoAdapter: "+this.mData.size());
        if(data.isEmpty())
        {
            fragment.checkEmpty();
        }

    }

    @NonNull
    @Override
    public PhotoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        geoDataClient = Places.getGeoDataClient(parent.getContext(),null);
        //view = mInflater.inflate(R.layout.photo_fragment, parent, false);
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.photo_fragment, null);

        return  new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoAdapter.ViewHolder holder, int position) {

        getPhoto(holder, mData, position);


        Log.d(TAG, "onBindViewHolder:"+"set data");

    }

    public void getPhoto(final PhotoAdapter.ViewHolder holder, List<PlacePhotoMetadata> mData, int pos)
    {
        Task<PlacePhotoResponse> photoResponse = geoDataClient.getPhoto(mData.get(pos));
        photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                PlacePhotoResponse photo = task.getResult();
                Bitmap photoBitmap = photo.getBitmap();
                Log.d(TAG, "photo "+photo.toString());

                //placeImage.invalidate();
                holder.myPhoto.setImageBitmap(photoBitmap);
            }
        });
    }
    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView myPhoto;

        ViewHolder(View itemView) {
            super(itemView);

            myPhoto= itemView.findViewById(R.id.photos);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    /*JSONObject getItem(int id) {
        return mData.get(id);
    }*/
    private void toastMessage(String message)
    {

        Toast t = Toast.makeText(view.getContext(), message, Toast.LENGTH_SHORT);
        //View v1 = t.getView();
        //v1.setBackgroundColor(Integer.parseInt("#cccccc"));
        t.show();

        //Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }
    // allows clicks events to be caught
    void setClickListener(PhotoAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
