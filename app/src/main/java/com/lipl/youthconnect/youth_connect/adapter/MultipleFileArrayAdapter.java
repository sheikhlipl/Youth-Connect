package com.lipl.youthconnect.youth_connect.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lipl.youthconnect.youth_connect.R;
import com.lipl.youthconnect.youth_connect.util.FileOption;
import com.lipl.youthconnect.youth_connect.util.YouthConnectSingleTone;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luminousinfoways on 23/12/15.
 */
public class MultipleFileArrayAdapter extends ArrayAdapter<FileOption> {

    private Context c;
    private int id;
    private List<FileOption> items;

    public MultipleFileArrayAdapter(Context context, int textViewResourceId,
                                    List<FileOption> objects) {
        super(context, textViewResourceId, objects);
        c = context;
        id = textViewResourceId;
        items = objects;
    }

    private class ViewHolder {
        TextView t1;
        TextView t2;
        CheckBox chkBox;
        RelativeLayout mainLayout;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        Log.v("ConvertView", String.valueOf(position));

        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) c.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.file_view_mulitile_choice, null);

            holder = new ViewHolder();
            holder.t1 = (TextView) convertView.findViewById(R.id.TextView01);
            holder.t2 = (TextView) convertView.findViewById(R.id.TextView02);
            holder.chkBox = (CheckBox) convertView.findViewById(R.id.chkBox);
            holder.mainLayout = (RelativeLayout) convertView.findViewById(R.id.mainLayout);
            convertView.setTag(holder);

            holder.chkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CheckBox checkBox = (CheckBox) view;
                    FileOption o = (FileOption) checkBox.getTag();
                    o.toggleChecked();

                    if(o.isSelected()){
                        addOrRemoveItemInStorage(o, false);
                    } else{
                        addOrRemoveItemInStorage(o, true);
                    }
                }
            });
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        FileOption option = items.get(position);
        if(option.getData().equalsIgnoreCase("folder")){
            holder.chkBox.setVisibility(View.GONE);
        } else if(option.getData().contains("File Size")){
            holder.chkBox.setVisibility(View.VISIBLE);
        } else{
            holder.chkBox.setVisibility(View.GONE);
        }

        holder.t1.setText(option.getName());
        holder.t2.setText(option.getData());
        holder.chkBox.setChecked(option.isSelected());
        holder.chkBox.setTag(option);

        return convertView;
    }

    private void addOrRemoveItemInStorage(FileOption fileOption, boolean isToRemove){
        if(YouthConnectSingleTone.getInstance().fileOptionList == null) {
            YouthConnectSingleTone.getInstance().fileOptionList = new ArrayList<FileOption>();
        }

        boolean isExist = false;
        int index = -1;
        for(int k = 0; k < YouthConnectSingleTone.getInstance().fileOptionList.size(); k++) {
            String path = YouthConnectSingleTone.getInstance().fileOptionList.get(k).getPath();
            if (fileOption != null) {
                String _path = fileOption.getPath();
                if (_path.equalsIgnoreCase(path)) {
                    isExist = true;
                    index = k;
                    continue;
                }
            }
        }
        if(isExist == true && isToRemove){
            if(index >= 0) {
                YouthConnectSingleTone.getInstance().fileOptionList.remove(index);
            }
        } else if(isExist == false && isToRemove == false) {
            YouthConnectSingleTone.getInstance().fileOptionList.add(fileOption);
        }
    }
}