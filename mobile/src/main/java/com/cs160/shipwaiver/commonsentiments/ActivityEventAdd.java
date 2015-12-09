package com.cs160.shipwaiver.commonsentiments;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import org.joda.time.DateTime;


import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.codetroopers.betterpickers.radialtimepicker.RadialTimePickerDialogFragment;
import com.parse.ParseException;
import com.parse.SaveCallback;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class ActivityEventAdd extends AppCompatActivity
        implements CalendarDatePickerDialogFragment.OnDateSetListener,
        RadialTimePickerDialogFragment.OnTimeSetListener{

    private static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker_name";
    private static final String FRAG_TAG_TIME_PICKER = "timePickerDialogFragment";
    private static final SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("dd", Locale.getDefault());
    private static final SimpleDateFormat YEAR_FORMAT = new SimpleDateFormat("yyyy", Locale.getDefault());
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("h:mm aaa", Locale.getDefault());

    private Calendar mCalendar = Calendar.getInstance(Locale.getDefault());

    private EditText mName;
    private EditText mDescription;
    private EditText mVenue;
    private TextView mStartDate;
    private TextView mStartTime;
    private TextView mEndDate;
    private TextView mEndTime;
    private TextView mSelectedDate;
    private TextView mSelectedTime;
    private String mSelectedRadio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_add);
        mName = (EditText) findViewById(R.id.event_name_edit);
        mDescription = (EditText) findViewById(R.id.description_edit);
        mVenue = (EditText) findViewById(R.id.venue_edit);
        mStartDate = (TextView) findViewById(R.id.start_date);
        mStartTime = (TextView) findViewById(R.id.start_time);
        mEndDate = (TextView) findViewById(R.id.end_date);
        mEndTime = (TextView) findViewById(R.id.end_time);

        mStartDate.setText(dateFormatted());
        mEndDate.setText(dateFormatted());

        mStartTime.setText(timeFormatted(mCalendar));
        mEndTime.setText(timeFormatted(mCalendar));

    }

    public void onButtonClicked(View view) {
        switch (view.getId()) {
            case R.id.cancel_button:
                finish();
                break;
            case R.id.post_button:
                HashMap<String, String> values = new HashMap<>();
                values.put("name", mName.getText().toString());
                values.put("description", mDescription.getText().toString());
                values.put("venue", mVenue.getText().toString());
                values.put("startDate", mStartDate.getText().toString());
                values.put("endDate", mEndDate.getText().toString());
                values.put("startTime", mStartTime.getText().toString());
                values.put("endTime", mEndTime.getText().toString());
                values.put("status", mSelectedRadio);
                SaveCallback saveCallback = new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        finish();
                    }
                };
                Event.add(getBaseContext(), values, saveCallback);
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
                    Log.d("Description", mDescription.getText().toString());
                    mSelectedRadio = "audience";
                    break;
            case R.id.presenter:
                if (checked)
                    mSelectedRadio = "presenter";
                    break;
        }
    }



    public void onDateClicked(View view) {
        FragmentManager fm = getSupportFragmentManager();
        DateTime now = DateTime.now();
        CalendarDatePickerDialogFragment calendarDatePickerDialogFragment = CalendarDatePickerDialogFragment
                .newInstance(ActivityEventAdd.this, now.getYear(), now.getMonthOfYear() - 1,
                        now.getDayOfMonth());
        calendarDatePickerDialogFragment.show(fm, FRAG_TAG_DATE_PICKER);
        mSelectedDate = (TextView) view;
    }

    public void onTimeClicked(View view) {
        DateTime now = DateTime.now();
        RadialTimePickerDialogFragment timePickerDialog = RadialTimePickerDialogFragment
                .newInstance(ActivityEventAdd.this, now.getHourOfDay(), now.getMinuteOfHour(),
                        DateFormat.is24HourFormat(ActivityEventAdd.this));
        timePickerDialog.show(getSupportFragmentManager(), FRAG_TAG_TIME_PICKER);
        mSelectedTime = (TextView) view;
    }

    @Override
    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
        mCalendar.set(year, monthOfYear, dayOfMonth);
        mSelectedDate.setText(dateFormatted());
    }

    @Override
    public void onTimeSet(RadialTimePickerDialogFragment dialog, int hourOfDay, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);

        mSelectedTime.setText(timeFormatted(calendar));
    }

    private String timeFormatted(Calendar calendar) {
        return TIME_FORMAT.format(calendar.getTime());
    }

    private String dateFormatted() {
        return String.format("%s, %s %s, %s", dayOfWeek(), shortMonthName(), dayFormatted(), yearFormatted());
    }

    private String dayOfWeek() {
        return mCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG,
                Locale.getDefault());
    }

    private String shortMonthName() {
        return mCalendar.getDisplayName(Calendar.MONTH, Calendar.SHORT,
                Locale.getDefault());
    }

    private String dayFormatted() {
        return DAY_FORMAT.format(mCalendar.getTime());
    }

    private String yearFormatted() {
        return YEAR_FORMAT.format(mCalendar.getTime());
    }

}
