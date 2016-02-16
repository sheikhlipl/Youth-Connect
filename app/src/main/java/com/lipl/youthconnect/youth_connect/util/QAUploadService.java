package com.lipl.youthconnect.youth_connect.util;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.lipl.youthconnect.youth_connect.pojo.Comment;
import com.lipl.youthconnect.youth_connect.pojo.PendingFileToUpload;
import com.lipl.youthconnect.youth_connect.pojo.QuestionAndAnswer;

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
import java.util.Date;
import java.util.List;

/**
 * Created by user on 31-01-2016.
 */
public class QAUploadService extends Service {
    private static final String TAG = "BroadcastService";
    public static final String BROADCAST_ACTION = "com.websmithing.broadcasttest.displayevent.qa";
    private final Handler handler = new Handler();
    Intent intent;
    int counter = 0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        intent = new Intent(BROADCAST_ACTION);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        handler.removeCallbacks(sendUpdatesToUI);
        handler.postDelayed(sendUpdatesToUI, 1000); // 1 second
    }

    private Runnable sendUpdatesToUI = new Runnable() {
        public void run() {
        if(Util.getNetworkConnectivityStatus(QAUploadService.this)) {

        }
        }
    };

    private void DisplayLoggingInfo(PendingFileToUpload pendingFileToUpload, int status) {

        intent.putExtra("time", new Date().toLocaleString());
        intent.putExtra("counter", String.valueOf(++counter));
        intent.putExtra("pendingFileToUpload", pendingFileToUpload);
        intent.putExtra("status", status);
        sendBroadcast(intent);
    }

    private String getJsonObjectDataForComment(List<Comment> commentList, String userId, String date){

        if(commentList == null || commentList.size() <= 0){
            return null;
        }

        /*
        *
        * {"qa_answer_id":"","user_id":"1","comment_description":"dfg hdfjhgkh","comment_date":"2015-12-12 12:12:12"}
        * */

        try {
            JSONArray array = new JSONArray();
            for(Comment comment : commentList) {

                if(comment != null) {
                    String commentDescription = comment.getComment_description();
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("qa_comment_id", "");
                    jsonObject.put("user_id", userId);
                    jsonObject.put("comment_description", commentDescription);
                    jsonObject.put("comment_date", date);
                    array.put(jsonObject);
                }
            }

            return array.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getJsonObjectDataForAnswer(String answer, String userId, String date, String answerId){

        /*
        *
        * {"qa_answer_id":"","user_id":"1","qadmin_description":"dfg hdfjhgkh","post_date":"2015-12-12 12:12:12"}
        * */

        try {
            JSONArray array = new JSONArray();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("qa_answer_id", answerId);
            jsonObject.put("user_id", userId);
            jsonObject.put("qadmin_description", answer);
            jsonObject.put("post_date", date);
            array.put(jsonObject);

            return array.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean doNetworkCall(QuestionAndAnswer questionAndAnswer){

        if(questionAndAnswer == null){
            return false;
        }

        String api_key = getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 1).getString(Constants.SP_USER_API_KEY, null);
        int userid = getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 1).getInt(Constants.SP_USER_ID, 0);

        if(api_key == null){
            return false;
        }

        if(userid <= 0){
            return false;
        }

        try {

            String qa_id = "";
            if(questionAndAnswer.getIs_id_generated_for_offline() == 0){
                qa_id = questionAndAnswer.getQid()+"";
            }
            String dateTime = Util.getCurrentDateAndTime(); // Format : 2015-12-12 12:12:12
            String user_id_of_user_asked_question = questionAndAnswer.getQuestion_by_user_id()+"";
            String answerId = "";
            if(questionAndAnswer != null
                    && questionAndAnswer.getAnswerList() != null
                    && questionAndAnswer.getAnswerList().get(0) != null){
                answerId = questionAndAnswer.getAnswerList().get(0).getQa_answer_id()+"";
            }

            String answer = "";
            if(questionAndAnswer != null && questionAndAnswer.getAnswerList() != null
                    && questionAndAnswer.getAnswerList().get(0) != null
                    && questionAndAnswer.getAnswerList().get(0).getQadmin_description() != null){
                answer = questionAndAnswer.getAnswerList().get(0).getQadmin_description();
            }
            String answerJson = "";
            if(answer != null && answer.trim().length() > 0
                    && getJsonObjectDataForAnswer(answer, userid+"", dateTime, answerId) != null){
                answerJson = getJsonObjectDataForAnswer(answer, userid+"", dateTime, answerId);
            }
            String commentJson = "";
            List<Comment> commentList = questionAndAnswer.getCommentList();
            if(getJsonObjectDataForComment(commentList, userid+"", dateTime) != null){
                commentJson = getJsonObjectDataForComment(commentList, userid+"", dateTime);
            }

            String qa_title = "";
            if(questionAndAnswer != null && questionAndAnswer.getQuestion() != null){
                qa_title = questionAndAnswer.getQuestion().getQa_title();
            }

            String qa_description = "";
            if(questionAndAnswer != null && questionAndAnswer.getQuestion() != null){
                qa_description = questionAndAnswer.getQuestion().getQa_description();
            }

            if(qa_title == null || qa_title.trim().length() <= 0){
                return false;
            }
            if(qa_description == null || qa_description.trim().length() <= 0){
                return false;
            }

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

            String isAns = "N";
            if(answerJson != null && answerJson.length() > 0) {
                isAns = "Y";
            } else {
                isAns = "N";
            }

            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("data[Qa][qa_id]", qa_id)
                    .appendQueryParameter("data[Qa][user_id]", user_id_of_user_asked_question)
                    .appendQueryParameter("data[Qa][qa_title]", qa_title)
                    .appendQueryParameter("data[Qa][qa_description]", qa_description)
                    .appendQueryParameter("response", "mobile")
                    .appendQueryParameter("data[QaAnswer]", answerJson)
                    .appendQueryParameter("data[QaComment]", commentJson)
                    .appendQueryParameter("data[Qa][is_answer]", isAns);

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
                return false;
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
                        return false;
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

}