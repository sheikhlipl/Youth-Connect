package com.lipl.youthconnect.youth_connect.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lipl.youthconnect.youth_connect.R;
import com.lipl.youthconnect.youth_connect.util.ActivityIndicator;
import com.lipl.youthconnect.youth_connect.util.Constants;
import com.lipl.youthconnect.youth_connect.util.Util;
import com.lipl.youthconnect.youth_connect.pojo.Document;
import com.lipl.youthconnect.youth_connect.pojo.PendingFileToUpload;
import com.lipl.youthconnect.youth_connect.pojo.User;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;

public class FileUploadFinalActivity extends ActionBarActivity {

    private static Toolbar mToolbar = null;
    private TextView tvEmptyView;
    private LinearLayoutManager mLayoutManager;

    protected Handler handler;
    private static final int FILE_REQ = 675;
    private static final String TAG = FileUploadFinalActivity.class.getSimpleName();
    private static final String FILES_TO_UPLOAD = "upload";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_upload_final);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle("Create Document");

        tvEmptyView = (TextView) findViewById(R.id.tvNoRecordFoundText);
        handler = new Handler();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MaterialEditText eventTitle = (MaterialEditText) findViewById(R.id.eventTitle);
                String title = eventTitle.getText().toString().trim();
                MaterialEditText eventPurpose = (MaterialEditText) findViewById(R.id.eventPurpose);
                String purpose = eventPurpose.getText().toString().trim();

                if(title == null || title.trim().length() <= 0){
                    eventTitle.setError("Provide title.");
                    return;
                }

                if(purpose == null || purpose.trim().length() <= 0){
                    eventPurpose.setError("Provide purpose.");
                    return;
                }

                //TODO
                // Get Doc id from web service if internet available
                // and if there is no connection then create a random 4 digit number to create a doc id for offline.
                // when internet will available then create the doc first then send the files against that document.

                if(Util.getNetworkConnectivityStatus(FileUploadFinalActivity.this)) {
                    CreateDocAsync createDocAsync = new CreateDocAsync();
                    createDocAsync.execute(title, purpose);
                } else{
                    //TODO for ofline
                    //Generate alphanumeric id for doc id

                    String doc_id = Util.getRandomAlphaNumericString();

                    Intent intent = new Intent(FileUploadFinalActivity.this, FileChooserMultipleActivity.class);
                    intent.putExtra(Constants.DOC_TITLE, title);
                    intent.putExtra(Constants.DOC_PURPOSE, purpose);
                    intent.putExtra(Constants.DOC_ID, doc_id);
                    intent.putExtra(Constants.IS_DOC_ID_AUTO_GENERATED, 1);
                    startActivityForResult(intent, FILE_REQ);
                    if (FileChooserMultipleActivity.fileUploadList != null) {
                        FileChooserMultipleActivity.fileUploadList.clear();
                    } else {
                        FileChooserMultipleActivity.fileUploadList = new ArrayList<PendingFileToUpload>();
                    }
                    finish();

                }
            }
        });
    }

    private String getJsonObjectDataForAssignForNodalOfficer(){

        /*
        * {"qa_answer_id":"","user_id":"1","qadmin_description":"dfg hdfjhgkh","post_date":"2015-12-12 12:12:12"}
        * */

        try {
            JSONArray array = new JSONArray();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("m_district_id", "0");
            jsonObject.put("user_id", "1");
            array.put(jsonObject);

            return array.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Async task to get sync camp table from server
     * */
    private class CreateDocAsync extends AsyncTask<String, Void, Integer> {

        private ActivityIndicator activityIndicator = ActivityIndicator.ctor(FileUploadFinalActivity.this);

        private static final String TAG = "CreateDocAsync";
        //private ProgressDialog progressDialog = null;
        private boolean isChangePassword = false;
        private String title = null;
        private String purpose = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /*if(progressDialog == null) {
                progressDialog = ProgressDialog.show(FileUploadFinalActivity.this, "Creating", "Please wait...");
            }*/
            if(activityIndicator == null){
                activityIndicator = new ActivityIndicator(FileUploadFinalActivity.this);
            }
            activityIndicator.show();
        }

        @Override
        protected Integer doInBackground(String... params) {

            title = params[0];
            purpose = params[1];

            String api_key = getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 1).getString(Constants.SP_USER_API_KEY, null);
            int user_type_id = getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 1).getInt(Constants.SP_USER_TYPE, 0);
            int user_id = getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 1).getInt(Constants.SP_USER_ID, 0);
            String desg_id = getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 1).getString(Constants.SP_USER_DESG_ID, null);

            String assignData = null;
            //if(user_type_id == 2){
                assignData = getJsonObjectDataForAssignForNodalOfficer();
            //} else{
                //TODO
            //}

            if(api_key == null || title == null || purpose == null){
                return null;
            }

            try {

                InputStream in = null;
                int resCode = -1;

                String link = Constants.BASE_URL+Constants.REQUEST_URL_DOC_CREATE;
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

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("data[DocumentMaster][user_id]", user_id+"")
                        .appendQueryParameter("response", "mobile")
                        .appendQueryParameter("data[DocumentMaster][m_desg_id]", desg_id)
                        .appendQueryParameter("data[DocumentMaster][m_user_type_id]", user_type_id+"")
                        .appendQueryParameter("data[DocumentMaster][document_title]", title)
                        .appendQueryParameter("data[DocumentMaster][document_purpose]", purpose)
                        .appendQueryParameter("data[DocumentAssign]", assignData);

                Log.i("FileChooserMultiple", "assigndata" + assignData);

                String query = builder.build().getEncodedQuery();

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();

                conn.connect();
                resCode = conn.getResponseCode();
                if (resCode == HttpURLConnection.HTTP_OK) {
                    in = conn.getInputStream();
                }

                if(in == null){
                    return 0;
                }

                BufferedReader reader =new BufferedReader(new InputStreamReader(in, "UTF-8"));
                String response = "",data="";

                while ((data = reader.readLine()) != null){
                    response += data + "\n";
                }

                Log.i(TAG, "Response : " + response);

                /**
                 * {
                    "doc_id": "275",
                    "status": 1
                 }
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

                if(response != null && response.trim().length() > 0){

                    String doc_id = "";
                    int status = 0;
                    JSONObject jsonObject = new JSONObject(response.trim());
                    if (jsonObject != null && jsonObject.isNull("doc_id") == false) {
                        doc_id = jsonObject.getString("doc_id");
                    }
                    if (jsonObject != null && jsonObject.isNull("status") == false) {
                        status = jsonObject.getInt("status");
                    }

                    if(status == 1){

                        if(doc_id != null
                                && doc_id.trim().length() > 0
                                && TextUtils.isDigitsOnly(doc_id)){

                            Document document = new Document(Parcel.obtain());
                            document.setDoc_master_id(Integer.parseInt(doc_id));

                            document.setUserFullName("");
                        }

                        return 0;
                    } else{
                        return 0;
                    }
                }
            } catch(SocketTimeoutException exception){
                Log.e(TAG, "GetFeedbackListAsync : doInBackground", exception);
            } catch(ConnectException exception){
                Log.e(TAG, "GetFileListAsyncTask : doInBackground", exception);
            } catch(MalformedURLException exception){
                Log.e(TAG, "LoginAsync : doInBackground", exception);
            } catch (IOException exception){
                Log.e(TAG, "LoginAsync : doInBackground", exception);
            } catch(Exception exception){
                Log.e(TAG, "LoginAsync : doInBackground", exception);
            }

            return 0;
        }

        @Override
        protected void onPostExecute(final Integer doc_id) {
            super.onPostExecute(doc_id);

            if(isChangePassword){
                AlertDialog.Builder builder = new AlertDialog.Builder(FileUploadFinalActivity.this, R.style.AppCompatAlertDialogStyle);
                builder.setTitle(getResources().getString(R.string.password_changed_title));
                builder.setMessage(getResources().getString(R.string.password_changed_description));
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent(FileUploadFinalActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("Exit me", true);
                        startActivity(intent);
                        finish();
                    }
                });
                builder.show();

                return;
            }

            //if(progressDialog != null) progressDialog.dismiss();
            if(activityIndicator == null){
                activityIndicator = new ActivityIndicator(FileUploadFinalActivity.this);
            }
            activityIndicator.dismiss();
            String dialogMessage = null;
            if(doc_id > 0) {
                getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 2).edit().putInt(Constants.IS_ACTION_TAKEN_FOR_DOC, 1).commit();
                dialogMessage = "Created successfully.";
            } else {
                getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 2).edit().putInt(Constants.IS_ACTION_TAKEN_FOR_DOC, 0).commit();
                dialogMessage = "Sorry, failed to upload your document.\nPlease try again";
            }

            if(dialogMessage != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(FileUploadFinalActivity.this, R.style.AppCompatAlertDialogStyle);
                builder.setTitle("Document Create");
                builder.setMessage(dialogMessage);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (doc_id > 0) {
                            Intent intent = new Intent(FileUploadFinalActivity.this, FileChooserMultipleActivity.class);
                            intent.putExtra(Constants.DOC_TITLE, title);
                            intent.putExtra(Constants.DOC_PURPOSE, purpose);
                            intent.putExtra(Constants.DOC_ID, doc_id+"");
                            intent.putExtra(Constants.IS_DOC_ID_AUTO_GENERATED, 0);
                            startActivityForResult(intent, FILE_REQ);
                            if (FileChooserMultipleActivity.fileUploadList != null) {
                                FileChooserMultipleActivity.fileUploadList.clear();
                            } else {
                                FileChooserMultipleActivity.fileUploadList = new ArrayList<PendingFileToUpload>();
                            }
                            finish();
                        }
                    }
                });
                builder.show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult()");
        if(requestCode == FILE_REQ){
            if(resultCode == RESULT_OK){
                String path = data.getExtras().getString(Constants.INTENT_KEY_FILE_PATH);
                File fileToBeUpload = new File(path);
                if(fileToBeUpload.exists()){
                    addToFileList(fileToBeUpload.getName());
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void addToFileList(String fileNameList){
        LinearLayout layoutFileListToUpload = (LinearLayout) findViewById(R.id.layoutFileListToUpload);

        RelativeLayout fileItem = (RelativeLayout) LayoutInflater.from(FileUploadFinalActivity.this).inflate(R.layout.list_row_file_item, null);
        TextView tvFileName = (TextView) fileItem.findViewById(R.id.tvFileName);
        tvFileName.setText(fileNameList);
        layoutFileListToUpload.addView(fileItem);
    }

    public static Toolbar getToolbar(){
        return mToolbar;
    }

    /**
     * When touch on screen outside the keyboard, the input keyboard will hide automatically
     * */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View v = getCurrentFocus();

        if (v != null &&
                (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) &&
                v instanceof EditText &&
                !v.getClass().getName().startsWith("android.webkit."))
        {
            int scrcoords[] = new int[2];
            v.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + v.getLeft() - scrcoords[0];
            float y = ev.getRawY() + v.getTop() - scrcoords[1];

            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom()) {
                Util.hideKeyboard(this);
            }
        }
        return super.dispatchTouchEvent(ev);
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
        getMenuInflater().inflate(R.menu.menu_file_actionbar, menu);
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
}