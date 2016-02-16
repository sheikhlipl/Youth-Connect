package com.lipl.youthconnect.youth_connect.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lipl.youthconnect.youth_connect.R;
import com.lipl.youthconnect.youth_connect.pojo.QuestionAndAnswer;

import java.util.List;

/**
 * Created by luminousinfoways on 29/01/16.
 */
public class SampleAdapter extends BaseAdapter {

    private List<QuestionAndAnswer> dataList;
    private Context context;

    public SampleAdapter(List<QuestionAndAnswer> dataList, Context context){
        this.dataList = dataList;
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
    public View getView(int position, View view, ViewGroup viewGroup) {

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.simple_list_item, null);
        }

        TextView textView = (TextView) view.findViewById(R.id.tvData);
        if(dataList.get(position).getQuestion() != null
                && dataList.get(position).getQuestion().getQa_description() != null) {
            textView.setText(dataList.get(position).getQuestion().getQa_description().trim());
        }

        return view;
    }
}
