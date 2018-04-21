package com.example.vikkram.placessearch;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONObject;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<FavAdapter.ViewHolder>{
    private List<JSONObject> mData;


    private LayoutInflater mInflater;
    private ReviewAdapter.ItemClickListener mClickListener;
    View view;

    ReviewAdapter(Context context, List<JSONObject> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    @NonNull
    @Override
    public FavAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull FavAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
