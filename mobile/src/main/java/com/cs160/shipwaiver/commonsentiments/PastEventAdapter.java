package com.cs160.shipwaiver.commonsentiments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.parse.ParseObject;

import java.util.ArrayList;

public class PastEventAdapter extends BaseAdapter {
    private ArrayList<ParseObject> eventList = new ArrayList<>();
    private Context context;

    public PastEventAdapter(Context context, ArrayList<ParseObject> eventList) {
        super();
        this.eventList = eventList;
        this.context = context;
    }

    public void setListData(ArrayList<ParseObject> eventList) {
        this.eventList = eventList;
    }

    @Override
    public int getCount() {
        return eventList.size();
    }

    @Override
    public Object getItem(int position) {
        return eventList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = LayoutInflater.from(context).
                inflate(R.layout.component_past_event_row, parent, false);

        TextView text1 = (TextView) rowView.findViewById(R.id.name);
        TextView text2 = (TextView) rowView.findViewById(R.id.desc);

        ParseObject event = eventList.get(position);
        text1.setText(event.getString("name"));
        text2.setText(event.getString("description"));

        return rowView;
    }
}
