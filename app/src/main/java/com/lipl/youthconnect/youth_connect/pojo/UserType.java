package com.lipl.youthconnect.youth_connect.pojo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by luminousinfoways on 09/10/15.
 */
public class UserType implements Parcelable {

    private int m_user_type_id;
    private String m_user_type;

    public int getM_user_type_id() {
        return m_user_type_id;
    }

    public void setM_user_type_id(int m_user_type_id) {
        this.m_user_type_id = m_user_type_id;
    }

    public String getM_user_type() {
        return m_user_type;
    }

    public void setM_user_type(String m_user_type) {
        this.m_user_type = m_user_type;
    }

    public static final Creator<UserType> CREATOR = new Creator<UserType>() {

        @Override
        public UserType createFromParcel(Parcel source) {
            return new UserType(source);
        }

        @Override
        public UserType[] newArray(int size) {
            UserType[] currentLocations = new UserType[size];
            return currentLocations;
        }
    };

    public UserType(Parcel in){
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(m_user_type_id);
        dest.writeString(m_user_type);
    }

    private void readFromParcel(Parcel in){
        m_user_type_id = in.readInt();
        m_user_type = in.readString();
    }
}