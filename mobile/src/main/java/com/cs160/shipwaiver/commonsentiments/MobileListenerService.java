package com.cs160.shipwaiver.commonsentiments;

import android.app.Service;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MobileListenerService extends WearableListenerService implements
        GoogleApiClient.ConnectionCallbacks {

    private GoogleApiClient mGoogleApiClient;

    private static final String CLICK_SENTIMENT = "/com.cs160.shipwaiver.commonsentiments.click_sentiment";
    private static final String DISPLAY_SENTIMENT = "/com.cs160.shipwaiver.commonsentiments.display_sentiment";


    @Override
    public void onCreate() {
        super.onCreate();

        Log.d("Starting", "Starting Service");

        mGoogleApiClient = new GoogleApiClient.Builder( this )
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i("ConnectionEstablished", "GoogleAPIClient Connected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("ConnectionSuspended", "Location services suspended. Please reconnect.");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startID) {
        mGoogleApiClient.connect();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.disconnect();
    }


    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        if (messageEvent.getPath().equals(CLICK_SENTIMENT)) {
            try {
                JSONObject objectIDs = new JSONObject(new String(messageEvent.getData()));
                String parseObjectID = objectIDs.getString("objectID");
                final String sentimentIDAsString = objectIDs.getString("sentimentIDAsString");
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
                query.whereEqualTo("objectId", parseObjectID);
                query.whereEqualTo("sentiments.viewId", sentimentIDAsString);
                query.include("sentiments.clickedUsers");
                query.getFirstInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject object, ParseException e) {
                        try {
                            JSONObject sentiment = new JSONObject();
                            sentiment.put("name", object.getString("name"));
                            sentiment.put("upvoteCount", object.getInt("upvoteCount"));
                            sentiment.put("clickedUsers", object.getList("clickedUsers"));
                            sentiment.put("sentimentIDAsString", sentimentIDAsString);
                            sendMessage(DISPLAY_SENTIMENT, sentiment.toString());
                        } catch (JSONException err) {
                            err.printStackTrace();
                        }
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

    private void sendMessage( final String path, final String text ) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes( mGoogleApiClient ).await();
                for (Node node : nodes.getNodes() ) {
                    Log.d("Here", "No?");
                    Log.d("SentText", text);
                    MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(
                            mGoogleApiClient, node.getId(), path, text.getBytes() ).await();
                }
            }
        }).start();
    }
}
