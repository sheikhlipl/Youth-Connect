package com.lipl.youthconnect.youth_connect.pojo;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.InputStream;
import java.io.Serializable;

/**
 * Created by luminousinfoways on 09/10/15.
 */
public class FileToUpload implements Serializable {

    private String mime_type;
    private Uri inputStream;
    private String file_name;
    private String download_link_url;

    public String getDownload_link_url() {
        return download_link_url;
    }

    public void setDownload_link_url(String download_link_url) {
        this.download_link_url = download_link_url;
    }

    public String getMime_type() {
        return mime_type;
    }

    public void setMime_type(String mime_type) {
        this.mime_type = mime_type;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public Uri getInputStream() {
        return inputStream;
    }

    public void setInputStream(Uri inputStream) {
        this.inputStream = inputStream;
    }
}