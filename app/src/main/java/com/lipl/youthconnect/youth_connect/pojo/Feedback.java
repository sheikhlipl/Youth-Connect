package com.lipl.youthconnect.youth_connect.pojo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by luminousinfoways on 09/10/15.
 */
public class Feedback implements Parcelable {

    private int reportID;
    private String reportTitle;
    private String reportType;
    private String reportShortDesc;
    private String reportCreatedBy;
    private String reportCreatedOn;
    private String LastDateofSubmission;
    private String LastResponseDate;
    private String color;
    private String FbAssignUserID;
    private String RecentSubmission;
    private String ShowSubmitButton;
    private String ShowRejectButton;
    private String ShowRejectMessage;
    private String ShowViewButton;
    private String reportStatus;
    private String noOfTimesReportSubmission;
    private String noOfTimesReportSubmitted;
    private String noOfTimesReportToBeSubmitted;
    private String canSubmitReportAfterDeadLine;

    public int getReportID() {
        return reportID;
    }

    public void setReportID(int reportID) {
        this.reportID = reportID;
    }

    public String getReportTitle() {
        return reportTitle;
    }

    public void setReportTitle(String reportTitle) {
        this.reportTitle = reportTitle;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getReportShortDesc() {
        return reportShortDesc;
    }

    public void setReportShortDesc(String reportShortDesc) {
        this.reportShortDesc = reportShortDesc;
    }

    public String getReportCreatedBy() {
        return reportCreatedBy;
    }

    public void setReportCreatedBy(String reportCreatedBy) {
        this.reportCreatedBy = reportCreatedBy;
    }

    public String getReportCreatedOn() {
        return reportCreatedOn;
    }

    public void setReportCreatedOn(String reportCreatedOn) {
        this.reportCreatedOn = reportCreatedOn;
    }

    public String getLastDateofSubmission() {
        return LastDateofSubmission;
    }

    public void setLastDateofSubmission(String lastDateofSubmission) {
        LastDateofSubmission = lastDateofSubmission;
    }

    public String getLastResponseDate() {
        return LastResponseDate;
    }

    public void setLastResponseDate(String lastResponseDate) {
        LastResponseDate = lastResponseDate;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getFbAssignUserID() {
        return FbAssignUserID;
    }

    public void setFbAssignUserID(String fbAssignUserID) {
        FbAssignUserID = fbAssignUserID;
    }

    public String getRecentSubmission() {
        return RecentSubmission;
    }

    public void setRecentSubmission(String recentSubmission) {
        RecentSubmission = recentSubmission;
    }

    public String getShowSubmitButton() {
        return ShowSubmitButton;
    }

    public void setShowSubmitButton(String showSubmitButton) {
        ShowSubmitButton = showSubmitButton;
    }

    public String getShowRejectButton() {
        return ShowRejectButton;
    }

    public void setShowRejectButton(String showRejectButton) {
        ShowRejectButton = showRejectButton;
    }

    public String getShowRejectMessage() {
        return ShowRejectMessage;
    }

    public void setShowRejectMessage(String showRejectMessage) {
        ShowRejectMessage = showRejectMessage;
    }

    public String getShowViewButton() {
        return ShowViewButton;
    }

    public void setShowViewButton(String showViewButton) {
        ShowViewButton = showViewButton;
    }

    public String getReportStatus() {
        return reportStatus;
    }

    public void setReportStatus(String reportStatus) {
        this.reportStatus = reportStatus;
    }

    public String getNoOfTimesReportSubmission() {
        return noOfTimesReportSubmission;
    }

    public void setNoOfTimesReportSubmission(String noOfTimesReportSubmission) {
        this.noOfTimesReportSubmission = noOfTimesReportSubmission;
    }

    public String getNoOfTimesReportSubmitted() {
        return noOfTimesReportSubmitted;
    }

    public void setNoOfTimesReportSubmitted(String noOfTimesReportSubmitted) {
        this.noOfTimesReportSubmitted = noOfTimesReportSubmitted;
    }

    public String getNoOfTimesReportToBeSubmitted() {
        return noOfTimesReportToBeSubmitted;
    }

    public void setNoOfTimesReportToBeSubmitted(String noOfTimesReportToBeSubmitted) {
        this.noOfTimesReportToBeSubmitted = noOfTimesReportToBeSubmitted;
    }

    public String getCanSubmitReportAfterDeadLine() {
        return canSubmitReportAfterDeadLine;
    }

    public void setCanSubmitReportAfterDeadLine(String canSubmitReportAfterDeadLine) {
        this.canSubmitReportAfterDeadLine = canSubmitReportAfterDeadLine;
    }

    public static final Creator<Feedback> CREATOR = new Creator<Feedback>() {

        @Override
        public Feedback createFromParcel(Parcel source) {
            return new Feedback(source);
        }

        @Override
        public Feedback[] newArray(int size) {
            Feedback[] currentLocations = new Feedback[size];
            return currentLocations;
        }
    };

    public Feedback(Parcel in){
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(reportID);
        dest.writeString(reportTitle);
        dest.writeString(reportType);
        dest.writeString(reportShortDesc);
        dest.writeString(reportCreatedBy);
        dest.writeString(reportCreatedOn);
        dest.writeString(LastDateofSubmission);
        dest.writeString(LastResponseDate);
        dest.writeString(color);
        dest.writeString(FbAssignUserID);
        dest.writeString(RecentSubmission);
        dest.writeString(ShowSubmitButton);
        dest.writeString(ShowRejectButton);
        dest.writeString(ShowRejectMessage);
        dest.writeString(ShowViewButton);
        dest.writeString(noOfTimesReportSubmission);
        dest.writeString(noOfTimesReportSubmitted);
        dest.writeString(noOfTimesReportToBeSubmitted);
        dest.writeString(canSubmitReportAfterDeadLine);
    }

    private void readFromParcel(Parcel in){
        reportID = in.readInt();
        reportTitle = in.readString();
        reportType = in.readString();
        reportShortDesc = in.readString();
        reportCreatedBy = in.readString();
        reportCreatedOn = in.readString();
        LastDateofSubmission = in.readString();
        LastResponseDate = in.readString();
        color = in.readString();
        FbAssignUserID = in.readString();
        RecentSubmission = in.readString();
        ShowSubmitButton = in.readString();
        ShowRejectButton = in.readString();
        ShowRejectMessage = in.readString();
        ShowViewButton = in.readString();
        noOfTimesReportSubmission = in.readString();
        noOfTimesReportSubmitted = in.readString();
        noOfTimesReportToBeSubmitted = in.readString();
        canSubmitReportAfterDeadLine = in.readString();
    }
}