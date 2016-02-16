package com.lipl.youthconnect.youth_connect.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luminousinfoways on 10/12/15.
 */
public class ShowcaseEvent implements Parcelable {

    private String name;
    private List<String> photosList;
    private List<String> videosList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getPhotosList() {
        return photosList;
    }

    public void setPhotosList(List<String> photosList) {
        this.photosList = photosList;
    }

    public List<String> getVideosList() {
        return videosList;
    }

    public void setVideosList(List<String> videosList) {
        this.videosList = videosList;
    }

    public static final Creator<ShowcaseEvent> CREATOR = new Creator<ShowcaseEvent>() {

        @Override
        public ShowcaseEvent createFromParcel(Parcel source) {
            return new ShowcaseEvent(source);
        }

        @Override
        public ShowcaseEvent[] newArray(int size) {
            ShowcaseEvent[] showcaseEvents = new ShowcaseEvent[size];
            return showcaseEvents;
        }
    };

    public ShowcaseEvent(Parcel in){
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);

        if (photosList == null) {
            photosList = new ArrayList();
        }
        dest.writeStringList(photosList);

        if (videosList == null) {
            videosList = new ArrayList();
        }
        dest.writeStringList(videosList);
    }

    private void readFromParcel(Parcel in){
        name = in.readString();
        if (photosList == null) {
            photosList = new ArrayList();
        }
        in.readStringList(photosList);
        if (videosList == null) {
            videosList = new ArrayList();
        }
        in.readStringList(videosList);
    }
}