package com.lipl.youthconnect.youth_connect.pojo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by luminousinfoways on 09/10/15.
 */
public class Desg implements Parcelable {

    private int m_desg_id;
    private String m_desg_nm;

    public int getM_desg_id() {
        return m_desg_id;
    }

    public void setM_desg_id(int m_desg_id) {
        this.m_desg_id = m_desg_id;
    }

    public String getM_desg_nm() {
        return m_desg_nm;
    }

    public void setM_desg_nm(String m_desg_nm) {
        this.m_desg_nm = m_desg_nm;
    }

    public static final Creator<Desg> CREATOR = new Creator<Desg>() {

        @Override
        public Desg createFromParcel(Parcel source) {
            return new Desg(source);
        }

        @Override
        public Desg[] newArray(int size) {
            Desg[] currentLocations = new Desg[size];
            return currentLocations;
        }
    };

    public Desg(Parcel in){
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(m_desg_id);
        dest.writeString(m_desg_nm);
    }

    private void readFromParcel(Parcel in){
        m_desg_id = in.readInt();
        m_desg_nm = in.readString();
    }
}