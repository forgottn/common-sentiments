package com.cs160.shipwaiver.commonsentiments;

import android.app.Application;

import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;

/**
 * Created by forgottn on 12/1/15.
 */
public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "065cF714ZPQ4iqQu0y6f8YhPtUIFcdGztqRGDPW4", "KVVK6d2LPoE3XB9xmqwubyUOe2jddgAI138WE58F");

        ParseUser.enableAutomaticUser();
        ParseUser.getCurrentUser().fetchIfNeededInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    if (object.getList("events") == null) {
                        ParseUser.getCurrentUser().put("events", new ArrayList<ParseObject>());
                        ParseUser.getCurrentUser().saveInBackground();
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
        ParseUser.getCurrentUser().saveInBackground();
    }
}
