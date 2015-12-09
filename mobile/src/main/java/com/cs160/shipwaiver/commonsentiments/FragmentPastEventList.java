package com.cs160.shipwaiver.commonsentiments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FragmentPastEventList extends Fragment
    implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleApiClient mGoogleApiClient;

    private Location mLastLocation;
    private LocationRequest mLocationRequest;

    private Context mContext;


    private MyLocation myLocation;
    MyAdapter adapter;
    private ArrayList<ParseObject> mEventList = new ArrayList<>();

    public ListView mListView;

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_past_event_list, container, false);
        mContext = getActivity();
        myLocation = new MyLocation();


        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(10000); // 1 second, in milliseconds

        mGoogleApiClient = new GoogleApiClient.Builder( mContext )
                .addApi(LocationServices.API)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .build();

        adapter = new MyAdapter(mContext.getApplicationContext(), mEventList, myLocation);

        mListView = (ListView) rootView.findViewById(R.id.past_event_list);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final ParseObject entry = (ParseObject) parent.getItemAtPosition(position);
                Log.d("objectID", entry.getObjectId());
                Intent i = new Intent(mContext, ActivityQuestionSentimentList.class);
                i.putExtra("objectID", entry.getObjectId());
                startActivity(i);
            }
        });

        return rootView;
    }

    private void handleNewLocation() {
        myLocation.setLatitudeAndLongitude(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        getEventList();
    }

    private void getEventList() {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.include("events");
        query.getInBackground(ParseUser.getCurrentUser().getObjectId(),
                new GetCallback<ParseUser>() {
                    @Override
                    public void done(ParseUser object, ParseException e) {
                        if (e == null) {
                            if (object.getList("events").size() > 0) {
                                List<ParseObject> pastEvents = object.getList("events");
                                mEventList = new ArrayList<>();
                                for (ParseObject event : pastEvents) {
                                    if (event.getDate("endDate").before(new Date())) {
                                        mEventList.add(event);
                                    }
                                }
                                adapter.setListData(mEventList);
                            } else {
                                mEventList.clear();
                            }
                        } else {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            Log.d("Location", "same");
            handleNewLocation();
        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("ConnectionSuspended", "Location services suspended. Please reconnect.");
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error

                connectionResult.startResolutionForResult((Activity) mContext, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i("Failed", "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        Log.d("Location", "Changed");
        handleNewLocation();
    }

    @Override
    public void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
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