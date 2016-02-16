package com.lipl.youthconnect.youth_connect.pojo;

import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class FieldOptions implements Parcelable {
	
	private List<Options> options;
    public List<Options> getOptions() {
        return options;
    }

    public void setOptions(List<Options> options) {
        this.options = options;
    }

    public FieldOptions(Parcel in){
		readFromParcel(in);
	}
	
	public static final Creator<FieldOptions> CREATOR = new Creator<FieldOptions>() {
		
		@Override
		public FieldOptions[] newArray(int size) {
			return new FieldOptions[size];
		}
		
		@Override
		public FieldOptions createFromParcel(Parcel source) {
			return new FieldOptions(source);
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeList(options);
	}
	
	@SuppressWarnings("unchecked")
	private void readFromParcel(Parcel in){
		options = in.readArrayList(Options.class.getClassLoader());
	}
}
