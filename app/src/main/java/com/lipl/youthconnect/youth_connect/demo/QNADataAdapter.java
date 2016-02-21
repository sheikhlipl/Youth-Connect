package com.lipl.youthconnect.youth_connect.demo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Document;
import com.couchbase.lite.LiveQuery;
import com.couchbase.lite.replicator.Replication;
import com.lipl.youthconnect.youth_connect.R;
import com.lipl.youthconnect.youth_connect.activity.EditAnswerActivity;
import com.lipl.youthconnect.youth_connect.activity.EditQuestionActivity;
import com.lipl.youthconnect.youth_connect.activity.QNADetailsActivity;
import com.lipl.youthconnect.youth_connect.pojo.QuestionAndAnswer;
import com.lipl.youthconnect.youth_connect.util.Constants;
import com.lipl.youthconnect.youth_connect.util.DatabaseUtil;
import com.lipl.youthconnect.youth_connect.util.QAUtil;
import com.lipl.youthconnect.youth_connect.util.Util;
import com.lipl.youthconnect.youth_connect.util.YouthConnectSingleTone;

import java.io.IOException;
import java.util.List;
import java.util.Random;

/**
 * Created by luminousinfoways on 29/01/16.
 */
public class QNADataAdapter extends LiveQueryAdapter implements Replication.ChangeListener {

    private Context context;
    private static final String TAG = "QADataAdapter";
    private boolean isFromForum = false;
    private boolean isFromAnswered = false;

    public QNADataAdapter(Context context,
                          LiveQuery query){
        super(context, query);
        this.context = context;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.list_row, null);
        }

        final Document questionAnswer = (Document) getItem(position);
        final QuestionAndAnswer questionAndAnswer = QAUtil.getQAFromDocument(questionAnswer);

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

        if(questionAndAnswer != null &&
                questionAndAnswer.getQuestion() != null &&
                questionAndAnswer.getQuestion().getAskedBy() != null
                && questionAndAnswer.getQuestion().getAskedBy().trim().length() > 0) {
            String user_name = questionAndAnswer.getQuestion().getAskedBy();
            String first_two_characters_of_name = user_name.substring(0, 2).toUpperCase();
            tvNumberOfComments.setText(first_two_characters_of_name);
        } else{
            tvNumberOfComments.setVisibility(View.GONE);
        }

        if(questionAndAnswer.getQuestion() != null
                && questionAndAnswer.getQuestion().getQa_title() != null
                && questionAndAnswer.getQuestion().getQa_title().length() > 0){
            String title = questionAndAnswer.getQuestion().getQa_title();
            tvName.setText(title);
        }

        if(questionAndAnswer.getQuestion() != null
                && questionAndAnswer.getQuestion().getPost_date() != null
                && questionAndAnswer.getQuestion().getPost_date().length() > 0){
            String time = questionAndAnswer.getQuestion().getPost_date();
            String time_to_show = getTimeToShow(time);
            if(time_to_show != null){
                tvTime.setText(time_to_show);
            } else{
                tvTime.setText(time);
            }
        }

        if(questionAndAnswer != null
                && questionAndAnswer.getQuestion() != null
                && questionAndAnswer.getQuestion().getAskedBy() != null) {
            String username = questionAndAnswer.getQuestion().getAskedBy();
            tvQusByUserName.setText(username);
        }

        ImageView imgStatus = (ImageView) view.findViewById(R.id.imgStatus);
        if(isFromForum){
            imgStatus.setImageResource(R.drawable.ic_done_white);
        } else if(isFromAnswered){
            if(questionAndAnswer != null
                    && questionAndAnswer.getAnswerList() != null
                    && questionAndAnswer.getAnswerList().size() > 0
                    && questionAndAnswer.getAnswerList().get(0) != null
                    && questionAndAnswer.getAnswerList().get(0).getIs_uploaded() == 1){
                imgStatus.setImageResource(R.drawable.ic_done_white);
            } else{
                imgStatus.setImageResource(R.drawable.ic_watch_later);
            }
        } else {
            if(questionAndAnswer != null
                    && questionAndAnswer.getQuestion() != null
                    && questionAndAnswer.getQuestion().getIs_uploaded() == 1){
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
                intent.putExtra(Constants.INTENT_KEY_QUESTION_AND_ANSWER, questionAndAnswer);
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

        final LinearLayout layoutEditAndDelete = (LinearLayout) view.findViewById(R.id.layoutEditAndDelete);
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                layoutEditAndDelete.setVisibility(View.VISIBLE);
                return true;
            }
        });

        ImageView imgPublish = (ImageView) view.findViewById(R.id.imgPublish);
        imgPublish.setTag(position);

        ImageView imgUnpublish = (ImageView) view.findViewById(R.id.imgUnpublish);
        imgUnpublish.setTag(position);

        ImageView imgEdit = (ImageView) view.findViewById(R.id.imgEdit);
        imgEdit.setTag(position);
        if(isFromAnswered){
            imgEdit.setVisibility(View.VISIBLE);
            imgPublish.setVisibility(View.GONE);
            imgUnpublish.setVisibility(View.GONE);
        } else if(isFromAnswered == false && isFromAnswered == false){
            imgEdit.setVisibility(View.VISIBLE);
            imgPublish.setVisibility(View.GONE);
            imgUnpublish.setVisibility(View.GONE);
        } else{
            imgEdit.setVisibility(View.GONE);
            if(questionAndAnswer != null) {
                int is_publish = questionAndAnswer.getIs_published();
                if (is_publish == 0) {
                    imgPublish.setVisibility(View.VISIBLE);
                    imgUnpublish.setVisibility(View.GONE);
                } else {
                    imgPublish.setVisibility(View.GONE);
                    imgUnpublish.setVisibility(View.VISIBLE);
                }
            }
        }

        imgPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = (Integer) v.getTag();
                String doc_id = questionAndAnswer.getQid();
                int is_publish = questionAndAnswer.getIs_published();
                if(is_publish == 0) {
                    try {
                        QAUtil.updateDocForPublish(DatabaseUtil.getDatabaseInstance(context, Constants.YOUTH_CONNECT_DATABASE), doc_id, 1);
                        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle);
                        builder.setTitle("Question publish");
                        builder.setMessage("Done.");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    DatabaseUtil.startReplications(context, QNADataAdapter.this, TAG);
                                } catch(CouchbaseLiteException exception){
                                    Log.e(TAG, "onClick()", exception);
                                } catch(IOException exception){
                                    Log.e(TAG, "onClick()", exception);
                                } catch (Exception exception){
                                    Log.e(TAG, "onClick()", exception);
                                }
                                dialog.dismiss();
                            }
                        });
                        builder.show();
                    } catch (CouchbaseLiteException exception) {
                        Log.e(TAG, "getView()", exception);
                    } catch (IOException exception) {
                        Log.e(TAG, "getView()", exception);
                    } catch (Exception exception) {
                        Log.e(TAG, "getView()", exception);
                    }
                }
            }
        });

        imgUnpublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = (Integer) v.getTag();
                String doc_id = questionAndAnswer.getQid();
                int is_publish = questionAndAnswer.getIs_published();
                if(is_publish == 1) {
                    try {
                        QAUtil.updateDocForPublish(DatabaseUtil.getDatabaseInstance(context, Constants.YOUTH_CONNECT_DATABASE), doc_id, 0);
                        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle);
                        builder.setTitle("Question publish");
                        builder.setMessage("Done.");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    DatabaseUtil.startReplications(context, QNADataAdapter.this, TAG);
                                } catch(CouchbaseLiteException exception){
                                    Log.e(TAG, "onClick()", exception);
                                } catch(IOException exception){
                                    Log.e(TAG, "onClick()", exception);
                                } catch (Exception exception){
                                    Log.e(TAG, "onClick()", exception);
                                }
                                dialog.dismiss();
                            }
                        });
                        builder.show();
                    } catch (CouchbaseLiteException exception) {
                        Log.e(TAG, "getView()", exception);
                    } catch (IOException exception) {
                        Log.e(TAG, "getView()", exception);
                    } catch (Exception exception) {
                        Log.e(TAG, "getView()", exception);
                    }
                }
            }
        });

        imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = (Integer) v.getTag();
                layoutEditAndDelete.setVisibility(View.GONE);
                /*
                * If in pending section then edit use for question to edit
                * If in answerd section then edit use for edit answer
                * */
                if(isFromAnswered){
                    //Edit answer
                    Intent intent = new Intent(context, EditAnswerActivity.class);
                    intent.putExtra(Constants.QUESTION_DETAILS, questionAndAnswer);
                    context.startActivity(intent);
                } else if(isFromAnswered == false && isFromAnswered == false){
                    //Edit question
                    Intent intent = new Intent(context, EditQuestionActivity.class);
                    intent.putExtra(Constants.QUESTION_DETAILS, questionAndAnswer);
                    context.startActivity(intent);
                }
            }
        });

        ImageView imgDelete = (ImageView) view.findViewById(R.id.imgDelete);
        if(isFromAnswered){
            imgDelete.setVisibility(View.VISIBLE);
        } else if(isFromAnswered == false && isFromAnswered == false){
            imgDelete.setVisibility(View.VISIBLE);
        } else{
            imgDelete.setVisibility(View.GONE);
        }
        imgDelete.setTag(position);
        imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutEditAndDelete.setVisibility(View.GONE);
                /*
                * Delete only works for pending and answered section
                * */
                int pos = (Integer) v.getTag();
                String doc_id = questionAndAnswer.getQid();
                try {
                    DatabaseUtil.deleteDoc(DatabaseUtil.getDatabaseInstance(context,
                            Constants.YOUTH_CONNECT_DATABASE), doc_id);
                    AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle);
                    builder.setTitle("Question delete");
                    builder.setMessage("Done.");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                DatabaseUtil.startReplications(context, QNADataAdapter.this, TAG);
                            } catch(CouchbaseLiteException exception){
                                Log.e(TAG, "onClick()", exception);
                            } catch(IOException exception){
                                Log.e(TAG, "onClick()", exception);
                            } catch (Exception exception){
                                Log.e(TAG, "onClick()", exception);
                            }
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                } catch(CouchbaseLiteException exception){
                    Log.e(TAG, "OnClick()", exception);
                } catch(IOException exception){
                    Log.e(TAG, "OnClick()", exception);
                } catch(Exception exception){
                    Log.e(TAG, "OnClick()", exception);
                }
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

    @Override
    public void changed(Replication.ChangeEvent event) {
        Intent intent_answered = new Intent(Constants.BROADCAST_ACTION_REPLICATION_CHANGE);
        QNADataAdapter.this.context.sendBroadcast(intent_answered);
    }
}