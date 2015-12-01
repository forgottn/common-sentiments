package com.cs160.shipwaiver.commonsentiments;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.maps.model.LatLng;
import com.parse.Parse;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by forgottn on 11/19/15.
 */
public class Event {
    public void add(Context context) {
        String[] sentimentTitles = {"Confused", "Too Fast", "Too Slow", "Excited", "Thumbs Up", "Thumbs Down", "Sleepy", "Sad", "Angry"};

        Parse.enableLocalDatastore(context);

        Parse.initialize(context, "065cF714ZPQ4iqQu0y6f8YhPtUIFcdGztqRGDPW4", "KVVK6d2LPoE3XB9xmqwubyUOe2jddgAI138WE58F");
        ArrayList<ParseObject> sentiments = new ArrayList<ParseObject>();
        for (String sentimentTitle : sentimentTitles) {
            ParseObject sentiment = new ParseObject("Sentiment");
            sentiment.put("name", sentimentTitle);
            sentiment.put("upvoteCount", 0);
            sentiments.add(sentiment);
        }

        ParseObject event = new ParseObject("Event");
        event.put("name", "Tech Talk");
        event.put("date", new Date());
        event.put("description", "some random tech talk");
        LatLng latlng = getLocationFromAddress(context, "2404 Fulton St. Berkeley, CA 94704");
        ParseGeoPoint point = new ParseGeoPoint(latlng.latitude, latlng.longitude);
        event.put("location", point);
        event.put("numberAttending", 0);
        event.put("questions", new ArrayList<ParseObject>());
        event.put("sentiments", sentiments);
        event.saveInBackground();
    }

    public LatLng getLocationFromAddress(Context context, String strAddress) {

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
