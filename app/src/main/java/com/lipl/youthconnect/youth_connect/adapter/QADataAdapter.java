package com.lipl.youthconnect.youth_connect.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lipl.youthconnect.youth_connect.R;
import com.lipl.youthconnect.youth_connect.activity.QNADetailsActivity;
import com.lipl.youthconnect.youth_connect.util.Constants;
import com.lipl.youthconnect.youth_connect.util.YouthConnectSingleTone;
import com.lipl.youthconnect.youth_connect.pojo.QuestionAndAnswer;

import java.util.List;

/**
 * Created by luminousinfoways on 29/01/16.
 */
public class QADataAdapter extends BaseAdapter {

    private List<QuestionAndAnswer> dataList;
    private Context context;
    private boolean isFromForum;
    private boolean isFromAnswered;

    public QADataAdapter(List<QuestionAndAnswer> questionAndAnswerList, Context context,
                         boolean isFromForum, boolean isFromAnswered){
        this.dataList = questionAndAnswerList;
        this.context = context;
        this.isFromForum = isFromForum;
        this.isFromAnswered = isFromAnswered;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int i) {
        return dataList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.list_row, null);
        }

        TextView tvName = (TextView) view.findViewById(R.id.tvFileTitle);
        TextView tvTime = (TextView) view.findViewById(R.id.tvTime);
        TextView tvQusByUserName = (TextView) view.findViewById(R.id.tvQusByUserName);
        TextView tvNumberOfComments = (TextView) view.findViewById(R.id.tvNumberOfComments);

        if(dataList.get(position).getQuestion() != null
                && dataList.get(position).getQuestion().getQa_title() != null
                && dataList.get(position).getQuestion().getQa_title().length() > 0){
            String title = dataList.get(position).getQuestion().getQa_title();
            tvName.setText(title);
        }

        if(dataList.get(position).getQuestion() != null
                && dataList.get(position).getQuestion().getPost_date() != null
                && dataList.get(position).getQuestion().getPost_date().length() > 0){
            String time = dataList.get(position).getQuestion().getPost_date();
            tvTime.setText(time);
        }

        if(dataList.get(position) != null
                && dataList.get(position).getQuestion() != null
                && dataList.get(position).getQuestion().getAskedBy() != null) {
            String username = dataList.get(position).getQuestion().getAskedBy();
            tvQusByUserName.setText(username);
        }

        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, QNADetailsActivity.class);
                intent.putExtra(Constants.INTENT_KEY_QUESTION_AND_ANSWER, dataList.get(position));
                if (isFromForum) {
                    intent.putExtra(Constants.INTENT_KEY_IS_FROM_FORUM, true);
                    intent.putExtra(Constants.INTENT_KEY_IS_FROM_ANSWERED, false);
                    YouthConnectSingleTone.getInstance().CURRENT_FRAGMENT_IN_QA = Constants.FRAGMENT_QA_SUB_FRAGMENT_FORUM;
                } else if (isFromAnswered) {
                    intent.putExtra(Constants.INTENT_KEY_IS_FROM_FORUM, false);
                    intent.putExtra(Constants.INTENT_KEY_IS_FROM_ANSWERED, true);
                    YouthConnectSingleTone.getInstance().CURRENT_FRAGMENT_IN_QA = Constants.FRAGMENT_QA_SUB_FRAGMENT_ANSWERED;
                } else {
                    intent.putExtra(Constants.INTENT_KEY_IS_FROM_FORUM, false);
                    intent.putExtra(Constants.INTENT_KEY_IS_FROM_ANSWERED, false);
                    YouthConnectSingleTone.getInstance().CURRENT_FRAGMENT_IN_QA = Constants.FRAGMENT_QA_SUB_FRAGMENT_PENDING;
                }
                context.startActivity(intent);
            }
        });

        return view;
    }
}
