package com.lipl.youthconnect.youth_connect.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.replicator.Replication;
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
import com.lipl.youthconnect.youth_connect.pojo.Doc;
import com.lipl.youthconnect.youth_connect.pojo.FileToUpload;
import com.lipl.youthconnect.youth_connect.util.ActivityIndicator;
import com.lipl.youthconnect.youth_connect.util.Constants;
import com.lipl.youthconnect.youth_connect.util.DatabaseUtil;
import com.lipl.youthconnect.youth_connect.util.DocUtil;
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
public class FileDetailsActivity extends ActionBarActivity implements View.OnClickListener, Replication.ChangeListener {

    private static Toolbar mToolbar = null;
    private Doc document = null;
    String host = "http://192.168.1.107";
    String port = "4984";
    String dbName = Constants.YOUTH_CONNECT_DATABASE;
    private static final String TAG = "FileDetailsActivity";
    /*
    * url to download file : http://192.168.1.107:4984/youth_connect/{doc_id}/{file_name}
    * */

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
            document = (Doc) getIntent().getExtras().getSerializable(Constants.INTENT_KEY_DOCUMENT);
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
            String createdBy = document.getCreated_by_user_name();
            TextView tvCreatedBy =  (TextView) findViewById(R.id.tvCreatedBy);
            tvCreatedBy.setText(createdBy);

            if(document.getDoc_title() != null) {
                TextView tvEventTitleValue = (TextView) findViewById(R.id.tvEventTitleValue);
                tvEventTitleValue.setText(document.getDoc_title());
            }

            if(document.getDoc_purpose() != null) {
                TextView tvEventPurpose = (TextView) findViewById(R.id.tvEventPurpose);
                tvEventPurpose.setText(document.getDoc_purpose());
            }

            List<FileToUpload> fileToUploads = document.getFileToUploads();

            if(fileToUploads != null && fileToUploads.size() > 0) {

                LinearLayout layoutDoc = (LinearLayout) findViewById(R.id.layoutFileDetails);
                for (int i = 0; i < fileToUploads.size(); i++) {
                    final RelativeLayout layoutDocItem = (RelativeLayout) LayoutInflater.from(FileDetailsActivity.this).inflate(R.layout.list_item_file, null);

                    final FileToUpload documentUpload = fileToUploads.get(i);
                    final String uploadFile = documentUpload.getFile_name();
                    TextView tvFileTitle = (TextView) layoutDocItem.findViewById(R.id.tvFileTitle);
                    tvFileTitle.setText(uploadFile);

                    final RelativeLayout layoutImage = (RelativeLayout) layoutDocItem.findViewById(R.id.layoutImage);
                    final ImageView imgFileDownload = (ImageView) layoutDocItem.findViewById(R.id.imgFileDownload);
                    final ProgressBar progressBar = (ProgressBar) layoutDocItem.findViewById(R.id.progressBar);

                    imgFileDownload.setTag(R.drawable.ic_file_download);
                    imgFileDownload.setImageResource(R.drawable.ic_file_download);
                    imgFileDownload.setBackgroundResource(R.color.blue);
                    progressBar.setVisibility(View.INVISIBLE);


                    layoutImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Integer integer = (Integer) imgFileDownload.getTag();
                            integer = integer == null ? 0 : integer;
                            if (integer == R.drawable.ic_file_download) {
                                if (Util.getNetworkConnectivityStatus(FileDetailsActivity.this)) {
                                    //Download file from download link
                                    String download_link = documentUpload.getDownload_link_url();
                                    if(download_link != null && download_link.length() > 0){
                                        DownloadFileFromURL downloadFileAsync = new DownloadFileFromURL(imgFileDownload, progressBar);
                                        downloadFileAsync.execute(download_link);
                                    }
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
                if(document != null && document.getIs_published() == 0){
                    String doc_id = document.getDoc_id();
                    try {
                        DocUtil.deleteDoc(DatabaseUtil.getDatabaseInstance(FileDetailsActivity.this,
                                Constants.YOUTH_CONNECT_DATABASE), doc_id);
                        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
                        builder.setTitle("Doc Publish");
                        builder.setMessage("Done.");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    DatabaseUtil.startReplications(FileDetailsActivity.this,
                                            FileDetailsActivity.this, TAG);
                                } catch(CouchbaseLiteException exception){
                                    Log.e(TAG, "sendDocumnetToNodalOfficers()", exception);
                                } catch(IOException exception){
                                    Log.e(TAG, "sendDocumnetToNodalOfficers()", exception);
                                } catch(Exception exception){
                                    Log.e(TAG, "sendDocumnetToNodalOfficers()", exception);
                                }
                                dialog.dismiss();
                            }
                        });
                        builder.show();
                    } catch(CouchbaseLiteException exception){
                        Log.e(TAG, "OnClick()", exception);
                    } catch(IOException exception){
                        Log.e(TAG, "OnClick()", exception);
                    } catch(Exception exception){
                        Log.e(TAG, "OnClick()", exception);
                    }
                }

                break;
            case R.id.fabPublish:

                // Publish Document
                // condition : which are not published and not deleted you can publish
                if(document != null && document.getIs_published() == 0){
                    String doc_id = document.getDoc_id();
                    try {
                        DocUtil.updateDocForPublishStatus(DatabaseUtil.getDatabaseInstance(FileDetailsActivity.this,
                                Constants.YOUTH_CONNECT_DATABASE), doc_id, 1);
                        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
                        builder.setTitle("Doc Publish");
                        builder.setMessage("Done.");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    DatabaseUtil.startReplications(FileDetailsActivity.this,
                                            FileDetailsActivity.this, TAG);
                                } catch(CouchbaseLiteException exception){
                                    Log.e(TAG, "sendDocumnetToNodalOfficers()", exception);
                                } catch(IOException exception){
                                    Log.e(TAG, "sendDocumnetToNodalOfficers()", exception);
                                } catch(Exception exception){
                                    Log.e(TAG, "sendDocumnetToNodalOfficers()", exception);
                                }
                                dialog.dismiss();
                            }
                        });
                        builder.show();
                    } catch(CouchbaseLiteException exception){
                        Log.e(TAG, "OnClick()", exception);
                    } catch(IOException exception){
                        Log.e(TAG, "OnClick()", exception);
                    } catch(Exception exception){
                        Log.e(TAG, "OnClick()", exception);
                    }
                }

                break;
            case R.id.fabUnpublish:

                // Un publish Document
                // condition : which are published and not deleted you can publish
                if(document != null && document.getIs_published() == 1){
                    String doc_id = document.getDoc_id();
                    try {
                        DocUtil.updateDocForPublishStatus(DatabaseUtil.getDatabaseInstance(FileDetailsActivity.this,
                                Constants.YOUTH_CONNECT_DATABASE), doc_id, 1);
                        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
                        builder.setTitle("Doc UnPublish");
                        builder.setMessage("Done.");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    DatabaseUtil.startReplications(FileDetailsActivity.this,
                                            FileDetailsActivity.this, TAG);
                                } catch(CouchbaseLiteException exception){
                                    Log.e(TAG, "sendDocumnetToNodalOfficers()", exception);
                                } catch(IOException exception){
                                    Log.e(TAG, "sendDocumnetToNodalOfficers()", exception);
                                } catch(Exception exception){
                                    Log.e(TAG, "sendDocumnetToNodalOfficers()", exception);
                                }
                                dialog.dismiss();
                            }
                        });
                    } catch(CouchbaseLiteException exception){
                        Log.e(TAG, "OnClick()", exception);
                    } catch(IOException exception){
                        Log.e(TAG, "OnClick()", exception);
                    } catch(Exception exception){
                        Log.e(TAG, "OnClick()", exception);
                    }
                }

                break;
            default:
                break;
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

                String download_link = f_url[0];

                if(download_link == null || download_link.trim().length() <= 0){
                    return null;
                }

                String req_url = download_link;

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
                File file = new File(fullPath, download_link);
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

    @Override
    public void changed(Replication.ChangeEvent event) {

    }
}