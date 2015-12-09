package com.cs160.shipwaiver.commonsentiments;


import android.content.Intent;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import org.json.JSONException;
import org.json.JSONObject;


public class WatchListenerService extends WearableListenerService {
    private static final String JOIN_EVENT = "/com.cs160.shipwaiver.commonsentiments.join_event";
    private static final String DISPLAY_SENTIMENT = "/com.cs160.shipwaiver.commonsentiments.display_sentiment";
    final String SENTIMENT_NOTIFICATION = "/com.cs160.shipwaiver.commonsentiments.sentiment_notification";
    private static final String QUESTION_NOTIFICATION = "/com.cs160.shipwaiver.commonsentiments.question_notification";


    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        if (messageEvent.getPath().equalsIgnoreCase(JOIN_EVENT)) {
            try {
                JSONObject object = new JSONObject(new String(messageEvent.getData()));
                String objectID = object.getString("objectId");

                Intent i = new Intent(this, SentimentsActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                i.putExtra("objectID", objectID);
                startActivity(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else if (messageEvent.getPath().equalsIgnoreCase(DISPLAY_SENTIMENT)) {
            try {
                Log.d("WatchListender", "DISPLAY SENTIMENT");
                JSONObject sentiment = new JSONObject(new String(messageEvent.getData()));

                Intent i = new Intent(this, SentimentDetailActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("name", sentiment.getString("name"));
                i.putExtra("upvoteCount", sentiment.getInt("upvoteCount"));
                i.putExtra("clickedUsers", sentiment.getJSONArray("clickedUsers").toString());
                i.putExtra("sentimentIDAsString", sentiment.getString("sentimentIDAsString"));
                startActivity(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (messageEvent.getPath().equalsIgnoreCase(SENTIMENT_NOTIFICATION)) {
            // Build notificaiton and display

        } else if (messageEvent.getPath().equalsIgnoreCase(QUESTION_NOTIFICATION)) {
            // Build notification and display

        } else {
            super.onMessageReceived( messageEvent );
        }
    }
}
