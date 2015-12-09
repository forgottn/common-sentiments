package com.cs160.shipwaiver.commonsentiments;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

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
                inflate(R.layout.component_question_row, parent, false);

        TextView text1 = (TextView) rowView.findViewById(R.id.question_text);
        final TextView text2 = (TextView) rowView.findViewById(R.id.upvote_count);

        final ParseObject question = questionList.get(position);
        text1.setText(question.getString("question"));
        text2.setText(String.format("%d", question.getInt("upvoteCount")));

        List<ParseUser> upvoteUsersList = question.getList("clickedUpvoteUsers");
        List<ParseUser> flagUsersList = question.getList("clickedFlagUsers");
        final ArrayList<ParseUser> upvoteUsers = new ArrayList<>(upvoteUsersList);
        final ArrayList<ParseUser> flagUsers = new ArrayList<>(flagUsersList);

        final boolean clickedUpvote = upvoteUsers.contains(ParseUser.getCurrentUser());
        final boolean clickedFlag = flagUsers.contains(ParseUser.getCurrentUser());

        if (clickedUpvote) {
            text2.setTextColor(Color.parseColor("#5CBFEA"));
        }

        ImageView upvoteArrow = (ImageView) rowView.findViewById(R.id.arrow);
        upvoteArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickedUpvote) {
                    // Already upvoted
                } else {
                    text2.setTextColor(Color.parseColor("#5CBFEA"));
                    question.increment("upvoteCount");
                    upvoteUsers.add(ParseUser.getCurrentUser());
                    question.put("clickedUpvoteUsers", upvoteUsers);
                    question.saveInBackground();
                    text2.setText(String.format("%d", question.getInt("upvoteCount")));
                }
            }
        });

        final ImageView flag = (ImageView) rowView.findViewById(R.id.flag);
        if (clickedFlag) {
            flag.setImageDrawable(context.getDrawable(R.drawable.flag_clicked));
        }

        flag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickedFlag) {
                    // Already upvoted
                } else {
                    flag.setImageDrawable(context.getDrawable(R.drawable.flag_clicked));
                    question.increment("flagCount");
                    flagUsers.add(ParseUser.getCurrentUser());
                    question.put("clickedFlagUsers", flagUsers);
                    if (question.getInt("flagCount") >= 3) {
                        question.deleteInBackground();
                    } else {
                        question.saveInBackground();
                    }
                }
            }
        });

        return rowView;
    }

}
