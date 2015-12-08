package com.cs160.shipwaiver.commonsentiments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ViewSwitcher;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class QuestionsSentimentsActivity extends AppCompatActivity {

    QuestionAdapter adapter;
    private ArrayList<ParseObject> mQuestionList = new ArrayList<>();

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
        final View firstView = mViewSwitcher.findViewById(R.id.question_list);
        View secondView = mViewSwitcher.findViewById(R.id.sentiment_list);

        mSwitchViewButton = (ImageView) findViewById(R.id.menu_switch);
        mSwitchViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mViewSwitcher.getCurrentView() != firstView) {
                    mViewSwitcher.showPrevious();
                } else {
                    mViewSwitcher.showNext();
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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getBaseContext(), AddQuestionActivity.class);
                startActivity(i);
            }
        });

        getQuestionList();
    }

    private void getQuestionList() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
        query.whereEqualTo("objectId", mParseObjectID);
        query.include("questions");
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
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

}
