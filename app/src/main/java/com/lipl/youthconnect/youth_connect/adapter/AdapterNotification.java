package com.lipl.youthconnect.youth_connect.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lipl.youthconnect.youth_connect.R;
import com.lipl.youthconnect.youth_connect.pojo.Notification;

import java.util.List;

/**
 * Created by Suhasini on 02.03.15.
 */
public class AdapterNotification extends BaseAdapter {

    private Context context;
    private List<Notification> notificationList;

    public AdapterNotification(Context context, List<Notification> notificationList) {
        this.context = context;
        this.notificationList = notificationList;
    }

    @Override
    public int getCount() {
        return notificationList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return notificationList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_notification, null);
        }

        TextView tvQuestion = (TextView) convertView.findViewById(R.id.tvQuestion);
        //ImageView imgNewRelease = (ImageView) convertView.findViewById(R.id.imgNewRelease);

        String senderName = notificationList.get(position).getUser().getFull_name();
        tvQuestion.setText(senderName + " " + notificationList.get(position).getNotification());
        if(notificationList.get(position).getIsNew() == 1){
            //imgNewRelease.setVisibility(View.VISIBLE);
            tvQuestion.setTypeface(Typeface.DEFAULT_BOLD);
        } else{
            //imgNewRelease.setVisibility(View.INVISIBLE);
            tvQuestion.setTypeface(Typeface.DEFAULT);
        }

        TextView tvTime = (TextView) convertView.findViewById(R.id.tvTime);
        String dateTime = notificationList.get(position).getCreated();
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
                } else{
                    tvTime.setText(dateTime);
                }
            }
        } catch(Exception e){
            Log.e("DataAdapter", "onBindViewHolder()", e);
        }

        return convertView;
    }
}