package com.cs160.shipwaiver.commonsentiments;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MobileListenerService extends WearableListenerService implements
        GoogleApiClient.ConnectionCallbacks {

    private GoogleApiClient mGoogleApiClient;

    private static final String CLICK_SENTIMENT = "/com.cs160.shipwaiver.commonsentiments.click_sentiment";
    private static final String DISPLAY_SENTIMENT = "/com.cs160.shipwaiver.commonsentiments.display_sentiment";
    private static final String CLICK_VOTE = "/com.cs160.shipwaiver.commonsentiments.click_vote";

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d("Starting", "Starting Service");

        mGoogleApiClient = new GoogleApiClient.Builder( this )
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .build();
        mGoogleApiClient.connect();
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
                query.include("sentiments");
                query.include("sentiments.clickedUsers");
                query.getFirstInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject object, ParseException e) {
                        if (e == null) {
                            try {
                                List<ParseObject> sentimentsList = object.getList("sentiments");
                                ArrayList<ParseObject> sentiments = new ArrayList<ParseObject>(sentimentsList);
                                for (ParseObject sentimentObject : sentiments) {
                                    if (sentimentObject.getString("viewId").equals(sentimentIDAsString)) {
                                        Log.d("MobileListener", "Got here");
                                        JSONObject sentiment = new JSONObject();
                                        sentiment.put("name", sentimentObject.getString("name"));
                                        sentiment.put("upvoteCount", sentimentObject.getInt("upvoteCount"));
                                        JSONArray clickedUsersArray = sentimentObject.getJSONArray("clickedUsers");
                                        sentiment.put("clickedUsers", clickedUsersArray);
                                        sentiment.put("sentimentIDAsString", sentimentIDAsString);
                                        sentiment.put("sentimentObjectId", sentimentObject.getObjectId());
                                        sendMessage(DISPLAY_SENTIMENT, sentiment.toString());
                                        break;
                                    }
                                }
                            } catch (JSONException err) {
                                err.printStackTrace();
                            }
                        } else {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (messageEvent.getPath().equals(CLICK_VOTE)) {
            String sentimentObjectId = new String(messageEvent.getData());
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Sentiment");
            query.whereEqualTo("objectId", sentimentObjectId);
            query.include("clickedUsers");
            Log.d("SentimentObject", sentimentObjectId);
            query.getFirstInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject object, ParseException e) {
                    Log.d("MobileListenr", "C'mon");
                    if (!object.getList("clickedUsers").contains(ParseUser.getCurrentUser())) {
                        object.increment("upvoteCount");
                        object.add("clickedUsers", ParseUser.getCurrentUser());
                        object.saveInBackground();
                    }
                }
            });
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
