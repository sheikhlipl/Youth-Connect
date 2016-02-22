package com.lipl.youthconnect.youth_connect.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
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
import org.json.JSONException;
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

public class PostAnswerActivity extends ActionBarActivity implements View.OnClickListener {

    private static Toolbar mToolbar = null;
    private QuestionAndAnswer questionAndAnswer = null;
    private boolean isEdit = false;
    private static final String TAG = "PostAnswerActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_your_answer);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle("Post Answer");

        if(getIntent().getExtras() != null){
            questionAndAnswer = getIntent().getExtras().getParcelable(Constants.INTENT_KEY_QUESTION_AND_ANSWER);
            isEdit = getIntent().getExtras().getBoolean(Constants.INTENT_KEY_QUESTION_AND_ANSWER_IS_EDIT);
        }

        Button btnAsk = (Button) findViewById(R.id.btnPost);
        btnAsk.setOnClickListener(this);
        Button btnReset = (Button) findViewById(R.id.btnReset);
        btnReset.setOnClickListener(this);

        if(isEdit){
            if(questionAndAnswer != null && questionAndAnswer.getAnswerList() != null
                    && questionAndAnswer.getAnswerList().size() > 0){
                String answer = questionAndAnswer.getAnswerList().get(0).getQadmin_description();
                if(answer != null && answer.length() > 0) {
                    MaterialEditText tvAnswer = (MaterialEditText) findViewById(R.id.answer);
                    tvAnswer.setText(answer);
                }
            }
        }
    }

    public static Toolbar getToolbar(){
        return mToolbar;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        MaterialEditText tvAnswer = (MaterialEditText) findViewById(R.id.answer);
        switch (id) {
            case R.id.btnPost:
                String answer = tvAnswer.getText().toString().trim();
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

                if (questionAndAnswer != null && questionAndAnswer.getQuestion() != null) {
                    String questionAndAnswerID = questionAndAnswer.getQid();
                    try {
                        List<Answer> previousData = questionAndAnswer.getAnswerList();
                        String answer_by_user_name = getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 1).getString(Constants.SP_USER_NAME, "");
                        if (questionAndAnswerID != null && questionAndAnswerID.trim().length() > 0) {
                            updateDoc(DatabaseUtil.getDatabaseInstance(PostAnswerActivity.this, Constants.YOUTH_CONNECT_DATABASE),
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
                if (isEdit) {
                    if (questionAndAnswer != null && questionAndAnswer.getAnswerList() != null
                            && questionAndAnswer.getAnswerList().size() > 0) {
                        String _answer = questionAndAnswer.getAnswerList().get(0).getQadmin_description();
                        if (_answer != null && _answer.length() > 0) {
                            MaterialEditText _tvAnswer = (MaterialEditText) findViewById(R.id.answer);
                            _tvAnswer.setText(_answer);
                        }
                    }
                }
                break;
            default:
                break;
        }
    }

    private void updateDoc(Database database, String documentId,
                           String answer_desc, String answer_by_username, List<Answer> previousAnswerList){
        Document document = database.getDocument(documentId);
        try {
            // Update the document with more data

            int answer_by_user_id = getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 1).getInt(Constants.SP_USER_ID, 0);
            List<Answer> answerDocument = createDocument(previousAnswerList, answer_desc, answer_by_username, answer_by_user_id);
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

    private ArrayList<Answer> createDocument(List<Answer> answerList, String answer_desc,
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

    @Override
    protected void onDestroy() {
        System.gc();
        super.onDestroy();
    }
}