package com.cs160.shipwaiver.commonsentiments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ViewSwitcher;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class QuestionsActivity extends AppCompatActivity {

    QuestionAdapter adapter;
    private ArrayList<ParseObject> mQuestionList = new ArrayList<>();

    public ListView mQuestionListView;
    public String mParseObjectID;

    private ImageView mExitEventButton;
    private ImageView mSwitchViewButton;
    private ImageView mSearchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);

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
        mQuestionListView = (ListView) findViewById(R.id.eventList);
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
