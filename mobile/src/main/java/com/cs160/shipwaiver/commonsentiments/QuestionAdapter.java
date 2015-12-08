package com.cs160.shipwaiver.commonsentiments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by forgottn on 12/7/15.
 */
public class QuestionAdapter extends BaseAdapter {

    private ArrayList<ParseObject> questionList = new ArrayList<>();
    private Context context;

    public QuestionAdapter(Context context, ArrayList<ParseObject> questionList) {
        super();
        this.questionList = questionList;
        this.context = context;
    }

    public void setListData(ArrayList<ParseObject> eventList) {
        this.questionList = eventList;
    }

    @Override
    public int getCount() {
        return questionList.size();
    }

    @Override
    public Object getItem(int position) {
        return questionList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = LayoutInflater.from(context).
                inflate(R.layout.question_row_layout, parent, false);

        TextView text1 = (TextView) rowView.findViewById(R.id.question_text);
        TextView text2 = (TextView) rowView.findViewById(R.id.upvote_count);

        ParseObject event = questionList.get(position);
        text1.setText(event.getString("question"));
        text2.setText(String.format("%d", event.getInt("upvoteCount")));

        return rowView;
    }

}
