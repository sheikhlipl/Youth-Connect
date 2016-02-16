package com.lipl.youthconnect.youth_connect.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lipl.youthconnect.youth_connect.R;
import com.lipl.youthconnect.youth_connect.activity.QNADetailsActivity;
import com.lipl.youthconnect.youth_connect.util.Constants;
import com.lipl.youthconnect.youth_connect.pojo.QuestionAndAnswer;

import java.util.List;

/**
 * Created by luminousinfoways on 29/01/16.
 */
public class SampleAdapterForQA extends BaseAdapter {

    private List<QuestionAndAnswer> questionAndAnswerList;
    private Context context;
    private boolean isFromForum;
    private boolean isFromAnswered;

    public SampleAdapterForQA(List<QuestionAndAnswer> questionAndAnswerList, Context context,
                              boolean isFromForum, boolean isFromAnswered){
        this.questionAndAnswerList = questionAndAnswerList;
        this.context = context;
        this.isFromAnswered = isFromAnswered;
        this.isFromForum = isFromForum;
    }

    @Override
    public int getCount() {
        return questionAndAnswerList.size();
    }

    @Override
    public Object getItem(int i) {
        return questionAndAnswerList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.list_row, null);
            view.setTag(position);
        }

        TextView tvName = (TextView) view.findViewById(R.id.tvFileTitle);
        TextView tvTime = (TextView) view.findViewById(R.id.tvTime);
        TextView tvQusByUserName = (TextView) view.findViewById(R.id.tvQusByUserName);
        TextView tvNumberOfComments = (TextView) view.findViewById(R.id.tvNumberOfComments);

        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                int position = (Integer) v.getTag();
                QuestionAndAnswer questionAndAnswer = questionAndAnswerList.get(position);

                Context context = v.getContext();
                Intent intent = new Intent(context, QNADetailsActivity.class);
                intent.putExtra(Constants.INTENT_KEY_QUESTION_AND_ANSWER, questionAndAnswer);
                if (isFromForum) {
                    intent.putExtra(Constants.INTENT_KEY_IS_FROM_FORUM, true);
                    intent.putExtra(Constants.INTENT_KEY_IS_FROM_ANSWERED, false);
                } else if (isFromAnswered) {
                    intent.putExtra(Constants.INTENT_KEY_IS_FROM_FORUM, false);
                    intent.putExtra(Constants.INTENT_KEY_IS_FROM_ANSWERED, true);
                } else {
                    intent.putExtra(Constants.INTENT_KEY_IS_FROM_FORUM, false);
                    intent.putExtra(Constants.INTENT_KEY_IS_FROM_ANSWERED, false);
                }
                context.startActivity(intent);
            }
        });

        QuestionAndAnswer singleStudent= (QuestionAndAnswer) questionAndAnswerList.get(position);
        tvName.setText(singleStudent.getQuestion().getQa_title());

        String dateTime = singleStudent.getQuestion().getPost_date();
        try {
            if (dateTime != null && dateTime.length() > 0) {
                //Format 2015-12-24 11:16:44
                if (dateTime.contains(" ")) {
                    String[] dt = dateTime.split(" ");
                    String date = dt[0];
                    String time = dt[1];

                    if (date != null && date.length() > 0 && date.contains("-")) {
                        String[] dd = date.split("-");
                        String year = dd[0];
                        String month = dd[1];
                        String day = dd[2];

                        String _date_time = day + "-" + month + "-" + year + " " + time;
                        tvTime.setText(_date_time);
                    }
                }
            }
        } catch(Exception e){
            Log.e("DataAdapter", "onBindViewHolder()", e);
        }

        if(singleStudent != null && singleStudent.getUser() != null) {
            tvQusByUserName.setText(singleStudent.getUser().getFull_name());
        }

        if(singleStudent != null && singleStudent.getCommentList() != null && singleStudent.getCommentList().size() > 0) {
            tvNumberOfComments.setText(singleStudent.getCommentList().size() + "");
        }

        return view;
    }
}
