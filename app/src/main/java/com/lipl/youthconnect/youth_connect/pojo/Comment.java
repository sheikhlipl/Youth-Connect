package com.lipl.youthconnect.youth_connect.pojo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by luminousinfoways on 09/10/15.
 */
public class Comment implements Parcelable {

    private int qa_comment_id;
    private String qa_id;
    private String comment_description;
    private String user_id;
    private String user_name;
    private String comment_date;
    private String created;
    private String modified;
    public String is_published;
    private String comment_by_user_name;
    private int is_uploaded;

    public String getComment_by_user_name() {
        return comment_by_user_name;
    }

    public void setComment_by_user_name(String comment_by_user_name) {
        this.comment_by_user_name = comment_by_user_name;
    }

    public String getIs_published() {
        return is_published;
    }

    public void setIs_published(String is_published) {
        this.is_published = is_published;
    }

    public int getIs_uploaded() {
        return is_uploaded;
    }

    public void setIs_uploaded(int is_uploaded) {
        this.is_uploaded = is_uploaded;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public int getQa_comment_id() {
        return qa_comment_id;
    }

    public void setQa_comment_id(int qa_comment_id) {
        this.qa_comment_id = qa_comment_id;
    }

    public String getQa_id() {
        return qa_id;
    }

    public void setQa_id(String qa_id) {
        this.qa_id = qa_id;
    }

    public String getComment_description() {
        return comment_description;
    }

    public void setComment_description(String comment_description) {
        this.comment_description = comment_description;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getComment_date() {
        return comment_date;
    }

    public void setComment_date(String comment_date) {
        this.comment_date = comment_date;
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

    public static final Creator<Comment> CREATOR = new Creator<Comment>() {

        @Override
        public Comment createFromParcel(Parcel source) {
            return new Comment(source);
        }

        @Override
        public Comment[] newArray(int size) {
            Comment[] currentLocations = new Comment[size];
            return currentLocations;
        }
    };

    public Comment(Parcel in){
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(qa_comment_id);
        dest.writeString(qa_id);
        dest.writeString(comment_description);
        dest.writeString(user_id);
        dest.writeString(comment_date);
        dest.writeString(created);
        dest.writeString(modified);
        dest.writeString(user_name);
        dest.writeInt(is_uploaded);
        dest.writeString(is_published);
        dest.writeInt(is_uploaded);
        dest.writeString(comment_by_user_name);
    }

    private void readFromParcel(Parcel in){
        qa_comment_id = in.readInt();
        qa_id = in.readString();
        comment_description = in.readString();
        user_id = in.readString();
        comment_date = in.readString();
        created = in.readString();
        modified = in.readString();
        user_name = in.readString();
        is_uploaded = in.readInt();
        is_published = in.readString();
        is_uploaded = in.readInt();
        comment_by_user_name = in.readString();
    }
}