package com.lipl.youthconnect.youth_connect.pojo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by luminousinfoways on 09/10/15.
 */
public class Question implements Parcelable {

    private String qa_id;
    private String qa_title;
    private String qa_description;
    private String user_id;
    private int is_answer;
    private int is_publish;
    private String post_date;
    private String created;
    private String modified;
    private String askedBy;

    public String getAskedBy() {
        return askedBy;
    }

    public void setAskedBy(String askedBy) {
        this.askedBy = askedBy;
    }

    public String getQa_id() {
        return qa_id;
    }

    public void setQa_id(String qa_id) {
        this.qa_id = qa_id;
    }

    public String getQa_title() {
        return qa_title;
    }

    public void setQa_title(String qa_title) {
        this.qa_title = qa_title;
    }

    public String getQa_description() {
        return qa_description;
    }

    public void setQa_description(String qa_description) {
        this.qa_description = qa_description;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public int getIs_answer() {
        return is_answer;
    }

    public void setIs_answer(int is_answer) {
        this.is_answer = is_answer;
    }

    public int getIs_publish() {
        return is_publish;
    }

    public void setIs_publish(int is_publish) {
        this.is_publish = is_publish;
    }

    public String getPost_date() {
        return post_date;
    }

    public void setPost_date(String post_date) {
        this.post_date = post_date;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public static final Creator<Question> CREATOR = new Creator<Question>() {

        @Override
        public Question createFromParcel(Parcel source) {
            return new Question(source);
        }

        @Override
        public Question[] newArray(int size) {
            Question[] currentLocations = new Question[size];
            return currentLocations;
        }
    };

    public Question(Parcel in){
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(qa_id);
        dest.writeString(qa_title);
        dest.writeString(qa_description);
        dest.writeString(user_id);
        dest.writeInt(is_answer);
        dest.writeInt(is_publish);
        dest.writeString(post_date);
        dest.writeString(created);
        dest.writeString(modified);
        dest.writeString(askedBy);
    }

    private void readFromParcel(Parcel in){
        qa_id = in.readString();
        qa_title = in.readString();
        qa_description = in.readString();
        user_id = in.readString();
        is_answer = in.readInt();
        is_publish = in.readInt();
        post_date = in.readString();
        created = in.readString();
        modified = in.readString();
        askedBy = in.readString();
    }
}