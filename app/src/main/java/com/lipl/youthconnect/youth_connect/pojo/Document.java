package com.lipl.youthconnect.youth_connect.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luminousinfoways on 09/10/15.
 */
public class Document implements Parcelable {

    private DocumentMaster documentMaster;
    private String userFullName;
    private int doc_master_id;
    private List<DocumentAssign> documentAssignList;
    private List<DocumentUpload> documentUploadList;

    public int getDoc_master_id() {
        return doc_master_id;
    }

    public void setDoc_master_id(int doc_master_id) {
        this.doc_master_id = doc_master_id;
    }

    public DocumentMaster getDocumentMaster() {
        return documentMaster;
    }

    public void setDocumentMaster(DocumentMaster documentMaster) {
        this.documentMaster = documentMaster;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public List<DocumentAssign> getDocumentAssignList() {
        return documentAssignList;
    }

    public void setDocumentAssignList(List<DocumentAssign> documentAssignList) {
        this.documentAssignList = documentAssignList;
    }

    public List<DocumentUpload> getDocumentUploadList() {
        return documentUploadList;
    }

    public void setDocumentUploadList(List<DocumentUpload> documentUploadList) {
        this.documentUploadList = documentUploadList;
    }

    public static final Creator<Document> CREATOR = new Creator<Document>() {

        @Override
        public Document createFromParcel(Parcel source) {
            return new Document(source);
        }

        @Override
        public Document[] newArray(int size) {
            Document[] currentLocations = new Document[size];
            return currentLocations;
        }
    };

    public Document(Parcel in){
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(documentMaster, flags);
        dest.writeString(userFullName);
        dest.writeTypedList(documentAssignList);
        dest.writeTypedList(documentUploadList);
        dest.writeInt(doc_master_id);
    }

    private void readFromParcel(Parcel in){
        documentMaster = in.readParcelable(DocumentMaster.class.getClassLoader());
        userFullName = in.readString();
        if (documentAssignList == null) {
            documentAssignList = new ArrayList();
        }
        in.readTypedList(documentAssignList, DocumentAssign.CREATOR);

        if (documentUploadList == null) {
            documentUploadList = new ArrayList();
        }
        in.readTypedList(documentUploadList, DocumentUpload.CREATOR);
        doc_master_id = in.readInt();
    }
}