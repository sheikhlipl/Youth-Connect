package com.lipl.youthconnect.youth_connect.pojo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by luminousinfoways on 09/10/15.
 */
public class Notification implements Parcelable {

    private int notification_id;
    private String module_name;
    private String module_id;
    private String from_user_id;
    private String from_desgs_id;
    private String from_user_type_id;
    private String to_user_id;
    private String to_desgs_id;
    private String to_user_type_id;
    private String to_read_user_id;
    private String notification_type;
    private String notification;
    private String created;
    private String modified;
    private User user;
    private int isNew;

    public int getIsNew() {
        return isNew;
    }

    public void setIsNew(int isNew) {
        this.isNew = isNew;
    }

    public int getNotification_id() {
        return notification_id;
    }

    public void setNotification_id(int notification_id) {
        this.notification_id = notification_id;
    }

    public String getModule_name() {
        return module_name;
    }

    public void setModule_name(String module_name) {
        this.module_name = module_name;
    }

    public String getModule_id() {
        return module_id;
    }

    public void setModule_id(String module_id) {
        this.module_id = module_id;
    }

    public String getFrom_user_id() {
        return from_user_id;
    }

    public void setFrom_user_id(String from_user_id) {
        this.from_user_id = from_user_id;
    }

    public String getFrom_desgs_id() {
        return from_desgs_id;
    }

    public void setFrom_desgs_id(String from_desgs_id) {
        this.from_desgs_id = from_desgs_id;
    }

    public String getFrom_user_type_id() {
        return from_user_type_id;
    }

    public void setFrom_user_type_id(String from_user_type_id) {
        this.from_user_type_id = from_user_type_id;
    }

    public String getTo_user_id() {
        return to_user_id;
    }

    public void setTo_user_id(String to_user_id) {
        this.to_user_id = to_user_id;
    }

    public String getTo_desgs_id() {
        return to_desgs_id;
    }

    public void setTo_desgs_id(String to_desgs_id) {
        this.to_desgs_id = to_desgs_id;
    }

    public String getTo_user_type_id() {
        return to_user_type_id;
    }

    public void setTo_user_type_id(String to_user_type_id) {
        this.to_user_type_id = to_user_type_id;
    }

    public String getTo_read_user_id() {
        return to_read_user_id;
    }

    public void setTo_read_user_id(String to_read_user_id) {
        this.to_read_user_id = to_read_user_id;
    }

    public String getNotification_type() {
        return notification_type;
    }

    public void setNotification_type(String notification_type) {
        this.notification_type = notification_type;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public static final Creator<Notification> CREATOR = new Creator<Notification>() {

        @Override
        public Notification createFromParcel(Parcel source) {
            return new Notification(source);
        }

        @Override
        public Notification[] newArray(int size) {
            Notification[] currentLocations = new Notification[size];
            return currentLocations;
        }
    };

    public Notification(Parcel in){
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(notification_id);
        dest.writeString(module_name);
        dest.writeString(module_id);
        dest.writeString(from_user_id);
        dest.writeString(from_desgs_id);
        dest.writeString(from_user_type_id);
        dest.writeString(to_user_id);
        dest.writeString(to_desgs_id);
        dest.writeString(to_user_type_id);
        dest.writeString(to_read_user_id);
        dest.writeString(notification_type);
        dest.writeString(notification);
        dest.writeString(created);
        dest.writeString(modified);
        dest.writeParcelable(user, flags);
        dest.writeInt(isNew);
    }

    private void readFromParcel(Parcel in){

        notification_id = in.readInt();
        module_name = in.readString();
        module_id = in.readString();
        from_user_id = in.readString();
        from_desgs_id = in.readString();
        from_user_type_id = in.readString();
        to_user_id = in.readString();
        to_desgs_id = in.readString();
        to_user_type_id = in.readString();
        to_read_user_id = in.readString();
        notification_type = in.readString();
        notification = in.readString();
        created = in.readString();
        modified = in.readString();
        user = in.readParcelable(User.class.getClassLoader());
        isNew = in.readInt();
    }
}