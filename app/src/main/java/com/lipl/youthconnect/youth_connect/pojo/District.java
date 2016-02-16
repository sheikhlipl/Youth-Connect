package com.lipl.youthconnect.youth_connect.pojo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by luminousinfoways on 09/10/15.
 */
public class District implements Parcelable {

    private int m_district_id;
    private String m_district;
    private String modifiedDate;

    public String getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public int getM_district_id() {
        return m_district_id;
    }

    public void setM_district_id(int m_district_id) {
        this.m_district_id = m_district_id;
    }

    public String getM_district() {
        return m_district;
    }

    public void setM_district(String m_district) {
        this.m_district = m_district;
    }

    public static final Creator<District> CREATOR = new Creator<District>() {

        @Override
        public District createFromParcel(Parcel source) {
            return new District(source);
        }

        @Override
        public District[] newArray(int size) {
            District[] currentLocations = new District[size];
            return currentLocations;
        }
    };

    public District(Parcel in){
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(m_district_id);
        dest.writeString(m_district);
        dest.writeString(modifiedDate);
    }

    private void readFromParcel(Parcel in){
        m_district_id = in.readInt();
        m_district = in.readString();
        modifiedDate = in.readString();
    }
}