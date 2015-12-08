package com.cs160.shipwaiver.commonsentiments;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

public class AddQuestionActivity extends AppCompatActivity {

    private EditText mQuestionText;
    private String mParseObjectID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question);
        mQuestionText = (EditText) findViewById(R.id.question_edit);

        Bundle bun = getIntent().getExtras();
        mParseObjectID = bun.getString("objectID");
    }

    public void onButtonClicked(View view) {
        switch (view.getId()) {
            case R.id.cancel_button:
                finish();
                break;
            case R.id.post_button:
                if (mQuestionText.getText().length() > 0) {
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
                    query.whereEqualTo("objectId", mParseObjectID);
                    query.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> objects, ParseException e) {
                            ParseObject event = objects.get(0);
                            List<ParseObject> questions = event.getList("questions");
                            ParseObject question = new ParseObject("Question");
                            question.put("question", mQuestionText.getText().toString());
                            question.put("upvoteCount", 1);
                            question.put("flagCount", 0);
                            question.saveInBackground();
                            questions.add(question);
                            event.put("questions", questions);
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
