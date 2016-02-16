package com.lipl.youthconnect.youth_connect.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import com.getbase.floatingactionbutton.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.lipl.youthconnect.youth_connect.R;
import com.lipl.youthconnect.youth_connect.util.ActivityIndicator;
import com.lipl.youthconnect.youth_connect.util.Constants;
import com.lipl.youthconnect.youth_connect.util.Util;
import com.lipl.youthconnect.youth_connect.pojo.Document;
import com.lipl.youthconnect.youth_connect.pojo.DocumentUpload;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
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
import java.net.URLConnection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by luminousinfoways on 21/12/15.
 */
public class FileDetailsActivity extends ActionBarActivity implements View.OnClickListener {

    private static Toolbar mToolbar = null;
    private Document document = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_details);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle("File Details");

        if(getIntent().getExtras() != null){
            document = getIntent().getExtras().getParcelable(Constants.INTENT_KEY_DOCUMENT);
        }

        int user_type_id = getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 0).getInt(Constants.SP_USER_TYPE, 0);
        FloatingActionsMenu multiple_actions_left = (FloatingActionsMenu) findViewById(R.id.multiple_actions_left);
        if(user_type_id == 1) {
            multiple_actions_left.setVisibility(View.VISIBLE);
        } else {
            multiple_actions_left.setVisibility(View.GONE);
        }

        FloatingActionButton fabDelete = (FloatingActionButton) findViewById(R.id.fabDelete);
        fabDelete.setOnClickListener(this);
        FloatingActionButton fabPublish = (FloatingActionButton) findViewById(R.id.fabPublish);
        fabPublish.setOnClickListener(this);
        FloatingActionButton fabUnpublish = (FloatingActionButton) findViewById(R.id.fabUnpublish);
        fabUnpublish.setOnClickListener(this);
        FloatingActionButton fabSendToNodalOfficers = (FloatingActionButton) findViewById(R.id.fabSendToNodalOfficers);
        fabSendToNodalOfficers.setOnClickListener(this);

        if(document != null){
            String createdBy = document.getUserFullName();
            TextView tvCreatedBy =  (TextView) findViewById(R.id.tvCreatedBy);
            tvCreatedBy.setText(createdBy);

            if(document.getDocumentMaster() != null
                    && document.getDocumentMaster().getDocument_title() != null) {
                TextView tvEventTitleValue = (TextView) findViewById(R.id.tvEventTitleValue);
                tvEventTitleValue.setText(document.getDocumentMaster().getDocument_title());
            }

            if(document.getDocumentMaster() != null
                    && document.getDocumentMaster().getDocument_purpose() != null) {
                TextView tvEventPurpose = (TextView) findViewById(R.id.tvEventPurpose);
                tvEventPurpose.setText(document.getDocumentMaster().getDocument_purpose());
            }

            List<DocumentUpload> documentUploadList = document.getDocumentUploadList();

            if(documentUploadList != null && documentUploadList.size() > 0) {

                LinearLayout layoutDoc = (LinearLayout) findViewById(R.id.layoutFileDetails);
                for (int i = 0; i < documentUploadList.size(); i++) {
                    final RelativeLayout layoutDocItem = (RelativeLayout) LayoutInflater.from(FileDetailsActivity.this).inflate(R.layout.list_item_file, null);

                    DocumentUpload documentUpload = documentUploadList.get(i);
                    String created = documentUpload.getCreated();
                    String modified = documentUpload.getModified();
                    int docId = documentUpload.getDocument_upload_id();
                    String doc_master_id = documentUpload.getDocument_master_id();
                    final String uploadFile = documentUpload.getUpload_file();

                    TextView tvFileTitle = (TextView) layoutDocItem.findViewById(R.id.tvFileTitle);
                    tvFileTitle.setText(uploadFile);

                    final RelativeLayout layoutImage = (RelativeLayout) layoutDocItem.findViewById(R.id.layoutImage);
                    final ImageView imgFileDownload = (ImageView) layoutDocItem.findViewById(R.id.imgFileDownload);
                    final ProgressBar progressBar = (ProgressBar) layoutDocItem.findViewById(R.id.progressBar);
                    if (isFileExists(uploadFile)) {
                        imgFileDownload.setTag(R.drawable.ic_insert_drive_file);
                        imgFileDownload.setImageResource(R.drawable.ic_insert_drive_file);
                        imgFileDownload.setBackgroundResource(R.drawable.circle_blue);
                        progressBar.setVisibility(View.INVISIBLE);
                    } else {
                        imgFileDownload.setTag(R.drawable.ic_file_download);
                        imgFileDownload.setImageResource(R.drawable.ic_file_download);
                        imgFileDownload.setBackgroundResource(R.color.blue);
                        progressBar.setVisibility(View.INVISIBLE);
                    }

                    layoutImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Integer integer = (Integer) imgFileDownload.getTag();
                            integer = integer == null ? 0 : integer;
                            if (integer == R.drawable.ic_file_download) {
                                if (Util.getNetworkConnectivityStatus(FileDetailsActivity.this)) {
                                    DownloadFileFromURL downloadFileAsync = new DownloadFileFromURL(imgFileDownload, progressBar);
                                    downloadFileAsync.execute(uploadFile);
                                }
                            } else if (integer == R.drawable.ic_insert_drive_file) {
                                String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/youth_connect";
                                openDocument(fullPath + "/" + uploadFile);
                                progressBar.setVisibility(View.INVISIBLE);
                            } else {
                                if (Util.getNetworkConnectivityStatus(FileDetailsActivity.this)) {
                                    DownloadFileFromURL downloadFileAsync = new DownloadFileFromURL(imgFileDownload, progressBar);
                                    downloadFileAsync.execute(uploadFile);
                                }
                            }
                        }
                    });

                    layoutDoc.addView(layoutDocItem);
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.fabSendToNodalOfficers:
                // Send to nodal officer
                Intent intent = new Intent(FileDetailsActivity.this, FileUploadNodalOfficerActivity.class);
                intent.putExtra(Constants.INTENT_KEY_DOCUMENT, document);
                startActivity(intent);
                break;
            case R.id.fabDelete:

                // condition : which are not published and not deleted you can delete
                // Delete document

                if(document != null
                        && document.getDocumentMaster() != null
                        && document.getDocumentMaster().getIs_published() != null
                        && document.getDocumentMaster().getIs_published().equalsIgnoreCase("N")) {

                    if(document.getDoc_master_id() > 0){

                    }

                    DeleteDocumentAsyncTask deleteDocumentAsyncTask = new DeleteDocumentAsyncTask();
                    deleteDocumentAsyncTask.execute();
                } else if(document != null
                        && document.getDocumentMaster() != null
                        && document.getDocumentMaster().getIs_published() != null
                        && document.getDocumentMaster().getIs_published().equalsIgnoreCase("Y")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(FileDetailsActivity.this, R.style.AppCompatAlertDialogStyle);
                    builder.setTitle("Doc Delete");
                    builder.setMessage("Sorry, this document can not be deleted.\nBecause it is published.");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                } else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(FileDetailsActivity.this, R.style.AppCompatAlertDialogStyle);
                    builder.setTitle("Doc Delete");
                    builder.setMessage("Sorry, this document can not be deleted.");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }

                break;
            case R.id.fabPublish:

                // Publish Document
                // condition : which are not published and not deleted you can publish

                if(document != null
                        && document.getDocumentMaster() != null
                        && document.getDocumentMaster().getIs_published() != null
                        && document.getDocumentMaster().getIs_published().equalsIgnoreCase("N")) {
                    PublishAndUnpublishDocumentAsyncTask publishAndUnpublishDocumentAsyncTask =
                            new PublishAndUnpublishDocumentAsyncTask();
                    publishAndUnpublishDocumentAsyncTask.execute("Y");

                } else if(document != null
                        && document.getDocumentMaster() != null
                        && document.getDocumentMaster().getIs_published() != null
                        && document.getDocumentMaster().getIs_published().equalsIgnoreCase("Y")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(FileDetailsActivity.this, R.style.AppCompatAlertDialogStyle);
                    builder.setTitle("Doc Publish");
                    builder.setMessage("Sorry, this document is already published.");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                } else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(FileDetailsActivity.this, R.style.AppCompatAlertDialogStyle);
                    builder.setTitle("Doc Publish");
                    builder.setMessage("Sorry, this document is already published");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }

                break;
            case R.id.fabUnpublish:

                // Un publish Document
                // condition : which are published and not deleted you can publish

                if(document != null
                        && document.getDocumentMaster() != null
                        && document.getDocumentMaster().getIs_published() != null
                        && document.getDocumentMaster().getIs_published().equalsIgnoreCase("Y")) {
                    PublishAndUnpublishDocumentAsyncTask publishAndUnpublishDocumentAsyncTask =
                            new PublishAndUnpublishDocumentAsyncTask();
                    publishAndUnpublishDocumentAsyncTask.execute("N");

                } else if(document != null
                        && document.getDocumentMaster() != null
                        && document.getDocumentMaster().getIs_published() != null
                        && document.getDocumentMaster().getIs_published().equalsIgnoreCase("N")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(FileDetailsActivity.this, R.style.AppCompatAlertDialogStyle);
                    builder.setTitle("Doc Unpublish");
                    builder.setMessage("Sorry, this document is not published.");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                } else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(FileDetailsActivity.this, R.style.AppCompatAlertDialogStyle);
                    builder.setTitle("Doc Unpublish");
                    builder.setMessage("Sorry, this document is not published.");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }

                break;
            default:
                break;
        }
    }

    private String getFileIds(){
        if(document == null || document.getDocumentUploadList() == null){
            return null;
        }

        // Format : [2,3,4]
        Set<Integer> docSet = new HashSet<Integer>();
        for(int i = 0; i < document.getDocumentUploadList().size(); i++){
            DocumentUpload documentUpload = document.getDocumentUploadList().get(i);
            if(documentUpload != null) {
                int document_upload_id = documentUpload.getDocument_upload_id();
                docSet.add(document_upload_id);
            }
        }

        try {
            JSONArray array = new JSONArray();
            for (Integer district_id : docSet) {
                array.put(district_id);
            }
            return array.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getDocumentIds(){
        if(document == null || document.getDocumentUploadList() == null){
            return null;
        }

        // Format : [2,3,4]
        Set<Integer> docSet = new HashSet<Integer>();
        if(document.getDoc_master_id() > 0){
            docSet.add(document.getDoc_master_id());
        } else if(document.getDocumentMaster() != null){
            document.getDocumentMaster().getDocument_master_id();
        } else{
            docSet.add(document.getDoc_master_id());
        }

        try {
            JSONArray array = new JSONArray();
            for (Integer district_id : docSet) {
                array.put(district_id);
            }
            return array.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Async task to get sync camp table from server
     * */
    private class DeleteDocumentAsyncTask extends AsyncTask<String, Void, Boolean> {

        private static final String TAG = "DeleteDocumentAsyncTask";
        //private ProgressDialog progressDialog = null;
        private boolean isChangePassword = false;
        private String message;
        private ActivityIndicator activityIndicator = ActivityIndicator.ctor(FileDetailsActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /*if(progressDialog == null) {
                progressDialog = ProgressDialog.show(FileDetailsActivity.this, "Deleting", "Please wait...");
            }*/
            if(activityIndicator == null){
                activityIndicator = new ActivityIndicator(FileDetailsActivity.this);
            }
            activityIndicator.show();

        }

        @Override
        protected Boolean doInBackground(String... params) {

            String api_key = getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 1).getString(Constants.SP_USER_API_KEY, null);

            if(api_key == null){
                return null;
            }

            try {

                InputStream in = null;
                int resCode = -1;

                String link = Constants.BASE_URL+Constants.REQUEST_URL_ADMIN_DOCUMENT_DELETE;
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

                String fileIds = getFileIds();
                String docIds = getDocumentIds();

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("document_upload_id", getFileIds())
                        .appendQueryParameter("response", "mobile")
                        .appendQueryParameter("document_id", getDocumentIds());

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
                    return null;
                }
                BufferedReader reader =new BufferedReader(new InputStreamReader(in, "UTF-8"));
                String response = "",data="";

                while ((data = reader.readLine()) != null){
                    response += data + "\n";
                }

                Log.i(TAG, "Response : " + response);

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

                /**
                 * {
                 {
                 "message":"File sent successfully."
                 }
                 * */

                if(response != null && response.length() > 0){

                    JSONObject res = new JSONObject(response);
                    message = res.optString("message");
                    int status = res.optInt("status");
                    if(status == 1){

                        return true;
                    } else {
                        return false;
                    }
                }
            } catch(SocketTimeoutException exception){
                Log.e(TAG, "GetFileListAsyncTask : doInBackground", exception);
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

            //if(progressDialog != null) progressDialog.dismiss();
            if(activityIndicator == null){
                activityIndicator = new ActivityIndicator(FileDetailsActivity.this);
            }
            activityIndicator.dismiss();

            if(isChangePassword){
                AlertDialog.Builder builder = new AlertDialog.Builder(FileDetailsActivity.this, R.style.AppCompatAlertDialogStyle);
                builder.setTitle(getResources().getString(R.string.password_changed_title));
                builder.setMessage(getResources().getString(R.string.password_changed_description));
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent(FileDetailsActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("Exit me", true);
                        startActivity(intent);
                        finish();
                    }
                });
                builder.show();

                return;
            }

            if(isSuccess){
                getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 2).edit().putInt(Constants.IS_ACTION_TAKEN_FOR_DOC, 1).commit();
            } else{
                getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 2).edit().putInt(Constants.IS_ACTION_TAKEN_FOR_DOC, 0).commit();
            }

            String dialogMessage = "";
            if(message != null && message.length() > 0){
                dialogMessage = message;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(FileDetailsActivity.this, R.style.AppCompatAlertDialogStyle);
            builder.setTitle("Youth Connect");
            builder.setMessage(dialogMessage);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            });
            builder.show();
        }
    }

    /**
     * Async task to get sync camp table from server
     * */
    private class PublishAndUnpublishDocumentAsyncTask extends AsyncTask<String, Void, Boolean> {

        private static final String TAG = "DeleteDocumentAsyncTask";
        //private ProgressDialog progressDialog = null;
        private boolean isChangePassword = false;
        private String message;
        private ActivityIndicator activityIndicator = ActivityIndicator.ctor(FileDetailsActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /*if(progressDialog == null) {
                progressDialog = ProgressDialog.show(FileDetailsActivity.this, "Processing", "Please wait...");
            }*/
            if(activityIndicator == null){
                activityIndicator = new ActivityIndicator(FileDetailsActivity.this);
            }
            activityIndicator.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {

            String is_publish = params[0];

            if(is_publish == null || is_publish.trim().length() <= 0){
                return null;
            }

            String api_key = getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 1).getString(Constants.SP_USER_API_KEY, null);

            if(api_key == null){
                return null;
            }

            try {

                InputStream in = null;
                int resCode = -1;

                String link = Constants.BASE_URL+Constants.REQUEST_URL_ADMIN_DOCUMENT_UPDATE;
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

                String fileIds = getFileIds();
                String docIds = getDocumentIds();

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("response", "mobile")
                        .appendQueryParameter("is_published", is_publish)
                        .appendQueryParameter("document_id", getDocumentIds());

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
                    return null;
                }
                BufferedReader reader =new BufferedReader(new InputStreamReader(in, "UTF-8"));
                String response = "",data="";

                while ((data = reader.readLine()) != null){
                    response += data + "\n";
                }

                Log.i(TAG, "Response : " + response);

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

                /**
                 * {
                 {
                 "message":"File sent successfully."
                 }
                 * */

                if(response != null && response.length() > 0){

                    JSONObject res = new JSONObject(response);
                    message = res.optString("message");
                    int status = res.optInt("status");
                    if(status == 1){

                        return true;
                    } else {
                        return false;
                    }
                }
            } catch(SocketTimeoutException exception){
                Log.e(TAG, "GetFileListAsyncTask : doInBackground", exception);
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

            //if(progressDialog != null) progressDialog.dismiss();
            if(activityIndicator == null){
                activityIndicator = new ActivityIndicator(FileDetailsActivity.this);
            }
            activityIndicator.dismiss();

            if(isChangePassword){
                AlertDialog.Builder builder = new AlertDialog.Builder(FileDetailsActivity.this, R.style.AppCompatAlertDialogStyle);
                builder.setTitle(getResources().getString(R.string.password_changed_title));
                builder.setMessage(getResources().getString(R.string.password_changed_description));
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent(FileDetailsActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("Exit me", true);
                        startActivity(intent);
                        finish();
                    }
                });
                builder.show();

                return;
            }

            if(isSuccess){
                getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 2).edit().putInt(Constants.IS_ACTION_TAKEN_FOR_DOC, 1).commit();
            } else{
                getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 2).edit().putInt(Constants.IS_ACTION_TAKEN_FOR_DOC, 0).commit();
            }

            String dialogMessage = "";
            if(message != null && message.length() > 0){
                dialogMessage = message;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(FileDetailsActivity.this, R.style.AppCompatAlertDialogStyle);
            builder.setTitle("Youth Connect");
            builder.setMessage(dialogMessage);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            });
            builder.show();
        }
    }

    private boolean isFileExists(String fileName){
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/youth_connect";
        String fullPath = path + "/" + fileName;
        File file = new File(fullPath);
        if(file.exists()) {
            return true;
        } else {
            return false;
        }
    }

    public void openDocument(String name) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        File file = new File(name);
        String extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString());
        String mimetype = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        if (extension.equalsIgnoreCase("") || mimetype == null) {
            // if there is no extension or there is no definite mimetype, still try to open the file
            intent.setDataAndType(Uri.fromFile(file), "text/*");
        } else {
            intent.setDataAndType(Uri.fromFile(file), mimetype);
        }
        // custom message for the intent
        Intent appIntent = Intent.createChooser(intent, "Choose an Application:");
        if(appIntent != null){
            startActivity(appIntent);
        } else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
            builder.setTitle(getResources().getString(R.string.no_app_found_title));
            builder.setMessage(getResources().getString(R.string.no_app_found_message));
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
        }
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

    /**
     * Background Async Task to download file
     * */
    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        private ImageView imgFileDownload = null;
        private ProgressBar progressBar = null;

        public DownloadFileFromURL(ImageView imgFileDownload, ProgressBar progressBar){
            this.imgFileDownload = imgFileDownload;
            this.progressBar = progressBar;
        }

        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(progressBar != null) {
                progressBar.setVisibility(View.VISIBLE);
            }
        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {

                String fileName = f_url[0];

                if(fileName == null || fileName.trim().length() <= 0){
                    return null;
                }

                String req_url = Constants.BASE_URL + Constants.DOCUMENT_DOWNLOAD_REQUEST_URL + f_url[0];

                URL url = new URL(req_url);
                URLConnection conection = url.openConnection();
                conection.connect();
                // getting file length
                int lenghtOfFile = conection.getContentLength();

                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/youth_connect";
                File dir = new File(fullPath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                OutputStream fOut = null;
                File file = new File(fullPath, fileName);
                if(file.exists())
                    file.delete();
                file.createNewFile();

                // Output stream to write file
                OutputStream output = new FileOutputStream(file);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress(""+(int)((total*100)/lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

                return file.getPath();
            } catch(SocketTimeoutException exception){
                Log.e("FilesDetailsActivity", "GetFeedbackListAsync : doInBackground", exception);
            } catch(ConnectException exception){
                Log.e("FilesDetailsActivity", "GetFeedbackListAsync : doInBackground", exception);
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        /**
         * Updating progress bar
         * */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
        }

        /**
         * After completing background task
         * Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(final String file_url) {
            // dismiss the dialog after the file was downloaded

            // Displaying downloaded image into image view
            // Reading image path from sdcard
            if(progressBar != null) {
                progressBar.setVisibility(View.INVISIBLE);
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (file_url != null && file_url.trim().length() > 0) {
                        imgFileDownload.setTag(R.drawable.ic_insert_drive_file);
                        imgFileDownload.setImageResource(R.drawable.ic_insert_drive_file);
                        imgFileDownload.setBackgroundResource(R.drawable.circle_blue);
                    }
                }
            }, 2000);

        }
    }

    public void saveImageToExternalStorage(Bitmap image) {
        String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/youth_connect";
        try
        {
            File dir = new File(fullPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            OutputStream fOut = null;
            File file = new File(fullPath, "image.png");
            if(file.exists())
                file.delete();
            file.createNewFile();
            fOut = new FileOutputStream(file);
            // 100 means no compression, the lower you go, the stronger the compression
            image.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
        }
        catch (Exception e)
        {
            Log.e("saveToExternalStorage()", e.getMessage());
        }
    }
}