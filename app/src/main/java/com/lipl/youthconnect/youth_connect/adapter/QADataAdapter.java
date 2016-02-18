package com.lipl.youthconnect.youth_connect.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lipl.youthconnect.youth_connect.R;
import com.lipl.youthconnect.youth_connect.activity.QNADetailsActivity;
import com.lipl.youthconnect.youth_connect.util.Constants;
import com.lipl.youthconnect.youth_connect.util.Util;
import com.lipl.youthconnect.youth_connect.util.YouthConnectSingleTone;
import com.lipl.youthconnect.youth_connect.pojo.QuestionAndAnswer;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

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

        int min = 1;
        int max = 5;
        Random r = new Random();
        int i1 = r.nextInt(max - min + 1) + min;
        switch (i1){
            case 1:
                tvNumberOfComments.setBackgroundResource(R.drawable.circle_blue);
                break;
            case 2:
                tvNumberOfComments.setBackgroundResource(R.drawable.circle_green);
                break;
            case 3:
                tvNumberOfComments.setBackgroundResource(R.drawable.circle_red);
                break;
            case 4:
                tvNumberOfComments.setBackgroundResource(R.drawable.circle_purple);
                break;
            case 5:
                tvNumberOfComments.setBackgroundResource(R.drawable.circle_yellow);
                break;
            default:
                tvNumberOfComments.setBackgroundResource(R.drawable.circle_purple);
                break;
        }

        if(dataList != null &&
                dataList.get(position) != null &&
                dataList.get(position).getQuestion() != null &&
                dataList.get(position).getQuestion().getAskedBy() != null
                && dataList.get(position).getQuestion().getAskedBy().trim().length() > 0) {
            String user_name = dataList.get(position).getQuestion().getAskedBy();
            String first_two_characters_of_name = user_name.substring(0, 2).toUpperCase();
            tvNumberOfComments.setText(first_two_characters_of_name);
        } else{
            tvNumberOfComments.setVisibility(View.GONE);
        }

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
            String time_to_show = getTimeToShow(time);
            if(time_to_show != null){
                tvTime.setText(time_to_show);
            } else{
                tvTime.setText(time);
            }
        }

        if(dataList.get(position) != null
                && dataList.get(position).getQuestion() != null
                && dataList.get(position).getQuestion().getAskedBy() != null) {
            String username = dataList.get(position).getQuestion().getAskedBy();
            tvQusByUserName.setText(username);
        }

        ImageView imgStatus = (ImageView) view.findViewById(R.id.imgStatus);
        if(isFromForum){
            imgStatus.setImageResource(R.drawable.ic_done_white);
        } else if(isFromAnswered){
            if(dataList != null && dataList.size() > 0 && dataList.get(position) != null
                    && dataList.get(position).getAnswerList() != null
                    && dataList.get(position).getAnswerList().get(0) != null
                    && dataList.get(position).getAnswerList().get(0).getIs_uploaded() == 1){
                imgStatus.setImageResource(R.drawable.ic_done_white);
            } else{
                imgStatus.setImageResource(R.drawable.ic_watch_later);
            }
        } else {
            if(dataList != null && dataList.size() > 0 && dataList.get(position) != null
                    && dataList.get(position).getQuestion() != null
                    && dataList.get(position).getQuestion().getIs_uploaded() == 1){
                imgStatus.setImageResource(R.drawable.ic_done_white);
            } else{
                imgStatus.setImageResource(R.drawable.ic_watch_later);
            }
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

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });

        return view;
    }

    private String getTimeToShow(String time){
        String currentDate = Util.getCurrentDateTime();
        if(time != null && time.trim().length() > 0 && time.trim().contains(" ")){
            String[] arr = time.trim().split(" ");
            for(int i = 0; i < arr.length; i++){
                String date = arr[0];
                if(date != null &&
                    date.length() > 0 &&
                    currentDate != null &&
                    currentDate.trim().length() > 0 &&
                    date.trim().equalsIgnoreCase(currentDate.trim())){
                    return arr[1] + " " + arr[2];
                } else if(date != null &&
                        date.length() > 0){
                    String[] crdate = date.split("-");
                    String mon = crdate[1];
                    String dd = crdate[0];
                    String monName = Util.getMonInWord(mon);
                    String to_return = monName + " " + dd;
                    return to_return;
                }
            }
        }
        return null;
    }
}
