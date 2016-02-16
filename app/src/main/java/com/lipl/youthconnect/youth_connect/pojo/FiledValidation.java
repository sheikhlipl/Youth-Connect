package com.lipl.youthconnect.youth_connect.pojo;

import android.os.Parcel;
import android.os.Parcelable;

public class FiledValidation implements Parcelable {
	
	private String min_range;
	private String max_range;
	private String validation_type;
	private String field_is_compulsory;
	
	/*	There are multiple no of validation type, those are
	1.	Numeric
	2.	Varchar
	3.	Alpha Numeric
	4.	Decimal
	5.	Email
	6.	Mobile
	7.	Pin
	8.	Name
	9.	Address*/

    public String getMin_range() {
        return min_range;
    }

    public void setMin_range(String min_range) {
        this.min_range = min_range;
    }

    public String getMax_range() {
        return max_range;
    }

    public void setMax_range(String max_range) {
        this.max_range = max_range;
    }

    public String getValidation_type() {
        return validation_type;
    }

    public void setValidation_type(String validation_type) {
        this.validation_type = validation_type;
    }

    public String getField_is_compulsory() {
        return field_is_compulsory;
    }

    public void setField_is_compulsory(String field_is_compulsory) {
        this.field_is_compulsory = field_is_compulsory;
    }

    public FiledValidation(Parcel in){
		readFromParcel(in);
	}
	
	public static final Creator<FiledValidation> CREATOR = new Creator<FiledValidation>() {
		
		@Override
		public FiledValidation[] newArray(int size) {
			return new FiledValidation[size];
		}
		
		@Override
		public FiledValidation createFromParcel(Parcel source) {
			return new FiledValidation(source);
		}
	};
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(min_range);
		dest.writeString(max_range);
		dest.writeString(validation_type);
		dest.writeString(field_is_compulsory);
	}
	
	private void readFromParcel(Parcel in){
		min_range = in.readString();
		max_range = in.readString();
		validation_type = in.readString();
		field_is_compulsory = in.readString();
	}

}
