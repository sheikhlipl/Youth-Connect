package com.lipl.youthconnect.youth_connect.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.lipl.youthconnect.youth_connect.R;
import com.lipl.youthconnect.youth_connect.util.Constants;
import com.lipl.youthconnect.youth_connect.util.Util;
import com.lipl.youthconnect.youth_connect.adapter.FileUploadArrayAdapter;
import com.lipl.youthconnect.youth_connect.pojo.FileChooseDetaiuls;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import java.util.List;

public class FileUploadDetailFinalActivity extends ActionBarActivity {

    private static Toolbar mToolbar = null;
    private ListView mListView = null;
    private FileUploadArrayAdapter adapter = null;
    private String title = "";
    private String purpose = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_upload);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle("Upload Selected Files");

        if(getIntent().getExtras() != null){
            title = getIntent().getExtras().getString(Constants.DOC_TITLE);
            purpose = getIntent().getExtras().getString(Constants.DOC_PURPOSE);
        }

        mListView = (ListView) findViewById(R.id.listView);
    }

    /**
     * Async task to get sync camp table from server
     * */
    private class FileUploadAsync extends AsyncTask<String, Void, Boolean> {

        private static final String TAG = "CreateDocAsync";
        //private ProgressDialog progressDialog = null;
        private List<FileChooseDetaiuls> fileDetails = null;
        private boolean isChangePassword = false;

        public FileUploadAsync(List<FileChooseDetaiuls> fileDetails){
            this.fileDetails = fileDetails;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /*if(progressDialog == null) {
                progressDialog = ProgressDialog.show(FileChooserMultipleActivity2.this, "Uploading", "Please wait...");
            }*/
        }

        @Override
        protected Boolean doInBackground(String... params) {

            String title = params[0];
            String purpose = params[1];

            String api_key = getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 1).getString(Constants.SP_USER_API_KEY, null);
            int user_type_id = getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 1).getInt(Constants.SP_USER_TYPE, 0);
            int user_id = getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 1).getInt(Constants.SP_USER_ID, 0);
            String desg_id = getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 1).getString(Constants.SP_USER_DESG_ID, null);

            String assignData = null;
            if(user_type_id == 2){
                assignData = getJsonObjectDataForAssignForNodal();
            } else{
                //TODO
            }

            String jsonData = getJsonObjectData(fileDetails);

            if(api_key == null){
                return null;
            }

            try {

                InputStream in = null;
                int resCode = -1;

                String link = Constants.BASE_URL+Constants.REQUEST_DOC_UPLOAD;
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
                        .appendQueryParameter("data[DocumentAssign]", assignData)
                        .appendQueryParameter("data[DocumentUpload]", jsonData);

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
                    return false;
                }

                BufferedReader reader =new BufferedReader(new InputStreamReader(in, "UTF-8"));
                String response = "",data="";

                while ((data = reader.readLine()) != null){
                    response += data + "\n";
                }

                Log.i(TAG, "Response : " + response);

                /**
                 * {
                 {
                 "message": 0
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

                if(response != null && response.trim().length() > 0 && TextUtils.isDigitsOnly(response.trim())){

//                    if(response.trim().contains("\n")){
//                        response = response.replace("\n", "");
//                    }

                    int res = Integer.parseInt(response.trim());
                    if(res == 1){
                        return true;
                    } else{
                        return false;
                    }
                }
            } catch(SocketTimeoutException exception){
                Log.e("FilesDetailsActivity", "GetFeedbackListAsync : doInBackground", exception);
            } catch(ConnectException exception){
                Log.e(TAG, "GetFileListAsyncTask : doInBackground", exception);
            } catch(MalformedURLException exception){
                Log.e(TAG, "LoginAsync : doInBackground", exception);
            } catch (IOException exception){
                Log.e(TAG, "LoginAsync : doInBackground", exception);
            } catch(Exception exception){
                Log.e(TAG, "LoginAsync : doInBackground", exception);
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean isSuccess) {
            super.onPostExecute(isSuccess);

            if(isChangePassword){
                AlertDialog.Builder builder = new AlertDialog.Builder(FileUploadDetailFinalActivity.this, R.style.AppCompatAlertDialogStyle);
                builder.setTitle(getResources().getString(R.string.password_changed_title));
                builder.setMessage(getResources().getString(R.string.password_changed_description));
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent(FileUploadDetailFinalActivity.this, MainActivity.class);
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
            String dialogMessage = null;
            if(isSuccess){
                dialogMessage = "Uploaded successfully.";
                showAlertDialog(dialogMessage, "Upload Document", "Ok", true);
            } else{
                dialogMessage = "Sorry, failed to upload your document.\nPlease try again";
                showAlertDialog(dialogMessage, "Upload Document", "Ok", false);
            }
        }
    }

    /**
     * To Show Material Alert Dialog
     *
     * @param message
     * @param title
     * */
    private void showAlertDialog(String message, String title, String positiveButtonText, final boolean isSuccess){
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
            builder.setTitle(title);
            builder.setMessage(message);
            builder.setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    if (isSuccess) {
                        finish();
                    }
                }
            });
            if (!((Activity) FileUploadDetailFinalActivity.this).isFinishing()) {
                builder.show();
            }
        } catch (WindowManager.BadTokenException exception){
            Log.e("FileChooserMultiple","showAlertDialog()",exception);
        }
    }

    private List<FileChooseDetaiuls> getFileDetailsList(List<String> filename) {

        int total_kb = 0;

        if(filename != null && filename.size() > 0){
            for(int i = 0; i < filename.size(); i++){
                String pathname = filename.get(i);
                File originalFile = new File(pathname);
                int file_size_in_kb = Integer.parseInt(String.valueOf(originalFile.length() / 1024));
                total_kb = total_kb + file_size_in_kb;
            }
        }

        if(total_kb > 2000){

            Snackbar snackbar = Snackbar.make(findViewById(R.id.layoutParent), "Can not upload more than 2MB.\nPlease upload with in 2MB at a time.", Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
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

            return null;
        }

        try {

            List<FileChooseDetaiuls> fileChooseDetaiulses = new ArrayList<FileChooseDetaiuls>();
            for (int i = 0; i < filename.size(); i++) {

                File originalFile = new File(filename.get(i));
                String extension = MimeTypeMap.getFileExtensionFromUrl(originalFile.toURL().toString());
                String fileType = "";
                if (extension != null) {
                    fileType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                }
                String encodedBase64 = null;
                try {
                    FileInputStream fileInputStreamReader = new FileInputStream(originalFile);
                    byte[] bytes = new byte[(int) originalFile.length()];
                    fileInputStreamReader.read(bytes);
                    encodedBase64 = new String(Base64.encodeBase64(bytes));

                    FileChooseDetaiuls fileChooseDetaiuls = new FileChooseDetaiuls(Parcel.obtain());
                    fileChooseDetaiuls.setFileType(fileType);
                    fileChooseDetaiuls.setExtension(extension);
                    fileChooseDetaiuls.setBase64Data(encodedBase64);
                    fileChooseDetaiuls.setFileName(originalFile.getPath());

                    fileChooseDetaiulses.add(fileChooseDetaiuls);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return fileChooseDetaiulses;
        } catch(IOException exp){
            return null;
        }
    }

    private String getJsonObjectData(List<FileChooseDetaiuls> detailsList){

        /*
        * {"qa_answer_id":"","user_id":"1","qadmin_description":"dfg hdfjhgkh","post_date":"2015-12-12 12:12:12"}
        * */

        try {
            JSONArray array = new JSONArray();

            for(int i = 0; i < detailsList.size(); i++){
                String fileType = detailsList.get(i).getFileType();
                //String extension = detailsList.get(i).getExtension();
                String fileName = detailsList.get(i).getFileName();
                String extension = "";
                if(fileName != null && fileName.length() > 0){
                    extension = fileName.substring(fileName.lastIndexOf(".") + 1);
                }
                String base64 = detailsList.get(i).getBase64Data();

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("upload_file", base64);
                jsonObject.put("upload_file_ext", extension);
                array.put(jsonObject);
            }

            return array.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getJsonObjectDataForAssignForNodal(){

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

    public static Toolbar getToolbar(){
        return mToolbar;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings_actionbar, menu);
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