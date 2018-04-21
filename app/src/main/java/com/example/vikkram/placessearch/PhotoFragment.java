package com.example.vikkram.placessearch;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PhotoFragment extends android.support.v4.app.Fragment{

    private static final String TAG = "PhotoFragment";
    View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.photo_fragment, container, false);
        return view;
    }
}
