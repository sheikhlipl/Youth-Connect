package com.lipl.youthconnect.youth_connect.pojo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by luminousinfoways on 09/10/15.
 */
public class CurrentLocation implements Parcelable {

    private String time;
    private String latitude;
    private String longitude;
    private String date;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public static final Creator<CurrentLocation> CREATOR = new Creator<CurrentLocation>() {

        @Override
        public CurrentLocation createFromParcel(Parcel source) {
            return new CurrentLocation(source);
        }

        @Override
        public CurrentLocation[] newArray(int size) {
            CurrentLocation[] currentLocations = new CurrentLocation[size];
            return currentLocations;
        }
    };

    public CurrentLocation(Parcel in){
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(time);
        dest.writeString(latitude);
        dest.writeString(longitude);
        dest.writeString(date);
    }

    private void readFromParcel(Parcel in){
        time = in.readString();
        latitude = in.readString();
        longitude = in.readString();
        date = in.readString();
    }
}
