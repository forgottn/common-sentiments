package com.cs160.shipwaiver.commonsentiments;


import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
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
                if (!object.getBoolean("isPresenter")) {
                    startActivity(i);
                }
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
        } else if (messageEvent.getPath().equalsIgnoreCase(SENTIMENT_NOTIFICATION) || messageEvent.getPath().equalsIgnoreCase(QUESTION_NOTIFICATION)) {
            try {
                Log.d("WatchListener", "Sentiment Notification");
                JSONObject notification = new JSONObject(new String(messageEvent.getData()));
                String contentTitle = String.format("%.0f%% audiences", notification.getDouble("audiencePercent"));
                String notificationText = notification.getString("content");
                // Build notificaiton and display
                Bitmap bitmap = Bitmap.createBitmap(320, 320, Bitmap.Config.ARGB_8888);
                bitmap.eraseColor(Color.parseColor("#3283B2"));

                NotificationCompat.Builder notificationBuilder =
                        new NotificationCompat.Builder(this)
                                .setContentTitle(contentTitle)
                                .setContentText(notificationText)
                                .setDefaults(Notification.DEFAULT_ALL)
                                .setSmallIcon(R.drawable.logo_small)
                                .extend(new NotificationCompat.WearableExtender().setBackground(bitmap));


                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                notificationManager.notify(0, notificationBuilder.build());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            super.onMessageReceived( messageEvent );
        }
    }
}
