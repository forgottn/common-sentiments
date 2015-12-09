package com.cs160.shipwaiver.commonsentiments;

import android.content.IntentSender;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.support.wearable.view.CardScrollView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SentimentDetailActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener  {

    private TextView mEmoVoteCountTextView;
    private TextView mEmoNameTextView;
    private ImageView mEmoImageView;
    private Button mVoteButton;
    private Button mBackButton;

    private GoogleApiClient mGoogleApiClient;

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private static final String CLICK_VOTE = "/com.cs160.shipwaiver.commonsentiments.click_vote";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sentiment_detail);

        mEmoNameTextView = (TextView) findViewById(R.id.watch_emo_name);
        mEmoVoteCountTextView = (TextView) findViewById(R.id.watch_emo_vote_count);
        mEmoImageView = (ImageView) findViewById(R.id.watch_emo_image);
        mVoteButton = (Button) findViewById(R.id.watch_vote);

        mGoogleApiClient = new GoogleApiClient.Builder( this )
                .addApi(Wearable.API)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .build();

        Bundle bun = getIntent().getExtras();
        String sentimentIDAsString = bun.getString("sentimentIDAsString");
        String name = bun.getString("name");
        final String objectId = bun.getString("sentimentObjectId");
        int upvoteCount = bun.getInt("upvoteCount");
        int id = getResources().getIdentifier(sentimentIDAsString, "drawable", getPackageName());
        mEmoImageView.setImageResource(id);
        mEmoNameTextView.setText(name);
        mEmoVoteCountTextView.setText(String.format("%d Votes", upvoteCount));

        mVoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("SentimentDetail", "Hi hi");
                sendMessage(CLICK_VOTE, objectId);
            }
        });
        mBackButton = (Button) findViewById(R.id.watch_back);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


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
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error

                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i("Failed", "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    public void onPause() {
        super.onPause();
        mGoogleApiClient.disconnect();
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
                finish();
            }
        }).start();
    }

}
