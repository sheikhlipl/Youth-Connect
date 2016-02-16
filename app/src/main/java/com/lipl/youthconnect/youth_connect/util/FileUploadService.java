package com.lipl.youthconnect.youth_connect.util;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcel;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;

import com.lipl.youthconnect.youth_connect.R;
import com.lipl.youthconnect.youth_connect.activity.MainActivity;
import com.lipl.youthconnect.youth_connect.pojo.Document;
import com.lipl.youthconnect.youth_connect.pojo.FileChooseDetaiuls;
import com.lipl.youthconnect.youth_connect.pojo.PendingFileToUpload;
import com.lipl.youthconnect.youth_connect.pojo.User;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONArray;
import org.json.JSONException;
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
import java.util.Date;
import java.util.List;

/**
 * Created by user on 31-01-2016.
 */
public class FileUploadService extends Service {
    private static final String TAG = "BroadcastService";
    public static final String BROADCAST_ACTION = "com.websmithing.broadcasttest.displayevent";
    private final Handler handler = new Handler();
    Intent intent;
    int counter = 0;

    private PendingFileToUpload pendingFileToUpload = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        intent = new Intent(BROADCAST_ACTION);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        handler.removeCallbacks(sendUpdatesToUI);
        handler.postDelayed(sendUpdatesToUI, 1000); // 1 second

        if(intent != null && intent.getExtras() != null){
            pendingFileToUpload = intent.getExtras().getParcelable("FileUpload");
        }
    }

    private Runnable sendUpdatesToUI = new Runnable() {
        public void run() {
        if(Util.getNetworkConnectivityStatus(FileUploadService.this)) {
            BackGroundTask backGroundTask = new BackGroundTask(this);
            backGroundTask.execute();
        }
        }
    };

    private void DisplayLoggingInfo(PendingFileToUpload pendingFileToUpload, int status) {

        pendingFileToUpload.setIs_uploaded(status);

        intent.putExtra("time", new Date().toLocaleString());
        intent.putExtra("counter", String.valueOf(++counter));
        intent.putExtra("pendingFileToUpload", pendingFileToUpload);
        intent.putExtra("status", status);
        sendBroadcast(intent);
    }

    private class BackGroundTask extends AsyncTask<Void, Void, Void> {

        private Runnable runnable;

        public BackGroundTask(Runnable runnable){
            this.runnable = runnable;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            String api_key = getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 1).getString(Constants.SP_USER_API_KEY, null);
            int user_type_id = getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 1).getInt(Constants.SP_USER_TYPE, 0);
            int user_id = getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 1).getInt(Constants.SP_USER_ID, 0);
            String desg_id = getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 1).getString(Constants.SP_USER_DESG_ID, null);

            /*DBHelper dbHelper = new DBHelper(FileUploadService.this);
            List<PendingFileToUpload> pendingFileToUploads = dbHelper.getPendingFileToUpload(user_id);
            dbHelper.close();*/

            PendingFileToUpload pendingFileToUpload = FileUploadService.this.pendingFileToUpload;

            //if(pendingFileToUploads != null && pendingFileToUploads.size() > 0){
                //for(int i = 0; i < pendingFileToUploads.size(); i++){

            if(pendingFileToUpload != null){

                String assignData = pendingFileToUpload.getAssignData();
                String title = pendingFileToUpload.getTitle();
                String purpose = pendingFileToUpload.getPurpose();
                String doc_id = pendingFileToUpload.getDoc_id()+"";
                String charset = "UTF-8";
                String requestURL = Constants.BASE_URL+Constants.REQUEST_URL_DOC_UPLOAD;

                File file = null;
                if(pendingFileToUpload != null && pendingFileToUpload.getFilePath() != null){
                    file = new File(pendingFileToUpload.getFilePath());
                }

                if(api_key != null){

                    try {
                        MultipartUtility multipart = new MultipartUtility(requestURL, charset, api_key);

                        multipart.addFormField("data[DocumentMaster][user_id]", user_id + "");
                        multipart.addFormField("response", "mobile");
                        multipart.addFormField("data[DocumentMaster][m_desg_id]", desg_id);
                        multipart.addFormField("data[DocumentMaster][m_user_type_id]", user_type_id + "");
                        multipart.addFormField("data[DocumentMaster][document_title]", title);
                        multipart.addFormField("data[DocumentMaster][document_purpose]", purpose);
                        multipart.addFormField("data[DocumentAssign]", assignData);
                        multipart.addFormField("data[DocumentUpload][document_master_id] ", doc_id);
                        multipart.addFilePart("data[DocumentUpload][upload_file]", file);



                        List<String> response = multipart.finish();

                        System.out.println("SERVER REPLIED:");
                        String res = "";
                        for (String line : response) {
                            res = res + line + "\n";
                        }
                        Log.i(TAG, res);

		            /*
						{
							"Response":[
								{
									"Response":
										{
											"submitStatus":"Success",
											"Message":"No snap found"
										}
								}
								]
						}

		             */


                        if(res != null && res.trim().length() > 0){

                            int status = 0;
                            JSONObject jsonObject = new JSONObject(res.trim());
                            if (jsonObject != null && jsonObject.isNull("status") == false) {
                                status = jsonObject.getInt("status");
                            }

                            if(status == 1){
                                DisplayLoggingInfo(pendingFileToUpload, 1);
                            } else{
                                DisplayLoggingInfo(pendingFileToUpload, 0);
                            }
                        }
                    }catch(SocketTimeoutException exception){
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


                   /* try {
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
                                .appendQueryParameter("data[DocumentUpload][document_master_id] ", doc_id)
                                .appendQueryParameter("data[DocumentUpload][upload_file]", upload_file);
                                //.appendQueryParameter("data[DocumentUpload]", jsonData);

                        Log.i("FileChooserMultiple", "assigndata" + assignData + " \n jsondata" + jsonData);

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

                        if(in != null){

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
                             * *

                            if(response != null && response.length() > 0 && response.charAt(0) == '{'){
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject != null && jsonObject.isNull("Apikey") == false) {
                                    String changePasswordDoneFromWebMsg = jsonObject.optString("Apikey");
                                    if(changePasswordDoneFromWebMsg.equalsIgnoreCase("Api key does not exit")){
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
                                    DisplayLoggingInfo(pendingFileToUpload, 1);
                                    //handler.postDelayed(runnable, 5000); // 5 seconds
                                } else {
                                    DisplayLoggingInfo(pendingFileToUpload, 0);
                                    //handler.postDelayed(runnable, 5000); // 5 seconds
                                }
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
                    }*/

                }
            }
            return null;
        }
    }
}
