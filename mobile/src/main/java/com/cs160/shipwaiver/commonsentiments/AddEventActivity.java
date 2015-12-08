package com.cs160.shipwaiver.commonsentiments;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

public class AddEventActivity extends AppCompatActivity {

    EditText name;
    EditText description;
    EditText venue;
    EditText startTime;
    EditText endTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
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
                Log.d("AddEvent", "Button clicked, but not added in list");
                break;
        }
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.audience:
                if (checked)
                    // Pirates are the best
                    break;
            case R.id.presenter:
                if (checked)
                    // Ninjas rule
                    break;
        }
    }

}
