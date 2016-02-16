package com.lipl.youthconnect.youth_connect.pojo;

import android.os.Parcel;
import android.os.Parcelable;

public class Options implements Parcelable {
	
	private String optionName;
	private int optionId;
    private boolean isSelected;

    public String getOptionName() {
        return optionName;
    }

    public void setOptionName(String optionName) {
        this.optionName = optionName;
    }

    public int getOptionId() {
        return optionId;
    }

    public void setOptionId(int optionId) {
        this.optionId = optionId;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

	public Options(Parcel in){
		readFromParcel(in);
	}
	
	public static final Creator<Options> CREATOR = new Creator<Options>() {
		
		@Override
		public Options[] newArray(int size) {
			return new Options[size];
		}
		
		@Override
		public Options createFromParcel(Parcel source) {
			return new Options(source);
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(optionName);
		dest.writeInt(optionId);
        dest.writeByte((byte) (isSelected ? 1 : 0));
    }
	
	private void readFromParcel(Parcel in){
		optionName = in.readString();
		optionId = in.readInt();
        isSelected = in.readByte() != 0;
    }

}
