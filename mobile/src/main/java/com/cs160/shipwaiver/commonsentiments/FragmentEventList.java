package com.cs160.shipwaiver.commonsentiments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ViewSwitcher;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseException;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class FragmentEventList extends Fragment implements
    GoogleApiClient.ConnectionCallbacks,
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
    private ViewSwitcher mActiveViewSwitcher;

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private static final String JOIN_EVENT = "/com.cs160.shipwaiver.commonsentiments.join_event";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_event_list, container, false);
        mContext = getActivity();
        myLocation = new MyLocation();


        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(10000); // 1 second, in milliseconds

        mGoogleApiClient = new GoogleApiClient.Builder( mContext )
                .addApi(Wearable.API)
                .addApi(LocationServices.API)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .build();

        adapter = new MyAdapter(mContext.getApplicationContext(), mEventList, myLocation);

        mListView = (ListView) rootView.findViewById(R.id.eventList);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final ParseObject entry = (ParseObject) parent.getItemAtPosition(position);
                Log.d("objectID", entry.getObjectId());
                final ViewSwitcher viewSwitcher = (ViewSwitcher) view;
                if (mActiveViewSwitcher != null && mActiveViewSwitcher != viewSwitcher) {
                    mActiveViewSwitcher.showPrevious();
                }

                mActiveViewSwitcher = viewSwitcher;
                View button_row = view.findViewById(R.id.join_or_back);
                viewSwitcher.showNext();
                Button backButton = (Button) button_row.findViewById(R.id.back_button);
                backButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewSwitcher.showPrevious();
                        mActiveViewSwitcher = null;
                    }
                });

                Button joinButton = (Button) button_row.findViewById(R.id.join_button);
                joinButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ParseQuery<ParseUser> query = ParseUser.getQuery();
                        query.include("events");
                        query.include("presenter");
                        query.getInBackground(ParseUser.getCurrentUser().getObjectId(),
                                new GetCallback<ParseUser>() {
                                    @Override
                                    public void done(ParseUser object, ParseException e) {
                                        if (e == null) {
                                            if (!object.getList("events").contains(entry)) {
                                                object.add("events", entry);
                                                entry.increment("numberAttending");
                                                object.saveInBackground();
                                                entry.saveInBackground();
                                            }
                                            Intent i = new Intent(mContext, ActivityQuestionSentimentList.class);
                                            i.putExtra("objectID", entry.getObjectId());
                                            viewSwitcher.showPrevious();
                                            mActiveViewSwitcher = null;
                                            try {
                                                JSONObject event = new JSONObject();
                                                event.put("objectId", entry.getObjectId());
                                                Log.d("FragmentEventList", ParseUser.getCurrentUser().getObjectId());
                                                Log.d("FragmentEventList", entry.getParseUser("presenter").getObjectId());
                                                boolean presenter = entry.getParseUser("presenter") == ParseUser.getCurrentUser();
                                                if (presenter) {
                                                    Log.d("FragmentEventList", "presenter is same");
                                                    Intent service = new Intent(mContext, PresenterNotificationListener.class);
                                                    service.putExtra("objectID", entry.getObjectId());
                                                    mContext.startService(service);
                                                }
                                                event.put("isPresenter", presenter);
                                                sendMessage(JOIN_EVENT, event.toString());
                                            } catch (JSONException err) {
                                                err.printStackTrace();
                                            }
                                            startActivity(i);

                                        } else {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                    }
                });

            }
        });

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mContext, ActivityEventAdd.class);
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
        ParseGeoPoint userLocation = new ParseGeoPoint(myLocation.getLatitude(), myLocation.getLongitude());
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
        query.whereWithinMiles("location", userLocation, 5.0);
        query.whereGreaterThan("endDate", new Date());
        query.addAscendingOrder("startDate");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (e == null) {
                    if (objects.size() > 0) {
                        mEventList = new ArrayList<>(objects);
                        adapter.setListData(mEventList);
                    } else {
                        mEventList.clear();
                    }
                    adapter.notifyDataSetChanged();
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

    private void sendMessage( final String path, final String text ) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("FragmentEventList", "c'monnnn");
                NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes( mGoogleApiClient ).await();
                for (Node node : nodes.getNodes() ) {
                    MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(
                            mGoogleApiClient, node.getId(), path, text.getBytes() ).await();
                }
            }
        }).start();
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
