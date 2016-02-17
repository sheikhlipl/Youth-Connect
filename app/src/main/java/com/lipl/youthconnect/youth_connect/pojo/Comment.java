package com.lipl.youthconnect.youth_connect.pojo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by luminousinfoways on 09/10/15.
 */
public class Comment implements Parcelable {

    private int qa_comment_id;
    //private String qa_id;
    private String comment_description;
    //private String user_id;
    //private String user_name;
    //private String comment_date;
    private String created;
    private int comment_by_user_id;
    //private String modified;
    public String is_published;
    private String comment_by_user_name;
    private int is_uploaded;

    public int getComment_by_user_id() {
        return comment_by_user_id;
    }

    public void setComment_by_user_id(int comment_by_user_id) {
        this.comment_by_user_id = comment_by_user_id;
    }

    public int getQa_comment_id() {
        return qa_comment_id;
    }

    public void setQa_comment_id(int qa_comment_id) {
        this.qa_comment_id = qa_comment_id;
    }

    public String getComment_description() {
        return comment_description;
    }

    public void setComment_description(String comment_description) {
        this.comment_description = comment_description;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getIs_published() {
        return is_published;
    }

    public void setIs_published(String is_published) {
        this.is_published = is_published;
    }

    public String getComment_by_user_name() {
        return comment_by_user_name;
    }

    public void setComment_by_user_name(String comment_by_user_name) {
        this.comment_by_user_name = comment_by_user_name;
    }

    public int getIs_uploaded() {
        return is_uploaded;
    }

    public void setIs_uploaded(int is_uploaded) {
        this.is_uploaded = is_uploaded;
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
        dest.writeString(comment_description);
        dest.writeString(created);
        dest.writeInt(is_uploaded);
        dest.writeString(is_published);
        dest.writeInt(is_uploaded);
        dest.writeString(comment_by_user_name);
        dest.writeInt(comment_by_user_id);
    }

    private void readFromParcel(Parcel in){
        qa_comment_id = in.readInt();
        comment_description = in.readString();
        created = in.readString();
        is_uploaded = in.readInt();
        is_published = in.readString();
        is_uploaded = in.readInt();
        comment_by_user_name = in.readString();
        comment_by_user_id = in.readInt();
    }
}