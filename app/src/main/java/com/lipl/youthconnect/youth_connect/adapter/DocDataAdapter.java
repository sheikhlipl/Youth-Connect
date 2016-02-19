package com.lipl.youthconnect.youth_connect.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lipl.youthconnect.youth_connect.R;
import com.lipl.youthconnect.youth_connect.activity.FileDetailsActivity;
import com.lipl.youthconnect.youth_connect.activity.QNADetailsActivity;
import com.lipl.youthconnect.youth_connect.pojo.Doc;
import com.lipl.youthconnect.youth_connect.util.Constants;
import com.lipl.youthconnect.youth_connect.pojo.Document;
import com.lipl.youthconnect.youth_connect.pojo.QuestionAndAnswer;
import com.lipl.youthconnect.youth_connect.util.Util;

import java.util.List;
import java.util.Random;

/**
 * Created by luminousinfoways on 29/01/16.
 */
public class DocDataAdapter extends BaseAdapter {

    private List<Doc> dataList;
    private Context context;
    private boolean isFromForum;
    private boolean isFromAnswered;

    public DocDataAdapter(List<Doc> questionAndAnswerList, Context context) {
        this.dataList = questionAndAnswerList;
        this.context = context;
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
            view = LayoutInflater.from(context).inflate(R.layout.list_row_file, null);
        }

        view.setTag(position);

        TextView tvFileTitle = (TextView) view.findViewById(R.id.tvFileTitle);
        TextView tvUserName = (TextView) view.findViewById(R.id.tvUserName);
        TextView tvNumberOfDocuments = (TextView) view.findViewById(R.id.tvNumberOfDocuments);

        if(dataList.get(position) != null
                && dataList.get(position).getDoc_title() != null
                && dataList.get(position).getDoc_title().length() > 0){
            String title = dataList.get(position).getDoc_title().trim();
            tvFileTitle.setText(title);
        }

        if(dataList.get(position).getCreated_by_user_name() != null
                && dataList.get(position).getCreated_by_user_name().length() > 0){
            String name = dataList.get(position).getCreated_by_user_name().trim();
            tvUserName.setText(name);
        }

        if(dataList.get(position).getFileToUploads() != null
                && dataList.get(position).getFileToUploads().size() > 0) {
            String noOfDocs = dataList.get(position).getFileToUploads().size()+"";
            tvNumberOfDocuments.setText(noOfDocs);
        }

        TextView tvTime = (TextView) view.findViewById(R.id.tvTime);
        if(dataList.get(position).getCreated() != null
                && dataList.get(position).getCreated() != null
                && dataList.get(position).getCreated().length() > 0){
            String time = dataList.get(position).getCreated();
            String time_to_show = getTimeToShow(time);
            if(time_to_show != null){
                tvTime.setText(time_to_show);
            } else{
                tvTime.setText(time);
            }
        }

        TextView bgCount = (TextView) view.findViewById(R.id.bgCount);
        int min = 1;
        int max = 5;
        Random r = new Random();
        int i1 = r.nextInt(max - min + 1) + min;
        switch (i1){
            case 1:
                bgCount.setBackgroundResource(R.drawable.circle_blue);
                break;
            case 2:
                bgCount.setBackgroundResource(R.drawable.circle_green);
                break;
            case 3:
                bgCount.setBackgroundResource(R.drawable.circle_red);
                break;
            case 4:
                bgCount.setBackgroundResource(R.drawable.circle_purple);
                break;
            case 5:
                bgCount.setBackgroundResource(R.drawable.circle_yellow);
                break;
            default:
                bgCount.setBackgroundResource(R.drawable.circle_purple);
                break;
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = (Integer) v.getTag();
                Doc document = dataList.get(pos);
                Context context = v.getContext();
                Intent intent = new Intent(context, FileDetailsActivity.class);
                intent.putExtra(Constants.INTENT_KEY_DOCUMENT, document);
                context.startActivity(intent);
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
