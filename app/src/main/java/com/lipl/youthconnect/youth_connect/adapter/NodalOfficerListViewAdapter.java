package com.lipl.youthconnect.youth_connect.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.lipl.youthconnect.youth_connect.R;
import com.lipl.youthconnect.youth_connect.activity.FileUploadNodalOfficerActivity;
import com.lipl.youthconnect.youth_connect.pojo.NodalUser;
import com.lipl.youthconnect.youth_connect.pojo.User;

import java.util.List;

/**
 * Created by luminousinfoways on 29/01/16.
 */
public class NodalOfficerListViewAdapter extends BaseAdapter {

    private List<NodalUser> userList;
    private Context context;
    boolean checkAll_flag = false;
    boolean checkItem_flag = false;

    public NodalOfficerListViewAdapter(List<NodalUser> userList, Context context){
        this.userList = userList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public Object getItem(int i) {
        return userList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    static class ViewHolder {
        //protected TextView text;
        protected CheckBox checkbox;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;
        if (convertView == null) {
            LayoutInflater inflator = LayoutInflater.from(context);
            convertView = inflator.inflate(R.layout.list_row_dist, null);
            viewHolder = new ViewHolder();
            //viewHolder.text = (TextView) convertView.findViewById(R.id.label);
            viewHolder.checkbox = (CheckBox) convertView.findViewById(R.id.tvDist);
            viewHolder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int getPosition = (Integer) buttonView.getTag();  // Here we get the position that we have set for the checkbox using setTag.
                    userList.get(getPosition).setIs_selected(buttonView.isChecked()); // Set the value of checkbox to maintain its state.
                }
            });
            convertView.setTag(viewHolder);
            //convertView.setTag(R.id.label, viewHolder.text);
            convertView.setTag(R.id.tvDist, viewHolder.checkbox);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.checkbox.setTag(position); // This line is important.

        //viewHolder.text.setText(list.get(position).getName());
        viewHolder.checkbox.setChecked(userList.get(position).is_selected());
        if(userList.get(position) != null &&
                userList.get(position).getFull_name() != null) {
            viewHolder.checkbox.setText(userList.get(position).getFull_name());
        }

        return convertView;
    }

    /*@Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.list_row_dist, null);
        }

        CheckBox checkBox = (CheckBox) view.findViewById(R.id.tvDist);
        if(userList.get(position) != null &&
                userList.get(position).getFull_name() != null) {
            checkBox.setText(userList.get(position).getFull_name());
        }
        checkBox.setTag(userList.get(position));

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                try {
                    NodalUser user = (NodalUser) compoundButton.getTag();
                    if (b) {
                        boolean isExist = false;
                        for (int i = 0; i < FileUploadNodalOfficerActivity.selectedNodalOfficers.size(); i++) {
                            int userid = FileUploadNodalOfficerActivity.selectedNodalOfficers.get(i).getUser_id();
                            if (user != null && user.getUser_id() > 0 && user.getUser_id() == userid) {
                                isExist = true;
                            }
                        }
                        if (isExist == false) {
                            FileUploadNodalOfficerActivity.selectedNodalOfficers.add(user);
                        }
                    } else {
                        boolean isExist = false;
                        int pos = -1;
                        for (int i = 0; i < FileUploadNodalOfficerActivity.selectedNodalOfficers.size(); i++) {
                            int userid = FileUploadNodalOfficerActivity.selectedNodalOfficers.get(i).getUser_id();
                            if (user != null && user.getUser_id() > 0 && user.getUser_id() == userid) {
                                isExist = true;
                                pos = i;
                            }
                        }
                        if (isExist == true && pos >= 0) {
                            FileUploadNodalOfficerActivity.selectedNodalOfficers.remove(pos);
                        }
                    }
                } catch (ClassCastException exception) {
                    Log.e("NodalOfficerAdapter", "Error", exception);
                } catch (Exception exception) {
                    Log.e("NodalOfficerAdapter", "Error", exception);
                }
            }
        });

        return view;
    }*/
}
