package com.lipl.youthconnect.youth_connect.pojo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by luminousinfoways on 09/10/15.
 */
public class Answer implements Parcelable {

    private int qa_answer_id;
    private int answer_by_user_id;
    private String qadmin_description;
    private String created;
    private int is_uploaded;
    private String answer_by_user_name;

    public int getAnswer_by_user_id() {
        return answer_by_user_id;
    }

    public void setAnswer_by_user_id(int answer_by_user_id) {
        this.answer_by_user_id = answer_by_user_id;
    }

    public int getQa_answer_id() {
        return qa_answer_id;
    }

    public void setQa_answer_id(int qa_answer_id) {
        this.qa_answer_id = qa_answer_id;
    }

    public String getQadmin_description() {
        return qadmin_description;
    }

    public void setQadmin_description(String qadmin_description) {
        this.qadmin_description = qadmin_description;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public int getIs_uploaded() {
        return is_uploaded;
    }

    public void setIs_uploaded(int is_uploaded) {
        this.is_uploaded = is_uploaded;
    }

    public String getAnswer_by_user_name() {
        return answer_by_user_name;
    }

    public void setAnswer_by_user_name(String answer_by_user_name) {
        this.answer_by_user_name = answer_by_user_name;
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
        dest.writeString(qadmin_description);
        dest.writeString(created);
        dest.writeString(answer_by_user_name);
        dest.writeInt(is_uploaded);
        dest.writeInt(answer_by_user_id);
    }

    private void readFromParcel(Parcel in){
        qa_answer_id = in.readInt();
        qadmin_description = in.readString();
        created = in.readString();
        answer_by_user_name = in.readString();
        is_uploaded = in.readInt();
        answer_by_user_id = in.readInt();
    }
}