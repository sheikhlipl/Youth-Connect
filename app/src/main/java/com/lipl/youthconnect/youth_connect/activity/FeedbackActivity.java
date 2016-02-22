package com.lipl.youthconnect.youth_connect.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.lipl.youthconnect.youth_connect.R;
import com.lipl.youthconnect.youth_connect.database.DBHelper;
import com.lipl.youthconnect.youth_connect.util.Constants;
import com.lipl.youthconnect.youth_connect.util.Util;
import com.lipl.youthconnect.youth_connect.adapter.FeedbackRecyclerAdapter;
import com.lipl.youthconnect.youth_connect.fragment.SendFeedbackFragment;
import com.lipl.youthconnect.youth_connect.fragment.ViewFeedbackFragment;
import com.lipl.youthconnect.youth_connect.fragment.ViewFeedbackNodalExpiredFragment;
import com.lipl.youthconnect.youth_connect.fragment.ViewFeedbackNodalPendingFragment;
import com.lipl.youthconnect.youth_connect.fragment.ViewFeedbackNodalSubmittedFragment;
import com.lipl.youthconnect.youth_connect.pojo.Feedback;
import com.lipl.youthconnect.youth_connect.util.YouthConnectSingleTone;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FeedbackActivity extends ActionBarActivity implements
        SendFeedbackFragment.OnFragmentInteractionListener,
        ViewFeedbackNodalPendingFragment.OnFragmentInteractionListener,
        ViewFeedbackNodalSubmittedFragment.OnFragmentInteractionListener,
        ViewFeedbackNodalExpiredFragment.OnFragmentInteractionListener ,
        ViewFeedbackFragment.OnFragmentInteractionListener,
        SwipeRefreshLayout.OnRefreshListener {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private static Toolbar mToolbar = null;
    private static final int USER_TYPE_ADMIN = 1;
    private static final int USER_TYPE_NODAL_OFFICER = 2;

    private final static int NUMBER_OF_TABS_FOR_USER_TYPE_NODAL_OFFICERS = 2;
    private final static int NUMBER_OF_TABS_FOR_USER_TYPE_ADMIN = 2;

    public SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle("Feedback");

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeColors(R.array.movie_serial_bg);
        swipeRefreshLayout.setOnRefreshListener(this);

        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                            fetchData();
                        }
                    }
        );
    }

    /**
     * This method is called when swipe refresh is pulled down
     */
    @Override
    public void onRefresh() {
        fetchData();
    }

    private void fetchData(){
        swipeRefreshLayout.setRefreshing(true);
        if(Util.getNetworkConnectivityStatus(FeedbackActivity.this)) {
            refreshView();
        } else{
            swipeRefreshLayout.setRefreshing(false);
            Snackbar snackbar = Snackbar.make(findViewById(R.id.coordinatorLayout), "No internet connection.", Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            View snackbarView = snackbar.getView();
            TextView tv = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
            tv.setTextColor(Color.WHITE);
            TextView tvAction = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_action);
            tvAction.setTextColor(Color.CYAN);
            snackbar.show();
        }
    }

    public static Toolbar getToolbar(){
        return mToolbar;
    }

    @Override
    public void onFragmentInteraction(String str) {

    }

    public void refreshView() {

        if (Util.getNetworkConnectivityStatus(FeedbackActivity.this)) {
            GetFeedbackListAsync async = new GetFeedbackListAsync();
            async.execute();
        }
    }

    /**
     * To Show Material Alert Dialog
     *
     * @param code Should be one of the global declared integer constants
     * @param message
     * @param title
     * */
    private void showAlertDialog(String message, String title, String positiveButtonText, String negativeButtonText, final int code){

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton(negativeButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_feedback_actionbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if(id == android.R.id.home){
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Async task to get sync camp table from server
     * */
    private class GetFeedbackListAsync extends AsyncTask<String, Void, List<Feedback>> {

        private static final String TAG = "GetFeedbackListAsync";
        //private ProgressDialog progressDialog = null;
        private boolean isChangePassword = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /*if(progressDialog == null) {
                progressDialog = ProgressDialog.show(FeedbackActivity.this, "Loading", "Please wait...");
            }*/
            swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected List<Feedback> doInBackground(String... params) {

            try {
                String api_key = getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 1).getString(Constants.SP_USER_API_KEY, null);

                if(api_key == null){
                    return null;
                }

                InputStream in = null;
                int resCode = -1;

                String link = Constants.BASE_URL+Constants.REPORT_LISTING_REQUEST_URL;
                URL url = new URL(link);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setAllowUserInteraction(false);
                conn.setInstanceFollowRedirects(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Authorization", api_key);

                conn.connect();
                resCode = conn.getResponseCode();
                if (resCode == HttpURLConnection.HTTP_OK) {
                    in = conn.getInputStream();
                }
                if(in == null){
                    return null;
                }
                BufferedReader reader =new BufferedReader(new InputStreamReader(in, "UTF-8"));
                String response = "",data="";

                while ((data = reader.readLine()) != null){
                    response += data + "\n";
                }

                Log.i(TAG, "Response : " + response);

                /**
                 *
                 * {
                 "Reports": [
                 {
                 "reportID": "6",
                 "reportTitle": "iuju",
                 "reportType": "Instant",
                 "reportShortDesc": "iuiou",
                 "reportCreatedBy": "Administrator",
                 "reportCreatedOn": "15-12-2015",
                 "LastDateofSubmission": "27-12-2015",
                 "LastResponseDate": "15-12-2015",
                 "color": "green",
                 "FbAssignUserID": "20",
                 "RecentSubmission": "N",
                 "ShowSubmitButton": "N",
                 "ShowRejectButton": "Y",
                 "ShowRejectMessage": "",
                 "ShowViewButton": "N",
                 "reportStatus": "S",
                 "noOfTimesReportSubmission": 0,
                 "noOfTimesReportSubmitted": 0,
                 "noOfTimesReportToBeSubmitted": 0,
                 "canSubmitReportAfterDeadLine": "N"
                 },
                 {
                 "reportID": "4",
                 "reportTitle": "avinash",
                 "reportType": "Instant",
                 "reportShortDesc": "descript",
                 "reportCreatedBy": "Administrator",
                 "reportCreatedOn": "15-12-2015",
                 "LastDateofSubmission": "20-12-2015",
                 "LastResponseDate": "15-12-2015",
                 "color": "green",
                 "FbAssignUserID": "15",
                 "RecentSubmission": "Y",
                 "ShowSubmitButton": "N",
                 "ShowRejectButton": "N",
                 "ShowRejectMessage": "",
                 "ShowViewButton": "Y",
                 "reportStatus": "S",
                 "noOfTimesReportSubmission": 2,
                 "noOfTimesReportSubmitted": 6,
                 "noOfTimesReportToBeSubmitted": -4,
                 "canSubmitReportAfterDeadLine": "N"
                 },
                 {
                 "reportID": "2",
                 "reportTitle": "Debi Prasad",
                 "reportType": "Instant",
                 "reportShortDesc": "East",
                 "reportCreatedBy": "Administrator",
                 "reportCreatedOn": "15-12-2015",
                 "LastDateofSubmission": "20-12-2015",
                 "LastResponseDate": "15-12-2015",
                 "color": "green",
                 "FbAssignUserID": "1",
                 "RecentSubmission": "Y",
                 "ShowSubmitButton": "Y",
                 "ShowRejectButton": "N",
                 "ShowRejectMessage": "",
                 "ShowViewButton": "Y",
                 "reportStatus": "S",
                 "noOfTimesReportSubmission": 2,
                 "noOfTimesReportSubmitted": 1,
                 "noOfTimesReportToBeSubmitted": 1,
                 "canSubmitReportAfterDeadLine": "N"
                 }
                 ]
                 }
                 *
                 * */

                if(response != null && response.length() > 0 && response.charAt(0) == '{'){
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject != null && jsonObject.isNull("Apikey") == false) {
                        String changePasswordDoneFromWebMsg = jsonObject.optString("Apikey");
                        if(changePasswordDoneFromWebMsg.equalsIgnoreCase("Api key does not exit")){
                            isChangePassword = true;
                            return null;
                        }
                    }
                }

                JSONObject jsonObject = new JSONObject(response);
                JSONArray array = jsonObject.getJSONArray("Reports");

                List<Feedback> feedbackList = new ArrayList<Feedback>();
                for (int i = 0; i < array.length(); i++) {
                    Feedback feedback = new Feedback(Parcel.obtain());
                    feedback.setReportID(array.getJSONObject(i).getInt("reportID"));
                    feedback.setReportTitle(array.getJSONObject(i).getString("reportTitle"));
                    feedback.setReportType(array.getJSONObject(i).getString("reportType"));
                    feedback.setReportCreatedBy(array.getJSONObject(i).getString("reportCreatedBy"));
                    feedback.setReportCreatedOn(array.getJSONObject(i).getString("reportCreatedOn"));
                    feedback.setLastDateofSubmission(array.getJSONObject(i).getString("LastDateofSubmission"));
                    feedback.setFbAssignUserID(array.getJSONObject(i).getString("FbAssignUserID"));
                    feedback.setRecentSubmission(array.getJSONObject(i).getString("RecentSubmission"));
                    feedback.setShowSubmitButton(array.getJSONObject(i).getString("ShowSubmitButton"));
                    feedback.setShowRejectButton(array.getJSONObject(i).getString("ShowRejectButton"));
                    feedback.setShowRejectMessage(array.getJSONObject(i).getString("ShowRejectMessage"));
                    feedback.setShowViewButton(array.getJSONObject(i).getString("ShowViewButton"));
                    feedback.setReportStatus(array.getJSONObject(i).getString("reportStatus"));
                    feedback.setNoOfTimesReportSubmitted(array.getJSONObject(i).getInt("noOfTimesReportSubmitted") + "");
                    feedback.setNoOfTimesReportSubmission(array.getJSONObject(i).getInt("noOfTimesReportSubmission") + "");
                    feedback.setNoOfTimesReportToBeSubmitted(array.getJSONObject(i).getInt("noOfTimesReportToBeSubmitted") + "");
                    feedback.setCanSubmitReportAfterDeadLine(array.getJSONObject(i).getString("canSubmitReportAfterDeadLine"));
                    feedbackList.add(feedback);

                }

                return feedbackList;
            } catch(SocketTimeoutException exception){
                Log.e(TAG, "GetFeedbackListAsync : doInBackground", exception);
            } catch(ConnectException exception){
                Log.e(TAG, "GetFeedbackListAsync : doInBackground", exception);
            } catch(MalformedURLException exception){
                Log.e(TAG, "GetFeedbackListAsync : doInBackground", exception);
            } catch (IOException exception){
                Log.e(TAG, "GetFeedbackListAsync : doInBackground", exception);
            } catch(Exception exception){
                Log.e(TAG, "GetFeedbackListAsync : doInBackground", exception);
            }

            return null;
        }

        @Override
        protected void onPostExecute(final List<Feedback> feedbackList) {
            super.onPostExecute(feedbackList);

            if(isChangePassword){
                AlertDialog.Builder builder = new AlertDialog.Builder(FeedbackActivity.this, R.style.AppCompatAlertDialogStyle);
                builder.setTitle(getResources().getString(R.string.password_changed_title));
                builder.setMessage(getResources().getString(R.string.password_changed_description));
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent(FeedbackActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("Exit me", true);
                        startActivity(intent);
                        finish();
                    }
                });
                builder.show();

                return;
            }

            if(feedbackList != null) {

                DBHelper databaseHelper = new DBHelper(FeedbackActivity.this);
                YouthConnectSingleTone.getInstance().submitedReport.clear();
                YouthConnectSingleTone.getInstance().submitedReport = databaseHelper.getSubmittedFeedback();
                Collections.reverse(YouthConnectSingleTone.getInstance().submitedReport);
                YouthConnectSingleTone.getInstance().pendingReport.clear();
                YouthConnectSingleTone.getInstance().pendingReport = databaseHelper.getPendingFeedbacks();
                Collections.reverse(YouthConnectSingleTone.getInstance().pendingReport);
                YouthConnectSingleTone.getInstance().expierdReport.clear();
                YouthConnectSingleTone.getInstance().expierdReport = databaseHelper.getExpieredFeedback();
                Collections.reverse(YouthConnectSingleTone.getInstance().expierdReport);
                databaseHelper.close();

                List<Feedback> submittedList = YouthConnectSingleTone.getInstance().submitedReport;
                List<Feedback> pendingList = YouthConnectSingleTone.getInstance().pendingReport;
                List<Feedback> expieredList = YouthConnectSingleTone.getInstance().expierdReport;

                Log.i("","");

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //if(progressDialog != null) progressDialog.dismiss();
                        swipeRefreshLayout.setRefreshing(false);
                        onPostProcess(feedbackList);
                    }
                }, 2000);
            } else{
                Snackbar snackbar = Snackbar.make(findViewById(R.id.layoutParent),
                        "Some problem occurred.", Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                View snackbarView = snackbar.getView();
                TextView tv = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                tv.setTextColor(Color.WHITE);
                TextView tvAction = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_action);
                tvAction.setTextColor(Color.CYAN);
                snackbar.show();
            }
        }
    }

    private void onPostProcess(List<Feedback> feedbackList){

        int userType = getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 1).getInt(Constants.SP_USER_TYPE, 0);
        if(userType == USER_TYPE_ADMIN){
            FeedbackRecyclerAdapter recyclerAdapter = new FeedbackRecyclerAdapter(getSupportFragmentManager(),
                    FeedbackActivity.this, USER_TYPE_ADMIN, feedbackList);
            viewPager = (ViewPager)findViewById(R.id.viewPager);
            viewPager.setAdapter(recyclerAdapter);
            tabLayout = (TabLayout)findViewById(R.id.tabView);
            tabLayout.addTab(tabLayout.newTab().setText(Constants.TAB_TITLE_FEEDBACK_SEND_FEEDBACK));
            tabLayout.addTab(tabLayout.newTab().setText(Constants.TAB_TITLE_FEEDBACK_VIEW_FEEDBACK));
        } else if(userType == USER_TYPE_NODAL_OFFICER){
            List<Feedback> submittedList = YouthConnectSingleTone.getInstance().submitedReport;
            List<Feedback> pendingList = YouthConnectSingleTone.getInstance().pendingReport;
            FeedbackRecyclerAdapter recyclerAdapter = new FeedbackRecyclerAdapter(getSupportFragmentManager(),
                    FeedbackActivity.this, USER_TYPE_NODAL_OFFICER, feedbackList);
            viewPager = (ViewPager)findViewById(R.id.viewPager);
            viewPager.setAdapter(recyclerAdapter);
            tabLayout = (TabLayout)findViewById(R.id.tabView);
            int number_of_pending_in_list = 0;
            if(pendingList != null) {
                number_of_pending_in_list = pendingList.size();
            }
            int number_of_submitted_in_list = 0;
            if(submittedList != null){
                number_of_submitted_in_list = submittedList.size();
            }
            tabLayout.addTab(tabLayout.newTab().setText(Constants.TAB_TITLE_FEEDBACK_PENDING + " (" + number_of_pending_in_list + ")"));
            tabLayout.addTab(tabLayout.newTab().setText(Constants.TAB_TITLE_FEEDBACK_SUBMITTED+ " (" + number_of_submitted_in_list + ")"));
            //tabLayout.addTab(tabLayout.newTab().setText(Constants.TAB_TITLE_FEEDBACK_EXPIRED));
        }
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setOnTabSelectedListener(
                new TabLayout.ViewPagerOnTabSelectedListener(viewPager) {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        super.onTabSelected(tab);
                        switch (tab.getPosition()) {
                            default:
                                break;
                        }
                    }
                });
        viewPager.getAdapter().notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(YouthConnectSingleTone.getInstance().isCameraCaptureActivityFinish == true){
            YouthConnectSingleTone.getInstance().isCameraCaptureActivityFinish = false;
            fetchData();
        }
    }

    @Override
    protected void onDestroy() {
        System.gc();
        super.onDestroy();
    }
}