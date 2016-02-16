package com.lipl.youthconnect.youth_connect.pojo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by luminousinfoways on 09/10/15.
 */
public class DocumentUpload implements Parcelable {

    private int document_upload_id;
    private String document_master_id;
    private String upload_file;
    private String created;
    private String modified;
    int isDownloaded = 0;

    public int getIsDownloaded() {
        return isDownloaded;
    }

    public void setIsDownloaded(int isDownloaded) {
        this.isDownloaded = isDownloaded;
    }

    public int getDocument_upload_id() {
        return document_upload_id;
    }

    public void setDocument_upload_id(int document_upload_id) {
        this.document_upload_id = document_upload_id;
    }

    public String getDocument_master_id() {
        return document_master_id;
    }

    public void setDocument_master_id(String document_master_id) {
        this.document_master_id = document_master_id;
    }

    public String getUpload_file() {
        return upload_file;
    }

    public void setUpload_file(String upload_file) {
        this.upload_file = upload_file;
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

    public static final Creator<DocumentUpload> CREATOR = new Creator<DocumentUpload>() {

        @Override
        public DocumentUpload createFromParcel(Parcel source) {
            return new DocumentUpload(source);
        }

        @Override
        public DocumentUpload[] newArray(int size) {
            DocumentUpload[] currentLocations = new DocumentUpload[size];
            return currentLocations;
        }
    };

    public DocumentUpload(Parcel in){
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(document_upload_id);
        dest.writeString(document_master_id);
        dest.writeString(upload_file);
        dest.writeString(created);
        dest.writeString(modified);
        dest.writeInt(isDownloaded);
    }

    private void readFromParcel(Parcel in){
        document_upload_id = in.readInt();
        document_master_id = in.readString();
        upload_file = in.readString();
        created = in.readString();
        modified = in.readString();
        isDownloaded = in.readInt();
    }
}