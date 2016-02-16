package com.lipl.youthconnect.youth_connect.pojo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by luminousinfoways on 09/10/15.
 */
public class Dashboard implements Parcelable {

    private int pennding_qsn;
    private int qsn_answered;
    private int pending_feedback;
    private int showcased_event;
    private int submitted_feedback;
    private int qsn_publish;
    private int count_user;

    public int getCount_user() {
        return count_user;
    }

    public void setCount_user(int count_user) {
        this.count_user = count_user;
    }

    public int getSubmitted_feedback() {
        return submitted_feedback;
    }

    public void setSubmitted_feedback(int submitted_feedback) {
        this.submitted_feedback = submitted_feedback;
    }

    public int getQsn_publish() {
        return qsn_publish;
    }

    public void setQsn_publish(int qsn_publish) {
        this.qsn_publish = qsn_publish;
    }

    public int getPennding_qsn() {
        return pennding_qsn;
    }

    public void setPennding_qsn(int pennding_qsn) {
        this.pennding_qsn = pennding_qsn;
    }

    public int getQsn_answered() {
        return qsn_answered;
    }

    public void setQsn_answered(int qsn_answered) {
        this.qsn_answered = qsn_answered;
    }

    public int getPending_feedback() {
        return pending_feedback;
    }

    public void setPending_feedback(int pending_feedback) {
        this.pending_feedback = pending_feedback;
    }

    public int getShowcased_event() {
        return showcased_event;
    }

    public void setShowcased_event(int showcased_event) {
        this.showcased_event = showcased_event;
    }

    public static final Creator<Dashboard> CREATOR = new Creator<Dashboard>() {

        @Override
        public Dashboard createFromParcel(Parcel source) {
            return new Dashboard(source);
        }

        @Override
        public Dashboard[] newArray(int size) {
            Dashboard[] currentLocations = new Dashboard[size];
            return currentLocations;
        }
    };

    public Dashboard(Parcel in){
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(pennding_qsn);
        dest.writeInt(qsn_answered);
        dest.writeInt(pending_feedback);
        dest.writeInt(showcased_event);
        dest.writeInt(submitted_feedback);
        dest.writeInt(qsn_publish);
        dest.writeInt(count_user);
    }

    private void readFromParcel(Parcel in){
        pennding_qsn = in.readInt();
        qsn_answered = in.readInt();
        pending_feedback = in.readInt();
        showcased_event = in.readInt();
        submitted_feedback = in.readInt();
        qsn_publish = in.readInt();
        count_user = in.readInt();
    }
}