package com.example.vikkram.placessearch;

import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.IDNA;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URLEncoder;
import java.util.Map;

public class PlacesInfo extends AppCompatActivity {


    private SectionsPageAdapter mSectionsPageAdapter;
    private ViewPager mViewPager;
    private static final String TAG = "PlacesInfo";
    String placename="";
    JSONObject js = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places_info);
        //this.getActionBar().setHomeButtonEnabled(true);
        //this.getActionBar().setDisplayHomeAsUpEnabled(true);
        //getActionBar().setDisplayHomeAsUpEnabled(true);

        String jsonData = getIntent().getStringExtra("placeinfo");

        //JSONObject js=null;
        try {
            js = new JSONObject(jsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject placedetails = null;


        try {
            placedetails = js.getJSONObject("result");
            placename= placedetails.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        String formatted_address="";

        try {
            formatted_address =placedetails.getString("vicinity");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "JSON data received in Places Info: "+formatted_address);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(placename);
        ActionBar ab = getSupportActionBar();
        ab.setHomeButtonEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);
        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.place_tabs);


        //TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);



        //onCreateOptionsMenu(this);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_info_outline);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_photos);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_maps);
        tabLayout.getTabAt(3).setIcon(R.drawable.ic_review);
        //mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        //tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));


    }
    public JSONObject getJSONData()
    {
        return js;
    }
    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        Bundle bundle = new Bundle();
        bundle.putString("placedetails",js.toString());
        InfoFragment tab1 = new InfoFragment();
        PhotoFragment tab2 = new PhotoFragment();
        MapFragment tab3 = new MapFragment();
        ReviewFragment tab4 = new ReviewFragment();
        tab1.setArguments(bundle);
        tab2.setArguments(bundle);
        tab3.setArguments(bundle);
        tab4.setArguments(bundle);

        adapter.addFragment(tab1, "Info");
        adapter.addFragment(tab2, "Photos");
        adapter.addFragment(tab3, "Map");
        adapter.addFragment(tab4, "Reviews");
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(1);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_places_info, menu);
        SharedPreferences myPrefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        Map<String, ?> entries = myPrefs.getAll();
        Log.d(TAG, "onCreateOptionsMenu: ");
        if(entries.containsKey(placename))
        {
            menu.findItem(R.id.action_favorite).setIcon(R.drawable.ic_placefav);
        }

        else
        {
            menu.findItem(R.id.action_favorite).setIcon(R.drawable.ic_placenofav);
        }

        //menu.findItem(R.id.action_favorite).setIcon(R.drawable.ic_placefav)

        //invalidateOptionsMenu();

        

        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //menu.findItem(R.id.penc).setVisible(false);
        //MenuItem fav1 = menu.getItem(R.id.action_favorite);
        Log.d(TAG, "onPrepareOptionsMenu: ");

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            Log.d(TAG, "onOptionsItemSelected: ");
            return true;
        }

        else if(id==R.id.twitter)
        {
            Log.d(TAG, "Selected twiiter item: ");
            String url = null;

            String address = "";
            String tweetText= "Check out ";
            try {
                url = js.getJSONObject("result").getString("website");
                //placename= js.getJSONObject("result").getString("name");
                address = js.getJSONObject("result").getString("formatted_address");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            tweetText+="Check out "+ URLEncoder.encode(placename)+" located at "+URLEncoder.encode(address)+" Website: ";
            String hashTags = "TravelAndEntertainmentSearch";
            //Log.d(TAG, "URL selected: "+url);


            Intent i = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://twitter.com/intent/tweet?text=" + tweetText + "&url=" + url + "&hashtags=" + hashTags));
            startActivity(i);
            //Intent intent = new Intent(getParent(), Uri.parse("https://twitter.com/intent/tweet?text=" + tweetText + "&url=" + url + "&hashtags=" + hashTags));
            //startActivity(intent);
            return true;
        }

        else if(id== R.id.action_favorite)
        {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
