package com.lipl.youthconnect.youth_connect.pojo;

import android.view.View;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Suhasini on 02-May-15.
 */
public class FormField implements Serializable {

    int fieldId;
    List<View> viewList;
    FieldDetails fieldDetails;

    public List<View> getViewList() {
        return viewList;
    }

    public void setViewList(List<View> viewList) {
        this.viewList = viewList;
    }

    public int getFieldId() {
        return fieldId;
    }

    public void setFieldId(int fieldId) {
        this.fieldId = fieldId;
    }

    public FieldDetails getFieldDetails() {
        return fieldDetails;
    }

    public void setFieldDetails(FieldDetails fieldDetails) {
        this.fieldDetails = fieldDetails;
    }
}
