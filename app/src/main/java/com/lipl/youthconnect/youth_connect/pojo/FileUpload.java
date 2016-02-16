package com.lipl.youthconnect.youth_connect.pojo;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.lipl.youthconnect.youth_connect.util.YouthConnectSingleTone;

/**
 * Created by luminousinfoways on 09/10/15.
 */
public class FileUpload implements Parcelable {

    private String filePath;
    private int file_type;
    private String uriPath;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getFile_type() {
        return file_type;
    }

    public void setFile_type(int file_type) {
        this.file_type = file_type;
    }

    public String getUriPath() {
        return uriPath;
    }

    public void setUriPath(String uriPath) {
        this.uriPath = uriPath;
    }

    public static final Creator<FileUpload> CREATOR = new Creator<FileUpload>() {

        @Override
        public FileUpload createFromParcel(Parcel source) {
            return new FileUpload(source);
        }

        @Override
        public FileUpload[] newArray(int size) {
            FileUpload[] currentLocations = new FileUpload[size];
            return currentLocations;
        }
    };

    public FileUpload(Parcel in){
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(filePath);
        dest.writeInt(file_type);
        dest.writeString(uriPath);
    }

    private void readFromParcel(Parcel in){
        filePath = in.readString();
        file_type = in.readInt();
        uriPath = in.readString();
    }
}