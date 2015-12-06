package com.cs160.shipwaiver.commonsentiments;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

import java.lang.Double;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by forgottn on 10/15/15.
 */
public class MyAdapter extends BaseAdapter {

    private ArrayList<ParseObject> eventList = new ArrayList<>();
    private Context context;
    private MyLocation myLocation;

    public MyAdapter(Context context, ArrayList<ParseObject> eventList, MyLocation myLocation) {
        super();
        this.eventList = eventList;
        this.context = context;
        this.myLocation = myLocation;
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
                inflate(R.layout.row_layout, parent, false);

        TextView text1 = (TextView) rowView.findViewById(R.id.name);
        TextView text2 = (TextView) rowView.findViewById(R.id.attending);
        TextView text3 = (TextView) rowView.findViewById(R.id.desc);
        TextView text4 = (TextView) rowView.findViewById(R.id.distance);
        TextView text5 = (TextView) rowView.findViewById(R.id.time);

        ParseObject event = eventList.get(position);
        text1.setText(event.getString("name"));
        text2.setText(String.format("%d Attendees", event.getInt("numberAttending")));
        text3.setText(event.getString("description"));

        ParseGeoPoint point = (ParseGeoPoint) event.get("location");
        Double distance = getDistanceBetweenLatitudeAndLongitude(point.getLongitude(), point.getLatitude(), myLocation.getLatitude(), myLocation.getLongitude());
        text4.setText(String.format("%.1f Miles", distance));

        Date startDate = event.getDate("startDate");
        Date endDate = event.getDate("endDate");
        String startTime = dateToTime(startDate);
        startTime = startTime.substring(0, startTime.length() - 3);
        String timeRange = startTime + " - " + dateToTime(endDate);
        text5.setText(timeRange);

        return rowView;
    }

    private String dateToTime(Date date) {
        DateFormat formatter = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.US);
        return formatter.format(date);
    }

    private Double getDistanceBetweenLatitudeAndLongitude( Double eLongitude, Double eLatitude, Double locLatitude, Double locLongitude) {
        Double earthRadius = 6371000.00;
        Double dLatitude = Math.toRadians(eLatitude - locLatitude);
        Double dLongitude = Math.toRadians(eLongitude - locLongitude);
        Double a = Math.sin(dLatitude / 2) * Math.sin(dLatitude / 2) +
                Math.cos(Math.toRadians(locLatitude)) * Math.cos(Math.toRadians(eLatitude)) *
                        Math.sin(dLongitude / 2) * Math.sin(dLongitude / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        Double distInKm = (earthRadius * c) / 1000.00;
        return distInKm * 0.621371;
    }

}
