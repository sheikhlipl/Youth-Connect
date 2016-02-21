package com.lipl.youthconnect.youth_connect.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.couchbase.lite.QueryRow;
import com.couchbase.lite.SavedRevision;
import com.lipl.youthconnect.youth_connect.R;
import com.lipl.youthconnect.youth_connect.util.DatabaseUtil;

import java.util.List;

/**
 * Created by Android Luminous on 2/21/2016.
 */
public class QASyncArrayAdapter extends ArrayAdapter<QueryRow> {

    private List<QueryRow> list;
    private final Context context;

    public QASyncArrayAdapter(Context context, int resource, int textViewResourceId, List<QueryRow> objects) {
        super(context, resource, textViewResourceId, objects);
        this.context = context;
    }

    private static class ViewHolder {
        TextView tvName;
        TextView tvTime;
        TextView tvQusByUserName;
        TextView tvNumberOfComments;
    }

    @Override
    public View getView(int position, View itemView, ViewGroup parent) {

        if (itemView == null) {
            LayoutInflater vi = (LayoutInflater)parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            itemView = vi.inflate(R.layout.list_row, null);
            ViewHolder vh = new ViewHolder();
            vh.tvName = (TextView) itemView.findViewById(R.id.tvName);
            vh.tvTime = (TextView) itemView.findViewById(R.id.tvTime);
            vh.tvQusByUserName = (TextView) itemView.findViewById(R.id.tvQusByUserName);
            vh.tvNumberOfComments = (TextView) itemView.findViewById(R.id.tvNumberOfComments);
            itemView.setTag(vh);
        }

        try {
            QueryRow row = getItem(position);
            SavedRevision currentRevision = row.getDocument().getCurrentRevision();

            String title = (String) currentRevision.getProperty(DatabaseUtil.QA_TITLE);
            TextView label = ((ViewHolder)itemView.getTag()).tvName;
            label.setText(title);

            String time = (String) currentRevision.getProperty(DatabaseUtil.QA_UPDATED_TIMESTAMP);
            TextView dateAndTime = ((ViewHolder)itemView.getTag()).tvTime;
            dateAndTime.setText(time);

            String created_by = (String) currentRevision.getProperty(DatabaseUtil.QA_ASKED_BY_USER_NAME);
            TextView user_name = ((ViewHolder)itemView.getTag()).tvQusByUserName;
            user_name.setText(created_by);

            Object comments =  currentRevision.getProperty(DatabaseUtil.QA_COMMENT);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return itemView;
    }
}
