package com.lipl.youthconnect.youth_connect.pojo;

import java.io.Serializable;
import java.util.List;

public class ReportForm implements Serializable {//Parcelable {
	
	List<FormDetails> formDetailsList;
    List<FormField> formFieldList;
	//List<FieldDetails> fieldDetailsList;

    public List<FormDetails> getFormDetailsList() {
        return formDetailsList;
    }

    public void setFormDetailsList(List<FormDetails> formDetailsList) {
        this.formDetailsList = formDetailsList;
    }

    public List<FormField> getFormFieldList() {
        return formFieldList;
    }

    public void setFormFieldList(List<FormField> formFieldList) {
        this.formFieldList = formFieldList;
    }
//    public List<FieldDetails> getFieldDetailsList() {
//        return fieldDetailsList;
//    }
//
//    public void setFieldDetailsList(List<FieldDetails> fieldDetailsList) {
//        this.fieldDetailsList = fieldDetailsList;
//    }

    /*public ReportForm(Parcel in){
		readFromParcel(in);
	}
	
	public List<FormDetails> getFormDetailsList() {
		return formDetailsList;
	}

	public void setFormDetailsList(List<FormDetails> formDetailsList) {
		this.formDetailsList = formDetailsList;
	}

	/*public String getAssignUserID() {
		return AssignUserID;
	}

	public void setAssignUserID(String assignUserID) {
		AssignUserID = assignUserID;
	}

	public String getFbAssignUserId() {
		return fbAssignUserId;
	}

	public void setFbAssignUserId(String fbAssignUserId) {
		this.fbAssignUserId = fbAssignUserId;
	}

	public List<FieldDetails> getFieldDetailsList() {
		return fieldDetailsList;
	}

	public void setFieldDetailsList(List<FieldDetails> fieldDetailsList) {
		this.fieldDetailsList = fieldDetailsList;
	}

	public static final Parcelable.Creator<ReportForm> CREATOR = new Creator<ReportForm>() {
		
		@Override
		public ReportForm[] newArray(int size) {
			return new ReportForm[size];
		}
		
		@Override
		public ReportForm createFromParcel(Parcel source) {
			return new ReportForm(source);
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeList(formDetailsList);
		//dest.writeString(AssignUserID);
		//dest.writeString(fbAssignUserId);
		dest.writeList(fieldDetailsList);
	}
	
	@SuppressWarnings({ "unchecked", "unchecked" })
	private void readFromParcel(Parcel in){
		formDetailsList = in.readArrayList(FormDetails.class.getClassLoader());
		//AssignUserID = in.readString();
		//fbAssignUserId = in.readString();
		fieldDetailsList = in.readArrayList(FieldDetails.class.getClassLoader());
	}*/
}
