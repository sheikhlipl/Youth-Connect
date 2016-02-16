package com.lipl.youthconnect.youth_connect.util;

import android.os.Parcel;
import android.util.Log;

import com.couchbase.lite.Document;
import com.lipl.youthconnect.youth_connect.pojo.Answer;
import com.lipl.youthconnect.youth_connect.pojo.Question;
import com.lipl.youthconnect.youth_connect.pojo.QuestionAndAnswer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Android Luminous on 2/16/2016.
 */
public class QAUtil {

    public static List<Answer> getAnswerListFromJson(String previousData){
        List<Answer> answerList = new ArrayList<Answer>();

        if(previousData != null && previousData.trim().length() > 0){
            try {
                JSONArray jsonArray = new JSONArray(previousData);
                for (int i = 0; i < jsonArray.length(); i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String answer_by_user_name = jsonObject.getString(DatabaseUtil.ANSWER_BY_USER_NAME);
                    String content = jsonObject.getString(DatabaseUtil.ANSWER_CONTENT);
                    String timeStamp = jsonObject.getString(DatabaseUtil.ANSWER_TIMESTAMP);

                    Answer answer = new Answer(Parcel.obtain());
                    answer.setCreated(timeStamp);
                    answer.setQadmin_description(content);
                    answer.setAnswer_by_user_name(answer_by_user_name);
                    answerList.add(answer);
                }
                return answerList;
            } catch(JSONException exception){
                Log.e("QA Util", "getAnswerListFromPreviousData()", exception);
            }
        }

        return answerList;
    }

    public static QuestionAndAnswer getQAFromDocument(Document document){

        try {
            String title = (String) document.getProperty(DatabaseUtil.QA_TITLE);
            String description = (String) document.getProperty(DatabaseUtil.QA_DESC);
            String updated_time_stamp = (String) document.getProperty(DatabaseUtil.QA_UPDATED_TIMESTAMP);

            Long timeStamp = Long.parseLong(updated_time_stamp);
            String postDate = Util.getDateAndTimeFromTimeStamp(timeStamp);
            String user_name = (String) document.getProperty(DatabaseUtil.QA_ASKED_BY_USER_NAME);
            int is_answered = (Integer) document.getProperty(DatabaseUtil.QA_IS_ANSWERED);
            int is_published = (Integer) document.getProperty(DatabaseUtil.QA_IS_PUBLISHED);
            String answerJson = (String) document.getProperty(DatabaseUtil.QA_ANSWER);
            String id = document.getId();

            Question question = new Question(Parcel.obtain());
            question.setQa_id(id);
            question.setQa_title(title);
            question.setQa_description(description);
            question.setAskedBy(user_name);
            question.setIs_answer(is_answered);
            question.setIs_publish(is_published);
            question.setPost_date(postDate);

            QuestionAndAnswer questionAndAnswer = new QuestionAndAnswer(Parcel.obtain());
            questionAndAnswer.setQuestion(question);
            questionAndAnswer.setAnswerJson(answerJson);
            questionAndAnswer.setQid(id);

            return questionAndAnswer;
        } catch(Exception exception){
            Log.e("QAUtil", "getQAFromDocument()", exception);
        }
        return null;
    }
}
