package com.lipl.youthconnect.youth_connect.pojo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by luminousinfoways on 09/10/15.
 */
public class DocumentAssign implements Parcelable {

    private int document_assign_id;
    private String document_master_id;
    private String m_district_id;
    private String user_id;
    private String created;
    private String modified;

    public int getDocument_assign_id() {
        return document_assign_id;
    }

    public void setDocument_assign_id(int document_assign_id) {
        this.document_assign_id = document_assign_id;
    }

    public String getDocument_master_id() {
        return document_master_id;
    }

    public void setDocument_master_id(String document_master_id) {
        this.document_master_id = document_master_id;
    }

    public String getM_district_id() {
        return m_district_id;
    }

    public void setM_district_id(String m_district_id) {
        this.m_district_id = m_district_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
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

    public static final Creator<DocumentAssign> CREATOR = new Creator<DocumentAssign>() {

        @Override
        public DocumentAssign createFromParcel(Parcel source) {
            return new DocumentAssign(source);
        }

        @Override
        public DocumentAssign[] newArray(int size) {
            DocumentAssign[] currentLocations = new DocumentAssign[size];
            return currentLocations;
        }
    };

    public DocumentAssign(Parcel in){
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(document_assign_id);
        dest.writeString(document_master_id);
        dest.writeString(m_district_id);
        dest.writeString(user_id);
        dest.writeString(created);
        dest.writeString(modified);
    }

    private void readFromParcel(Parcel in){
        document_assign_id = in.readInt();
        document_master_id = in.readString();
        m_district_id = in.readString();
        user_id = in.readString();
        created = in.readString();
        modified = in.readString();
    }
}