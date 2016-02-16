package com.lipl.youthconnect.youth_connect.util;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcel;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.lipl.youthconnect.youth_connect.pojo.Document;
import com.lipl.youthconnect.youth_connect.pojo.PendingFileToUpload;
import com.lipl.youthconnect.youth_connect.pojo.User;

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
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by user on 31-01-2016.
 */
public class PendingFileUploadService extends Service {
    private static final String TAG = "PendingFileService";
    public static final String BROADCAST_ACTION = "com.websmithing.broadcasttest.displayevent";
    private final Handler handler = new Handler();
    Intent intent;
    int counter = 0;

    private ArrayList<PendingFileToUpload> pendingFileToUpload = null;

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

        if (intent != null && intent.getExtras() != null) {
            pendingFileToUpload = intent.getExtras().getParcelableArrayList("FileUpload");
        }
    }

    private Runnable sendUpdatesToUI = new Runnable() {
        public void run() {
            if(Util.getNetworkConnectivityStatus(PendingFileUploadService.this)) {
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

    private void replaceOldDocIdByNew(int oldDocID, int newDocId) {

        ArrayList<PendingFileToUpload> pList = new ArrayList<PendingFileToUpload>();

        for (PendingFileToUpload pd : pendingFileToUpload) {
            if (pd.getDoc_id() == oldDocID) {
                pd.setDoc_id(newDocId);
            }
        }

        pList.addAll(pendingFileToUpload);
        pendingFileToUpload.clear();
        pendingFileToUpload.addAll(pList);
    }

    private class BackGroundTask extends AsyncTask<Void, Void, Void> {

        private Runnable runnable;

        public BackGroundTask(Runnable runnable) {
            this.runnable = runnable;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            if(pendingFileToUpload == null){
                return null;
            }

            List<Integer> docIds = new ArrayList<Integer>();
            for (PendingFileToUpload pendingFileToUpload1 : pendingFileToUpload) {
                if(pendingFileToUpload1 != null) {
                    docIds.add(pendingFileToUpload1.getDoc_id());
                }
            }

            Set<Integer> uniquedocIds = new HashSet<Integer>(docIds);
            for (Integer uid : uniquedocIds) {
                for (int i = 0; pendingFileToUpload.size() > 0; i++) {
                    PendingFileToUpload pd = pendingFileToUpload.get(i);
                    if (pd != null) {
                        if (pd.getDoc_id() == uid) {
                            String title = pd.getTitle();
                            String purpose = pd.getPurpose();
                            String api_key = getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 1).getString(Constants.SP_USER_API_KEY, null);
                            int user_type_id = getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 1).getInt(Constants.SP_USER_TYPE, 0);
                            int user_id = getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 1).getInt(Constants.SP_USER_ID, 0);
                            String desg_id = getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 1).getString(Constants.SP_USER_DESG_ID, null);
                            String assignData = pd.getAssignData();
                            if (Util.getNetworkConnectivityStatus(PendingFileUploadService.this)) {
                                int dcid = getDocId(title, purpose, PendingFileUploadService.this, api_key,
                                        user_id, desg_id, user_type_id, assignData);
                                replaceOldDocIdByNew(uid, dcid);
                            } else {
                                return null;
                            }
                        }
                    }
                }
            }

            for (int g = 0; g < pendingFileToUpload.size(); g++) {

                PendingFileToUpload _pendingFileToUpload = pendingFileToUpload.get(g);
                if (Util.getNetworkConnectivityStatus(PendingFileUploadService.this)) {

                    String api_key = getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 1).getString(Constants.SP_USER_API_KEY, null);
                    int user_type_id = getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 1).getInt(Constants.SP_USER_TYPE, 0);
                    int user_id = getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 1).getInt(Constants.SP_USER_ID, 0);
                    String desg_id = getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 1).getString(Constants.SP_USER_DESG_ID, null);

                    if (_pendingFileToUpload != null) {

                        String assignData = _pendingFileToUpload.getAssignData();
                        String title = _pendingFileToUpload.getTitle();
                        String purpose = _pendingFileToUpload.getPurpose();
                        String doc_id = _pendingFileToUpload.getDoc_id() + "";
                        String charset = "UTF-8";
                        String requestURL = Constants.BASE_URL + Constants.REQUEST_URL_DOC_UPLOAD;

                        if (doc_id == null) {
                            doc_id = "0";
                            return null;
                        }

                        File file = null;
                        if (_pendingFileToUpload != null && _pendingFileToUpload.getFilePath() != null) {
                            file = new File(_pendingFileToUpload.getFilePath());
                        }

                        if (api_key != null) {

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


                                if (res != null && res.trim().length() > 0) {

                                    int status = 0;
                                    JSONObject jsonObject = new JSONObject(res.trim());
                                    if (jsonObject != null && jsonObject.isNull("status") == false) {
                                        status = jsonObject.getInt("status");
                                    }

                                    if (status == 1) {
                                        DisplayLoggingInfo(_pendingFileToUpload, 1);
                                    } else {
                                        DisplayLoggingInfo(_pendingFileToUpload, 0);
                                    }
                                }
                            } catch (SocketTimeoutException exception) {
                                Log.e(TAG, "GetFeedbackListAsync : doInBackground", exception);
                            } catch (ConnectException exception) {
                                Log.e(TAG, "GetFileListAsyncTask : doInBackground", exception);
                            } catch (MalformedURLException exception) {
                                Log.e(TAG, "LoginAsync : doInBackground", exception);
                            } catch (IOException exception) {
                                Log.e(TAG, "LoginAsync : doInBackground", exception);
                            } catch (Exception exception) {
                                Log.e(TAG, "LoginAsync : doInBackground", exception);
                            }
                        }
                    }
                }
            }

            return null;
        }

        private int getDocId(String title, String purpose, Context context, String api_key,
                             int user_id, String desg_id, int user_type_id, String assignData) {

            try {

                InputStream in = null;
                int resCode = -1;

                String link = Constants.BASE_URL + Constants.REQUEST_URL_DOC_CREATE;
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
                        .appendQueryParameter("data[DocumentMaster][user_id]", user_id + "")
                        .appendQueryParameter("response", "mobile")
                        .appendQueryParameter("data[DocumentMaster][m_desg_id]", desg_id)
                        .appendQueryParameter("data[DocumentMaster][m_user_type_id]", user_type_id + "")
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

                if (in == null) {
                    return 0;
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                String response = "", data = "";

                while ((data = reader.readLine()) != null) {
                    response += data + "\n";
                }

                Log.i(TAG, "Response : " + response);

                /**
                 * {
                 "doc_id": "275",
                 "status": 1
                 }
                 * */

                if (response != null && response.length() > 0 && response.charAt(0) == '{') {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject != null && jsonObject.isNull("Apikey") == false) {
                        String changePasswordDoneFromWebMsg = jsonObject.optString("Apikey");
                        if (changePasswordDoneFromWebMsg.equalsIgnoreCase("Api key does not exit")) {
                            return 0;
                        }
                    }
                }

                if (response != null && response.trim().length() > 0) {

                    String doc_id = "";
                    int status = 0;
                    JSONObject jsonObject = new JSONObject(response.trim());
                    if (jsonObject != null && jsonObject.isNull("doc_id") == false) {
                        doc_id = jsonObject.getString("doc_id");
                    }
                    if (jsonObject != null && jsonObject.isNull("status") == false) {
                        status = jsonObject.getInt("status");
                    }

                    if (status == 1) {

                        if (doc_id != null
                                && doc_id.trim().length() > 0
                                && TextUtils.isDigitsOnly(doc_id)) {

                            Document document = new Document(Parcel.obtain());
                            document.setDoc_master_id(Integer.parseInt(doc_id));

                            document.setUserFullName("");

                        }

                        return 0;
                    } else {
                        return 0;
                    }
                }
            } catch (Exception exception) {

            }
            return 0;
        }
    }
}
