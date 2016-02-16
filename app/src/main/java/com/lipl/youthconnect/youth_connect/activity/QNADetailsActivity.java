package com.lipl.youthconnect.youth_connect.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lipl.youthconnect.youth_connect.R;
import com.lipl.youthconnect.youth_connect.util.ActivityIndicator;
import com.lipl.youthconnect.youth_connect.util.Constants;
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
import java.util.List;

/**
 * Created by luminousinfoways on 18/12/15.
 */
public class QNADetailsActivity extends ActionBarActivity implements View.OnClickListener {

    private static Toolbar mToolbar = null;
    private QuestionAndAnswer questionAndAnswer = null;
    private boolean isFromForum = false;
    private boolean isFromAnswered = false;
    private static final int REQ_ASK = 123;

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

        Button btnPostAnswer = (Button) findViewById(R.id.btnPostAnswer);
        btnPostAnswer.setOnClickListener(this);
        Button btnPostComment = (Button) findViewById(R.id.btnPostComment);
        btnPostComment.setOnClickListener(this);
        Button btnEditQus = (Button) findViewById(R.id.btnEditQus);
        btnEditQus.setOnClickListener(this);
        Button tvCommentsTitle = (Button) findViewById(R.id.tvCommentsTitle);
        tvCommentsTitle.setOnClickListener(this);

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
                btnPostAnswer.setText("Edit your answer");
            } else{
                btnPostAnswer.setText("Post your answer");
            }
        }

        int user_type = getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 1).getInt(Constants.SP_USER_TYPE, 2);
        Button btnPublish = (Button) findViewById(R.id.btnPublish);
        btnPublish.setOnClickListener(this);
        if(user_type == 1){
            btnPostAnswer.setVisibility(View.VISIBLE);
            if(isFromAnswered){
                btnPublish.setVisibility(View.VISIBLE);
                btnPostComment.setVisibility(View.VISIBLE);
            } else {
                btnPublish.setVisibility(View.GONE);
                btnPostComment.setVisibility(View.GONE);
            }
        } else{
            btnPostAnswer.setVisibility(View.GONE);
            btnPublish.setVisibility(View.GONE);
            if(isFromAnswered){
                btnPostComment.setVisibility(View.VISIBLE);
            } else {
                btnPostComment.setVisibility(View.GONE);
            }
        }

        if(isFromAnswered || isFromForum){
            btnEditQus.setVisibility(View.GONE);
        } else{
            btnEditQus.setVisibility(View.VISIBLE);
        }

        if(isFromAnswered == false && isFromForum == false){
            TextView tvAnswersTitle = (TextView) findViewById(R.id.tvAnswersTitle);
            tvAnswersTitle.setVisibility(View.GONE);
            LinearLayout layoutAnswerList = (LinearLayout) findViewById(R.id.layoutAnswerList);
            layoutAnswerList.setVisibility(View.GONE);
            Button _tvCommentsTitle = (Button) findViewById(R.id.tvCommentsTitle);
            _tvCommentsTitle.setVisibility(View.GONE);
        } else{
            TextView tvAnswersTitle = (TextView) findViewById(R.id.tvAnswersTitle);
            tvAnswersTitle.setVisibility(View.VISIBLE);
            LinearLayout layoutAnswerList = (LinearLayout) findViewById(R.id.layoutAnswerList);
            layoutAnswerList.setVisibility(View.VISIBLE);
            Button _tvCommentsTitle = (Button) findViewById(R.id.tvCommentsTitle);
            _tvCommentsTitle.setVisibility(View.VISIBLE);
        }

        if(isFromForum){
            LinearLayout layoutButton = (LinearLayout) findViewById(R.id.layoutButton);
            layoutButton.setVisibility(View.GONE);
            Button _btnPublish = (Button) findViewById(R.id.btnPublish);
            _btnPublish.setVisibility(View.GONE);
            Button _tvCommentsTitle = (Button) findViewById(R.id.tvCommentsTitle);
            _tvCommentsTitle.setVisibility(View.GONE);
        } else if(isFromAnswered){
            LinearLayout layoutButton = (LinearLayout) findViewById(R.id.layoutButton);
            layoutButton.setVisibility(View.VISIBLE);
            Button _btnPublish = (Button) findViewById(R.id.btnPublish);
            _btnPublish.setVisibility(View.VISIBLE);
            Button _btnEditQus = (Button) findViewById(R.id.btnEditQus);
            _btnEditQus.setVisibility(View.GONE);
        } else{
            LinearLayout layoutButton = (LinearLayout) findViewById(R.id.layoutButton);
            layoutButton.setVisibility(View.VISIBLE);
            Button _btnPublish = (Button) findViewById(R.id.btnPublish);
            _btnPublish.setVisibility(View.GONE);
            Button _btnEditQus = (Button) findViewById(R.id.btnEditQus);
            _btnEditQus.setVisibility(View.VISIBLE);
            Button _btnPostAnswer = (Button) findViewById(R.id.btnPostAnswer);
            _btnPostAnswer.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.btnPostAnswer:

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

                break;
            case R.id.btnPostComment:

                Intent intentComment = new Intent(QNADetailsActivity.this, PostCommentActivity.class);
                intentComment.putExtra(Constants.INTENT_KEY_QUESTION_AND_ANSWER, questionAndAnswer);
                startActivity(intentComment);
                //finish();

                break;

            case R.id.btnPublish:

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

                break;

            case R.id.btnEditQus:

                Intent intent = new  Intent(QNADetailsActivity.this, AskQuestionActivity.class);
                intent.putExtra(Constants.QUESTION_DETAILS, questionAndAnswer);
                startActivityForResult(intent, Constants.INTENT_QNADETAILS_TO_ASKQUESTION_REQUSET_CODE);

                break;

            case R.id.tvCommentsTitle:

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

                break;

            default:
                break;
        }
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

    @Override
    protected void onResume() {
        super.onResume();
        int isToFinish = getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 1).getInt(Constants.IS_ACTION_TAKEN_FOR_QA, 0);
        if(isToFinish == 1){
            YouthConnectSingleTone.getInstance().CURRENT_FRAGMENT_IN_QA = Constants.FRAGMENT_QA_SUB_FRAGMENT_ANSWERED;
            finish();
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
}