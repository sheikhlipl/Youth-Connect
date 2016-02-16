package com.lipl.youthconnect.youth_connect.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.lipl.youthconnect.youth_connect.R;
import com.lipl.youthconnect.youth_connect.util.ActivityIndicator;
import com.lipl.youthconnect.youth_connect.util.Constants;
import com.lipl.youthconnect.youth_connect.util.DatabaseUtil;
import com.lipl.youthconnect.youth_connect.util.QAUtil;
import com.lipl.youthconnect.youth_connect.util.Util;
import com.lipl.youthconnect.youth_connect.pojo.Answer;
import com.lipl.youthconnect.youth_connect.pojo.Comment;
import com.lipl.youthconnect.youth_connect.pojo.Question;
import com.lipl.youthconnect.youth_connect.pojo.QuestionAndAnswer;
import com.lipl.youthconnect.youth_connect.pojo.User;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONArray;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostCommentActivity extends ActionBarActivity implements View.OnClickListener {

    private static Toolbar mToolbar = null;
    private QuestionAndAnswer questionAndAnswer = null;
    private SwipeRefreshLayout swipe_refresh_layout;
    private static final String TAG = "PostCommentActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_your_comment);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle("Post Comment");

        if(getIntent().getExtras() != null){
            questionAndAnswer = getIntent().getExtras().getParcelable(Constants.INTENT_KEY_QUESTION_AND_ANSWER);
        }

        Button btnAsk = (Button) findViewById(R.id.btnPost);
        btnAsk.setOnClickListener(this);
        Button btnReset = (Button) findViewById(R.id.btnReset);
        btnReset.setOnClickListener(this);

        swipe_refresh_layout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipe_refresh_layout.setColorSchemeColors(R.array.movie_serial_bg);
    }

    public static Toolbar getToolbar(){
        return mToolbar;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        MaterialEditText tvAnswer = (MaterialEditText) findViewById(R.id.answer);
        switch (id){
            case R.id.btnPost:
                String answer = tvAnswer.getText().toString().trim();
                if(answer == null || answer.trim().length() <= 0){
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
                }

                if (questionAndAnswer == null || questionAndAnswer.getQid() == null || questionAndAnswer.getQid().length() <= 0) {
                    return;
                }

                String answerId = "";
                if (questionAndAnswer != null && questionAndAnswer.getAnswerList() != null &&
                        questionAndAnswer.getAnswerList().size() > 0) {
                    answerId = questionAndAnswer.getAnswerList().get(0).getQa_answer_id() + "";
                    if (answerId == null) {
                        answerId = "";
                    }
                }

                if (questionAndAnswer != null && questionAndAnswer.getQuestion() != null) {
                    String questionAndAnswerID = questionAndAnswer.getQid();
                    try {
                        List<Comment> previousData = questionAndAnswer.getCommentList();
                        String answer_by_user_name = getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 1).getString(Constants.SP_USER_NAME, "");
                        if (questionAndAnswerID != null && questionAndAnswerID.trim().length() > 0) {
                            updateDoc(DatabaseUtil.getDatabaseInstance(PostCommentActivity.this, Constants.YOUTH_CONNECT_DATABASE),
                                    questionAndAnswerID, answer, answer_by_user_name, previousData);
                        }
                    } catch (CouchbaseLiteException exception) {
                        Log.e(TAG, "onClick()", exception);
                    } catch (IOException exception) {
                        Log.e(TAG, "onClick()", exception);
                    } catch (Exception exception) {
                        Log.e(TAG, "onClick()", exception);
                    }
                }

                break;
            case R.id.btnReset:
                tvAnswer.setText("");
                break;
            default:
                break;
        }
    }

    private void updateDoc(Database database, String documentId,
                           String answer_desc, String answer_by_username, List<Comment> previousData){
        Document document = database.getDocument(documentId);
        try {
            // Update the document with more data

            ArrayList<Comment> commentJson = createDocument(previousData, answer_desc, answer_by_username);
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

    private ArrayList<Comment> createDocument(List<Comment> commentList, String comment_desc, String answer_by_username){
        if(commentList == null) {
            commentList = new ArrayList<Comment>();
        }

        Comment comment = new Comment(Parcel.obtain());
        comment.setComment_description(comment_desc);
        comment.setComment_by_user_name(answer_by_username);
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
}