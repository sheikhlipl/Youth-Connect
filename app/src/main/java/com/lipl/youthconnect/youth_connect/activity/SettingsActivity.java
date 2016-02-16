package com.lipl.youthconnect.youth_connect.activity;

import android.content.DialogInterface;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lipl.youthconnect.youth_connect.R;
import com.lipl.youthconnect.youth_connect.util.ActivityIndicator;
import com.lipl.youthconnect.youth_connect.util.Constants;
import com.lipl.youthconnect.youth_connect.util.PasswordValidator;
import com.lipl.youthconnect.youth_connect.util.Util;
import com.lipl.youthconnect.youth_connect.pojo.User;

import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends ActionBarActivity implements View.OnClickListener {

    private static Toolbar mToolbar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle("Settings");

        Button btnResetPassword = (Button) findViewById(R.id.btnResetPassword);
        btnResetPassword.setOnClickListener(this);

        int user_id = getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 0).getInt(Constants.SP_USER_ID, 0);
        if(user_id > 0){
            User user = new User(Parcel.obtain());

            if(user != null){
                String fullname = user.getFull_name();
                String mob_no = user.getMobile_no();
                String email_id = user.getEmail_id();

                TextView tvProfileValue = (TextView) findViewById(R.id.tvProfileValue);
                tvProfileValue.setText(fullname);

                TextView tvOrgValue = (TextView) findViewById(R.id.tvNameValue);
                tvOrgValue.setText(mob_no);

                TextView tvNumberValue = (TextView) findViewById(R.id.tvNumberValue);
                tvNumberValue.setText(email_id);
            }
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.btnResetPassword:

                EditText etPrvPassword = (EditText) findViewById(R.id.etPrvPassword);
                EditText etCrntPassword = (EditText) findViewById(R.id.etCrntPassword);
                EditText etCrntConfirmPassword = (EditText) findViewById(R.id.etCrntConfirmPassword);

                String privousPassword = etPrvPassword.getText().toString().trim();
                String currentPassword = etCrntPassword.getText().toString().trim();
                String confirmPassword = etCrntConfirmPassword.getText().toString().trim();

                if(privousPassword == null || privousPassword.length() <= 0){
                    etPrvPassword.setError("Enter your previous password.");
                    return;
                }

                if(currentPassword == null || currentPassword.length() <= 0){
                    etCrntPassword.setError("Enter new password.");
                    return;
                }

                if(confirmPassword == null || confirmPassword.length() <= 0){
                    etCrntConfirmPassword.setError("Enter new password again.");
                    return;
                }

                if(confirmPassword.equalsIgnoreCase(currentPassword) == false){
                    etCrntConfirmPassword.setError("Password mismatch.");
                    return;
                }

                if(privousPassword.equalsIgnoreCase(currentPassword) == true){
                    etCrntPassword.setError("Enter different password. Current password can not be equal to previous one.");
                    return;
                }

                PasswordValidator passwordValidator = new PasswordValidator();
                boolean isValidate = passwordValidator.validate(currentPassword);

                if(isValidate == false){
                    etCrntPassword.setError(getResources().getString(R.string.password_validation_msg));
                    return;
                }

                if(Util.getNetworkConnectivityStatus(SettingsActivity.this)){
                    ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
                    ChangePasswordAsync changePasswordAsync = new ChangePasswordAsync(progressBar);
                    changePasswordAsync.execute(privousPassword, currentPassword);
                } else{
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.coordinatorLayout), "No internet connection." , Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
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

    private class ChangePasswordAsync extends AsyncTask<String, Integer, Void> {

        String TAG = "ChangePasswordAsync";
        int status = 0;
        String message = "";
        //private ProgressBar progressBar = null;
        private ActivityIndicator activityIndicator = ActivityIndicator.ctor(SettingsActivity.this);

        public ChangePasswordAsync(ProgressBar progressBar){
            //this.progressBar = progressBar;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /*if(progressBar != null && isFinishing() == false){
                progressBar.setVisibility(View.VISIBLE);
            }*/
            if(activityIndicator == null){
                activityIndicator = new ActivityIndicator(SettingsActivity.this);
            }
            activityIndicator.show();
        }

        @Override
        protected Void doInBackground(String... params) {

            String apikey = getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 1).getString(Constants.SP_USER_API_KEY, null);
            if(apikey == null)
                return null;

            InputStream in = null;
            int resCode = -1;

            String oldPass = params[0];
            String newPass = params[1];

            try{
                String link = Constants.BASE_URL+Constants.REQUEST_URL_CHANGE_PASSWORD;
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
                conn.setRequestProperty("Authorization", apikey);

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("oldPwd", oldPass)
                        .appendQueryParameter("newPwd", newPass);
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

				/*
				 * {"Response":{"submitStatus":"Failed","Message":"Sorry Old Password mismatch.","apiKey":""}}
				 * */

                if(Util.isJSONValid(response) == false){
                    message = "Server side error occur.";
                    return null;
                }

                JSONObject jsonObject = new JSONObject(response);

                JSONObject obj = jsonObject.getJSONObject("Response");

                String sts = obj.getString("submitStatus");
                message = obj.getString("Message");
                if(sts.equalsIgnoreCase("Success"))
                    status = 1;
                else
                    status = 0;

                if(status == 1){
                    String api_key = obj.getString("apiKey");
                    getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 2).edit().putString(Constants.SP_USER_API_KEY, api_key).commit();
                } else{
                    if(message.equalsIgnoreCase("Api key does not exit")){
                        message = "Please logout and login again to get your app work.";
                    }
                }

            } catch(ConnectTimeoutException e){
                message = "Connection time out.\nPlease reset internet connection.";
            } catch(Exception e){
                Log.e(TAG, "Exception", e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            /*if(progressBar != null && isFinishing() == false){
                progressBar.setVisibility(View.GONE);
            }*/
            if(activityIndicator == null){
                activityIndicator = new ActivityIndicator(SettingsActivity.this);
            }
            activityIndicator.dismiss();

            if(status == 1){
                if(message.length() <= 0)
                    message = "Password has been changed successfully.";
            } else{
                if(message.length() <= 0)
                    message = "Sorry Password could not be changed. Please try again.";
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this, R.style.AppCompatAlertDialogStyle);
            builder.setTitle("Change Password");
            builder.setMessage(message);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (status == 1) {
                    finish();
                }
                }
            });
            builder.show();
        }
    }
}