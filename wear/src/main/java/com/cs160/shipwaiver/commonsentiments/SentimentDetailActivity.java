package com.cs160.shipwaiver.commonsentiments;

import android.os.Bundle;
import android.app.Activity;
import android.support.wearable.view.CardScrollView;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SentimentDetailActivity extends Activity {

    private TextView mEmoVoteCountTextView;
    private TextView mEmoNameTextView;
    private ImageView mEmoImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sentiment_detail);

        CardScrollView cardScrollView =
                (CardScrollView) findViewById(R.id.card_scroll_view);
        cardScrollView.setCardGravity(Gravity.BOTTOM);

        mEmoNameTextView = (TextView) findViewById(R.id.watch_emo_name);
        mEmoVoteCountTextView = (TextView) findViewById(R.id.watch_emo_vote_count);
        mEmoImageView = (ImageView) findViewById(R.id.watch_emo_image);

        try {
            Bundle bun = getIntent().getExtras();
            String sentimentIDAsString = bun.getString("sentimentIDAsString");
            String name = bun.getString("name");
            int upvoteCount = bun.getInt("upvoteCount");
            JSONArray clickedUsers = new JSONArray(bun.getString("clickedUsers"));
            int id = getResources().getIdentifier(sentimentIDAsString, "id", getPackageName());
            mEmoImageView.setImageResource(id);
            mEmoNameTextView.setText(name);
            mEmoVoteCountTextView.setText(String.format("%d Votes", upvoteCount));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
