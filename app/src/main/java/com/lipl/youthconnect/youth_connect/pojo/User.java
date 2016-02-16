package com.lipl.youthconnect.youth_connect.pojo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by luminousinfoways on 09/10/15.
 */
public class User implements Parcelable {

    int user_id;
    String full_name;
    String username;
    String password;
    String m_user_type_id;
    String m_desg_id;
    String m_state_id;
    String m_district_id;
    String m_organization_id;
    String m_block_id;
    String mobile_no;
    String email_id;
    String is_active;
    String created;
    String modified;
    String api_key;

    public String getApi_key() {
        return api_key;
    }

    public void setApi_key(String api_key) {
        this.api_key = api_key;
    }

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getM_user_type_id() {
        return m_user_type_id;
    }

    public void setM_user_type_id(String m_user_type_id) {
        this.m_user_type_id = m_user_type_id;
    }

    public String getM_desg_id() {
        return m_desg_id;
    }

    public void setM_desg_id(String m_desg_id) {
        this.m_desg_id = m_desg_id;
    }

    public String getM_state_id() {
        return m_state_id;
    }

    public void setM_state_id(String m_state_id) {
        this.m_state_id = m_state_id;
    }

    public String getM_district_id() {
        return m_district_id;
    }

    public void setM_district_id(String m_district_id) {
        this.m_district_id = m_district_id;
    }

    public String getM_organization_id() {
        return m_organization_id;
    }

    public void setM_organization_id(String m_organization_id) {
        this.m_organization_id = m_organization_id;
    }

    public String getMobile_no() {
        return mobile_no;
    }

    public void setMobile_no(String mobile_no) {
        this.mobile_no = mobile_no;
    }

    public String getEmail_id() {
        return email_id;
    }

    public void setEmail_id(String email_id) {
        this.email_id = email_id;
    }

    public String getIs_active() {
        return is_active;
    }

    public void setIs_active(String is_active) {
        this.is_active = is_active;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public String getM_block_id() {
        return m_block_id;
    }

    public void setM_block_id(String m_block_id) {
        this.m_block_id = m_block_id;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {

        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            User[] currentLocations = new User[size];
            return currentLocations;
        }
    };

    public User(Parcel in){
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
        dest.writeString(username);
        dest.writeString(password);
        dest.writeString(m_user_type_id);
        dest.writeString(m_desg_id);
        dest.writeString(m_state_id);
        dest.writeString(m_district_id);
        dest.writeString(m_block_id);
        dest.writeString(m_organization_id);
        dest.writeString(mobile_no);
        dest.writeString(email_id);
        dest.writeString(is_active);
        dest.writeString(created);
        dest.writeString(modified);
        dest.writeString(api_key);
    }

    private void readFromParcel(Parcel in){
        user_id = in.readInt();
        full_name = in.readString();
        username = in.readString();
        password = in.readString();
        m_user_type_id = in.readString();
        m_desg_id = in.readString();
        m_state_id = in.readString();
        m_district_id = in.readString();
        m_block_id = in.readString();
        m_organization_id = in.readString();
        mobile_no = in.readString();
        email_id = in.readString();
        is_active = in.readString();
        created = in.readString();
        modified = in.readString();
        api_key = in.readString();
    }
}