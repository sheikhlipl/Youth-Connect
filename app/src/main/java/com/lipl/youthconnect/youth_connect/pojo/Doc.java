package com.lipl.youthconnect.youth_connect.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by luminousinfoways on 09/10/15.
 */
public class Doc implements Serializable {

    private String doc_id;
    private String doc_title;
    private String doc_purpose;
    private List<FileToUpload> fileToUploads;
    private String created;
    private int is_uploaded;
    private String created_by_user_name;
    private int created_by_user_id;
    private List<AssignedToUSer> doc_assigned_to_user_ids;

    public String getDoc_id() {
        return doc_id;
    }

    public void setDoc_id(String doc_id) {
        this.doc_id = doc_id;
    }

    public String getDoc_title() {
        return doc_title;
    }

    public void setDoc_title(String doc_title) {
        this.doc_title = doc_title;
    }

    public String getDoc_purpose() {
        return doc_purpose;
    }

    public void setDoc_purpose(String doc_purpose) {
        this.doc_purpose = doc_purpose;
    }

    public List<FileToUpload> getFileToUploads() {
        return fileToUploads;
    }

    public void setFileToUploads(List<FileToUpload> fileToUploads) {
        this.fileToUploads = fileToUploads;
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

    public String getCreated_by_user_name() {
        return created_by_user_name;
    }

    public void setCreated_by_user_name(String created_by_user_name) {
        this.created_by_user_name = created_by_user_name;
    }

    public int getCreated_by_user_id() {
        return created_by_user_id;
    }

    public void setCreated_by_user_id(int created_by_user_id) {
        this.created_by_user_id = created_by_user_id;
    }

    public List<AssignedToUSer> getDoc_assigned_to_user_ids() {
        return doc_assigned_to_user_ids;
    }

    public void setDoc_assigned_to_user_ids(List<AssignedToUSer> doc_assigned_to_user_ids) {
        this.doc_assigned_to_user_ids = doc_assigned_to_user_ids;
    }

   /* public static final Creator<Doc> CREATOR = new Creator<Doc>() {

        @Override
        public Doc createFromParcel(Parcel source) {
            return new Doc(source);
        }

        @Override
        public Doc[] newArray(int size) {
            Doc[] currentLocations = new Doc[size];
            return currentLocations;
        }
    };

    public Doc(Parcel in){
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(doc_title);
        dest.writeString(doc_purpose);
        dest.writeTypedList(fileToUploads);
        dest.writeTypedList(doc_assigned_to_user_ids);
        dest.writeString(created);
        dest.writeInt(is_uploaded);
        dest.writeString(created_by_user_name);
        dest.writeInt(created_by_user_id);
        dest.writeString(doc_id);
    }

    private void readFromParcel(Parcel in){
        doc_title = in.readString();
        doc_purpose = in.readString();

        if (fileToUploads == null) {
            fileToUploads = new ArrayList();
        }
        in.readTypedList(fileToUploads, FileToUpload.CREATOR);

        if (doc_assigned_to_user_ids == null) {
            doc_assigned_to_user_ids = new ArrayList();
        }
        in.readTypedList(doc_assigned_to_user_ids, AssignedToUSer.CREATOR);

        created = in.readString();
        is_uploaded = in.readInt();
        created_by_user_name = in.readString();
        created_by_user_id = in.readInt();
        doc_id = in.readString();
    }*/
}