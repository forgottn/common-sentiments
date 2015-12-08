package com.cs160.shipwaiver.commonsentiments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class QuestionsSentimentsActivity extends AppCompatActivity {

    QuestionAdapter adapter;
    private ArrayList<ParseObject> mQuestionList = new ArrayList<>();
    private HashMap<String, ParseObject> mSentiments = new HashMap<>();

    public ListView mQuestionListView;
    public String mParseObjectID;

    private ViewSwitcher mViewSwitcher;

    private ImageView mExitEventButton;
    private ImageView mSwitchViewButton;
    private ImageView mSearchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions_sentiments);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getBaseContext(), AddQuestionActivity.class);
                startActivity(i);
            }
        });
        fab.setVisibility(View.GONE);

        Bundle bun = getIntent().getExtras();
        mParseObjectID = bun.getString("objectID");

        adapter = new QuestionAdapter(getApplicationContext(), mQuestionList);

        mExitEventButton = (ImageView) findViewById(R.id.menu_close);
        mExitEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mViewSwitcher = (ViewSwitcher) findViewById(R.id.switch_type);
        final View questionView = mViewSwitcher.findViewById(R.id.question_list);

        mSwitchViewButton = (ImageView) findViewById(R.id.menu_switch);
        mSwitchViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mViewSwitcher.getCurrentView() != questionView) {
                    mViewSwitcher.showPrevious();
                    fab.setVisibility(View.VISIBLE);
                } else {
                    mViewSwitcher.showNext();
                    fab.setVisibility(View.GONE);

                }
            }
        });
        mQuestionListView = (ListView) findViewById(R.id.question_list);
        mQuestionListView.setAdapter(adapter);

        mQuestionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });



        getQuestionListAndSentiments();
    }

    private void getQuestionListAndSentiments() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
        query.whereEqualTo("objectId", mParseObjectID);
        query.include("questions");
        query.include("sentiments");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    List<ParseObject> questionsObjects = objects.get(0).getList("questions");
                    if (questionsObjects.size() > 0) {
                        mQuestionList = new ArrayList<>(questionsObjects);
                        adapter.setListData(mQuestionList);
                    } else {
                        mQuestionList.clear();
                    }
                    adapter.notifyDataSetChanged();

                    List<ParseObject> sentimentObjects = objects.get(0).getList("sentiments");
                    for (int i = 0; i < sentimentObjects.size(); i++) {
                        ParseObject sentiment = sentimentObjects.get(i);
                        mSentiments.put(sentiment.getString("viewId"), sentiment);
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }


    public void onSentimentClicked(View view) {
        ImageView emoClicked = (ImageView) view;
        final String idAsString = view.getResources().getResourceName(view.getId()).split("/")[1];
        LayoutInflater inflater = LayoutInflater.from(this);
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.content_sentiment_clicked, null, false);
        RelativeLayout container = (RelativeLayout) findViewById(R.id.sentiments_container);
        ImageView emo = (ImageView) layout.findViewById(R.id.emo);
        emo.setImageDrawable(emoClicked.getDrawable());


        TextView emoTitle = (TextView) layout.findViewById(R.id.emo_title);
        emoTitle.setText(mSentiments.get(idAsString).getString("name"));

        TextView emoVotes = (TextView) layout.findViewById(R.id.emo_votes);
        emoVotes.setText(String.format("%d Votes", mSentiments.get(idAsString).getInt("upvoteCount")));

        Button backButton = (Button) layout.findViewById(R.id.emo_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RelativeLayout container = (RelativeLayout) findViewById(R.id.sentiments_container);
                RelativeLayout sentimentView = (RelativeLayout) container.findViewById(R.id.clicked_sentiment_view);

                container.removeView(sentimentView);
                container.findViewById(R.id.sentiment_list).setVisibility(View.VISIBLE);
            }
        });

        Button voteButton = (Button) layout.findViewById(R.id.emo_vote_button);
        voteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSentiments.get(idAsString).increment("upvoteCount");
                mSentiments.get(idAsString).saveInBackground();
                TextView emoVotes = (TextView) findViewById(R.id.emo_votes);
                emoVotes.setText(String.format("%d Votes", mSentiments.get(idAsString).getInt("upvoteCount")));
            }
        });

        container.findViewById(R.id.sentiment_list).setVisibility(View.GONE);
        container.addView(layout);

    }
}
