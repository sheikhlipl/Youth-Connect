package com.lipl.youthconnect.youth_connect.pojo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by luminousinfoways on 09/10/15.
 */
public class NodalUser implements Parcelable {

    int user_id;
    String full_name;
    String m_district_id;

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getM_district_id() {
        return m_district_id;
    }

    public void setM_district_id(String m_district_id) {
        this.m_district_id = m_district_id;
    }

    public static final Creator<NodalUser> CREATOR = new Creator<NodalUser>() {

        @Override
        public NodalUser createFromParcel(Parcel source) {
            return new NodalUser(source);
        }

        @Override
        public NodalUser[] newArray(int size) {
            NodalUser[] currentLocations = new NodalUser[size];
            return currentLocations;
        }
    };

    public NodalUser(Parcel in){
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(user_id);
        dest.writeString(full_name);
        dest.writeString(m_district_id);
    }

    private void readFromParcel(Parcel in){
        user_id = in.readInt();
        full_name = in.readString();
        m_district_id = in.readString();
    }
}