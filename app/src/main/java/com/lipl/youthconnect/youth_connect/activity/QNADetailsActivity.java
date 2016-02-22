package com.lipl.youthconnect.youth_connect.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.replicator.Replication;
import com.lipl.youthconnect.youth_connect.R;
import com.lipl.youthconnect.youth_connect.pojo.Comment;
import com.lipl.youthconnect.youth_connect.util.ActivityIndicator;
import com.lipl.youthconnect.youth_connect.util.Constants;
import com.lipl.youthconnect.youth_connect.util.DatabaseUtil;
import com.lipl.youthconnect.youth_connect.util.FileUploadService;
import com.lipl.youthconnect.youth_connect.util.QAUtil;
import com.lipl.youthconnect.youth_connect.util.Util;
import com.lipl.youthconnect.youth_connect.pojo.Answer;
import com.lipl.youthconnect.youth_connect.pojo.QuestionAndAnswer;
import com.lipl.youthconnect.youth_connect.util.YouthConnectSingleTone;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by luminousinfoways on 18/12/15.
 */
public class QNADetailsActivity extends ActionBarActivity implements View.OnClickListener, Replication.ChangeListener {

    private static Toolbar mToolbar = null;
    private QuestionAndAnswer questionAndAnswer = null;
    private boolean isFromForum = false;
    private boolean isFromAnswered = false;
    private static final int REQ_ASK = 123;
    private static final String TAG = "QNADetailsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qus_n_ans_details);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle("Details");

        getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 2).edit().putInt(Constants.IS_ACTION_TAKEN_FOR_QA, 0).commit();
        if(getIntent().getExtras() != null){
            questionAndAnswer = getIntent().getExtras().getParcelable(Constants.INTENT_KEY_QUESTION_AND_ANSWER);
            isFromForum = getIntent().getExtras().getBoolean(Constants.INTENT_KEY_IS_FROM_FORUM);
            isFromAnswered = getIntent().getExtras().getBoolean(Constants.INTENT_KEY_IS_FROM_ANSWERED);
        }

        //Button btnPostAnswer = (Button) findViewById(R.id.btnPostAnswer);
        //btnPostAnswer.setOnClickListener(this);
        ImageView btnPostComment = (ImageView) findViewById(R.id.imgSend);
        btnPostComment.setOnClickListener(this);
        //Button btnEditQus = (Button) findViewById(R.id.btnEditQus);
        //btnEditQus.setOnClickListener(this);
        //Button tvCommentsTitle = (Button) findViewById(R.id.tvCommentsTitle);
        //tvCommentsTitle.setOnClickListener(this);

        if(questionAndAnswer != null) {
            TextView tvQuestionTitle = (TextView) findViewById(R.id.tvQuestionTitle);
            tvQuestionTitle.setText(questionAndAnswer.getQuestion().getQa_title());
            TextView tvQuestionDescription = (TextView) findViewById(R.id.tvQuestionDescription);
            tvQuestionDescription.setText(questionAndAnswer.getQuestion().getQa_description());

            LinearLayout layoutAnswerList = (LinearLayout) findViewById(R.id.layoutAnswerList);
            //LinearLayout layoutCommentLsit = (LinearLayout) findViewById(R.id.layoutCommentLsit);

            //String answerJosn = questionAndAnswer.getAnswerJson();
            List<Answer> answerList = questionAndAnswer.getAnswerList();
            if(answerList != null && answerList.size() > 0) {
                for (int i = 0; i < answerList.size(); i++) {
                    if (answerList != null && answerList.size() > 0) {
                        RelativeLayout itemLayout = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.answer_list_item, null);
                        TextView tvAnsContent = (TextView) itemLayout.findViewById(R.id.tvAnswerContent);
                        tvAnsContent.setText(answerList.get(i).getQadmin_description());

                        layoutAnswerList.addView(itemLayout);
                    }
                }
                //btnPostAnswer.setText("Edit your answer");
            } else{
                //btnPostAnswer.setText("Post your answer");
            }
        }

        int user_type = getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 1).getInt(Constants.SP_USER_TYPE, 2);
        //Button btnPublish = (Button) findViewById(R.id.btnPublish);
        //btnPublish.setOnClickListener(this);
        if(user_type == 1){
            //btnPostAnswer.setVisibility(View.VISIBLE);
            if(isFromAnswered){
                //btnPublish.setVisibility(View.VISIBLE);
                //btnPostComment.setVisibility(View.VISIBLE);
            } else {
                //btnPublish.setVisibility(View.GONE);
                //btnPostComment.setVisibility(View.GONE);
            }
        } else{
            //btnPostAnswer.setVisibility(View.GONE);
            //btnPublish.setVisibility(View.GONE);
            if(isFromAnswered){
                //btnPostComment.setVisibility(View.VISIBLE);
            } else {
                //btnPostComment.setVisibility(View.GONE);
            }
        }

        RelativeLayout layoutPostComment = (RelativeLayout) findViewById(R.id.layoutPostComment);
        if(isFromForum){
            layoutPostComment.setVisibility(View.GONE);
        } else if(isFromAnswered){
            layoutPostComment.setVisibility(View.VISIBLE);
        } else{
            if(user_type == 1){
                // visible for admin to answer
                layoutPostComment.setVisibility(View.VISIBLE);
            } else{
                // invisible for nodal
                layoutPostComment.setVisibility(View.GONE);
            }
        }

        EditText etComment = (EditText) findViewById(R.id.etComment);
        if(isFromAnswered == false && isFromForum == false){
            //Pending
            etComment.setHint("Post answer here.");
        } else if(isFromAnswered){
            etComment.setHint("Post comment here.");
        }

        if(isFromAnswered == false && isFromForum == false){
            //TextView tvAnswersTitle = (TextView) findViewById(R.id.tvAnswersTitle);
            //tvAnswersTitle.setVisibility(View.GONE);
            LinearLayout layoutAnswerList = (LinearLayout) findViewById(R.id.layoutAnswerList);
            layoutAnswerList.setVisibility(View.GONE);
            //Button _tvCommentsTitle = (Button) findViewById(R.id.tvCommentsTitle);
            //_tvCommentsTitle.setVisibility(View.GONE);
        } else{
            //TextView tvAnswersTitle = (TextView) findViewById(R.id.tvAnswersTitle);
            //tvAnswersTitle.setVisibility(View.VISIBLE);
            LinearLayout layoutAnswerList = (LinearLayout) findViewById(R.id.layoutAnswerList);
            layoutAnswerList.setVisibility(View.VISIBLE);
            //Button _tvCommentsTitle = (Button) findViewById(R.id.tvCommentsTitle);
            //_tvCommentsTitle.setVisibility(View.VISIBLE);
        }

        if(isFromForum){
            //LinearLayout layoutButton = (LinearLayout) findViewById(R.id.layoutButton);
            //layoutButton.setVisibility(View.GONE);
            //Button _btnPublish = (Button) findViewById(R.id.btnPublish);
            //_btnPublish.setVisibility(View.GONE);
            //Button _tvCommentsTitle = (Button) findViewById(R.id.tvCommentsTitle);
            //_tvCommentsTitle.setVisibility(View.GONE);
        } else if(isFromAnswered){
//            LinearLayout layoutButton = (LinearLayout) findViewById(R.id.layoutButton);
//            layoutButton.setVisibility(View.VISIBLE);
//            Button _btnPublish = (Button) findViewById(R.id.btnPublish);
//            _btnPublish.setVisibility(View.VISIBLE);
//            Button _btnEditQus = (Button) findViewById(R.id.btnEditQus);
//            _btnEditQus.setVisibility(View.GONE);
        } else{
//            LinearLayout layoutButton = (LinearLayout) findViewById(R.id.layoutButton);
//            layoutButton.setVisibility(View.VISIBLE);
//            Button _btnPublish = (Button) findViewById(R.id.btnPublish);
//            _btnPublish.setVisibility(View.GONE);
//            Button _btnEditQus = (Button) findViewById(R.id.btnEditQus);
//            _btnEditQus.setVisibility(View.VISIBLE);
//            Button _btnPostAnswer = (Button) findViewById(R.id.btnPostAnswer);
//            _btnPostAnswer.setVisibility(View.VISIBLE);
        }

        setCommentsToList();
        try {
            DatabaseUtil.startReplications(QNADetailsActivity.this, this, TAG);
        } catch(CouchbaseLiteException exception){
            Log.e(TAG, "onCreate()", exception);
        } catch(IOException exception){
            Log.e(TAG, "onCreate()", exception);
        } catch(Exception exception){
            Log.e(TAG, "onCreate()", exception);
        }
    }

    private void setCommentsToList(){

        LinearLayout layoutCommentList = (LinearLayout) findViewById(R.id.layoutCommentLsit);
        layoutCommentList.removeAllViews();
        List<Comment> commentList = new ArrayList<Comment>();
        if(isFromForum){
            commentList = getPublishedCommentList();
        } else {
            if(questionAndAnswer != null) {
                commentList = questionAndAnswer.getCommentList();
            }
        }

        Collections.reverse(commentList);

        if(commentList != null &&  commentList.size() > 0) {
            for (int i = 0; i < commentList.size(); i++) {
                if (commentList != null && commentList.size() > 0) {
                    RelativeLayout itemLayout = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.comment_list_item, null);
                    TextView tvCommentDesc = (TextView) itemLayout.findViewById(R.id.tvCommentDesc);
                    tvCommentDesc.setText(commentList.get(i).getComment_description().trim());

                    TextView tvAnswerBy = (TextView) itemLayout.findViewById(R.id.tvAnswerBy);
                    tvAnswerBy.setText(commentList.get(i).getComment_by_user_name());

                    try {
                        String updated_time_stamp = (String) commentList.get(i).getCreated();
                        Long timeStamp = Long.parseLong(updated_time_stamp);
                        String dateTime = Util.getDateAndTimeFromTimeStamp(timeStamp);
                        TextView tvAnswerDateTime = (TextView) itemLayout.findViewById(R.id.tvAnswerDateTime);
                        tvAnswerDateTime.setText(dateTime);
                    } catch(Exception e){
                        Log.e("DataAdapter", "onBindViewHolder()", e);
                    }

                    /*ImageView imgPendingIcon = (ImageView) itemLayout.findViewById(R.id.imgPendingIcon);
                    if(commentList.get(i) != null
                            && commentList.get(i).getIs_uploaded() == 0) {
                        imgPendingIcon.setImageResource(R.drawable.ic_watch_later);
                    } else {
                        imgPendingIcon.setImageResource(R.drawable.ic_watch_later);
                    }*/

                    layoutCommentList.addView(itemLayout);
                }
            }
        }
    }

    private List<Comment> getPublishedCommentList(){
        List<Comment> publishedCommentList = new ArrayList<Comment>();
        if(questionAndAnswer != null){
            //String commentJson = questionAndAnswer.getCommentJson();
            List<Comment> cList = questionAndAnswer.getCommentList(); //QAUtil.getCommentListFromJson(commentJson);
            if(cList != null) {
                for (Comment comment : cList) {
                    if(comment != null && comment.getIs_published() != null
                            && comment.getIs_published().trim().length() > 0
                            && comment.getIs_published().equalsIgnoreCase("Y")){
                        publishedCommentList.add(comment);
                    }
                }
            }
        }

        return publishedCommentList;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            /*case R.id.btnPostAnswer:

                Intent intentAnswer = new Intent(QNADetailsActivity.this, PostAnswerActivity.class);
                intentAnswer.putExtra(Constants.INTENT_KEY_QUESTION_AND_ANSWER, questionAndAnswer);
                List<Answer> answerList = questionAndAnswer.getAnswerList();
                if(answerList != null && answerList.size() > 0) {
                    intentAnswer.putExtra(Constants.INTENT_KEY_QUESTION_AND_ANSWER_IS_EDIT, true);
                } else{
                    intentAnswer.putExtra(Constants.INTENT_KEY_QUESTION_AND_ANSWER_IS_EDIT, false);
                }
                startActivity(intentAnswer);
                //finish();

                break;*/
            case R.id.imgSend:

                /*Intent intentComment = new Intent(QNADetailsActivity.this, PostCommentActivity.class);
                intentComment.putExtra(Constants.INTENT_KEY_QUESTION_AND_ANSWER, questionAndAnswer);
                startActivity(intentComment);*/
                //finish();

                if(isFromAnswered) {
                    final EditText etComment = (EditText) findViewById(R.id.etComment);
                    final String answer = etComment.getText().toString().trim();
                    if (answer == null || answer.trim().length() <= 0) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
                        builder.setTitle("Post Comment");
                        builder.setMessage("Enter your comment.");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.show();
                        return;
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
                    builder.setTitle("Post Comment");
                    builder.setMessage("Are you sure want to post this comment?");
                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (questionAndAnswer == null || questionAndAnswer.getQid() == null || questionAndAnswer.getQid().length() <= 0) {
                                return;
                            }

                            if (questionAndAnswer != null && questionAndAnswer.getQuestion() != null) {
                                String questionAndAnswerID = questionAndAnswer.getQid();
                                try {
                                    List<Comment> previousData = questionAndAnswer.getCommentList();
                                    String answer_by_user_name = getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 1).getString(Constants.SP_USER_NAME, "");
                                    if (questionAndAnswerID != null && questionAndAnswerID.trim().length() > 0) {
                                        int comment_by_user_id = getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 1).getInt(Constants.SP_USER_ID, 0);
                                        updateDoc(DatabaseUtil.getDatabaseInstance(QNADetailsActivity.this, Constants.YOUTH_CONNECT_DATABASE),
                                                questionAndAnswerID, answer, answer_by_user_name, previousData, comment_by_user_id);
                                    }
                                } catch (CouchbaseLiteException exception) {
                                    Log.e(TAG, "onClick()", exception);
                                } catch (IOException exception) {
                                    Log.e(TAG, "onClick()", exception);
                                } catch (Exception exception) {
                                    Log.e(TAG, "onClick()", exception);
                                }
                            }

                            etComment.setText("");
                            setCommentsToList();
                            try {
                                DatabaseUtil.startReplications(QNADetailsActivity.this, QNADetailsActivity.this, TAG);
                            } catch (CouchbaseLiteException exception) {
                                Log.e(TAG, "onClick()", exception);
                            } catch (IOException exception) {
                                Log.e(TAG, "onClick()", exception);
                            }
                            AlertDialog.Builder builder12 = new AlertDialog.Builder(QNADetailsActivity.this, R.style.AppCompatAlertDialogStyle);
                            builder12.setTitle("Post Comment");
                            builder12.setMessage("Done.");
                            builder12.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    try {
                                        DatabaseUtil.startReplications(QNADetailsActivity.this,
                                                QNADetailsActivity.this, TAG);
                                    } catch(CouchbaseLiteException exception){
                                        Log.e(TAG, "onClick()", exception);
                                    } catch(IOException exception){
                                        Log.e(TAG, "onClick()", exception);
                                    } catch (Exception exception){
                                        Log.e(TAG, "onClick()", exception);
                                    }
                                    dialog.dismiss();
                                    finish();
                                }
                            });
                            builder12.show();
                        }
                    });
                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();

                } else if(isFromAnswered == false && isFromForum == false){
                    EditText etComment = (EditText) findViewById(R.id.etComment);
                    final String answer = etComment.getText().toString().trim();
                    if (answer == null || answer.trim().length() <= 0) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
                        builder.setTitle("Post Answer");
                        builder.setMessage("Enter your answer.");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.show();
                    }

                    if (questionAndAnswer == null || questionAndAnswer.getQid() == null || questionAndAnswer.getQid().length() <= 0) {
                        return;
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
                    builder.setTitle("Post Answer");
                    builder.setMessage("Are you sure want to post your answer?");
                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            if (questionAndAnswer != null && questionAndAnswer.getQuestion() != null) {
                                String questionAndAnswerID = questionAndAnswer.getQid();
                                try {
                                    List<Answer> previousData = questionAndAnswer.getAnswerList();
                                    String answer_by_user_name = getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 1).getString(Constants.SP_USER_NAME, "");
                                    if (questionAndAnswerID != null && questionAndAnswerID.trim().length() > 0) {
                                        int answer_by_user_id = getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 1).getInt(Constants.SP_USER_ID, 0);
                                        updateDocForAnswer(DatabaseUtil.getDatabaseInstance(QNADetailsActivity.this, Constants.YOUTH_CONNECT_DATABASE),
                                                questionAndAnswerID, answer, answer_by_user_name, previousData, answer_by_user_id);
                                    }
                                    AlertDialog.Builder builder12 = new AlertDialog.Builder(QNADetailsActivity.this, R.style.AppCompatAlertDialogStyle);
                                    builder12.setTitle("Post Answer");
                                    builder12.setMessage("Done.");
                                    builder12.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            try {
                                                DatabaseUtil.startReplications(QNADetailsActivity.this,
                                                        QNADetailsActivity.this, TAG);
                                            } catch(CouchbaseLiteException exception){
                                                Log.e(TAG, "onClick()", exception);
                                            } catch(IOException exception){
                                                Log.e(TAG, "onClick()", exception);
                                            } catch (Exception exception){
                                                Log.e(TAG, "onClick()", exception);
                                            }
                                            Intent intent = new Intent(QNADetailsActivity.this, QAAnsweredActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            dialog.dismiss();
                                            finish();
                                        }
                                    });
                                    builder12.show();
                                } catch (CouchbaseLiteException exception) {
                                    Log.e(TAG, "onClick()", exception);
                                } catch (IOException exception) {
                                    Log.e(TAG, "onClick()", exception);
                                } catch (Exception exception) {
                                    Log.e(TAG, "onClick()", exception);
                                }
                            }
                        }
                    });
                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }
                break;

            /*case R.id.btnPublish:

                String qa_id = questionAndAnswer.getQid()+"";
                int userId = getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 1).getInt(Constants.SP_USER_ID, 0);
                String _user_id = "";
                if(userId == 0){
                    return;
                }
                _user_id = userId+"";

                String answerId = "";
                if(questionAndAnswer != null && questionAndAnswer.getAnswerList() != null &&
                        questionAndAnswer.getAnswerList().size() > 0) {
                    answerId = questionAndAnswer.getAnswerList().get(0).getQa_answer_id()+"";
                    if (answerId == null) {
                        answerId = "";
                    }
                }
                String dateTime = getCurrentDateAndTime(); // Format : 2015-12-12 12:12:12
                if(Util.getNetworkConnectivityStatus(QNADetailsActivity.this)) {
                    PostAnswerAsync postAnswerAsync = new PostAnswerAsync();
                    postAnswerAsync.execute(qa_id, _user_id);
                }

                break;*/

            /*case R.id.btnEditQus:

                Intent intent = new  Intent(QNADetailsActivity.this, AskQuestionActivity.class);
                intent.putExtra(Constants.QUESTION_DETAILS, questionAndAnswer);
                startActivityForResult(intent, Constants.INTENT_QNADETAILS_TO_ASKQUESTION_REQUSET_CODE);

                break;*/

            /*case R.id.tvCommentsTitle:

                Intent intent_comment = new Intent(QNADetailsActivity.this, CommentListActivity.class);
                intent_comment.putExtra(Constants.QUESTION_DETAILS, questionAndAnswer);
                if (isFromForum) {
                    intent_comment.putExtra(Constants.INTENT_KEY_IS_FROM_FORUM, true);
                    intent_comment.putExtra(Constants.INTENT_KEY_IS_FROM_ANSWERED, false);
                } else if (isFromAnswered) {
                    intent_comment.putExtra(Constants.INTENT_KEY_IS_FROM_FORUM, false);
                    intent_comment.putExtra(Constants.INTENT_KEY_IS_FROM_ANSWERED, true);
                } else {
                    intent_comment.putExtra(Constants.INTENT_KEY_IS_FROM_FORUM, false);
                    intent_comment.putExtra(Constants.INTENT_KEY_IS_FROM_ANSWERED, false);
                }
                startActivityForResult(intent_comment, Constants.INTENT_QNADETAILS_TO_ASKQUESTION_REQUSET_CODE);

                break;*/

            default:
                break;
        }
    }

    private void updateDocForAnswer(Database database, String documentId,
                           String answer_desc, String answer_by_username,
                                    List<Answer> previousAnswerList, int answer_by_user_id){
        Document document = database.getDocument(documentId);
        try {
            // Update the document with more data

            List<Answer> answerDocument = createDocumentForAnswer(previousAnswerList, answer_desc,
                    answer_by_username, answer_by_user_id);
            if(answerDocument != null) {
                Map<String, Object> updatedProperties = new HashMap<String, Object>();
                updatedProperties.putAll(document.getProperties());
                updatedProperties.put(DatabaseUtil.QA_ANSWER, answerDocument);
                updatedProperties.put(DatabaseUtil.QA_IS_ANSWERED, 1);
                // Save to the Couchbase local Couchbase Lite DB
                document.putProperties(updatedProperties);
            }
        } catch (CouchbaseLiteException e) {
            com.couchbase.lite.util.Log.e(TAG, "Error putting", e);
        } catch(Exception exception){
            Log.e(TAG, "updateDocument()", exception);
        }
    }

    private ArrayList<Answer> createDocumentForAnswer(List<Answer> answerList, String answer_desc,
                                                      String answer_by_username, int answer_by_user_id){
        // Create a new document and add data

        if(answerList == null) {
            answerList = new ArrayList<Answer>();
        }

        Answer answer = new Answer(Parcel.obtain());
        answer.setQadmin_description(answer_desc);
        answer.setAnswer_by_user_name(answer_by_username);
        answer.setAnswer_by_user_id(answer_by_user_id);
        String timestamp = System.currentTimeMillis()+"";
        answer.setCreated(timestamp);
        answerList.add(answer);

        /*String jsonData = null;
        List<Answer> answers = QAUtil.getAnswerListFromJson(previousData);

        try {
            JSONArray array = new JSONArray();
            String timestamp = System.currentTimeMillis()+"";
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(DatabaseUtil.ANSWER_BY_USER_NAME, answer_by_username);
            jsonObject.put(DatabaseUtil.ANSWER_CONTENT, answer_desc);
            jsonObject.put(DatabaseUtil.ANSWER_TIMESTAMP, timestamp);
            jsonObject.put(DatabaseUtil.ANSWER_IS_UPLOADED, 0);
            array.put(jsonObject);
            if(answers != null && answers.size() > 0){
                for(int i = 0; i < answers.size(); i++){
                    Answer answer = answers.get(i);
                    if(answer.getAnswer_by_user_name() != null
                            && answer.getQadmin_description() != null
                            && answer.getCreated() != null) {
                        JSONObject jsonObject1 = new JSONObject();
                        jsonObject1.put(DatabaseUtil.ANSWER_BY_USER_NAME, answer.getAnswer_by_user_name());
                        jsonObject1.put(DatabaseUtil.ANSWER_CONTENT, answer.getQadmin_description());
                        jsonObject1.put(DatabaseUtil.ANSWER_TIMESTAMP, answer.getCreated());
                        jsonObject1.put(DatabaseUtil.ANSWER_IS_UPLOADED, answer.getIs_uploaded());
                        array.put(jsonObject1);
                    }
                }
            }*/
        return new ArrayList<Answer>(answerList);
    }

    private void updateDoc(Database database, String documentId,
                           String answer_desc, String answer_by_username,
                           List<Comment> previousData, int comment_by_user_id){
        Document document = database.getDocument(documentId);
        try {
            // Update the document with more data

            ArrayList<Comment> commentJson = createDocument(previousData, answer_desc,
                    answer_by_username, comment_by_user_id);
            if(commentJson != null) {
                Map<String, Object> updatedProperties = new HashMap<String, Object>();
                updatedProperties.putAll(document.getProperties());
                updatedProperties.put(DatabaseUtil.QA_COMMENT, commentJson);
                // Save to the Couchbase local Couchbase Lite DB
                document.putProperties(updatedProperties);
            }
        } catch (CouchbaseLiteException e) {
            com.couchbase.lite.util.Log.e(TAG, "Error putting", e);
        } catch(Exception exception){
            Log.e(TAG, "updateDocument()", exception);
        }
    }

    private ArrayList<Comment> createDocument(List<Comment> commentList, String comment_desc,
                                              String answer_by_username, int comment_by_user_id){
        if(commentList == null) {
            commentList = new ArrayList<Comment>();
        }

        Comment comment = new Comment(Parcel.obtain());
        comment.setComment_description(comment_desc);
        comment.setComment_by_user_name(answer_by_username);
        comment.setComment_by_user_id(comment_by_user_id);
        String timestamp = System.currentTimeMillis()+"";
        comment.setCreated(timestamp);
        commentList.add(comment);
        return new ArrayList<Comment>(commentList);


        /*String jsonData = null;
        List<Comment> answers = QAUtil.getCommentListFromJson(previousData);

        try {
            JSONArray array = new JSONArray();
            String timestamp = System.currentTimeMillis()+"";
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(DatabaseUtil.COMMENT_BY_USER_NAME, answer_by_username);
            jsonObject.put(DatabaseUtil.COMMENT_CONTENT, comment_desc);
            jsonObject.put(DatabaseUtil.COMMENT_TIMESTAMP, timestamp);
            jsonObject.put(DatabaseUtil.COMMENT_IS_UPLOADED, 0);
            array.put(jsonObject);
            if(answers != null && answers.size() > 0){
                for(int i = 0; i < answers.size(); i++){
                    Comment answer = answers.get(i);
                    if(answer.getComment_by_user_name() != null
                            && answer.getComment_description() != null
                            && answer.getCreated() != null) {
                        JSONObject jsonObject1 = new JSONObject();
                        jsonObject1.put(DatabaseUtil.COMMENT_BY_USER_NAME, answer.getComment_by_user_name());
                        jsonObject1.put(DatabaseUtil.COMMENT_CONTENT, answer.getComment_description());
                        jsonObject1.put(DatabaseUtil.COMMENT_TIMESTAMP, answer.getCreated());
                        jsonObject1.put(DatabaseUtil.COMMENT_IS_UPLOADED, answer.getIs_uploaded());
                        array.put(jsonObject1);
                    }
                }
            }

            return array.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonData;*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == Constants.INTENT_QNADETAILS_TO_ASKQUESTION_REQUSET_CODE){
            int res = data.getExtras().getInt(Constants.QUESTION_RESULT);
            if(res == 1){
                finish();
            }
        }
    }

    private String getCurrentDateAndTime(){
        String year = Util.getCurrentYear()+"";
        String month = Util.getCurrentMonth()+"";
        String day = Util.getCurrentDay()+"";
        String hour = Util.geCurrentHour()+"";
        String minute = Util.geCurrentMinute()+"";
        String second = Util.geCurrentSecond()+"";

        // Format : 2015-12-12 12:12:12
        String dateTime = year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second;
        Log.i("Date Time", dateTime);

        return dateTime;
    }

    /**
     * Async task to get sync camp table from server
     * */
    private class PostAnswerAsync extends AsyncTask<String, Void, Boolean> {

        private static final String TAG = "LoginAsync";
        //private ProgressDialog progressDialog = null;
        private boolean isChangePassword = false;
        private ActivityIndicator activityIndicator = ActivityIndicator.ctor(QNADetailsActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /*if(progressDialog == null) {
                progressDialog = ProgressDialog.show(QNADetailsActivity.this, "Posting", "Please wait...");
            }*/
            if(activityIndicator == null){
                activityIndicator = new ActivityIndicator(QNADetailsActivity.this);
            }
            activityIndicator.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {

            String api_key = getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 1).getString(Constants.SP_USER_API_KEY, null);

            if(api_key == null){
                return null;
            }

            try {

                String qa_id = params[0];
                String _user_id = params[1];

                InputStream in = null;
                int resCode = -1;

                String link = Constants.BASE_URL+Constants.QUESTION_ASK_FORUM;
                URL url = new URL(link);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setAllowUserInteraction(false);
                conn.setInstanceFollowRedirects(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Authorization", api_key);

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("data[Qa][qa_id]", qa_id)
                                //.appendQueryParameter("data[Qa][user_id]", _user_id)
                        .appendQueryParameter("response", "mobile")
                        .appendQueryParameter("data[QaAnswer]", "")
                        .appendQueryParameter("data[QaComment]", "")
                        .appendQueryParameter("data[Qa][is_publish]", "Y");

                String query = builder.build().getEncodedQuery();

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();

                conn.connect();
                resCode = conn.getResponseCode();
                if (resCode == HttpURLConnection.HTTP_OK) {
                    in = conn.getInputStream();
                }
                if(in == null){
                    return null;
                }
                BufferedReader reader =new BufferedReader(new InputStreamReader(in, "UTF-8"));
                String response = "",data="";

                while ((data = reader.readLine()) != null){
                    response += data + "\n";
                }

                Log.i(TAG, "Response : " + response);

                /**
                 * {
                 {
                 "message": "successfully inserted"
                 }
                 * */

                if(response != null && response.length() > 0 && response.charAt(0) == '{'){
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject != null && jsonObject.isNull("Apikey") == false) {
                        String changePasswordDoneFromWebMsg = jsonObject.optString("Apikey");
                        if(changePasswordDoneFromWebMsg.equalsIgnoreCase("Api key does not exit")){
                            isChangePassword = true;
                            return null;
                        }
                    }
                }

                if(response != null && response.length() > 0){

                    JSONObject res = new JSONObject(response);
                    String message = res.optString("message");
                    if(message != null && message.trim().length() > 0 && message.equalsIgnoreCase("successfully inserted")){
                        return true;
                    }
                }
            } catch(SocketTimeoutException exception){
                Log.e(TAG, "GetFeedbackListAsync : doInBackground", exception);
            } catch(ConnectException exception){
                Log.e(TAG, "GetFeedbackListAsync : doInBackground", exception);
            } catch(MalformedURLException exception){
                Log.e(TAG, "LoginAsync : doInBackground", exception);
            } catch (IOException exception){
                Log.e(TAG, "LoginAsync : doInBackground", exception);
            } catch(Exception exception){
                Log.e(TAG, "LoginAsync : doInBackground", exception);
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean isSuccess) {
            super.onPostExecute(isSuccess);

            //if(progressDialog != null) progressDialog.dismiss();

            if(activityIndicator == null){
                activityIndicator = new ActivityIndicator(QNADetailsActivity.this);
            }
            activityIndicator.dismiss();

            if(isChangePassword){
                AlertDialog.Builder builder = new AlertDialog.Builder(QNADetailsActivity.this, R.style.AppCompatAlertDialogStyle);
                builder.setTitle(getResources().getString(R.string.password_changed_title));
                builder.setMessage(getResources().getString(R.string.password_changed_description));
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent(QNADetailsActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("Exit me", true);
                        startActivity(intent);
                        finish();
                    }
                });
                builder.show();

                return;
            }

            String dialogMessage = null;
            if(isSuccess){
                YouthConnectSingleTone.getInstance().CURRENT_FRAGMENT_IN_QA = Constants.FRAGMENT_QA_SUB_FRAGMENT_PENDING;
                dialogMessage = "Published successfully.";
                showAlertDialog(dialogMessage, "Publish Answer", "Ok", true);
            } else{
                dialogMessage = "Sorry, failed to publish your answer.\nPlease try again";
                showAlertDialog(dialogMessage, "Publish Answer", "Ok", false);
            }
        }
    }

    /**
     * To Show Material Alert Dialog
     *
     * @param message
     * @param title
     * */
    private void showAlertDialog(String message, String title, String positiveButtonText, final boolean isSuccess){

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (isSuccess) {
                    finish();
                }
            }
        });
        builder.show();
    }

    public static Toolbar getToolbar(){
        return mToolbar;
    }

    /**
     * When touch on screen outside the keyboard, the input keyboard will hide automatically
     * */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View v = getCurrentFocus();

        if (v != null &&
                (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) &&
                v instanceof EditText &&
                !v.getClass().getName().startsWith("android.webkit."))
        {
            int scrcoords[] = new int[2];
            v.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + v.getLeft() - scrcoords[0];
            float y = ev.getRawY() + v.getTop() - scrcoords[1];

            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom()) {
                Util.hideKeyboard(this);
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * To Show Material Alert Dialog
     *
     * @param code Should be one of the global declared integer constants
     * @param message
     * @param title
     * */
    private void showAlertDialog(String message, String title, String positiveButtonText, String negativeButtonText, final int code){

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton(negativeButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings_actionbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if(id == android.R.id.home){
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Async task to get sync camp table from server
     * */
    private class AskQuesAsync extends AsyncTask<String, Void, Boolean> {

        private static final String TAG = "LoginAsync";
        private ProgressDialog progressDialog = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(progressDialog == null) {
                progressDialog = ProgressDialog.show(QNADetailsActivity.this, "Sending", "Please wait...");
            }
        }

        @Override
        protected Boolean doInBackground(String... params) {

            String api_key = getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 1).getString(Constants.SP_USER_API_KEY, null);

            if(api_key == null){
                return null;
            }

            try {

                String qa_id = params[0];
                String qa_title = params[1];
                String qa_description = params[2];
                String _user_id = params[3];

                InputStream in = null;
                int resCode = -1;

                String link = Constants.BASE_URL+Constants.QUESTION_ASK_FORUM;
                URL url = new URL(link);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setAllowUserInteraction(false);
                conn.setInstanceFollowRedirects(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Authorization", api_key);

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("data[Qa][qa_id]", qa_id)
                        .appendQueryParameter("data[Qa][qa_title]", qa_title)
                        .appendQueryParameter("data[Qa][qa_description]", qa_description)
                        .appendQueryParameter("data[Qa][user_id]", _user_id)
                        .appendQueryParameter("response", "mobile");
                String query = builder.build().getEncodedQuery();

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();

                conn.connect();
                resCode = conn.getResponseCode();
                if (resCode == HttpURLConnection.HTTP_OK) {
                    in = conn.getInputStream();
                }
                BufferedReader reader =new BufferedReader(new InputStreamReader(in, "UTF-8"));
                String response = "",data="";

                while ((data = reader.readLine()) != null){
                    response += data + "\n";
                }

                Log.i(TAG, "Response : " + response);

                /**
                 * {
                     {
                     "message": "successfully inserted"
                     }
                 * */

                if(response != null && response.length() > 0){

                    JSONObject res = new JSONObject(response);
                    String message = res.optString("message");
                    if(message != null && message.trim().length() > 0 && message.equalsIgnoreCase("successfully inserted")){
                        return true;
                    }
                }
            } catch(SocketTimeoutException exception){
                Log.e(TAG, "LoginAsync : doInBackground", exception);
            } catch(ConnectException exception){
                Log.e(TAG, "LoginAsync : doInBackground", exception);
            } catch(MalformedURLException exception){
                Log.e(TAG, "LoginAsync : doInBackground", exception);
            } catch (IOException exception){
                Log.e(TAG, "LoginAsync : doInBackground", exception);
            } catch(Exception exception){
                Log.e(TAG, "LoginAsync : doInBackground", exception);
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean isSuccess) {
            super.onPostExecute(isSuccess);

            if(progressDialog != null) progressDialog.dismiss();
            String dialogMessage = null;
            if(isSuccess){
                dialogMessage = "Posted successfully.";
            } else{
                dialogMessage = "Sorry, failed to post your question.\nPlease try again";
            }
            showAlertDialog(dialogMessage, "Post Question", "Ok");
        }
    }

    /**
     * To Show Material Alert Dialog
     *
     * @param message
     * @param title
     * */
    private void showAlertDialog(String message, String title, String positiveButtonText){

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        builder.show();
    }

    @Override
    public void changed(Replication.ChangeEvent event) {
        Replication replication = event.getSource();
        com.couchbase.lite.util.Log.i(TAG, "Replication : " + replication + "changed.");
        if(!replication.isRunning()){
            String msg = String.format("Replicator %s not running", replication);
            com.couchbase.lite.util.Log.i(TAG, msg);
        } else{
            int processed = replication.getCompletedChangesCount();
            int total = replication.getChangesCount();
            String msg = String.format("Replicator processed %d / %d", processed, total);
            com.couchbase.lite.util.Log.i(TAG, msg);
        }

        if(event.getError() != null){
            showError("Sync error", event.getError());
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {

                } catch (Exception exception) {
                    com.couchbase.lite.util.Log.e(TAG, "changed()", exception);
                }
            }
        });
    }

    public void showError(final String errorMessage, final Throwable throwable){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String msg = String.format("%s: %s", errorMessage, throwable);
                com.couchbase.lite.util.Log.e(TAG, msg, throwable);
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try{
                if(questionAndAnswer != null) {
                    String doc_id = questionAndAnswer.getQid();
                    if(doc_id != null) {
                        Document doc = DatabaseUtil.getDocumentFromDocumentId(DatabaseUtil
                                .getDatabaseInstance(QNADetailsActivity.this, Constants.YOUTH_CONNECT_DATABASE), doc_id);
                        if(doc != null) {
                            questionAndAnswer = QAUtil.getQAFromDocument(doc);
                        }
                    }
                }
                setCommentsToList();
            } catch(Exception exception){
                Log.e(TAG, "OnReceive()", exception);
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(Constants.BROADCAST_ACTION_REPLICATION_CHANGE));
        int isToFinish = getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 1).getInt(Constants.IS_ACTION_TAKEN_FOR_QA, 0);
        if(isToFinish == 1){
            YouthConnectSingleTone.getInstance().CURRENT_FRAGMENT_IN_QA = Constants.FRAGMENT_QA_SUB_FRAGMENT_ANSWERED;
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onDestroy() {
        System.gc();
        super.onDestroy();
    }
}