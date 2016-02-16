package com.lipl.youthconnect.youth_connect.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.lipl.youthconnect.youth_connect.util.DynamicForm;

public class FieldDetails implements Parcelable {
	
	private int fieldID;
	private DynamicForm.FieldType fieldType;
	private String labelName;
	private String fieldDefaultValue;
    private String fieldValue;
	private String is_multiple;
    private boolean markAsRed;
    //private List<View> viewList;
	//private String field_is_compulsory;
	private FiledValidation fieldValidation;
	private FieldOptions fieldOptions;

    private boolean isInvalid;
    private String errorMsg;

    //public List<View> getViewList() {
       // return viewList;
   // }

   // public void setViewList(List<View> viewList) {
       // this.viewList = viewList;
    //}


    public boolean isInvalid() {
        return isInvalid;
    }

    public void setIsInvalid(boolean isInvalid) {
        this.isInvalid = isInvalid;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public boolean isMarkAsRed() {
        return markAsRed;
    }

    public void setMarkAsRed(boolean markAsRed) {
        this.markAsRed = markAsRed;
    }

    public DynamicForm.FieldType getFieldType() {
        return fieldType;
    }

    public void setFieldType(DynamicForm.FieldType fieldType) {
        this.fieldType = fieldType;
    }

    public String getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }

    public int getFieldID() {
        return fieldID;
    }

    public void setFieldID(int fieldID) {
        this.fieldID = fieldID;
    }

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    public String getFieldDefaultValue() {
        return fieldDefaultValue;
    }

    public void setFieldDefaultValue(String fieldDefaultValue) {
        this.fieldDefaultValue = fieldDefaultValue;
    }

    public String getIs_multiple() {
        return is_multiple;
    }

    public void setIs_multiple(String is_multiple) {
        this.is_multiple = is_multiple;
    }

    public FiledValidation getFieldValidation() {
        return fieldValidation;
    }

    public void setFieldValidation(FiledValidation fieldValidation) {
        this.fieldValidation = fieldValidation;
    }

    public FieldOptions getFieldOptions() {
        return fieldOptions;
    }

    public void setFieldOptions(FieldOptions fieldOptions) {
        this.fieldOptions = fieldOptions;
    }

    public FieldDetails(Parcel in){
		readFromParcel(in);
	}
	
	public static final Creator<FieldDetails> CREATOR = new Creator<FieldDetails>() {
		
		@Override
		public FieldDetails[] newArray(int size) {
			return new FieldDetails[size];
		}
		
		@Override
		public FieldDetails createFromParcel(Parcel source) {
			return new FieldDetails(source);
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(fieldID);
		dest.writeSerializable(fieldType);
		dest.writeString(labelName);
		dest.writeString(fieldDefaultValue);
        dest.writeString(fieldValue);
		dest.writeString(is_multiple);
		dest.writeParcelable(fieldValidation, PARCELABLE_WRITE_RETURN_VALUE);
		dest.writeParcelable(fieldOptions, PARCELABLE_WRITE_RETURN_VALUE);
        dest.writeByte((byte) (markAsRed ? 1 : 0));
        dest.writeByte((byte) (isInvalid ? 1 : 0));
        dest.writeString(errorMsg);
	}
	
	private void readFromParcel(Parcel in){
		fieldID = in.readInt();
		fieldType = (DynamicForm.FieldType) in.readSerializable();
		labelName = in.readString();
		fieldDefaultValue = in.readString();
        fieldValue = in.readString();
		is_multiple = in.readString();
		fieldValidation = in.readParcelable(FiledValidation.class.getClassLoader());
		fieldOptions = in.readParcelable(FieldOptions.class.getClassLoader());
        markAsRed = in.readByte() != 0;
        isInvalid = in.readByte() != 0;
        errorMsg = in.readString();
	}
}
