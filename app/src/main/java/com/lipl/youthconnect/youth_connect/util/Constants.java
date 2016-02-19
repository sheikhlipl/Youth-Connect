package com.lipl.youthconnect.youth_connect.util;

/**
 * Created by user on 10-05-2015.
 */
public class Constants {

    public static final String LOCAL = "http://192.168.1.110:8686/sports_connect";
    public static final String CLOUD = "http://45.114.50.55:8686/sports_connect_demo";
    public static final String CLOUD_LOCAL = "http://45.114.50.55:8686/sports_connect";
    public static final String LIVE = "http://45.114.50.54:81/sports_connect_demo";// http://45.114.50.54:81/sports_connect_demo

    public static final String BASE_URL = LIVE;
    public static final String LOGIN_REQUEST_URL = "/WebServices/loginCheck";
    public static final String REPORT_LISTING_REQUEST_URL = "/MobileApps/reportListing/";
    public static final String REPORT_FORM_DETAILS_REQUEST_URL = "/MobileApps/reportFormDetails/";
    public static final String REPORT_SUBMIT_REQUEST_URL = "/MobileApps/reportEntryDetails/";
    public static final String QUESTION_ANSWER_LIST_REQUEST_URL = "/WebServices/questionList";
    public static final String QUESTION_ANSWER_LIST_WITHOUT_COMMENT_REQUEST_URL = "/WebServices/questionListNew";
    public static final String REQUEST_URL_COMMENT_LIST = "/WebServices/commentList";
    public static final String QUESTION_ASK_FORUM = "/Qas/qaAddForm";
    public static final String DOCUMENT_DOWNLOAD_REQUEST_URL = "/app/webroot/files/Documents/";
    public static final String REQUEST_DOC_LIST = "/WebServices/docList";
    public static final String REQUEST_URL_DIST_LIST = "/WebServices/districtList";
    public static final String REQUEST_NOTIFICATION_COUNT = "/CommonHtmlFields/getNotificationCount";
    public static final String REQUEST_NOTIFICATION_LIST = "/CommonHtmlFields/getNotification";
    public static final String REQUEST_DOC_UPLOAD = "/WebServices/adminDocUpload";
    public static final String REQUEST_DASHBOARD = "/WebServices/adminDashboard";
    public static final String REQUEST_URL_CHANGE_PASSWORD = "/MobileApps/changePassword";
    public static final String REQUEST_URL_ADMIN_SEND_TO_NODAL_OFFICER = "/WebServices/documentSendNodalOfficer";
    public static final String REQUEST_URL_ADMIN_DOCUMENT_DELETE = "/WebServices/documentDelete";
    public static final String REQUEST_URL_ADMIN_DOCUMENT_UPDATE = "/WebServices/documentUpdate";
    public static final String REQUEST_URL_DOC_UPLOAD = "/WebServices/adminFileUpload";
    public static final String REQUEST_URL_DOC_CREATE = "/WebServices/adminDocUploadNew";
    public static final String REQUEST_URL_GET_DEVICE_ID = "/WebServices/userUpdate";

    public static final String YOUTH_CONNECT_DATABASE = "youth_connect";

    public static final String SHAREDPREFERENCE_KEY = "sp_sports_connect";
    public static final String SP_NOTIFICATION_COUNT = "notification_count";

    public static final String INTENT_USER_TYPE_ADMIN = "admin";
    public static final String INTENT_USER_TYPE_NODAL_OFFICER = "nodal officer";

    public static final String SP_LOGIN_STATUS = "login_status";
    public static final String SP_USER_ID = "user_id";
    public static final String SP_USER_API_KEY = "user_api_key";
    public static final String SP_USER_DESG_ID = "user_desg_id";
    public static final String SP_USER_TYPE = "user_type";
    public static final String SP_USER_NAME = "user_name";

    public static final String TAB_TITLE_FEEDBACK_SEND_FEEDBACK = "Send Feedback";
    public static final String TAB_TITLE_FEEDBACK_VIEW_FEEDBACK = "View Feedback";

    public static final String TAB_TITLE_FEEDBACK_PENDING = "Pending";
    public static final String TAB_TITLE_FEEDBACK_SUBMITTED = "Submitted";
    public static final String TAB_TITLE_FEEDBACK_EXPIRED = "Expired";

    public static final String TAB_TITLE_HOME_SHOWCASE = "Showcase";
    public static final String TAB_TITLE_HOME_DASHBOARD = "Dashboard";
    public static final String TAB_TITLE_HOME_REPORT = "Report";
    public static final String TAB_TITLE_HOME_FILE_UPLOAD = "File Upload";
    public static final String TAB_TITLE_HOME_VIEW_FILE = "View File";

    public static final String TAB_TITLE_FORUM = "Forum";
    public static final String TAB_TITLE_PENDING = "Pending";
    public static final String TAB_TITLE_ANSWERED = "Answered";

    public static final String ROBOTO_LIGHT = "font/roboto_light.ttf";
    public static final String ROBOTO_THIN = "font/roboto_thin.ttf";
    public static final String ROBOTO_BOLD_CONDENSED = "font/roboto-bold_condensed.ttf";

    public static final String SHOWCASE_EVENT_INTENT_KEY = "showcase_event_name";

    public static final String INTENT_KEY_CURRENT_FILE_TO_BE_UPDATE = "currentFieldToBeUpdate";
    public static final String INTENT_KEY_FIELD_DETAILS = "fieldDetails";
    public static final String INTENT_KEY_FILE_PATH = "filepath";
    public static final String INTENT_KEY_QUESTION_AND_ANSWER = "QuestionAndAnswerItem";
    public static final String INTENT_KEY_QUESTION_AND_ANSWER_IS_EDIT = "isEdit";
    public static final String INTENT_KEY_DOCUMENT = "Document";
    public static final String INTENT_KEY_DISTRICT = "District";
    public static final String INTENT_KEY_NODAL_OFFICER = "NodalOfficer";
    public static final String INTENT_KEY_IS_FROM_FORUM = "isFromForum";
    public static final String INTENT_KEY_IS_FROM_ANSWERED = "isFromAnswered";

    public static final String FRAGMENT_FORUM = "4";
    public static final String FRAGMENT_PENDING = "5";
    public static final String FRAGMENT_ANSWERED = "6";

    public static final int FRAGMENT_QA = 1;
    public static final int FRAGMENT_NOTIFICATION = 2;
    public static final int FRAGMENT_HOME_DASHBOARD = 3;
    public static final int FRAGMENT_HOME_SHOWCASE = 4;
    public static final int FRAGMENT_QA_FORUM = 5;
    public static final int FRAGMENT_QA_PENDING = 6;
    public static final int FRAGMENT_QA_ANSWERED = 7;
    public static final int NOTIFY_ME_ID = 123;

    public static final String DOC_TITLE = "title";
    public static final String DOC_PURPOSE = "purpose";
    public static final String DOC_ID = "doc_id";
    public static final String IS_DOC_ID_AUTO_GENERATED = "is_doc_id_auto_generated";
    public static final String QUESTION_DETAILS = "question_details";
    public static final String QUESTION_RESULT = "ask_result";
    public static final int INTENT_QNADETAILS_TO_ASKQUESTION_REQUSET_CODE = 123;
    public static final int INTENT_FEEDBACKDETAILS_TO_FEEDBACK_REQUSET_CODE = 657;

    public static final String FRAGMENT_HOME_PAGE = "Home";
    public static final String FRAGMENT_HOME_DASHBOARD_PAGE = "Home_Dashboard";
    public static final String FRAGMENT_HOME_SHOWCASE_PAGE = "Home_Showcase";
    public static final String FRAGMENT_QA_PAGE_PENDING = "QaPending";
    public static final String FRAGMENT_QA_PAGE_FORUM = "QaForum";
    public static final String FRAGMENT_QA_PAGE_ANSWERED = "QaAnswered";
    public static final String FRAGMENT_NOTIFICATION_PAGE = "Notification";

    public static final String SP_IS_FIRST_TIME_HOME = "is_for_first_time_showcase";
    public static final String SP_FROM_NOTIFICATION_PANEL = "from_notification_panel";

    public static final int FRAGMENT_HOME_SUB_FRAGMENT_DASHBOARD = 12;
    public static final int FRAGMENT_HOME_SUB_FRAGMENT_SHOWCASE = 13;
    public static final int FRAGMENT_QA_SUB_FRAGMENT_FORUM = 14;
    public static final int FRAGMENT_QA_SUB_FRAGMENT_PENDING = 15;
    public static final int FRAGMENT_QA_SUB_FRAGMENT_ANSWERED = 16;

    public static final int SECTION_HOME = 17;
    public static final int SECTION_QA = 18;
    public static final int SECTION_NOTIFICATION = 19;

    public static final String POST_COMMENT_SUCCESS_ACTION = "com.post.action.sent.success";
    public static final String POST_COMMENT_FAILURE_ACTION = "com.post.action.sent.failure";

    public static final int IMAGE = 0;
    public static final int VIDEO = 1;
    public static final int DOC = 2;
    public static final int AUDIO = 3;

    public static final String CURRENT_FRAGMENT_OF_QA = "current_qa_page";
    public static final String IS_ACTION_TAKEN_FOR_DOC = "is_action_taken_for_doc";
    public static final String IS_ACTION_TAKEN_FOR_QA = "is_action_taken_for_qa";

    public static final String BROADCAST_ACTION_REPLICATION_CHANGE
            = "com.lipl.youthconnect.youth_connect.replication.change";

    public static final String NEXT_ACTIVITY = "nextActivity";
}