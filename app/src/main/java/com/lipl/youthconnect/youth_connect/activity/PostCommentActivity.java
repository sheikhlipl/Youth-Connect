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

import com.lipl.youthconnect.youth_connect.R;
import com.lipl.youthconnect.youth_connect.util.ActivityIndicator;
import com.lipl.youthconnect.youth_connect.util.Constants;
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
import java.util.List;

public class PostCommentActivity extends ActionBarActivity implements View.OnClickListener {

    private static Toolbar mToolbar = null;
    private QuestionAndAnswer questionAndAnswer = null;
    private SwipeRefreshLayout swipe_refresh_layout;

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

                break;
            case R.id.btnReset:
                tvAnswer.setText("");
                break;
            default:
                break;
        }
    }

    private String getCurrentDateAndTime(){
        String year = Util.getCurrentYear()+"";
        String month = Util.getCurrentMonth()+"";
        String day = Util.getCurrentDay()+"";
        String hour = Util.geCurrentHour()+"";
        String minute = Util.geCurrentMinute()+"";
        String second = Util.geCurrentSecond()+"";

        if(second != null && second.trim().length() == 1){
            second = "0" + second;
        }

        // Format : 2015-12-12 12:12:12
        String dateTime = year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second;
        Log.i("Date Time", dateTime);

        return dateTime;
    }

    private String getJsonObjectData(String commentDescription, String userId, String date){

        /*
        *
        * {"qa_answer_id":"","user_id":"1","comment_description":"dfg hdfjhgkh","comment_date":"2015-12-12 12:12:12"}
        * */

        try {
            JSONArray array = new JSONArray();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("qa_comment_id", "");
            jsonObject.put("user_id", userId);
            jsonObject.put("comment_description", commentDescription);
            jsonObject.put("comment_date", date);
            array.put(jsonObject);

            return array.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Async task to get sync camp table from server
     * */
    private class PostAnswerAsync extends AsyncTask<String, Void, Boolean> {

        private static final String TAG = "PostAnswerAsync";
        private ProgressDialog progressDialog = null;
        private boolean isChangePassword = false;
        private ActivityIndicator activityIndicator = ActivityIndicator.ctor(PostCommentActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            swipe_refresh_layout.setRefreshing(true);
            Button btnPostComment = (Button) findViewById(R.id.btnPost);
            btnPostComment.setEnabled(false);
            btnPostComment.setClickable(false);

            if(activityIndicator == null){
                activityIndicator = new ActivityIndicator(PostCommentActivity.this);
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
                String answerJson = params[2];

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
                        .appendQueryParameter("data[Qa][user_id]", _user_id)
                        .appendQueryParameter("response", "mobile")
                        .appendQueryParameter("data[QaAnswer]", "")
                        .appendQueryParameter("data[QaComment]", answerJson);

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

            if(activityIndicator == null){
                activityIndicator = new ActivityIndicator(PostCommentActivity.this);
            }
            activityIndicator.dismiss();

            if(isChangePassword){
                AlertDialog.Builder builder = new AlertDialog.Builder(PostCommentActivity.this, R.style.AppCompatAlertDialogStyle);
                builder.setTitle(getResources().getString(R.string.password_changed_title));
                builder.setMessage(getResources().getString(R.string.password_changed_description));
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent(PostCommentActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("Exit me", true);
                        startActivity(intent);
                        finish();
                    }
                });
                builder.show();

                return;
            }

            swipe_refresh_layout.setRefreshing(false);
            Button btnPostComment = (Button) findViewById(R.id.btnPost);
            btnPostComment.setEnabled(true);
            btnPostComment.setClickable(true);
            String dialogMessage = null;
            if(isSuccess){
                dialogMessage = "Posted successfully.";
                showAlertDialog(dialogMessage, "Post Comment", "Ok", true);
            } else{
                dialogMessage = "Sorry, failed to post your comment.\nPlease try again";
                showAlertDialog(dialogMessage, "Post Comment", "Ok", false);
            }

        }
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

    public static class PostCommentSuccessReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constants.POST_COMMENT_SUCCESS_ACTION)) {
                changeUIForSuccessPost();
            }

            if (intent.getAction().equals(Constants.POST_COMMENT_FAILURE_ACTION)) {
                changeUIForFailurePost();
            }
        }
    }

    private static void changeUIForSuccessPost(){

    }

    private static void changeUIForFailurePost(){

    }
}