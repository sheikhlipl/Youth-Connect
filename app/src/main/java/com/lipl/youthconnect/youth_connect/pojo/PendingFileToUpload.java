package com.lipl.youthconnect.youth_connect.pojo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by luminousinfoways on 09/10/15.
 */
public class PendingFileToUpload implements Parcelable {

    private String user_id;
    private String m_desg_id;
    private String user_type_id;
    private String title;
    private String purpose;
    private String assignData;
    //private String jsonData;
    private String filePath;
    private String fileUri;
    private int fileType;
    private int is_uploaded;
    private int doc_id;
    private int isDocIdFromServer;

    public int getIsDocIdFromServer() {
        return isDocIdFromServer;
    }

    public void setIsDocIdFromServer(int isDocIdFromServer) {
        this.isDocIdFromServer = isDocIdFromServer;
    }

    public int getDoc_id() {
        return doc_id;
    }

    public void setDoc_id(int doc_id) {
        this.doc_id = doc_id;
    }

    public int getIs_uploaded() {
        return is_uploaded;
    }

    public void setIs_uploaded(int is_uploaded) {
        this.is_uploaded = is_uploaded;
    }

    public int getFileType() {
        return fileType;
    }

    public void setFileType(int fileType) {
        this.fileType = fileType;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileUri() {
        return fileUri;
    }

    public void setFileUri(String fileUri) {
        this.fileUri = fileUri;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getAssignData() {
        return assignData;
    }

    public void setAssignData(String assignData) {
        this.assignData = assignData;
    }

    /*public String getJsonData() {
        return jsonData;
    }

    public void setJsonData(String jsonData) {
        this.jsonData = jsonData;
    }*/

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getM_desg_id() {
        return m_desg_id;
    }

    public void setM_desg_id(String m_desg_id) {
        this.m_desg_id = m_desg_id;
    }

    public String getUser_type_id() {
        return user_type_id;
    }

    public void setUser_type_id(String user_type_id) {
        this.user_type_id = user_type_id;
    }

    public static final Creator<PendingFileToUpload> CREATOR = new Creator<PendingFileToUpload>() {

        @Override
        public PendingFileToUpload createFromParcel(Parcel source) {
            return new PendingFileToUpload(source);
        }

        @Override
        public PendingFileToUpload[] newArray(int size) {
            PendingFileToUpload[] currentLocations = new PendingFileToUpload[size];
            return currentLocations;
        }
    };

    public PendingFileToUpload(Parcel in){
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(user_id);
        dest.writeString(m_desg_id);
        dest.writeString(user_type_id);
        dest.writeString(title);
        dest.writeString(purpose);
        dest.writeString(assignData);
        //dest.writeString(jsonData);
        dest.writeString(filePath);
        dest.writeString(fileUri);
        dest.writeInt(fileType);
        dest.writeInt(is_uploaded);
        dest.writeInt(doc_id);
        dest.writeInt(isDocIdFromServer);
    }

    private void readFromParcel(Parcel in){
        user_id = in.readString();
        m_desg_id = in.readString();
        user_type_id = in.readString();
        title = in.readString();
        purpose = in.readString();
        assignData = in.readString();
        //jsonData = in.readString();
        filePath = in.readString();
        fileUri = in.readString();
        fileType = in.readInt();
        is_uploaded = in.readInt();
        doc_id = in.readInt();
        isDocIdFromServer = in.readInt();
    }
}