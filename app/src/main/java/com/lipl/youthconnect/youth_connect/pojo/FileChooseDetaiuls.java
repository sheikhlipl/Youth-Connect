package com.lipl.youthconnect.youth_connect.pojo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by luminousinfoways on 09/10/15.
 */
public class FileChooseDetaiuls implements Parcelable {

    private String base64Data;
    private String extension;
    private String fileType;
    private String fileName;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getBase64Data() {
        return base64Data;
    }

    public void setBase64Data(String base64Data) {
        this.base64Data = base64Data;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public static final Creator<FileChooseDetaiuls> CREATOR = new Creator<FileChooseDetaiuls>() {

        @Override
        public FileChooseDetaiuls createFromParcel(Parcel source) {
            return new FileChooseDetaiuls(source);
        }

        @Override
        public FileChooseDetaiuls[] newArray(int size) {
            FileChooseDetaiuls[] currentLocations = new FileChooseDetaiuls[size];
            return currentLocations;
        }
    };

    public FileChooseDetaiuls(Parcel in){
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(base64Data);
        dest.writeString(extension);
        dest.writeString(fileType);
        dest.writeString(fileName);
    }

    private void readFromParcel(Parcel in){
        base64Data = in.readString();
        extension = in.readString();
        fileType = in.readString();
        fileName = in.readString();
    }
}