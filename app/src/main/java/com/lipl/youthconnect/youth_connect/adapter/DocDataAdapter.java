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
import com.lipl.youthconnect.youth_connect.util.Constants;
import com.lipl.youthconnect.youth_connect.pojo.Document;
import com.lipl.youthconnect.youth_connect.pojo.QuestionAndAnswer;

import java.util.List;

/**
 * Created by luminousinfoways on 29/01/16.
 */
public class DocDataAdapter extends BaseAdapter {

    private List<Document> dataList;
    private Context context;
    private boolean isFromForum;
    private boolean isFromAnswered;

    public DocDataAdapter(List<Document> questionAndAnswerList, Context context) {
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
            view.setTag(dataList.get(position));
        }

        TextView tvFileTitle = (TextView) view.findViewById(R.id.tvFileTitle);
        TextView tvUserName = (TextView) view.findViewById(R.id.tvUserName);
        TextView tvNumberOfDocuments = (TextView) view.findViewById(R.id.tvNumberOfDocuments);

        if(dataList.get(position).getDocumentMaster() != null
                && dataList.get(position).getDocumentMaster().getDocument_title() != null
                && dataList.get(position).getDocumentMaster().getDocument_title().length() > 0){
            String title = dataList.get(position).getDocumentMaster().getDocument_title().trim();
            tvFileTitle.setText(title);
        }

        if(dataList.get(position).getUserFullName() != null
                && dataList.get(position).getUserFullName().length() > 0){
            String name = dataList.get(position).getUserFullName().trim();
            tvUserName.setText(name);
        }

        if(dataList.get(position).getDocumentUploadList() != null
                && dataList.get(position).getDocumentUploadList().size() > 0) {
            String noOfDocs = dataList.get(position).getDocumentUploadList().size()+"";
            tvNumberOfDocuments.setText(noOfDocs);
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Document document = (Document) v.getTag();
                Context context = v.getContext();
                Intent intent = new Intent(context, FileDetailsActivity.class);
                intent.putExtra(Constants.INTENT_KEY_DOCUMENT, document);
                context.startActivity(intent);
            }
        });

        return view;
    }
}
