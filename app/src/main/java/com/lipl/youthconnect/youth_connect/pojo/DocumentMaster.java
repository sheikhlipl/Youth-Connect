package com.lipl.youthconnect.youth_connect.pojo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by luminousinfoways on 09/10/15.
 */
public class DocumentMaster implements Parcelable {

    private int document_master_id;
    private String document_title;
    private String document_purpose;
    private String user_id;
    private String m_user_type_id;
    private String m_desg_id;
    private String is_published;
    private String is_archive;
    private String created;
    private String modified;

    public int getDocument_master_id() {
        return document_master_id;
    }

    public void setDocument_master_id(int document_master_id) {
        this.document_master_id = document_master_id;
    }

    public String getDocument_title() {
        return document_title;
    }

    public void setDocument_title(String document_title) {
        this.document_title = document_title;
    }

    public String getDocument_purpose() {
        return document_purpose;
    }

    public void setDocument_purpose(String document_purpose) {
        this.document_purpose = document_purpose;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getM_user_type_id() {
        return m_user_type_id;
    }

    public void setM_user_type_id(String m_user_type_id) {
        this.m_user_type_id = m_user_type_id;
    }

    public String getM_desg_id() {
        return m_desg_id;
    }

    public void setM_desg_id(String m_desg_id) {
        this.m_desg_id = m_desg_id;
    }

    public String getIs_published() {
        return is_published;
    }

    public void setIs_published(String is_published) {
        this.is_published = is_published;
    }

    public String getIs_archive() {
        return is_archive;
    }

    public void setIs_archive(String is_archive) {
        this.is_archive = is_archive;
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

    public static final Creator<DocumentMaster> CREATOR = new Creator<DocumentMaster>() {

        @Override
        public DocumentMaster createFromParcel(Parcel source) {
            return new DocumentMaster(source);
        }

        @Override
        public DocumentMaster[] newArray(int size) {
            DocumentMaster[] currentLocations = new DocumentMaster[size];
            return currentLocations;
        }
    };

    public DocumentMaster(Parcel in){
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(document_master_id);
        dest.writeString(document_title);
        dest.writeString(document_purpose);
        dest.writeString(user_id);
        dest.writeString(m_user_type_id);
        dest.writeString(m_desg_id);
        dest.writeString(is_published);
        dest.writeString(is_archive);
        dest.writeString(created);
        dest.writeString(modified);
    }

    private void readFromParcel(Parcel in){

        document_master_id = in.readInt();
        document_title = in.readString();
        document_purpose = in.readString();
        user_id = in.readString();
        m_user_type_id = in.readString();
        m_desg_id = in.readString();
        is_published = in.readString();
        is_archive = in.readString();
        created = in.readString();
        modified = in.readString();
    }
}