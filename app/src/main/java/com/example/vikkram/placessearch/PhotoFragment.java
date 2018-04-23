package com.example.vikkram.placessearch;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PhotoFragment extends android.support.v4.app.Fragment{

    private static final String TAG = "PhotoFragment";
    private GeoDataClient geoDataClient;
    private int currentPhotoIndex = 0;
    private List<PlacePhotoMetadata> photosDataList;
    private static PhotoAdapter adapter;

    View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.photo_adapter, container, false);


        geoDataClient = Places.getGeoDataClient(getContext(),null);
        JSONObject json= ((PlacesInfo)this.getActivity()).js;
        photosDataList = new ArrayList<>();
        try {
            String placeid=json.getJSONObject("result").getString("place_id");
            getPhotoMetadata(placeid);

        } catch (JSONException e) {
            e.printStackTrace();
        }

       // Log.d(TAG, "Place metadata: "+photosDataList.size());
        RecyclerView recyclerView = view.findViewById(R.id.photo_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PhotoAdapter(getContext(), photosDataList);
        //adapter.setClickListener(this);

        //recyclerView.invalidate();
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


    }

    private void getPhotoMetadata(String placeId) {

        final Task<PlacePhotoMetadataResponse> photoResponse =
                geoDataClient.getPlacePhotos(placeId);

        photoResponse.addOnCompleteListener
                (new OnCompleteListener<PlacePhotoMetadataResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
                        currentPhotoIndex = 0;

                        PlacePhotoMetadataResponse photos = task.getResult();
                        PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();

                        Log.d(TAG, "number of photos "+photoMetadataBuffer.getCount());

                        for(PlacePhotoMetadata photoMetadata : photoMetadataBuffer){
                            photosDataList.add(photoMetadataBuffer.get(0).freeze());
                            Log.d(TAG, "added ");
                        }

                        photoMetadataBuffer.release();
                        //displayPhoto();
                    }
                });
        //Log.d(TAG, "Place metadata: "+photosDataList.size());
    }

}
