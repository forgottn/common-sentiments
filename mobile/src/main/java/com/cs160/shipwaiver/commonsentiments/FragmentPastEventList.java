package com.cs160.shipwaiver.commonsentiments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FragmentPastEventList extends Fragment {

    private Context mContext;
    private PastEventAdapter pastEventAdapter;
    private ArrayList<ParseObject> mPastEventList = new ArrayList<>();

    public ListView mPastListView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_past_event_list, container, false);
        mContext = getActivity();

        pastEventAdapter = new PastEventAdapter(mContext.getApplicationContext(), mPastEventList);

        mPastListView = (ListView) rootView.findViewById(R.id.past_event_list);
        mPastListView.setAdapter(pastEventAdapter);

        mPastListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final ParseObject entry = (ParseObject) parent.getItemAtPosition(position);
                Log.d("objectID", entry.getObjectId());
                Intent i = new Intent(mContext, ActivityQuestionSentimentList.class);
                i.putExtra("objectID", entry.getObjectId());
                startActivity(i);
            }
        });

        getEventList();

        return rootView;
    }

    private void getEventList() {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.include("events");
        query.addAscendingOrder("startDate");
        query.getInBackground(ParseUser.getCurrentUser().getObjectId(),
                new GetCallback<ParseUser>() {
                    @Override
                    public void done(ParseUser object, ParseException e) {
                        if (e == null) {
                            if (object.getList("events").size() > 0) {
                                List<ParseObject> pastEvents = object.getList("events");
                                mPastEventList = new ArrayList<>();
                                for (ParseObject event : pastEvents) {
                                    if (event.getDate("endDate").compareTo(new Date()) < 0) {
                                        mPastEventList.add(event);
                                    }
                                }
                                pastEventAdapter.setListData(mPastEventList);
                            } else {
                                mPastEventList.clear();
                            }
                            pastEventAdapter.notifyDataSetChanged();
                        } else {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
