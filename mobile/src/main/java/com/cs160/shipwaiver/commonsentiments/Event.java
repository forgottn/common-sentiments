package com.cs160.shipwaiver.commonsentiments;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.maps.model.LatLng;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Event {
    public static void add(Context context, HashMap<String, String> values, final SaveCallback saveCallback) {
        String[] sentimentTitles = {"Confused", "Too Fast", "Too Slow", "Excited", "Thumbs Up", "Thumbs Down", "Sleepy", "Sad", "Angry"};
        String[] sentimentViewIds = {"emo_confused", "emo_toofast", "emo_slow", "emo_excited", "emo_thumbsu", "emo_thumbsd", "emo_sleepy", "emo_sad", "emo_angry"};

        ArrayList<ParseObject> sentiments = new ArrayList<ParseObject>();
        for (int i = 0; i < sentimentTitles.length; i++) {
            ParseObject sentiment = new ParseObject("Sentiment");
            sentiment.put("name", sentimentTitles[i]);
            sentiment.put("viewId", sentimentViewIds[i]);
            sentiment.put("upvoteCount", 0);
            sentiment.put("clickedUsers", new ArrayList<ParseUser>());
            sentiments.add(sentiment);
        }

        final ParseObject event = new ParseObject("Event");
        event.put("name", values.get("name"));
        event.put("startDate", getDateFromString(values.get("startDate") + " " + values.get("startTime")));
        event.put("endDate", getDateFromString(values.get("endDate") + " " + values.get("endTime")));
        event.put("description", values.get("description"));
        LatLng latlng = getLocationFromAddress(context, values.get("venue"));
        ParseGeoPoint point = new ParseGeoPoint(latlng.latitude, latlng.longitude);
        event.put("location", point);
        event.put("questions", new ArrayList<ParseObject>());
        event.put("sentiments", sentiments);
        event.put("numberAttending", 0);
        if (values.get("status").equals("presenter")) {
            event.put("presenter", ParseUser.getCurrentUser());
            event.put("numberAttending", 1);
            event.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        ParseUser.getCurrentUser().add("events", event);
                        ParseUser.getCurrentUser().saveInBackground(saveCallback);
                    } else {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            event.saveInBackground(saveCallback);
        }
    }

    public static LatLng getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (Exception ex) {

            ex.printStackTrace();
        }

        return p1;
    }

    private static Date getDateFromString(String date) {
        SimpleDateFormat format = new SimpleDateFormat("EEEE, MMM dd, yyyy h:mm aaa", Locale.getDefault());
        try {
            return format.parse(date);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
