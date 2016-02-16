package com.lipl.youthconnect.youth_connect.pojo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by luminousinfoways on 09/10/15.
 */
public class State implements Parcelable {

    private int m_state_id;
    private String m_state;

    public int getM_state_id() {
        return m_state_id;
    }

    public void setM_state_id(int m_state_id) {
        this.m_state_id = m_state_id;
    }

    public String getM_state() {
        return m_state;
    }

    public void setM_state(String m_state) {
        this.m_state = m_state;
    }

    public static final Creator<State> CREATOR = new Creator<State>() {

        @Override
        public State createFromParcel(Parcel source) {
            return new State(source);
        }

        @Override
        public State[] newArray(int size) {
            State[] currentLocations = new State[size];
            return currentLocations;
        }
    };

    public State(Parcel in){
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(m_state_id);
        dest.writeString(m_state);
    }

    private void readFromParcel(Parcel in){
        m_state_id = in.readInt();
        m_state = in.readString();
    }
}