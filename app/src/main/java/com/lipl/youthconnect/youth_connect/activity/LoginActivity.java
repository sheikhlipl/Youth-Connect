package com.lipl.youthconnect.youth_connect.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lipl.youthconnect.youth_connect.LogExActivity;
import com.lipl.youthconnect.youth_connect.R;
import com.lipl.youthconnect.youth_connect.pojo.Block;
import com.lipl.youthconnect.youth_connect.pojo.Desg;
import com.lipl.youthconnect.youth_connect.pojo.District;
import com.lipl.youthconnect.youth_connect.pojo.Organization;
import com.lipl.youthconnect.youth_connect.pojo.State;
import com.lipl.youthconnect.youth_connect.pojo.User;
import com.lipl.youthconnect.youth_connect.pojo.UserType;
import com.lipl.youthconnect.youth_connect.util.Constants;
import com.lipl.youthconnect.youth_connect.util.Util;
import com.lipl.youthconnect.youth_connect.util.YouthConnectSingleTone;
import com.rengwuxian.materialedittext.MaterialEditText;

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
import java.util.List;

/**
 * Created by luminousinfoways on 08/12/15.
 */
public class LoginActivity extends Activity implements View.OnClickListener {

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);

        Button btnReset = (Button) findViewById(R.id.btnReset);
        btnReset.setOnClickListener(this);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        MaterialEditText etEmail = (MaterialEditText) findViewById(R.id.email);
        String email = etEmail.getText().toString().trim();

        MaterialEditText etPassword = (MaterialEditText) findViewById(R.id.password);
        String password = etPassword.getText().toString().trim();

        switch (id){
            case R.id. btnLogin:

                if(email == null || email.length() <= 0){
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.layoutParent), "Enter your userid.", Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                    View snackbarView = snackbar.getView();
                    TextView tv = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                    tv.setTextColor(Color.WHITE);
                    TextView tvAction = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_action);
                    tvAction.setTextColor(Color.CYAN);
                    //snackbar.show();

                    if(etEmail != null){
                        etEmail.setError("Enter your userid.");
                    }

                    return;
                }

                if(password == null || password.length() <= 0){
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.layoutParent), "Enter your password.", Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                    View snackbarView = snackbar.getView();
                    TextView tv = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                    tv.setTextColor(Color.WHITE);
                    TextView tvAction = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_action);
                    tvAction.setTextColor(Color.CYAN);
                    //snackbar.show();

                    if(etPassword != null){
                        etPassword.setError("Enter your password.");
                    }

                    return;
                }

                if(Util.getNetworkConnectivityStatus(LoginActivity.this)) {
                    LoginAsync loginAsync = new LoginAsync();
                    loginAsync.execute(email, password);
                } else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
                    builder.setTitle(getResources().getString(R.string.no_internet_connection_title));
                    builder.setMessage(getResources().getString(R.string.no_internet_connection_message));
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    });
                    builder.show();
                }

                break;
            case R.id.btnReset:

                if(etEmail != null){
                    etEmail.setText("");
                }

                if(etPassword != null){
                    etPassword.setText("");
                }

                break;
            default:
                break;
        }
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

    private void onPreExecuteTask(){
        if(isFinishing() == false) {
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);
            Button btnLogin = (Button) findViewById(R.id.btnLogin);
            btnLogin.setClickable(false);
            btnLogin.setEnabled(false);
            Button btnReset = (Button) findViewById(R.id.btnReset);
            btnReset.setClickable(false);
            btnReset.setEnabled(false);
            MaterialEditText email = (MaterialEditText) findViewById(R.id.email);
            email.setEnabled(false);
            email.setClickable(false);
            MaterialEditText password = (MaterialEditText) findViewById(R.id.password);
            password.setEnabled(false);
            password.setClickable(false);
        }
    }

    /**
     * Async task to get sync camp table from server
     * */
    private class LoginAsync extends AsyncTask<String, Void, User> {

        private static final String TAG = "LoginAsync";
        private String message = "";
        //private ProgressDialog progressDialog = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /*if(progressDialog == null) {
                progressDialog = ProgressDialog.show(LoginActivity.this, "Loading", "Please wait...");
            }*/
            onPreExecuteTask();
        }

        @Override
        protected User doInBackground(String... params) {

            try {

                String _username = params[0];
                String _password = params[1];

                InputStream in = null;
                int resCode = -1;

                String link = Constants.BASE_URL+Constants.LOGIN_REQUEST_URL;
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

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("username", _username)
                        .appendQueryParameter("password", _password);
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

                Log.i(TAG, "Response : "+response);

                /**
                 * {
                 "User": {
                 "user_id": "2",
                 "full_name": "Angul Nodal1",
                 "username": "angulnodal1",
                 "password": "5f4dcc3b5aa765d61d8327deb882cf99",
                 "m_user_type_id": "2",
                 "m_desg_id": "2",
                 "m_state_id": "1",
                 "m_district_id": "1",
                 "m_block_id": "0",
                 "m_organization_id": "1",
                 "mobile_no": "7877898988",
                 "email_id": "angulnodal1@gmail.com",
                 "api_key": "d03d1fc38b1722b0bf9bbb9475c9064e",
                 "is_active": "Y",
                 "created": "2015-12-12 00:00:00",
                 "modified": "2015-12-12 00:00:00"
                 },
                 "MUserType": {
                 "m_user_type_id": "2",
                 "m_user_type": "Nodal Officer"
                 },
                 "MDesg": {
                 "m_desg_id": "2",
                 "m_desg_nm": "Nodal Officer"
                 },
                 "MState": {
                 "m_state_id": "1",
                 "m_state": "Odisha"
                 },
                 "MDistrict": {
                 "m_district_id": "1",
                 "m_district": "Angul"
                 },
                 "MBlock": {
                 "m_block_id": null,
                 "m_block_nm": null
                 },
                 "MOrganization": {
                 "m_organization_id": "1",
                 "organization_name": "Siddhi Vinayak Science College, Similipada"
                 },
                 "login_status": 1,
                 "message": "Successfully Logged In"
                 }
                 * */

                if(response != null && response.length() > 0){

                    //DBHelper dbHelper = new DBHelper(LoginActivity.this);

                    JSONObject res = new JSONObject(response.trim());
                    int status = res.optInt("login_status");
                    message = res.optString("message");
                    if(status == 1){
                        JSONObject userObj = res.optJSONObject("User");
                        String user_id = userObj.optString("user_id");
                        String full_name = userObj.optString("full_name");
                        String username = userObj.optString("username");
                        String password = userObj.optString("password");
                        String api_key = userObj.optString("api_key");
                        String m_user_type_id = userObj.optString("m_user_type_id");
                        String m_desg_id = userObj.optString("m_desg_id");
                        String m_state_id = userObj.optString("m_state_id");
                        String m_district_id = userObj.optString("m_district_id");
                        String m_block_id = userObj.optString("m_block_id");
                        String m_organization_id = userObj.optString("m_organization_id");
                        String mobile_no = userObj.optString("mobile_no");
                        String email_id = userObj.optString("email_id");
                        String is_active = userObj.optString("is_active");
                        String created = userObj.optString("created");
                        String modified = userObj.optString("modified");

                        User user = null;
                        if(user_id != null && TextUtils.isDigitsOnly(user_id)) {
                            user = new User(Parcel.obtain());
                            user.setUser_id(Integer.parseInt(user_id));
                            user.setFull_name(full_name);
                            user.setApi_key(api_key);
                            user.setUsername(username);
                            user.setPassword(password);
                            user.setM_user_type_id(m_user_type_id);
                            user.setM_desg_id(m_desg_id);
                            user.setM_state_id(m_state_id);
                            user.setM_district_id(m_district_id);
                            user.setM_block_id(m_block_id);
                            user.setM_organization_id(m_organization_id);
                            user.setMobile_no(mobile_no);
                            user.setEmail_id(email_id);
                            user.setIs_active(is_active);
                            user.setCreated(created);
                            user.setModified(modified);
                            //dbHelper.insertUser(user);
                        }

                        JSONObject userTypeObj = res.optJSONObject("MUserType");
                        String user_type_id = userTypeObj.optString("m_user_type_id");
                        String m_user_type = userTypeObj.optString("m_user_type");

                        if(user_type_id != null && TextUtils.isDigitsOnly(user_type_id)) {
                            UserType userType = new UserType(Parcel.obtain());
                            userType.setM_user_type_id(Integer.parseInt(user_type_id));
                            userType.setM_user_type(m_user_type);
                            //dbHelper.insertUserType(userType);
                        }

                        JSONObject dsegObj = res.optJSONObject("MDesg");
                        String desg_id = dsegObj.optString("m_desg_id");
                        String m_desg_nm = dsegObj.optString("m_desg_nm");

                        if(desg_id != null && TextUtils.isDigitsOnly(desg_id)) {
                            Desg _desg = new Desg(Parcel.obtain());
                            _desg.setM_desg_id(Integer.parseInt(desg_id));
                            _desg.setM_desg_nm(m_desg_nm);
                            //dbHelper.insertDesignation(_desg);
                        }

                        JSONObject stateObj = res.optJSONObject("MState");
                        String state_id = stateObj.optString("m_state_id");
                        String m_state = stateObj.optString("m_state");

                        if(state_id != null && TextUtils.isDigitsOnly(state_id)) {
                            State state1 = new State(Parcel.obtain());
                            state1.setM_state_id(Integer.parseInt(state_id));
                            state1.setM_state(m_state);
                            //dbHelper.insertState(state1);
                        }

                        JSONObject districtObj = res.optJSONObject("MDistrict");
                        String district_id = districtObj.optString("m_district_id");
                        String m_district = districtObj.optString("m_district");

                        if(district_id != null && TextUtils.isDigitsOnly(district_id)) {
                            District district1 = new District(Parcel.obtain());
                            district1.setM_district_id(Integer.parseInt(district_id));
                            district1.setM_district(m_district);
                            //dbHelper.insetDistrict(district1);
                        }

                        JSONObject blockObj = res.optJSONObject("MBlock");
                        String block_id = blockObj.optString("m_block_id");
                        String m_block_nm = blockObj.optString("m_block_nm");

                        if(block_id != null && TextUtils.isDigitsOnly(block_id)) {
                            Block block = new Block(Parcel.obtain());
                            block.setM_block_id(Integer.parseInt(block_id));
                            block.setM_block_nm(m_block_nm);
                            //dbHelper.insertBlock(block);
                        }

                        JSONObject orgObj = res.optJSONObject("MOrganization");
                        String organization_id = orgObj.optString("m_organization_id");
                        String organization_name = orgObj.optString("organization_name");

                        if(organization_id != null && TextUtils.isDigitsOnly(organization_id)) {
                            Organization organization = new Organization(Parcel.obtain());
                            organization.setM_organization_id(Integer.parseInt(organization_id));
                            organization.setOrganization_name(organization_name);
                            //dbHelper.insertOrganisation(organization);
                        }
                        //dbHelper.close();
                        return user;
                    }
                }
            } catch(SocketTimeoutException exception){
                Log.e(TAG, "LoginAsync : doInBackground", exception);
            } catch(ConnectException exception){
                Log.e(TAG, "LoginAsync : doInBackground", exception);
            } catch(MalformedURLException exception){
                Log.e(TAG, "LoginAsync : doInBackground", exception);
            } catch (IOException exception){
                Log.e(TAG, "LoginAsync : doInBackground", exception);
            } catch(Exception exception){
                Log.e(TAG, "LoginAsync : doInBackground", exception);
            }

            return null;
        }

        @Override
        protected void onPostExecute(final User user) {
            super.onPostExecute(user);

            onPostExecuteTask();

            if(user != null) {
                getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 2).edit().putInt(Constants.SP_LOGIN_STATUS, 1).commit();
                getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 2).edit().putInt(Constants.SP_USER_ID, user.getUser_id()).commit();
                getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 2).edit().putString(Constants.SP_USER_API_KEY, user.getApi_key()).commit();
                getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 2).edit().putString(Constants.SP_USER_DESG_ID, user.getM_desg_id()).commit();
                getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 2).edit().putString(Constants.SP_USER_NAME, user.getFull_name()).commit();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        int userType = Integer.parseInt(user.getM_user_type_id());
                        if (userType == 2) {
                            getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 2).edit().putInt(Constants.SP_USER_TYPE, 2).commit();
                        } else if (userType == 1) {
                            getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 2).edit().putInt(Constants.SP_USER_TYPE, 1).commit();
                        }

                        //getNotification();
                        syncDistrictList();

                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }
                }, 0);
            } else{

                String _snack_bar_msg = "";
                if(message != null && message.length() <= 0){
                    _snack_bar_msg = "No internet connection.";
                } else {
                    _snack_bar_msg = "Invalid username or password.";
                }

                Snackbar snackbar = Snackbar.make(findViewById(R.id.layoutParent), _snack_bar_msg , Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
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

    private void onPostExecuteTask(){
        if(isFinishing() == false) {
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
            progressBar.setVisibility(View.GONE);
            Button btnLogin = (Button) findViewById(R.id.btnLogin);
            btnLogin.setClickable(true);
            btnLogin.setEnabled(true);
            Button btnReset = (Button) findViewById(R.id.btnReset);
            btnReset.setClickable(true);
            btnReset.setEnabled(true);
            MaterialEditText email = (MaterialEditText) findViewById(R.id.email);
            email.setEnabled(true);
            email.setClickable(true);
            MaterialEditText password = (MaterialEditText) findViewById(R.id.password);
            password.setEnabled(true);
            password.setClickable(true);
        }
    }

    /**
     * Execute asynctask to sync camp table
     * */
    private void syncDistrictList(){
        if(Util.getNetworkConnectivityStatus(LoginActivity.this)) {
            SyncDetails asyncTask = new SyncDetails();
            asyncTask.execute();
        }
    }

    /**
     * Async task to get sync camp table from server
     * */
    private class SyncDetails extends AsyncTask<String, Void, Void> {

        private static final String TAG = "SyncDetails";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            onPreExecuteTask();
        }

        @Override
        protected Void doInBackground(String... params) {

            try {

                InputStream in = null;
                int resCode = -1;

                String link = Constants.BASE_URL+Constants.REQUEST_URL_DIST_LIST;
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

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("modified_date", "");
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

                Log.i(TAG, "Response : "+response);

                /**
                 * {
                 "DISTRICT": {
                 "1": "Angul",
                 "2": "Boudh",
                 "3": "Bhadrak",
                 "4": "Balangir",
                 "5": "Bargarh",
                 "6": "Balasore",
                 "7": "Cuttack",
                 "8": "Debagarh",
                 "9": "Dhenkanal",
                 "10": "Ganjam",
                 "11": "Gajapati",
                 "12": "Jharsuguda",
                 "13": "Jajapur",
                 "14": "Jagatsinghapur",
                 "15": "Khordha",
                 "16": "Kendujhar",
                 "17": "Kalahandi",
                 "18": "Kandhamal",
                 "19": "Koraput",
                 "20": "Kendrapara",
                 "21": "Malkangiri",
                 "22": "Mayurbhanj",
                 "23": "Nabarangapur",
                 "24": "Nuapada",
                 "25": "Nayagarh",
                 "26": "Puri",
                 "27": "Rayagada",
                 "28": "Sambalpur",
                 "29": "Subarnapur",
                 "30": "Sundargarh"
                 },
                 "NODAL": [
                 {
                 "User": {
                 "user_id": "2",
                 "m_district_id": "1",
                 "full_name": "Angul Nodal1"
                 }
                 },
                 {
                 "User": {
                 "user_id": "3",
                 "m_district_id": "1",
                 "full_name": "Angul Nodal2"
                 }
                 }
                 ]
                 }
                 * */

                if(response != null && response.length() > 0){

                    JSONObject res = new JSONObject(response);
                    JSONArray district = res.optJSONArray("DISTRICT");

                    if(district == null){
                        return null;
                    }

                    List<District> districtList = new ArrayList<District>();

                    for(int i = 0; i < district.length(); i++){

                        JSONObject distObj = district.optJSONObject(i).optJSONObject("MDistrict");
                        String dist_id = distObj.optString("m_district_id");
                        String m_district = distObj.optString("m_district");
                        String modifiedDate = Util.getCurentDate();

                        if(dist_id != null && dist_id.trim().length() > 0 && TextUtils.isDigitsOnly(dist_id)) {
                            District district1 = new District(Parcel.obtain());
                            district1.setM_district(m_district);
                            district1.setM_district_id(Integer.parseInt(dist_id));
                            district1.setModifiedDate(modifiedDate);
                            districtList.add(district1);
                        }
                    }

                    JSONArray nodal = res.optJSONArray("NODAL");

                    if(nodal == null){
                        return null;
                    }

                    List<User> nodalUserList = new ArrayList<User>();

                    for(int i = 0; i < nodal.length(); i++){

                        JSONObject userObj = nodal.optJSONObject(i).optJSONObject("User");
                        String user_id = userObj.optString("user_id");
                        String m_district_id = userObj.optString("m_district_id");
                        String full_name = userObj.optString("full_name");

                        if(user_id != null && user_id.trim().length() > 0 && TextUtils.isDigitsOnly(user_id)) {
                            User user = new User(Parcel.obtain());
                            user.setUser_id(Integer.parseInt(user_id));
                            user.setM_district_id(m_district_id);
                            user.setFull_name(full_name);
                            user.setM_user_type_id("2");
                            nodalUserList.add(user);
                        }
                    }

                    if(nodalUserList != null && nodalUserList.size() > 0){
                        YouthConnectSingleTone.getInstance().nodalOfficerUsers.addAll(nodalUserList);
                    }
                    return null;
                }
            } catch(SocketTimeoutException exception){
                Log.e(TAG, "LoginAsync : doInBackground", exception);
            } catch(ConnectException exception){
                Log.e(TAG, "LoginAsync : doInBackground", exception);
            } catch(MalformedURLException exception){
                Log.e(TAG, "LoginAsync : doInBackground", exception);
            } catch (IOException exception){
                Log.e(TAG, "LoginAsync : doInBackground", exception);
            } catch(Exception exception){
                Log.e(TAG, "LoginAsync : doInBackground", exception);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void user) {
            super.onPostExecute(user);

            onPostExecuteTask();

            int loginStatus = getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 1).getInt(Constants.SP_LOGIN_STATUS, 0);
            if(loginStatus == 1){
                //getNotificationCount();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }
        }
    }
}