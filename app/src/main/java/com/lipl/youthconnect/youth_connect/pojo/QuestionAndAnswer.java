package com.lipl.youthconnect.youth_connect.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by luminousinfoways on 09/10/15.
 */
public class QuestionAndAnswer implements Parcelable {

    private String qid;
    private Question question;
    private User user;
    private List<Answer> answerList;
    private List<Comment> commentList;
    private int question_by_user_id;
    private int is_uploaded;
    private int is_id_generated_for_offline;
    private String answerJson;

    public String getAnswerJson() {
        return answerJson;
    }

    public void setAnswerJson(String answerJson) {
        this.answerJson = answerJson;
    }

    public int getIs_id_generated_for_offline() {
        return is_id_generated_for_offline;
    }

    public void setIs_id_generated_for_offline(int is_id_generated_for_offline) {
        this.is_id_generated_for_offline = is_id_generated_for_offline;
    }

    public int getIs_uploaded() {
        return is_uploaded;
    }

    public void setIs_uploaded(int is_uploaded) {
        this.is_uploaded = is_uploaded;
    }

    public int getQuestion_by_user_id() {
        return question_by_user_id;
    }

    public void setQuestion_by_user_id(int question_by_user_id) {
        this.question_by_user_id = question_by_user_id;
    }

    public String getQid() {
        return qid;
    }

    public void setQid(String qid) {
        this.qid = qid;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Answer> getAnswerList() {
        return answerList;
    }

    public void setAnswerList(List<Answer> answerList) {
        this.answerList = answerList;
    }

    public List<Comment> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<Comment> commentList) {
        this.commentList = commentList;
    }

    public static final Creator<QuestionAndAnswer> CREATOR = new Creator<QuestionAndAnswer>() {

        @Override
        public QuestionAndAnswer createFromParcel(Parcel source) {
            return new QuestionAndAnswer(source);
        }

        @Override
        public QuestionAndAnswer[] newArray(int size) {
            QuestionAndAnswer[] currentLocations = new QuestionAndAnswer[size];
            return currentLocations;
        }
    };

    public QuestionAndAnswer(Parcel in){
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(qid);
        dest.writeParcelable(question, flags);
        dest.writeParcelable(user, flags);
        dest.writeTypedList(answerList);
        dest.writeTypedList(commentList);
        dest.writeInt(question_by_user_id);
        dest.writeInt(is_uploaded);
        dest.writeInt(is_id_generated_for_offline);
        dest.writeString(answerJson);
    }

    private void readFromParcel(Parcel in){
        qid = in.readString();
        question = (Question) in.readParcelable(Question.class.getClassLoader());
        user = (User) in.readParcelable(User.class.getClassLoader());

        if (answerList == null) {
            answerList = new ArrayList();
        }
        in.readTypedList(answerList, Answer.CREATOR);

        if (commentList == null) {
            commentList = new ArrayList();
        }
        in.readTypedList(commentList, Comment.CREATOR);
        question_by_user_id = in.readInt();
        is_uploaded = in.readInt();
        is_id_generated_for_offline = in.readInt();
        answerJson = in.readString();
    }
}