package com.example.vikkram.placessearch;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;


    public class Tab2Fragment extends Fragment {
        private static final String TAG = "Tab2Fragment";
        private static FavAdapter adapter;
        private Button btnTEST;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.tab2_fragment,container,false);
           // btnTEST = (Button) view.findViewById(R.id.btnTEST2);
            /*
            btnTEST.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getActivity(), "TESTING BUTTON CLICK 2",Toast.LENGTH_SHORT).show();
                }
            });*/

            /*
            ArrayList<JSONObject> favorites = new ArrayList<>();
            //SharedPreferences prefs;
            SharedPreferences myPrefs = getContext().getSharedPreferences("MyPrefs", MODE_PRIVATE);

            Map<String, ?> entries = myPrefs.getAll();
            //JSONParser parser = new JSONParser();
            TextView nofav = view.findViewById(R.id.nofavorites);
            boolean isEmpty = true;
            for(Map.Entry<String,?> entry: entries.entrySet())
            {
                Log.d(TAG, "JSON value obtained:" );
                Log.d(TAG, entry.getValue().toString());
                isEmpty= false;
                String jsonData = entry.getValue().toString();

                try {
                    JSONObject js = new JSONObject(jsonData);
                    favorites.add(js);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            if(!isEmpty)
            {
                nofav.setVisibility(View.GONE);
            }
            else
            {
                nofav.setVisibility(View.VISIBLE);
            }
            RecyclerView recyclerView = view.findViewById(R.id.favcontainer);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new FavAdapter(getContext(), favorites);
            //adapter.setClickListener(this);
            recyclerView.setAdapter(adapter);

            //adapter.notifyDataSetChanged();
            */
            return view;
        }

        @Override
        public void onResume() {
            super.onResume();
            SharedPreferences myPrefs = getContext().getSharedPreferences("MyPrefs", MODE_PRIVATE);
            ArrayList<JSONObject> favorites = new ArrayList<>();
            Map<String, ?> entries = myPrefs.getAll();
            //JSONParser parser = new JSONParser();
            TextView nofav = getView().findViewById(R.id.nofavorites);
            boolean isEmpty = true;
            super.onStart();
            for(Map.Entry<String,?> entry: entries.entrySet())
            {
                Log.d(TAG, "JSON value obtained:" );
                Log.d(TAG, entry.getValue().toString());
                isEmpty= false;
                String jsonData = entry.getValue().toString();

                try {
                    JSONObject js = new JSONObject(jsonData);
                    favorites.add(js);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            if(!isEmpty)
            {
                nofav.setVisibility(View.GONE);
            }
            else
            {
                nofav.setVisibility(View.VISIBLE);
            }
            RecyclerView recyclerView = getView().findViewById(R.id.favcontainer);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new FavAdapter(getContext(), favorites);
            //adapter.setClickListener(this);

            //recyclerView.invalidate();
            recyclerView.setAdapter(adapter);
        }

        @Override
        public void onStart() {
            SharedPreferences myPrefs = getContext().getSharedPreferences("MyPrefs", MODE_PRIVATE);
            ArrayList<JSONObject> favorites = new ArrayList<>();
            Map<String, ?> entries = myPrefs.getAll();
            //JSONParser parser = new JSONParser();
            TextView nofav = getView().findViewById(R.id.nofavorites);
            boolean isEmpty = true;
            super.onStart();
            for(Map.Entry<String,?> entry: entries.entrySet())
            {
                Log.d(TAG, "JSON value obtained:" );
                Log.d(TAG, entry.getValue().toString());
                isEmpty= false;
                String jsonData = entry.getValue().toString();

                try {
                    JSONObject js = new JSONObject(jsonData);
                    favorites.add(js);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            if(!isEmpty)
            {
                nofav.setVisibility(View.GONE);
            }
            else
            {
                nofav.setVisibility(View.VISIBLE);
            }
            RecyclerView recyclerView = getView().findViewById(R.id.favcontainer);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new FavAdapter(getContext(), favorites);
            //adapter.setClickListener(this);

            //recyclerView.invalidate();
            recyclerView.setAdapter(adapter);
        }
    }

