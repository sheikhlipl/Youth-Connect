package com.lipl.youthconnect.youth_connect.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.lipl.youthconnect.youth_connect.R;
import com.lipl.youthconnect.youth_connect.util.FileOption;

import java.util.List;

/**
 * Created by luminousinfoways on 23/12/15.
 */
public class FileUploadArrayAdapter extends ArrayAdapter<FileOption> {

    private Context c;
    private int id;
    private List<FileOption> items;

    public FileUploadArrayAdapter(Context context, int textViewResourceId,
                                  List<FileOption> objects) {
        super(context, textViewResourceId, objects);
        c = context;
        id = textViewResourceId;
        items = objects;
    }

    private class ViewHolder {
        TextView t1;
        CheckBox chkBox;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        Log.v("ConvertView", String.valueOf(position));

        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) c.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.file_upload_list_item, null);

            holder = new ViewHolder();
            holder.t1 = (TextView) convertView.findViewById(R.id.TextView01);
            holder.chkBox = (CheckBox) convertView.findViewById(R.id.chkBox);
            convertView.setTag(holder);

            holder.chkBox.setOnClickListener( new View.OnClickListener() {
                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v ;
                    FileOption option = (FileOption) cb.getTag();
                    option.setIsSelected(cb.isChecked());
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
        holder.chkBox.setChecked(option.isSelected());
        holder.chkBox.setTag(option);

        return convertView;
    }
}