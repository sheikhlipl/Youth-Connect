package com.lipl.youthconnect.youth_connect.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Parcel;
import android.text.TextUtils;
import android.util.Log;

import com.lipl.youthconnect.youth_connect.pojo.Dashboard;
import com.lipl.youthconnect.youth_connect.pojo.District;
import com.lipl.youthconnect.youth_connect.pojo.Feedback;
import com.lipl.youthconnect.youth_connect.pojo.NodalUser;

import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

	public static final String DATABASE_NAME = "sportsnyouth.db";

    //User
    public static final String USER_TABLE_NAME = "User";
    public static final String USER_COLUMN_ID = "user_id";
    public static final String USER_COLUMN_FULL_NAME = "full_name";
    public static final String USER_COLUMN_DISTRICT_ID = "district_id";

    //District
    public static final String DISTRICT_TABLE_NAME = "District";
    public static final String DISTRICT_ID = "dist_id";
    public static final String DISTRICT_NAME = "dist_name";
    public static final String DISTRICT_MODIFIED_DATE = "modified_date";

    //Dashboard
    public static final String DASHBOARD_TABLE_NAME = "Dashboard";
    public static final String DASHBOARD_USER_ID = "user_id";
    public static final String DASHBOARD_PENDING_QUESTION = "pending_question";
    public static final String DASHBOARD_QUESTION_ANSWERED = "question_answered";
    public static final String DASHBOARD_PENDING_FEEDBACK = "pending_feedback";
    public static final String DASHBOARD_SHOWCASE_EVENT = "showcase_event";
    public static final String DASHBOARD_SUBMITTED_FEEDBACK = "submitted_feedback";
    public static final String DASHBOARD_QSN_PUBLISH = "qsn_publish";
    public static final String DASHBOARD_COUNT_USER = "count_user";

    //Feedback
    public static final String FEEDBACK_TABLE_NAME = "Feedback";
    public static final String FEEDBACK_REPORT_ID = "reportID";
    public static final String FEEDBACK_REPORT_TITLE = "report_title";
    public static final String FEEDBACK_REPORT_TYPE = "report_type";
    public static final String FEEDBACK_REPORT_SHORT_DESC = "report_short_desc";
    public static final String FEEDBACK_REPORT_CREATED_BY = "reportCreatedBy";
    public static final String FEEDBACK_REPORT_CREATED_ON = "reportCreatedOn";
    public static final String FEEDBACK_REPORT_LAST_DATE_OF_SUBMISSION = "LastDateofSubmission";
    public static final String FEEDBACK_REPORT_LAST_RESPONSE_DATE = "LastResponseDate";
    public static final String FEEDBACK_REPORT_COLOR = "color";
    public static final String FEEDBACK_REPORT_FB_ASSIGNED_USERID = "FbAssignUserID";
    public static final String FEEDBACK_REPORT_RECENT_SUBMISSION = "RecentSubmission";
    public static final String FEEDBACK_REPORT_SHOW_SUBMIT_BUTTON = "ShowSubmitButton";
    public static final String FEEDBACK_REPORT_SHOW_REJECT_BUTTON = "ShowRejectButton";
    public static final String FEEDBACK_REPORT_SHOW_REJECT_MESSAGE = "ShowRejectMessage";
    public static final String FEEDBACK_REPORT_SHOW_VIEW_BUTTON= "ShowViewButton";
    public static final String FEEDBACK_REPORT_REPORT_STATUS= "reportStatus";
    public static final String FEEDBACK_REPORT_NO_OF_TIMES_REPORT_SUBMISSION= "noOfTimesReportSubmission";
    public static final String FEEDBACK_REPORT_NO_OF_TIMES_REPORT_SUBMITTED= "noOfTimesReportSubmitted";
    public static final String FEEDBACK_REPORT_NO_OF_TIMES_REPORT_TO_BE_SUBMITTED= "noOfTimesReportToBeSubmitted";
    public static final String FEEDBACK_REPORT_NO_OF_TIMES_REPORT_CAN_SUBMIT_REPORT_AFTER_DEADLINE = "canSubmitReportAfterDeadLine";

    private static final int NEW_DATABASE_VERSION = 1;
    //private static final int OLD_DATABASE_VERSION = 6;

    public DBHelper(Context context)
    {
        super(context, DATABASE_NAME, null, NEW_DATABASE_VERSION);
    }

   @Override
   public void onCreate(SQLiteDatabase db) {

        /*
        * Creation of User table
        */
        db.execSQL(
                "create table " + USER_TABLE_NAME +
                        "(" + USER_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "  //"(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + USER_COLUMN_FULL_NAME + " text, "
                        + USER_COLUMN_DISTRICT_ID + " text) "
        );

       /*
        * Creation of District table
        */
       db.execSQL(
               "create table " + DISTRICT_TABLE_NAME +
                       "(" + DISTRICT_ID + " INTEGER PRIMARY KEY, "  //"(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                       + DISTRICT_NAME + " text, "
               + DISTRICT_MODIFIED_DATE + " text) "
       );

       /*
        * Creation of Feedback table
        */
       db.execSQL(
               "create table " + FEEDBACK_TABLE_NAME +
                       "(" + FEEDBACK_REPORT_ID + " INTEGER PRIMARY KEY, "  //"(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                       + FEEDBACK_REPORT_TITLE + " text, "
                       + FEEDBACK_REPORT_TYPE + " text, "
                       + FEEDBACK_REPORT_SHORT_DESC + " text, "
                       + FEEDBACK_REPORT_CREATED_BY + " text, "
                       + FEEDBACK_REPORT_CREATED_ON + " text, "
                       + FEEDBACK_REPORT_LAST_DATE_OF_SUBMISSION + " text, "
                       + FEEDBACK_REPORT_LAST_RESPONSE_DATE + " text, "
                       + FEEDBACK_REPORT_COLOR + " text, "
                       + FEEDBACK_REPORT_FB_ASSIGNED_USERID + " text, "
                       + FEEDBACK_REPORT_RECENT_SUBMISSION + " text, "
                       + FEEDBACK_REPORT_SHOW_SUBMIT_BUTTON + " text, "
                       + FEEDBACK_REPORT_SHOW_REJECT_BUTTON + " text, "
                       + FEEDBACK_REPORT_SHOW_REJECT_MESSAGE + " text, "
                       + FEEDBACK_REPORT_SHOW_VIEW_BUTTON + " text, "
                       + FEEDBACK_REPORT_REPORT_STATUS + " text, "
                       + FEEDBACK_REPORT_NO_OF_TIMES_REPORT_SUBMISSION + " text, "
                       + FEEDBACK_REPORT_NO_OF_TIMES_REPORT_SUBMITTED + " text, "
                       + FEEDBACK_REPORT_NO_OF_TIMES_REPORT_TO_BE_SUBMITTED + " text, "
                       + FEEDBACK_REPORT_NO_OF_TIMES_REPORT_CAN_SUBMIT_REPORT_AFTER_DEADLINE + " text) "
       );

       /*
        * Creation of Dashboard table
        */
       db.execSQL(
               "create table " + DASHBOARD_TABLE_NAME +
                       "(" + DASHBOARD_USER_ID + " INTEGER PRIMARY KEY, "  //"(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                       + DASHBOARD_PENDING_QUESTION + " integer, "
                       + DASHBOARD_QUESTION_ANSWERED + " integer, "
                       + DASHBOARD_PENDING_FEEDBACK + " integer, "
                       + DASHBOARD_SHOWCASE_EVENT + " integer, "
                       + DASHBOARD_SUBMITTED_FEEDBACK + " integer, "
                       + DASHBOARD_QSN_PUBLISH + " integer, "
                       + DASHBOARD_COUNT_USER + " integer) "
       );
   }

   @Override
   public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
       db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE_NAME);
       db.execSQL("DROP TABLE IF EXISTS " + DISTRICT_TABLE_NAME);
       db.execSQL("DROP TABLE IF EXISTS " + FEEDBACK_TABLE_NAME);

       //Added in new version 2
       db.execSQL("DROP TABLE IF EXISTS " + DASHBOARD_TABLE_NAME);
       onCreate(db);

       /*String upgradeQuery1 = "ALTER TABLE "+DASHBOARD_TABLE_NAME+" ADD COLUMN "+DASHBOARD_COUNT_USER+" INTEGER ";
       String upgradeQuery2 = " ALTER TABLE "+COMMENT_TABLE_NAME+" ADD COLUMN "+COMMENT_IS_UPLOADED+" INTEGER ";
       if ((oldVersion == OLD_DATABASE_VERSION || oldVersion == 1)&& newVersion == NEW_DATABASE_VERSION) {
           db.execSQL(upgradeQuery1);
           db.execSQL(upgradeQuery2);
       }*/
   }

    /*
    * @User Table
    */
    public boolean insertUser(NodalUser user)
    {
        if(user == null){
            return false;
        }

        if(isUserExistInDatabase(user)) {
            updateUser(user);
            return true;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(USER_COLUMN_ID, user.getUser_id());
        contentValues.put(USER_COLUMN_FULL_NAME, user.getFull_name());
        contentValues.put(USER_COLUMN_DISTRICT_ID, user.getM_district_id());

        db.insert(USER_TABLE_NAME, null, contentValues);
        db.close();
        return true;
    }

    public boolean isUserExistInDatabase(NodalUser user){

        if(user == null){
            return false;
        }

        boolean isAvailable = false;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res =  db.rawQuery("select * from " + USER_TABLE_NAME + " WHERE " +
                USER_COLUMN_ID + " = ? ", new String[]{Integer.toString(user.getUser_id())});
        if(res != null && res.getCount() > 0){
            isAvailable = true;
        }
        res.close();
        db.close();
        return isAvailable;
    }

    public boolean updateUser(NodalUser user) {

        if(user == null){
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(USER_COLUMN_ID, user.getUser_id());
        contentValues.put(USER_COLUMN_FULL_NAME, user.getFull_name());
        contentValues.put(USER_COLUMN_DISTRICT_ID, user.getM_district_id());

        db.update(USER_TABLE_NAME, contentValues,
                USER_COLUMN_ID + " = ? ",
                new String[]{Integer.toString(user.getUser_id())});
        db.close();
        return true;
    }
    public boolean deleteUser (NodalUser user) {

        if(user == null){
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(USER_TABLE_NAME,
                USER_COLUMN_ID + " = ? ",
                new String[]{Integer.toString(user.getUser_id())});
        db.close();
        return true;
    }

    public List<NodalUser> getAllNodalUsers(){
        List<NodalUser> userList = new ArrayList<NodalUser>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select * from " + USER_TABLE_NAME, new String[]{});
        res.moveToFirst();
        while(res.isAfterLast() == false){
            NodalUser user = new NodalUser(Parcel.obtain());
            user.setUser_id(res.getInt(res.getColumnIndex(USER_COLUMN_ID)));
            user.setM_district_id(res.getString(res.getColumnIndex(USER_COLUMN_DISTRICT_ID)));
            user.setFull_name(res.getString(res.getColumnIndex(USER_COLUMN_FULL_NAME)));
            userList.add(user);
            res.moveToNext();
        }
        return userList;
    }

    /*
    * @District Table
    */
    public boolean insetDistrict  (District district)
    {
        if(district == null){
            return false;
        }

        if(isExistDistInDatabase(district)) {
            updateDistrict(district);
            return true;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(DISTRICT_ID, district.getM_district_id());
        contentValues.put(DISTRICT_NAME, district.getM_district());
        contentValues.put(DISTRICT_MODIFIED_DATE, district.getM_district());

        db.insert(DISTRICT_TABLE_NAME, null, contentValues);
        db.close();
        return true;
    }

    public boolean isExistDistInDatabase(District district){

        if(district == null){
            return false;
        }

        boolean isAvailable = false;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res =  db.rawQuery("select * from " + DISTRICT_TABLE_NAME + " WHERE " +
                DISTRICT_ID + " = ? ", new String[]{Integer.toString(district.getM_district_id())});
        if(res != null && res.getCount() > 0){
            isAvailable = true;
        }
        res.close();
        db.close();
        return isAvailable;
    }

    public boolean updateDistrict(District district) {

        if(district == null){
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(DISTRICT_ID, district.getM_district_id());
        contentValues.put(DISTRICT_NAME, district.getM_district());
        contentValues.put(DISTRICT_MODIFIED_DATE, district.getM_district());

        db.update(DISTRICT_TABLE_NAME, contentValues,
                DISTRICT_ID + " = ? ",
                new String[]{Integer.toString(district.getM_district_id())});
        db.close();
        return true;
    }

    public boolean deleteDistrict (District district) {

        if(district == null){
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DISTRICT_TABLE_NAME,
                DISTRICT_ID + " = ? ",
                new String[]{Integer.toString(district.getM_district_id())});
        db.close();
        return true;
    }

    public District getDistrictDetailsFromDistID (int distId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + DISTRICT_TABLE_NAME + " WHERE "
                + DISTRICT_ID + " = ? ", new String[]{Integer.toString(distId)});
        if (res == null || res.getCount() <= 0){ res.close();db.close(); return null;}

        res.moveToFirst();
        District district = new District(Parcel.obtain());
        district.setM_district_id(res.getInt(res.getColumnIndex(DISTRICT_ID)));
        district.setM_district(res.getString(res.getColumnIndex(DISTRICT_NAME)));
        district.setModifiedDate(res.getString(res.getColumnIndex(DISTRICT_MODIFIED_DATE)));

        res.moveToNext();
        res.close();
        db.close();

        return district;
    }

    public List<District> getAllDistricts(){
        List<District> districtList = new ArrayList<District>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+DISTRICT_TABLE_NAME, null );
        res.moveToFirst();
        while(res.isAfterLast() == false){
            District district = new District(Parcel.obtain());
            district.setM_district_id(res.getInt(res.getColumnIndex(DISTRICT_ID)));
            district.setM_district(res.getString(res.getColumnIndex(DISTRICT_NAME)));
            district.setModifiedDate(res.getString(res.getColumnIndex(DISTRICT_MODIFIED_DATE)));
            districtList.add(district);
            res.moveToNext();
        }
        return districtList;
    }

    /*
    * @Feedback Table
    */

    public boolean deleteFeedback (Feedback feedback) {

        if(feedback == null){
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(FEEDBACK_TABLE_NAME,
                FEEDBACK_REPORT_ID + " = ? ",
                new String[]{Integer.toString(feedback.getReportID())});
        db.close();
        return true;
    }

    public Feedback getFeedbackFromFeedbackId (int feedbackId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select * from " + FEEDBACK_TABLE_NAME + " WHERE "
                + FEEDBACK_REPORT_ID + " = ? ", new String[]{Integer.toString(feedbackId)});
        if (res == null || res.getCount() <= 0){ res.close();db.close(); return null;}

        res.moveToFirst();

        Feedback feedback = new Feedback(Parcel.obtain());
        feedback.setReportID(res.getInt(res.getColumnIndex(FEEDBACK_REPORT_ID)));
        feedback.setReportTitle(res.getString(res.getColumnIndex(FEEDBACK_REPORT_TITLE)));
        feedback.setReportType(res.getString(res.getColumnIndex(FEEDBACK_REPORT_TYPE)));
        feedback.setReportShortDesc(res.getString(res.getColumnIndex(FEEDBACK_REPORT_SHORT_DESC)));
        feedback.setReportCreatedBy(res.getString(res.getColumnIndex(FEEDBACK_REPORT_CREATED_BY)));
        feedback.setReportCreatedOn(res.getString(res.getColumnIndex(FEEDBACK_REPORT_CREATED_ON)));
        feedback.setLastDateofSubmission(res.getString(res.getColumnIndex(FEEDBACK_REPORT_LAST_DATE_OF_SUBMISSION)));
        feedback.setLastResponseDate(res.getString(res.getColumnIndex(FEEDBACK_REPORT_LAST_RESPONSE_DATE)));
        feedback.setColor(res.getString(res.getColumnIndex(FEEDBACK_REPORT_COLOR)));
        feedback.setFbAssignUserID(res.getString(res.getColumnIndex(FEEDBACK_REPORT_FB_ASSIGNED_USERID)));
        feedback.setRecentSubmission(res.getString(res.getColumnIndex(FEEDBACK_REPORT_RECENT_SUBMISSION)));
        feedback.setShowSubmitButton(res.getString(res.getColumnIndex(FEEDBACK_REPORT_SHOW_SUBMIT_BUTTON)));
        feedback.setShowRejectButton(res.getString(res.getColumnIndex(FEEDBACK_REPORT_SHOW_REJECT_BUTTON)));
        feedback.setShowRejectMessage(res.getString(res.getColumnIndex(FEEDBACK_REPORT_SHOW_REJECT_MESSAGE)));
        feedback.setShowViewButton(res.getString(res.getColumnIndex(FEEDBACK_REPORT_SHOW_VIEW_BUTTON)));
        feedback.setReportStatus(res.getString(res.getColumnIndex(FEEDBACK_REPORT_REPORT_STATUS)));
        feedback.setNoOfTimesReportSubmission(res.getString(res.getColumnIndex(FEEDBACK_REPORT_NO_OF_TIMES_REPORT_SUBMISSION)));
        feedback.setNoOfTimesReportToBeSubmitted(res.getString(res.getColumnIndex(FEEDBACK_REPORT_NO_OF_TIMES_REPORT_TO_BE_SUBMITTED)));
        feedback.setCanSubmitReportAfterDeadLine(res.getString(res.getColumnIndex(FEEDBACK_REPORT_NO_OF_TIMES_REPORT_CAN_SUBMIT_REPORT_AFTER_DEADLINE)));

        res.moveToNext();
        res.close();
        db.close();

        return feedback;
    }

    public List<Feedback> getSubmittedFeedback(){
        List<Feedback> feedbackList = new ArrayList<Feedback>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select * from " + FEEDBACK_TABLE_NAME + " where " + FEEDBACK_REPORT_RECENT_SUBMISSION + "=\"Y\"", null);
                //+ " AND " + FEEDBACK_REPORT_NO_OF_TIMES_REPORT_TO_BE_SUBMITTED + "=0", null);
        //Cursor res =  db.rawQuery( "select * from "+FEEDBACK_TABLE_NAME+" where ("+FEEDBACK_REPORT_REPORT_STATUS+"=\"S\""
          //      +" AND "+FEEDBACK_REPORT_NO_OF_TIMES_REPORT_TO_BE_SUBMITTED+"=0) OR "+ FEEDBACK_REPORT_REPORT_STATUS + "=\"E\"", null );
        res.moveToFirst();
        while(res.isAfterLast() == false){
            Feedback feedback = new Feedback(Parcel.obtain());
            feedback.setReportID(res.getInt(res.getColumnIndex(FEEDBACK_REPORT_ID)));
            feedback.setReportTitle(res.getString(res.getColumnIndex(FEEDBACK_REPORT_TITLE)));
            feedback.setReportType(res.getString(res.getColumnIndex(FEEDBACK_REPORT_TYPE)));
            feedback.setReportShortDesc(res.getString(res.getColumnIndex(FEEDBACK_REPORT_SHORT_DESC)));
            feedback.setReportCreatedBy(res.getString(res.getColumnIndex(FEEDBACK_REPORT_CREATED_BY)));
            feedback.setReportCreatedOn(res.getString(res.getColumnIndex(FEEDBACK_REPORT_CREATED_ON)));
            feedback.setLastDateofSubmission(res.getString(res.getColumnIndex(FEEDBACK_REPORT_LAST_DATE_OF_SUBMISSION)));
            feedback.setLastResponseDate(res.getString(res.getColumnIndex(FEEDBACK_REPORT_LAST_RESPONSE_DATE)));
            feedback.setColor(res.getString(res.getColumnIndex(FEEDBACK_REPORT_COLOR)));
            feedback.setFbAssignUserID(res.getString(res.getColumnIndex(FEEDBACK_REPORT_FB_ASSIGNED_USERID)));
            feedback.setRecentSubmission(res.getString(res.getColumnIndex(FEEDBACK_REPORT_RECENT_SUBMISSION)));
            feedback.setShowSubmitButton(res.getString(res.getColumnIndex(FEEDBACK_REPORT_SHOW_SUBMIT_BUTTON)));
            feedback.setShowRejectButton(res.getString(res.getColumnIndex(FEEDBACK_REPORT_SHOW_REJECT_BUTTON)));
            feedback.setShowRejectMessage(res.getString(res.getColumnIndex(FEEDBACK_REPORT_SHOW_REJECT_MESSAGE)));
            feedback.setShowViewButton(res.getString(res.getColumnIndex(FEEDBACK_REPORT_SHOW_VIEW_BUTTON)));
            feedback.setReportStatus(res.getString(res.getColumnIndex(FEEDBACK_REPORT_REPORT_STATUS)));
            feedback.setNoOfTimesReportSubmission(res.getString(res.getColumnIndex(FEEDBACK_REPORT_NO_OF_TIMES_REPORT_SUBMISSION)));
            feedback.setNoOfTimesReportToBeSubmitted(res.getString(res.getColumnIndex(FEEDBACK_REPORT_NO_OF_TIMES_REPORT_TO_BE_SUBMITTED)));
            feedback.setCanSubmitReportAfterDeadLine(res.getString(res.getColumnIndex(FEEDBACK_REPORT_NO_OF_TIMES_REPORT_CAN_SUBMIT_REPORT_AFTER_DEADLINE)));
            feedbackList.add(feedback);
            res.moveToNext();
        }
        return feedbackList;
    }

    public List<Feedback> getPendingFeedbacks(){
        List<Feedback> feedbackList = new ArrayList<Feedback>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select * from " + FEEDBACK_TABLE_NAME + " where " + FEEDBACK_REPORT_RECENT_SUBMISSION + "=\"N\"", null);
        //Cursor res =  db.rawQuery("select * from " + FEEDBACK_TABLE_NAME + " where " + FEEDBACK_REPORT_REPORT_STATUS + "=\"S\""
          //      + " AND " + FEEDBACK_REPORT_NO_OF_TIMES_REPORT_TO_BE_SUBMITTED + ">0", null);
        res.moveToFirst();
        while(res.isAfterLast() == false){
            Feedback feedback = new Feedback(Parcel.obtain());
            feedback.setReportID(res.getInt(res.getColumnIndex(FEEDBACK_REPORT_ID)));
            feedback.setReportTitle(res.getString(res.getColumnIndex(FEEDBACK_REPORT_TITLE)));
            feedback.setReportType(res.getString(res.getColumnIndex(FEEDBACK_REPORT_TYPE)));
            feedback.setReportShortDesc(res.getString(res.getColumnIndex(FEEDBACK_REPORT_SHORT_DESC)));
            feedback.setReportCreatedBy(res.getString(res.getColumnIndex(FEEDBACK_REPORT_CREATED_BY)));
            feedback.setReportCreatedOn(res.getString(res.getColumnIndex(FEEDBACK_REPORT_CREATED_ON)));
            feedback.setLastDateofSubmission(res.getString(res.getColumnIndex(FEEDBACK_REPORT_LAST_DATE_OF_SUBMISSION)));
            feedback.setLastResponseDate(res.getString(res.getColumnIndex(FEEDBACK_REPORT_LAST_RESPONSE_DATE)));
            feedback.setColor(res.getString(res.getColumnIndex(FEEDBACK_REPORT_COLOR)));
            feedback.setFbAssignUserID(res.getString(res.getColumnIndex(FEEDBACK_REPORT_FB_ASSIGNED_USERID)));
            feedback.setRecentSubmission(res.getString(res.getColumnIndex(FEEDBACK_REPORT_RECENT_SUBMISSION)));
            feedback.setShowSubmitButton(res.getString(res.getColumnIndex(FEEDBACK_REPORT_SHOW_SUBMIT_BUTTON)));
            feedback.setShowRejectButton(res.getString(res.getColumnIndex(FEEDBACK_REPORT_SHOW_REJECT_BUTTON)));
            feedback.setShowRejectMessage(res.getString(res.getColumnIndex(FEEDBACK_REPORT_SHOW_REJECT_MESSAGE)));
            feedback.setShowViewButton(res.getString(res.getColumnIndex(FEEDBACK_REPORT_SHOW_VIEW_BUTTON)));
            feedback.setReportStatus(res.getString(res.getColumnIndex(FEEDBACK_REPORT_REPORT_STATUS)));
            feedback.setNoOfTimesReportSubmission(res.getString(res.getColumnIndex(FEEDBACK_REPORT_NO_OF_TIMES_REPORT_SUBMISSION)));
            feedback.setNoOfTimesReportToBeSubmitted(res.getString(res.getColumnIndex(FEEDBACK_REPORT_NO_OF_TIMES_REPORT_TO_BE_SUBMITTED)));
            feedback.setCanSubmitReportAfterDeadLine(res.getString(res.getColumnIndex(FEEDBACK_REPORT_NO_OF_TIMES_REPORT_CAN_SUBMIT_REPORT_AFTER_DEADLINE)));
            feedbackList.add(feedback);
            res.moveToNext();
        }
        if(feedbackList != null) {
            int count = feedbackList.size();
            Log.i("DBHelper", "Pending list count from dtabse is "+count);
        }
        return feedbackList;
    }

    public List<Feedback> getRejectedFeedbacks(){
        List<Feedback> feedbackList = new ArrayList<Feedback>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+FEEDBACK_TABLE_NAME+" where "+FEEDBACK_REPORT_REPORT_STATUS+"=\"R\"", null );
        res.moveToFirst();
        while(res.isAfterLast() == false){
            Feedback feedback = new Feedback(Parcel.obtain());
            feedback.setReportID(res.getInt(res.getColumnIndex(FEEDBACK_REPORT_ID)));
            feedback.setReportTitle(res.getString(res.getColumnIndex(FEEDBACK_REPORT_TITLE)));
            feedback.setReportType(res.getString(res.getColumnIndex(FEEDBACK_REPORT_TYPE)));
            feedback.setReportShortDesc(res.getString(res.getColumnIndex(FEEDBACK_REPORT_SHORT_DESC)));
            feedback.setReportCreatedBy(res.getString(res.getColumnIndex(FEEDBACK_REPORT_CREATED_BY)));
            feedback.setReportCreatedOn(res.getString(res.getColumnIndex(FEEDBACK_REPORT_CREATED_ON)));
            feedback.setLastDateofSubmission(res.getString(res.getColumnIndex(FEEDBACK_REPORT_LAST_DATE_OF_SUBMISSION)));
            feedback.setLastResponseDate(res.getString(res.getColumnIndex(FEEDBACK_REPORT_LAST_RESPONSE_DATE)));
            feedback.setColor(res.getString(res.getColumnIndex(FEEDBACK_REPORT_COLOR)));
            feedback.setFbAssignUserID(res.getString(res.getColumnIndex(FEEDBACK_REPORT_FB_ASSIGNED_USERID)));
            feedback.setRecentSubmission(res.getString(res.getColumnIndex(FEEDBACK_REPORT_RECENT_SUBMISSION)));
            feedback.setShowSubmitButton(res.getString(res.getColumnIndex(FEEDBACK_REPORT_SHOW_SUBMIT_BUTTON)));
            feedback.setShowRejectButton(res.getString(res.getColumnIndex(FEEDBACK_REPORT_SHOW_REJECT_BUTTON)));
            feedback.setShowRejectMessage(res.getString(res.getColumnIndex(FEEDBACK_REPORT_SHOW_REJECT_MESSAGE)));
            feedback.setShowViewButton(res.getString(res.getColumnIndex(FEEDBACK_REPORT_SHOW_VIEW_BUTTON)));
            feedback.setReportStatus(res.getString(res.getColumnIndex(FEEDBACK_REPORT_REPORT_STATUS)));
            feedback.setNoOfTimesReportSubmission(res.getString(res.getColumnIndex(FEEDBACK_REPORT_NO_OF_TIMES_REPORT_SUBMISSION)));
            feedback.setNoOfTimesReportToBeSubmitted(res.getString(res.getColumnIndex(FEEDBACK_REPORT_NO_OF_TIMES_REPORT_TO_BE_SUBMITTED)));
            feedback.setCanSubmitReportAfterDeadLine(res.getString(res.getColumnIndex(FEEDBACK_REPORT_NO_OF_TIMES_REPORT_CAN_SUBMIT_REPORT_AFTER_DEADLINE)));
            feedbackList.add(feedback);
            res.moveToNext();
        }
        return feedbackList;
    }

    public List<Feedback> getExpieredFeedback(){

        List<Feedback> reportList = new ArrayList<Feedback>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select * from " + FEEDBACK_TABLE_NAME + " where " + FEEDBACK_REPORT_REPORT_STATUS + "=\"E\"", null);
        //Cursor res =  db.rawQuery( "select * from "+REPORT_TABLE_NAME, null );
        res.moveToFirst();
        while(res.isAfterLast() == false){

            Feedback feedback = new Feedback(Parcel.obtain());
            feedback.setReportID(res.getInt(res.getColumnIndex(FEEDBACK_REPORT_ID)));
            feedback.setReportTitle(res.getString(res.getColumnIndex(FEEDBACK_REPORT_TITLE)));
            feedback.setReportType(res.getString(res.getColumnIndex(FEEDBACK_REPORT_TYPE)));
            feedback.setReportShortDesc(res.getString(res.getColumnIndex(FEEDBACK_REPORT_SHORT_DESC)));
            feedback.setReportCreatedBy(res.getString(res.getColumnIndex(FEEDBACK_REPORT_CREATED_BY)));
            feedback.setReportCreatedOn(res.getString(res.getColumnIndex(FEEDBACK_REPORT_CREATED_ON)));
            feedback.setLastDateofSubmission(res.getString(res.getColumnIndex(FEEDBACK_REPORT_LAST_DATE_OF_SUBMISSION)));
            feedback.setLastResponseDate(res.getString(res.getColumnIndex(FEEDBACK_REPORT_LAST_RESPONSE_DATE)));
            feedback.setColor(res.getString(res.getColumnIndex(FEEDBACK_REPORT_COLOR)));
            feedback.setFbAssignUserID(res.getString(res.getColumnIndex(FEEDBACK_REPORT_FB_ASSIGNED_USERID)));
            feedback.setRecentSubmission(res.getString(res.getColumnIndex(FEEDBACK_REPORT_RECENT_SUBMISSION)));
            feedback.setShowSubmitButton(res.getString(res.getColumnIndex(FEEDBACK_REPORT_SHOW_SUBMIT_BUTTON)));
            feedback.setShowRejectButton(res.getString(res.getColumnIndex(FEEDBACK_REPORT_SHOW_REJECT_BUTTON)));
            feedback.setShowRejectMessage(res.getString(res.getColumnIndex(FEEDBACK_REPORT_SHOW_REJECT_MESSAGE)));
            feedback.setShowViewButton(res.getString(res.getColumnIndex(FEEDBACK_REPORT_SHOW_VIEW_BUTTON)));
            feedback.setReportStatus(res.getString(res.getColumnIndex(FEEDBACK_REPORT_REPORT_STATUS)));
            feedback.setNoOfTimesReportSubmission(res.getString(res.getColumnIndex(FEEDBACK_REPORT_NO_OF_TIMES_REPORT_SUBMISSION)));
            feedback.setNoOfTimesReportToBeSubmitted(res.getString(res.getColumnIndex(FEEDBACK_REPORT_NO_OF_TIMES_REPORT_TO_BE_SUBMITTED)));
            feedback.setCanSubmitReportAfterDeadLine(res.getString(res.getColumnIndex(FEEDBACK_REPORT_NO_OF_TIMES_REPORT_CAN_SUBMIT_REPORT_AFTER_DEADLINE)));
            reportList.add(feedback);
            res.moveToNext();
        }

        return reportList;
    }

    public List<Feedback> getAllFeedbacks(){

        List<Feedback> reportList = new ArrayList<Feedback>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select * from " + FEEDBACK_TABLE_NAME , null);
        res.moveToFirst();
        while(res.isAfterLast() == false){

            Feedback feedback = new Feedback(Parcel.obtain());
            feedback.setReportID(res.getInt(res.getColumnIndex(FEEDBACK_REPORT_ID)));
            feedback.setReportTitle(res.getString(res.getColumnIndex(FEEDBACK_REPORT_TITLE)));
            feedback.setReportType(res.getString(res.getColumnIndex(FEEDBACK_REPORT_TYPE)));
            feedback.setReportShortDesc(res.getString(res.getColumnIndex(FEEDBACK_REPORT_SHORT_DESC)));
            feedback.setReportCreatedBy(res.getString(res.getColumnIndex(FEEDBACK_REPORT_CREATED_BY)));
            feedback.setReportCreatedOn(res.getString(res.getColumnIndex(FEEDBACK_REPORT_CREATED_ON)));
            feedback.setLastDateofSubmission(res.getString(res.getColumnIndex(FEEDBACK_REPORT_LAST_DATE_OF_SUBMISSION)));
            feedback.setLastResponseDate(res.getString(res.getColumnIndex(FEEDBACK_REPORT_LAST_RESPONSE_DATE)));
            feedback.setColor(res.getString(res.getColumnIndex(FEEDBACK_REPORT_COLOR)));
            feedback.setFbAssignUserID(res.getString(res.getColumnIndex(FEEDBACK_REPORT_FB_ASSIGNED_USERID)));
            feedback.setRecentSubmission(res.getString(res.getColumnIndex(FEEDBACK_REPORT_RECENT_SUBMISSION)));
            feedback.setShowSubmitButton(res.getString(res.getColumnIndex(FEEDBACK_REPORT_SHOW_SUBMIT_BUTTON)));
            feedback.setShowRejectButton(res.getString(res.getColumnIndex(FEEDBACK_REPORT_SHOW_REJECT_BUTTON)));
            feedback.setShowRejectMessage(res.getString(res.getColumnIndex(FEEDBACK_REPORT_SHOW_REJECT_MESSAGE)));
            feedback.setShowViewButton(res.getString(res.getColumnIndex(FEEDBACK_REPORT_SHOW_VIEW_BUTTON)));
            feedback.setReportStatus(res.getString(res.getColumnIndex(FEEDBACK_REPORT_REPORT_STATUS)));
            feedback.setNoOfTimesReportSubmission(res.getString(res.getColumnIndex(FEEDBACK_REPORT_NO_OF_TIMES_REPORT_SUBMISSION)));
            feedback.setNoOfTimesReportToBeSubmitted(res.getString(res.getColumnIndex(FEEDBACK_REPORT_NO_OF_TIMES_REPORT_TO_BE_SUBMITTED)));
            feedback.setCanSubmitReportAfterDeadLine(res.getString(res.getColumnIndex(FEEDBACK_REPORT_NO_OF_TIMES_REPORT_CAN_SUBMIT_REPORT_AFTER_DEADLINE)));
            reportList.add(feedback);
            res.moveToNext();
        }

        return reportList;
    }

    public boolean insertUpdateReport (Feedback feedback)
    {
        if(feedback == null){
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(FEEDBACK_REPORT_ID, feedback.getReportID());
        contentValues.put(FEEDBACK_REPORT_TITLE, feedback.getReportTitle());
        contentValues.put(FEEDBACK_REPORT_TYPE, feedback.getReportType());
        contentValues.put(FEEDBACK_REPORT_SHORT_DESC, feedback.getReportShortDesc());
        contentValues.put(FEEDBACK_REPORT_CREATED_BY, feedback.getReportCreatedBy());
        contentValues.put(FEEDBACK_REPORT_CREATED_ON, feedback.getReportCreatedOn());
        contentValues.put(FEEDBACK_REPORT_LAST_DATE_OF_SUBMISSION, feedback.getLastDateofSubmission());
        contentValues.put(FEEDBACK_REPORT_LAST_RESPONSE_DATE, feedback.getLastResponseDate());
        contentValues.put(FEEDBACK_REPORT_COLOR, feedback.getColor());
        contentValues.put(FEEDBACK_REPORT_FB_ASSIGNED_USERID, feedback.getFbAssignUserID());
        contentValues.put(FEEDBACK_REPORT_RECENT_SUBMISSION, feedback.getRecentSubmission());
        contentValues.put(FEEDBACK_REPORT_SHOW_SUBMIT_BUTTON, feedback.getShowSubmitButton());
        contentValues.put(FEEDBACK_REPORT_SHOW_REJECT_BUTTON, feedback.getShowRejectButton());
        contentValues.put(FEEDBACK_REPORT_SHOW_REJECT_MESSAGE, feedback.getShowRejectMessage());
        contentValues.put(FEEDBACK_REPORT_SHOW_VIEW_BUTTON, feedback.getShowViewButton());
        contentValues.put(FEEDBACK_REPORT_REPORT_STATUS, feedback.getReportStatus());
        contentValues.put(FEEDBACK_REPORT_NO_OF_TIMES_REPORT_SUBMISSION, feedback.getNoOfTimesReportSubmission());
        contentValues.put(FEEDBACK_REPORT_NO_OF_TIMES_REPORT_TO_BE_SUBMITTED, feedback.getNoOfTimesReportToBeSubmitted());
        contentValues.put(FEEDBACK_REPORT_NO_OF_TIMES_REPORT_CAN_SUBMIT_REPORT_AFTER_DEADLINE, feedback.getCanSubmitReportAfterDeadLine());

        if(isContainReport(feedback.getReportID())){
            db.update(FEEDBACK_TABLE_NAME, contentValues, FEEDBACK_REPORT_ID+" = ? ", new String[] { Integer.toString(feedback.getReportID()) } );
        } else{
            db.insert(FEEDBACK_TABLE_NAME, null, contentValues);
        }

        return true;
    }

    private boolean isContainReport (int report_id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select * from " + FEEDBACK_TABLE_NAME + " where " + FEEDBACK_REPORT_ID + "=" + report_id, null);
        res.moveToFirst();
        int count = 0 ;
        while(res.isAfterLast() == false){
            count = count + 1;
            res.moveToNext();
        }

        if(count > 0){
            return true;
        }else{
            return false;
        }
    }

    /*
    * @Dashboard Table
    */
    public boolean insertDashboard(Dashboard dashboard, int userID)
    {
        if(dashboard == null){
            return false;
        }

        if(isExistDashboardInDatabase(userID)) {
            updateDashboard(dashboard, userID);
            return true;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(DASHBOARD_USER_ID, userID);
        contentValues.put(DASHBOARD_PENDING_QUESTION, dashboard.getPennding_qsn());
        contentValues.put(DASHBOARD_QUESTION_ANSWERED, dashboard.getQsn_answered());
        contentValues.put(DASHBOARD_PENDING_FEEDBACK, dashboard.getPending_feedback());
        contentValues.put(DASHBOARD_SHOWCASE_EVENT, dashboard.getShowcased_event());
        contentValues.put(DASHBOARD_SUBMITTED_FEEDBACK, dashboard.getSubmitted_feedback());
        contentValues.put(DASHBOARD_QSN_PUBLISH, dashboard.getQsn_publish());
        contentValues.put(DASHBOARD_COUNT_USER, dashboard.getCount_user());

        db.insert(DASHBOARD_TABLE_NAME, null, contentValues);
        db.close();
        return true;
    }

    public boolean isExistDashboardInDatabase(int userid) {
        boolean isAvailable = false;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res =  db.rawQuery("select * from " + DASHBOARD_TABLE_NAME + " WHERE " +
                DASHBOARD_USER_ID + " = ? ", new String[]{Integer.toString(userid)});
        if(res != null && res.getCount() > 0){
            isAvailable = true;
        }
        res.close();
        db.close();
        return isAvailable;
    }

    public boolean updateDashboard(Dashboard dashboard, int userID) {

        if(dashboard == null){
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(DASHBOARD_USER_ID, userID);
        contentValues.put(DASHBOARD_PENDING_QUESTION, dashboard.getPennding_qsn());
        contentValues.put(DASHBOARD_QUESTION_ANSWERED, dashboard.getQsn_answered());
        contentValues.put(DASHBOARD_PENDING_FEEDBACK, dashboard.getPending_feedback());
        contentValues.put(DASHBOARD_SHOWCASE_EVENT, dashboard.getShowcased_event());
        contentValues.put(DASHBOARD_SUBMITTED_FEEDBACK, dashboard.getSubmitted_feedback());
        contentValues.put(DASHBOARD_QSN_PUBLISH, dashboard.getQsn_publish());
        contentValues.put(DASHBOARD_COUNT_USER, dashboard.getCount_user());

        db.update(DASHBOARD_TABLE_NAME, contentValues,
                DASHBOARD_USER_ID + " = ? ",
                new String[]{Integer.toString(userID)});
        db.close();
        return true;
    }

    public boolean deleteDashboard (int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DASHBOARD_TABLE_NAME,
                DASHBOARD_USER_ID + " = ? ",
                new String[]{Integer.toString(userId)});
        db.close();
        return true;
    }

    public Dashboard getDashboardDetailsFromUserID (int userID) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select * from " + DASHBOARD_TABLE_NAME + " WHERE "
                + DASHBOARD_USER_ID + " = ? ", new String[]{Integer.toString(userID)});
        if (res == null || res.getCount() <= 0){ res.close();db.close(); return null;}

        res.moveToFirst();
        Dashboard dashboard = new Dashboard(Parcel.obtain());
        dashboard.setPennding_qsn(res.getInt(res.getColumnIndex(DASHBOARD_PENDING_QUESTION)));
        dashboard.setQsn_answered(res.getInt(res.getColumnIndex(DASHBOARD_QUESTION_ANSWERED)));
        dashboard.setPending_feedback(res.getInt(res.getColumnIndex(DASHBOARD_PENDING_FEEDBACK)));
        dashboard.setShowcased_event(res.getInt(res.getColumnIndex(DASHBOARD_SHOWCASE_EVENT)));
        dashboard.setSubmitted_feedback(res.getInt(res.getColumnIndex(DASHBOARD_SUBMITTED_FEEDBACK)));
        dashboard.setQsn_publish(res.getInt(res.getColumnIndex(DASHBOARD_QSN_PUBLISH)));
        dashboard.setCount_user(res.getInt(res.getColumnIndex(DASHBOARD_COUNT_USER)));

        res.moveToNext();
        res.close();
        db.close();

        return dashboard;
    }

    public void deleteAllDataInDatabase(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(USER_TABLE_NAME, null, null);
        db.delete(DISTRICT_TABLE_NAME, null, null);
        db.delete(FEEDBACK_TABLE_NAME, null, null);
        db.delete(DASHBOARD_TABLE_NAME, null, null);
        db.close();
    }
}