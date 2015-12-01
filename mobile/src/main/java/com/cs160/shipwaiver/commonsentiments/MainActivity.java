package com.cs160.shipwaiver.commonsentiments;

import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;


import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private GoogleApiClient mGoogleApiClient;

    private Location mLastLocation;

    private MyLocation myLocation;
    MyAdapter adapter;
    private ArrayList<ParseObject> mEventList = new ArrayList<>();

    public ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        myLocation = new MyLocation();

        mGoogleApiClient = new GoogleApiClient.Builder( this )
            .addApi(LocationServices.API)
            .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                @Override
                public void onConnected(Bundle bundle) {
                    mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                    if (mLastLocation != null) {
                        Log.d("Wtf", "wtf");
                        myLocation.setLatitudeAndLongitude(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                        getEventList();
                    }
                }

                @Override
                public void onConnectionSuspended(int i) {

                }
            }).build();

        adapter = new MyAdapter(getApplicationContext(), mEventList, myLocation);

        mListView = (ListView) findViewById(R.id.eventList);
        mListView.setAdapter(adapter);



    }

    private void getEventList() {
        Parse.enableLocalDatastore(getApplicationContext());
        Parse.initialize(getApplicationContext(), "065cF714ZPQ4iqQu0y6f8YhPtUIFcdGztqRGDPW4", "KVVK6d2LPoE3XB9xmqwubyUOe2jddgAI138WE58F");
        ParseGeoPoint userLocation = new ParseGeoPoint(myLocation.getLatitude(), myLocation.getLongitude());
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
        query.whereWithinMiles("location", userLocation, 5.0);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (e == null) {
                    Log.d("pls", objects.get(0).getString("name"));
                    mEventList = new ArrayList<>(objects);
                    adapter.setListData(mEventList);
                    adapter.notifyDataSetChanged();
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
