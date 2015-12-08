package com.cs160.shipwaiver.commonsentiments;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.maps.model.LatLng;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by forgottn on 11/19/15.
 */
public class Event {
    public static void add(Context context, SaveCallback eventSaveCallback) {
        String[] sentimentTitles = {"Confused", "Too Fast", "Too Slow", "Excited", "Thumbs Up", "Thumbs Down", "Sleepy", "Sad", "Angry"};
        String[] sentimentViewIds = {"emo_confused", "emo_toofast", "emo_slow", "emo_excited", "emo_thumbsu", "emo_thumbsd", "emo_sleepy", "emo_sad", "emo_angry"};

        ArrayList<ParseObject> sentiments = new ArrayList<ParseObject>();
        for (int i = 0; i < sentimentTitles.length; i++) {
            ParseObject sentiment = new ParseObject("Sentiment");
            sentiment.put("name", sentimentTitles[i]);
            sentiment.put("viewId", sentimentViewIds[i]);
            sentiment.put("upvoteCount", 0);
            sentiments.add(sentiment);
        }

        ParseObject event = new ParseObject("Event");
        event.put("name", "Tech Talk");
        event.put("date", new Date());
        event.put("startDate", new Date());
        event.put("endDate", new Date());
        event.put("description", "some random tech talk");
        LatLng latlng = getLocationFromAddress(context, "2404 Fulton St. Berkeley, CA 94704");
        ParseGeoPoint point = new ParseGeoPoint(latlng.latitude, latlng.longitude);
        event.put("location", point);
        event.put("numberAttending", 0);
        event.put("questions", new ArrayList<ParseObject>());
        event.put("sentiments", sentiments);
        event.saveInBackground(eventSaveCallback);
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
}
