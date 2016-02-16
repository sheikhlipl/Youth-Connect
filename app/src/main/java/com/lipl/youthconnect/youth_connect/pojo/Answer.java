package com.lipl.youthconnect.youth_connect.pojo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by luminousinfoways on 09/10/15.
 */
public class Answer implements Parcelable {

    private int qa_answer_id;
    private String qa_id;
    private String qadmin_description;
    private String user_id;
    private String post_date;
    private String created;
    private String modified;
    private User user;
    private String answer_by_user_name;

    public String getAnswer_by_user_name() {
        return answer_by_user_name;
    }

    public void setAnswer_by_user_name(String answer_by_user_name) {
        this.answer_by_user_name = answer_by_user_name;
    }

    public int getQa_answer_id() {
        return qa_answer_id;
    }

    public void setQa_answer_id(int qa_answer_id) {
        this.qa_answer_id = qa_answer_id;
    }

    public String getQa_id() {
        return qa_id;
    }

    public void setQa_id(String qa_id) {
        this.qa_id = qa_id;
    }

    public String getQadmin_description() {
        return qadmin_description;
    }

    public void setQadmin_description(String qadmin_description) {
        this.qadmin_description = qadmin_description;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public static final Creator<Answer> CREATOR = new Creator<Answer>() {

        @Override
        public Answer createFromParcel(Parcel source) {
            return new Answer(source);
        }

        @Override
        public Answer[] newArray(int size) {
            Answer[] currentLocations = new Answer[size];
            return currentLocations;
        }
    };

    public Answer(Parcel in){
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(qa_answer_id);
        dest.writeString(qa_id);
        dest.writeString(qadmin_description);
        dest.writeString(user_id);
        dest.writeString(post_date);
        dest.writeString(created);
        dest.writeString(modified);
        dest.writeParcelable(user, flags);
        dest.writeString(answer_by_user_name);
    }

    private void readFromParcel(Parcel in){
        qa_answer_id = in.readInt();
        qa_id = in.readString();
        qadmin_description = in.readString();
        user_id = in.readString();
        post_date = in.readString();
        created = in.readString();
        modified = in.readString();
        user = in.readParcelable(User.class.getClassLoader());
        answer_by_user_name = in.readString();
    }
}