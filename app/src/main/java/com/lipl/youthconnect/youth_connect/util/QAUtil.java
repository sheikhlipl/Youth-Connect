package com.lipl.youthconnect.youth_connect.util;

import android.content.Context;
import android.os.Parcel;
import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;
import com.lipl.youthconnect.youth_connect.pojo.Answer;
import com.lipl.youthconnect.youth_connect.pojo.Comment;
import com.lipl.youthconnect.youth_connect.pojo.Doc;
import com.lipl.youthconnect.youth_connect.pojo.Question;
import com.lipl.youthconnect.youth_connect.pojo.QuestionAndAnswer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Android Luminous on 2/16/2016.
 */
public class QAUtil {

    private static final String TAG = "QAUtil";

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
            int asked_by_user_id = (Integer) document.getProperty(DatabaseUtil.QA_ASKED_BY_USER_ID);

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
            question.setPost_date(postDate);
            question.setQus_asked_by_user_id(asked_by_user_id);
            question.setIs_uploaded(is_uploaded);

            QuestionAndAnswer questionAndAnswer = new QuestionAndAnswer(Parcel.obtain());
            questionAndAnswer.setQuestion(question);
            questionAndAnswer.setAnswerList(answerArrayList);
            questionAndAnswer.setCommentList(commentArrayList);
            questionAndAnswer.setIs_published(is_published);
            questionAndAnswer.setQid(id);

            return questionAndAnswer;
        } catch(Exception exception){
            Log.e("QAUtil", "getQAFromDocument()", exception);
        }
        return null;
    }

    public static void updateDocForEditQuestion(Database database, String documentId,
                           String qus_title, String qus_desc, int asked_by_user_id){
        Document document = database.getDocument(documentId);
        try {
            // Update the document with more data
            Map<String, Object> updatedProperties = new HashMap<String, Object>();
            updatedProperties.putAll(document.getProperties());
            updatedProperties.put(DatabaseUtil.QA_TITLE, qus_title);
            updatedProperties.put(DatabaseUtil.QA_ASKED_BY_USER_ID, asked_by_user_id);
            updatedProperties.put(DatabaseUtil.QA_DESC, qus_desc);
            // Save to the Couchbase local Couchbase Lite DB
            document.putProperties(updatedProperties);
        } catch (CouchbaseLiteException e) {
            com.couchbase.lite.util.Log.e("QAUtil", "Error putting", e);
        } catch(Exception exception){
            Log.e("QAUtil", "updateDocument()", exception);
        }
    }

    public static void updateDocForEditAnswer(Database database, String documentId,
                                          List<Answer> answerList, int asked_by_user_id){
        Document document = database.getDocument(documentId);
        try {
            // Update the document with more data
            Map<String, Object> updatedProperties = new HashMap<String, Object>();
            updatedProperties.putAll(document.getProperties());
            updatedProperties.put(DatabaseUtil.QA_ANSWER, answerList);
            updatedProperties.put(DatabaseUtil.QA_ASKED_BY_USER_ID, asked_by_user_id);
            // Save to the Couchbase local Couchbase Lite DB
            document.putProperties(updatedProperties);
        } catch (CouchbaseLiteException e) {
            com.couchbase.lite.util.Log.e("QAUtil", "Error putting", e);
        } catch(Exception exception){
            Log.e("QAUtil", "updateDocument()", exception);
        }
    }

    public static void updateDocForPublish(Database database, String documentId,
                                           int is_publish){
        Document document = database.getDocument(documentId);
        try {
            // Update the document with more data
            Map<String, Object> updatedProperties = new HashMap<String, Object>();
            updatedProperties.putAll(document.getProperties());
            updatedProperties.put(DatabaseUtil.QA_IS_PUBLISHED, is_publish);
            // Save to the Couchbase local Couchbase Lite DB
            document.putProperties(updatedProperties);
        } catch (CouchbaseLiteException e) {
            com.couchbase.lite.util.Log.e("QAUtil", "Error putting", e);
        } catch(Exception exception){
            Log.e("QAUtil", "updateDocument()", exception);
        }
    }

    public static List<QuestionAndAnswer> getPendingQuestionAndAnswerList(Context context) throws
            CouchbaseLiteException, IOException, Exception{
        List<QuestionAndAnswer> questionAndAnswerList = new ArrayList<QuestionAndAnswer>();

        Database database = DatabaseUtil.getDatabaseInstance(context, Constants.YOUTH_CONNECT_DATABASE);
        List<String> allDocIds = DatabaseUtil.getAllDocumentIds(context);
        for(String doc_id : allDocIds){
            Document document = DatabaseUtil.getDocumentFromDocumentId(database, doc_id);
            QuestionAndAnswer questionAndAnswer = getQAFromDocument(document);
            if(questionAndAnswer != null && (questionAndAnswer.getAnswerList() == null
                    || questionAndAnswer.getAnswerList().size() <= 0)){
                questionAndAnswerList.add(questionAndAnswer);
            }
        }

        return questionAndAnswerList;
    }

    public static List<QuestionAndAnswer> getAnsweredQuestionAndAnswerList(Context context) throws
            CouchbaseLiteException, IOException, Exception{
        List<QuestionAndAnswer> questionAndAnswerList = new ArrayList<QuestionAndAnswer>();

        Database database = DatabaseUtil.getDatabaseInstance(context, Constants.YOUTH_CONNECT_DATABASE);
        List<String> allDocIds = DatabaseUtil.getAllDocumentIds(context);
        for(String doc_id : allDocIds){
            Document document = DatabaseUtil.getDocumentFromDocumentId(database, doc_id);
            QuestionAndAnswer questionAndAnswer = getQAFromDocument(document);
            if(questionAndAnswer != null && questionAndAnswer.getAnswerList() != null
                    && questionAndAnswer.getAnswerList().size() > 0){
                questionAndAnswerList.add(questionAndAnswer);
            }
        }

        return questionAndAnswerList;
    }

    public static List<QuestionAndAnswer> getPublishedQuestionAndAnswerList(Context context) throws
            CouchbaseLiteException, IOException, Exception{
        List<QuestionAndAnswer> questionAndAnswerList = new ArrayList<QuestionAndAnswer>();

        Database database = DatabaseUtil.getDatabaseInstance(context, Constants.YOUTH_CONNECT_DATABASE);
        List<String> allDocIds = DatabaseUtil.getAllDocumentIds(context);
        for(String doc_id : allDocIds){
            Document document = DatabaseUtil.getDocumentFromDocumentId(database, doc_id);
            QuestionAndAnswer questionAndAnswer = getQAFromDocument(document);
            if(questionAndAnswer != null && questionAndAnswer.getIs_published() ==1){
                questionAndAnswerList.add(questionAndAnswer);
            }
        }

        return questionAndAnswerList;
    }
}
