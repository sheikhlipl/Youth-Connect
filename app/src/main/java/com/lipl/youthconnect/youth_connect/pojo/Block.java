package com.lipl.youthconnect.youth_connect.pojo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by luminousinfoways on 09/10/15.
 */
public class Block implements Parcelable {

    private int m_block_id;
    private String m_block_nm;

    public int getM_block_id() {
        return m_block_id;
    }

    public void setM_block_id(int m_block_id) {
        this.m_block_id = m_block_id;
    }

    public String getM_block_nm() {
        return m_block_nm;
    }

    public void setM_block_nm(String m_block_nm) {
        this.m_block_nm = m_block_nm;
    }

    public static final Creator<Block> CREATOR = new Creator<Block>() {

        @Override
        public Block createFromParcel(Parcel source) {
            return new Block(source);
        }

        @Override
        public Block[] newArray(int size) {
            Block[] currentLocations = new Block[size];
            return currentLocations;
        }
    };

    public Block(Parcel in){
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(m_block_id);
        dest.writeString(m_block_nm);
    }

    private void readFromParcel(Parcel in){
        m_block_id = in.readInt();
        m_block_nm = in.readString();
    }
}