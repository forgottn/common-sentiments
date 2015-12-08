package com.cs160.shipwaiver.commonsentiments;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class AddQuestionActivity extends AppCompatActivity {

    private Button cancelButton;
    private Button postButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question);

    }

    public void onButtonClicked(View view) {
        switch (view.getId()) {
            case R.id.cancel_button:
                finish();
                break;
            case R.id.post_button:
                // save the stuff
                break;
            default:
                Log.d("QuestionAdd", "Button clicked, but not added in list");
                break;
        }
    }

}
