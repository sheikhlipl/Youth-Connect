package com.lipl.youthconnect.youth_connect.util;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.lipl.youthconnect.youth_connect.pojo.Document;
import com.lipl.youthconnect.youth_connect.pojo.Feedback;
import com.lipl.youthconnect.youth_connect.pojo.Notification;
import com.lipl.youthconnect.youth_connect.pojo.ReportForm;
import com.lipl.youthconnect.youth_connect.pojo.User;

public class YouthConnectSingleTone {

	private static YouthConnectSingleTone instance = null;
	public List<Feedback> submitedReport = null;
	public List<Feedback> expierdReport = null;
	public List<Feedback> rejectedReport = null;
	public List<Feedback> pendingReport = null;
	public List<Feedback> allReport = null;
	public List<Notification> notificationListForNotificationPanel = null;
	public ReportForm mReportForm = null;
	public List<FileOption> fileOptionList = null;
	public List<Document> documentList = null;
	public List<Document> showcaseDocumentList = null;
	public List<User> nodalOfficerUsers = null;

	public Context context = null;

	public boolean isCameraCaptureActivityFinish = false;

	public int notificationCount = 0;
	public int notificationCountFromWeb = 0;
	
	public boolean isBackFromSubmit = false;
	public int currentFragmentOnMainActivity = Constants.FRAGMENT_HOME_DASHBOARD;

	public int CURRENT_FRAGMENT_IN_HOME = Constants.FRAGMENT_HOME_SUB_FRAGMENT_DASHBOARD;
	public int CURRENT_FRAGMENT_IN_QA = Constants.FRAGMENT_QA_SUB_FRAGMENT_FORUM;
	public int CURRENT_FRAGMENT_IN_MAIN_ACTIVITY = Constants.SECTION_HOME;
	public static int IS_FROM_QUESTION_ASK_WITH_SUCCESS = 0;

	public enum COMMENT_ASYNC_STATUS { IS_NORMAL, IS_PROCESSING, IS_COMPLETED_SUCCESSFULLY, IS_COMPLETED_FAILURE  };

	public COMMENT_ASYNC_STATUS mCommentAsyncStatus = COMMENT_ASYNC_STATUS.IS_NORMAL;

	public static int POST_COMMENT_ASYNC_COUNT = 0;

	public YouthConnectSingleTone(){
		submitedReport = new ArrayList<Feedback>();
		expierdReport = new ArrayList<Feedback>();
		rejectedReport = new ArrayList<Feedback>();
		pendingReport = new ArrayList<Feedback>();
		allReport = new ArrayList<Feedback>();
		notificationListForNotificationPanel = new ArrayList<Notification>();
		mReportForm = new ReportForm();
		fileOptionList = new ArrayList<FileOption>();
		documentList = new ArrayList<Document>();
		showcaseDocumentList = new ArrayList<Document>();
		nodalOfficerUsers = new ArrayList<User>();
	}
	
	public static YouthConnectSingleTone getInstance(){
		if(instance == null){
			instance = new YouthConnectSingleTone();
		}
		
		return instance;
	}
}