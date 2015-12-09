package com.cs160.shipwaiver.commonsentiments;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PresenterNotificationListener extends Service implements
    GoogleApiClient.ConnectionCallbacks {


    private static final int INTERVAL = 30000;
    private static final int SECOND = 1000;
    private static final double THRESHOLD = 0.1;
    private GoogleApiClient mGoogleApiClient;

    private String mObjectID;

    private JSONObject sentNotification;
    private JSONObject sentQuestionNotification;

    private static final String SENTIMENT_NOTIFICATION = "/com.cs160.shipwaiver.commonsentiments.sentiment_notification";
    private static final String QUESTION_NOTIFICATION = "/com.cs160.shipwaiver.commonsentiments.question_notification";


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
    public int onStartCommand(Intent intent, int flags, int startID) {
        mObjectID = intent.getExtras().getString("objectID");
        createAndStartTimer();
        return START_STICKY;
    }

    private void createAndStartTimer() {
        CountDownTimer timer = new CountDownTimer(INTERVAL, SECOND) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("PresenterNotification", "Should get here");
                        final ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
                        query.whereEqualTo("objectId", mObjectID);
                        query.include("sentiments");
                        query.include("questions");
                        query.getFirstInBackground(new GetCallback<ParseObject>() {
                            @Override
                            public void done(ParseObject object, ParseException e) {
                                double numberAttending = object.getDouble("numberAttending");
                                List<ParseObject> sentimentsList = object.getList("sentiments");
                                ArrayList<ParseObject> sentiments = new ArrayList<>(sentimentsList);
                                try {
                                    JSONObject topNotification = null;
                                    double maxUpvote = 0.0;
                                    for (ParseObject sentiment : sentiments) {
                                        double upvoteCount = sentiment.getDouble("upvoteCount");
                                        double audiencePercent = upvoteCount / numberAttending * 100;
                                        if (upvoteCount / numberAttending >= THRESHOLD &&
                                                upvoteCount > maxUpvote) {
                                            Log.d("PresenterNotfication", "Got here");
                                            JSONObject notification = new JSONObject();
                                            notification.put("audiencePercent", audiencePercent);
                                            notification.put("content", sentiment.get("name"));
                                            maxUpvote = upvoteCount;
                                            topNotification = notification;
                                            // Send a small notification to watch
//                                        sendMessage();
                                        }
                                    }
                                    if (topNotification != null && (sentNotification == null ||
                                            sentNotification.getDouble("audiencePercent") != topNotification.getDouble("audiencePercent")
                                            || !sentNotification.getString("content").equals(topNotification.getString("content")))) {
                                        sentNotification = topNotification;
                                        sendMessage(SENTIMENT_NOTIFICATION, topNotification.toString());
                                    }
                                } catch (JSONException err) {
                                    err.printStackTrace();
                                }

                                List<ParseObject> questionsList = object.getList("questions");
                                ArrayList<ParseObject> questions = new ArrayList<>(questionsList);

                                try {
                                    JSONObject topQuestionNotification = null;
                                    double maxQuestionUpvote = 0.0;
                                    for (ParseObject question : questions) {
                                        double upvoteCount = question.getDouble("upvoteCount");
                                        double audiencePercent = upvoteCount / numberAttending * 100;
                                        if (upvoteCount / numberAttending >= THRESHOLD &&
                                                upvoteCount > maxQuestionUpvote) {
                                            Log.d("PresenterNotfication", "Got here");
                                            JSONObject notification = new JSONObject();
                                            notification.put("audiencePercent", audiencePercent);
                                            notification.put("content", question.get("question"));
                                            maxQuestionUpvote = upvoteCount;
                                            topQuestionNotification = notification;
                                        }
                                    }

                                    if ((topQuestionNotification != null && questions.size() > 0) && (sentQuestionNotification == null
                                    || sentQuestionNotification.getDouble("audiencePercent") != topQuestionNotification.getDouble("audiencePercent")
                                            || !sentQuestionNotification.getString("content").equals(topQuestionNotification.getString("content")))) {
                                        sentQuestionNotification = topQuestionNotification;
                                        sendMessage(QUESTION_NOTIFICATION, topQuestionNotification.toString());
                                    }
                                } catch (JSONException err) {
                                    err.printStackTrace();
                                }
                            }
                        });
                    }
                }).start();

                createAndStartTimer();
            }
        };

        timer.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("DESTROY", "PLS");
        mGoogleApiClient.disconnect();
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
    public IBinder onBind(Intent intent) {
        return null;
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
