package com.lipl.youthconnect.youth_connect.pojo;

import java.io.Serializable;

public class FormDetails implements Serializable {//Parcelable {
	
	private String formID;
	private String formName;
	//private String snapAllowed;
	//private String geoTaggingAllowed;

    public String getFormID() {
        return formID;
    }

    public void setFormID(String formID) {
        this.formID = formID;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }


	
	/*public FormDetails(Parcel in){
		readFromParcel(in);
	}
	
	public static final Parcelable.Creator<FormDetails> CREATOR = new Creator<FormDetails>() {
		
		@Override
		public FormDetails[] newArray(int size) {
			return new FormDetails[size];
		}
		
		@Override
		public FormDetails createFromParcel(Parcel source) {
			return new FormDetails(source);
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(formID);
		dest.writeString(formName);
		//dest.writeString(snapAllowed);
		//dest.writeString(geoTaggingAllowed);
	}
	
	public String getFormID() {
		return formID;
	}

	public void setFormID(String formID) {
		this.formID = formID;
	}

	public String getFormName() {
		return formName;
	}

	public void setFormName(String formName) {
		this.formName = formName;
	}

	/*public String getSnapAllowed() {
		return snapAllowed;
	}

	public void setSnapAllowed(String snapAllowed) {
		this.snapAllowed = snapAllowed;
	}

	public String getGeoTaggingAllowed() {
		return geoTaggingAllowed;
	}

	public void setGeoTaggingAllowed(String geoTaggingAllowed) {
		this.geoTaggingAllowed = geoTaggingAllowed;
	}

	private void readFromParcel(Parcel in){
		formID = in.readString();
		formName = in.readString();
		//snapAllowed = in.readString();
		//geoTaggingAllowed = in.readString();
	}*/
}