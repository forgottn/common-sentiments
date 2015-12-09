package com.cs160.shipwaiver.commonsentiments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ActivityQuestionAdd extends AppCompatActivity {

    private EditText mQuestionText;
    private String mParseObjectID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_add);
        mQuestionText = (EditText) findViewById(R.id.question_edit);

        Bundle bun = getIntent().getExtras();
        mParseObjectID = bun.getString("objectID");
    }

    public void onButtonClicked(View view) {
        switch (view.getId()) {
            case R.id.cancel_button:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle("Are you sure you want to cancel?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }

                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
                break;
            case R.id.post_button:
                if (mQuestionText.getText().length() > 0) {
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
                    query.whereEqualTo("objectId", mParseObjectID);
                    query.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> objects, ParseException e) {
                            ParseObject event = objects.get(0);
                            ParseObject question = new ParseObject("Question");
                            question.put("question", mQuestionText.getText().toString());
                            question.put("upvoteCount", 1);
                            question.put("flagCount", 0);
                            question.put("clickedUpvoteUsers", Collections.singletonList(ParseUser.getCurrentUser()));
                            question.put("clickedFlagUsers", new ArrayList<ParseUser>());
                            question.saveInBackground();
                            event.add("questions", question);
                            event.saveInBackground();
                        }
                    });
                    finish();
                } else {
                    // Show some error message
                }
                break;
            default:
                Log.d("QuestionAdd", "Button clicked, but not added in list");
                break;
        }
    }

}
