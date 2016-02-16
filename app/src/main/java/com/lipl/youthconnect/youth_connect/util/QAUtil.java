package com.lipl.youthconnect.youth_connect.util;

import android.os.Parcel;
import android.util.Log;

import com.couchbase.lite.Document;
import com.lipl.youthconnect.youth_connect.pojo.Answer;
import com.lipl.youthconnect.youth_connect.pojo.Comment;
import com.lipl.youthconnect.youth_connect.pojo.Question;
import com.lipl.youthconnect.youth_connect.pojo.QuestionAndAnswer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
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
                    int is_uploaded = jsonObject.getInt(DatabaseUtil.ANSWER_IS_UPLOADED);

                    Answer answer = new Answer(Parcel.obtain());
                    answer.setCreated(timeStamp);
                    answer.setQadmin_description(content);
                    answer.setAnswer_by_user_name(answer_by_user_name);
                    answer.setIs_uploaded(is_uploaded);
                    answerList.add(answer);
                }
                return answerList;
            } catch(JSONException exception){
                Log.e("QA Util", "getAnswerListFromPreviousData()", exception);
            }
        }

        return answerList;
    }

    public static List<Comment> getCommentListFromJson(String previousData){
        List<Comment> comments = new ArrayList<Comment>();

        if(previousData != null && previousData.trim().length() > 0){
            try {
                JSONArray jsonArray = new JSONArray(previousData);
                for (int i = 0; i < jsonArray.length(); i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String answer_by_user_name = jsonObject.getString(DatabaseUtil.COMMENT_BY_USER_NAME);
                    String content = jsonObject.getString(DatabaseUtil.COMMENT_CONTENT);
                    String timeStamp = jsonObject.getString(DatabaseUtil.COMMENT_TIMESTAMP);
                    int is_uploaded = jsonObject.getInt(DatabaseUtil.COMMENT_IS_UPLOADED);

                    Comment comment = new Comment(Parcel.obtain());
                    comment.setCreated(timeStamp);
                    comment.setComment_description(content);
                    comment.setComment_by_user_name(answer_by_user_name);
                    comment.setIs_uploaded(is_uploaded);
                    comments.add(comment);
                }
                return comments;
            } catch(JSONException exception){
                Log.e("QA Util", "getAnswerListFromPreviousData()", exception);
            }
        }

        return comments;
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
            int is_uploaded = (Integer) document.getProperty(DatabaseUtil.QA_IS_UPLOADED);

            ArrayList<LinkedHashMap<String, String>> answerJson = (ArrayList<LinkedHashMap<String, String>>) document.getProperty(DatabaseUtil.QA_ANSWER);
            ArrayList<LinkedHashMap<String, String>> commentJson = (ArrayList<LinkedHashMap<String, String>>) document.getProperty(DatabaseUtil.QA_COMMENT);

            ArrayList<Answer> answerArrayList = new ArrayList<Answer>();
            if(answerJson != null && answerJson.size() > 0){
                for(int i = 0; i <answerJson.size(); i++){
                    LinkedHashMap<String, String> answers = (LinkedHashMap<String, String>) (answerJson.get(i));
                    if((answers.get("qadmin_description") != null)
                            && (answers.get("answer_by_user_name") != null)
                        && (answers.get("answer_by_user_name") != null)) {
                        Answer answer = new Answer(Parcel.obtain());
                        answer.setQadmin_description(answers.get("qadmin_description"));
                        answer.setAnswer_by_user_name(answers.get("answer_by_user_name"));
                        answer.setCreated(answers.get("created"));
                        answerArrayList.add(answer);
                    }
                }
            }

            ArrayList<Comment> commentArrayList = new ArrayList<Comment>();
            if(commentJson != null && commentJson.size() > 0){
                for(int i = 0; i <commentJson.size(); i++){
                    LinkedHashMap<String, String> comments = (LinkedHashMap<String, String>) (commentJson.get(i));
                    if((comments.get("comment_description") != null)
                            && (comments.get("comment_by_user_name") != null)
                            && (comments.get("created") != null)) {
                        Comment answer = new Comment(Parcel.obtain());
                        answer.setComment_description(comments.get("comment_description"));
                        answer.setComment_by_user_name(comments.get("comment_by_user_name"));
                        answer.setCreated(comments.get("created"));
                        commentArrayList.add(answer);
                    }
                }
            }

            String id = document.getId();

            Question question = new Question(Parcel.obtain());
            question.setQa_id(id);
            question.setQa_title(title);
            question.setQa_description(description);
            question.setAskedBy(user_name);
            question.setIs_answer(is_answered);
            question.setIs_publish(is_published);
            question.setPost_date(postDate);
            question.setIs_uploaded(is_uploaded);

            QuestionAndAnswer questionAndAnswer = new QuestionAndAnswer(Parcel.obtain());
            questionAndAnswer.setQuestion(question);
            questionAndAnswer.setAnswerList(answerArrayList);
            questionAndAnswer.setCommentList(commentArrayList);
            questionAndAnswer.setQid(id);

            return questionAndAnswer;
        } catch(Exception exception){
            Log.e("QAUtil", "getQAFromDocument()", exception);
        }
        return null;
    }
}
