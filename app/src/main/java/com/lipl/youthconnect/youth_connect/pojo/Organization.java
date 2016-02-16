package com.lipl.youthconnect.youth_connect.pojo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by luminousinfoways on 09/10/15.
 */
public class Organization implements Parcelable {

    private int m_organization_id;
    private String organization_name;

    public int getM_organization_id() {
        return m_organization_id;
    }

    public void setM_organization_id(int m_organization_id) {
        this.m_organization_id = m_organization_id;
    }

    public String getOrganization_name() {
        return organization_name;
    }

    public void setOrganization_name(String organization_name) {
        this.organization_name = organization_name;
    }

    public static final Creator<Organization> CREATOR = new Creator<Organization>() {

        @Override
        public Organization createFromParcel(Parcel source) {
            return new Organization(source);
        }

        @Override
        public Organization[] newArray(int size) {
            Organization[] currentLocations = new Organization[size];
            return currentLocations;
        }
    };

    public Organization(Parcel in){
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(m_organization_id);
        dest.writeString(organization_name);
    }

    private void readFromParcel(Parcel in){
        m_organization_id = in.readInt();
        organization_name = in.readString();
    }
}