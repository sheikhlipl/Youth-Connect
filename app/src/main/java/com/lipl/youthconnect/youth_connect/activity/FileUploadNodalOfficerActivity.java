package com.lipl.youthconnect.youth_connect.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.lipl.youthconnect.youth_connect.R;
import com.lipl.youthconnect.youth_connect.util.ActivityIndicator;
import com.lipl.youthconnect.youth_connect.util.Constants;
import com.lipl.youthconnect.youth_connect.util.Util;
import com.lipl.youthconnect.youth_connect.adapter.NodalOfficerListViewAdapter;
import com.lipl.youthconnect.youth_connect.pojo.Document;
import com.lipl.youthconnect.youth_connect.pojo.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FileUploadNodalOfficerActivity extends ActionBarActivity {

    private static Toolbar mToolbar = null;
    private TextView tvEmptyView;
    //private RecyclerView listView;
    private ListView listView;
    private NodalOfficerListViewAdapter mAdapter;

    private List<User> nodalOfficers;
    public static List<User> selectedNodalOfficers;

    private SearchView search;
    protected Handler handler;

    private Document document = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_upload_nodal_officer);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle("Select Nodal Officers");

        search = (SearchView) findViewById(R.id.searchView1);
        search.setQueryHint("SearchView");

        //*** setOnQueryTextFocusChangeListener ***
        search.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                
            }
        });

        //*** setOnQueryTextListener ***
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                setNodalOfficersList(newText);
                return false;
            }
        });

        tvEmptyView = (TextView) findViewById(R.id.tvNoRecordFoundText);
        listView = (ListView) findViewById(R.id.distList);
        if(nodalOfficers == null) {
            nodalOfficers = new ArrayList<User>();
        }
        handler = new Handler();

        // create an Object for Adapter
        mAdapter = new NodalOfficerListViewAdapter(nodalOfficers, FileUploadNodalOfficerActivity.this);

        // set the adapter object to the Recyclerview
        listView.setAdapter(mAdapter);
        //  mAdapter.notifyDataSetChanged();

        if (nodalOfficers.isEmpty()) {
            listView.setVisibility(View.GONE);
            tvEmptyView.setVisibility(View.VISIBLE);
        } else {
            listView.setVisibility(View.VISIBLE);
            tvEmptyView.setVisibility(View.GONE);
        }

        mAdapter.notifyDataSetChanged();
        selectedNodalOfficers = new ArrayList<User>();

        if(getIntent().getExtras() != null){
            document = getIntent().getExtras().getParcelable(Constants.INTENT_KEY_DOCUMENT);
        }

    }

    private void setNodalOfficersList(String filterText){

        final List<User> nodalOfficerList = new ArrayList<User>();
        if(filterText != null && filterText.trim().length() > 0){
            for(int i = 0; i < nodalOfficers.size(); i++){
                String m_district = nodalOfficers.get(i).getFull_name();
                if(m_district.contains(filterText)){
                    nodalOfficerList.add(nodalOfficers.get(i));
                }
            }
        } else{
            nodalOfficerList.addAll(nodalOfficers);
        }

        // create an Object for Adapter
        mAdapter = new NodalOfficerListViewAdapter(nodalOfficerList, FileUploadNodalOfficerActivity.this);

        // set the adapter object to the Recyclerview
        listView.setAdapter(mAdapter);
        //  mAdapter.notifyDataSetChanged();


        if (nodalOfficerList.isEmpty()) {
            listView.setVisibility(View.GONE);
            tvEmptyView.setVisibility(View.VISIBLE);
        } else {
            listView.setVisibility(View.VISIBLE);
            tvEmptyView.setVisibility(View.GONE);
        }

        mAdapter.notifyDataSetChanged();
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
        getMenuInflater().inflate(R.menu.menu_actionbar_nodal_officer_list, menu);
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
        } else if(id == android.R.id.home) {
            onBackPressed();
        } else if(id == R.id.action_send) {

            sendDocumnetToNodalOfficers();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void sendDocumnetToNodalOfficers(){
        if(Util.getNetworkConnectivityStatus(FileUploadNodalOfficerActivity.this)) {
            SendToNodalOfficerAsyncTask sendToNodalOfficerAsyncTask = new SendToNodalOfficerAsyncTask();
            sendToNodalOfficerAsyncTask.execute();
        } else{
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

    private String getJsonDataForAssignUser() {
        // Format : [{"m_district_id":"2","user_id":"4"}]
        try {
            JSONArray array = new JSONArray();
            for(int i = 0; i < selectedNodalOfficers.size(); i++){
                String district_id = selectedNodalOfficers.get(i).getM_district_id();
                String user_id = selectedNodalOfficers.get(i).getUser_id()+"";

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("m_district_id", district_id);
                jsonObject.put("user_id", user_id);
                array.put(jsonObject);
            }
            return array.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getJsonDataForDistrictList() {
        // Format : [2,3,4]
        Set<Integer> districtSet = new HashSet<Integer>();
        for(int i = 0; i < selectedNodalOfficers.size(); i++){
            String districtId = selectedNodalOfficers.get(i).getM_district_id();
            if(districtId != null && districtId.length() > 0
                    && TextUtils.isDigitsOnly(districtId)) {
                int districtID = Integer.parseInt(districtId);
                districtSet.add(districtID);
            }
        }

        try {
            JSONArray array = new JSONArray();
            for (Integer district_id : districtSet) {
                array.put(district_id);
            }
            return array.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getJsonDataForDocIds() {
        // Format : [2,3,4]

        if(document == null){
            return null;
        }

        if(document.getDoc_master_id() <= 0 && document.getDocumentMaster() != null){
            String docIds = document.getDocumentMaster().getDocument_master_id()+"";
            return docIds;
        } else {
            String docIds = document.getDoc_master_id() + "";
            return docIds;
        }
    }

    /**
     * Async task to get sync camp table from server
     * */
    private class SendToNodalOfficerAsyncTask extends AsyncTask<String, Void, Boolean> {

        private static final String TAG = "SendToNodalOfficerATask";
        //private ProgressDialog progressDialog = null;
        private boolean isChangePassword = false;
        private String message;
        private ActivityIndicator activityIndicator = ActivityIndicator.ctor(FileUploadNodalOfficerActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /*if(progressDialog == null) {
                progressDialog = ProgressDialog.show(FileUploadNodalOfficerActivity.this, "Sending", "Please wait...");
            }*/
            if(activityIndicator == null){
                activityIndicator = new ActivityIndicator(FileUploadNodalOfficerActivity.this);
            }
            activityIndicator.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {

            String api_key = getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 1).getString(Constants.SP_USER_API_KEY, null);

            if(api_key == null){
                return null;
            }

            if(getJsonDataForDocIds() == null || getJsonDataForDocIds().length() <= 0 ||
                getJsonDataForDistrictList() == null || getJsonDataForDistrictList().length() <= 0 ||
                    getJsonDataForAssignUser() == null || getJsonDataForAssignUser().length() <= 0) {
                return null;
            }

            try {

                String user_id = getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 0).getInt(Constants.SP_USER_ID, 0)+"";
                String desg_id = getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 0).getString(Constants.SP_USER_DESG_ID, null);
                String user_type_id = getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 0).getInt(Constants.SP_USER_TYPE, 0)+"";

                InputStream in = null;
                int resCode = -1;

                String link = Constants.BASE_URL+Constants.REQUEST_URL_ADMIN_SEND_TO_NODAL_OFFICER;
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

                String docIds = getJsonDataForDocIds();
                String districtIds = getJsonDataForDistrictList();
                String users = getJsonDataForAssignUser();


                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("data[DocumentMaster][user_id]", user_id)
                        .appendQueryParameter("data[DocumentMaster][m_desg_id]", desg_id)
                        .appendQueryParameter("data[DocumentMaster][m_user_type_id]", user_type_id)
                        .appendQueryParameter("data[DocumentMaster][docIds]", getJsonDataForDocIds())
                        .appendQueryParameter("response", "mobile")
                        .appendQueryParameter("data[DocumentMaster][m_district_id]", getJsonDataForDistrictList())
                        .appendQueryParameter("data[DocumentAssign]", getJsonDataForAssignUser());

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
                activityIndicator = new ActivityIndicator(FileUploadNodalOfficerActivity.this);
            }
            activityIndicator.dismiss();

            if(isChangePassword){
                AlertDialog.Builder builder = new AlertDialog.Builder(FileUploadNodalOfficerActivity.this, R.style.AppCompatAlertDialogStyle);
                builder.setTitle(getResources().getString(R.string.password_changed_title));
                builder.setMessage(getResources().getString(R.string.password_changed_description));
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent(FileUploadNodalOfficerActivity.this, MainActivity.class);
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
            AlertDialog.Builder builder = new AlertDialog.Builder(FileUploadNodalOfficerActivity.this, R.style.AppCompatAlertDialogStyle);
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
}