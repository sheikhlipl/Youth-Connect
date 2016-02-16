package com.lipl.youthconnect.youth_connect.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.lipl.youthconnect.youth_connect.LogExActivity;
import com.lipl.youthconnect.youth_connect.R;
import com.lipl.youthconnect.youth_connect.util.Constants;
import com.lipl.youthconnect.youth_connect.util.Util;
import com.newrelic.agent.android.NewRelic;

import io.fabric.sdk.android.Fabric;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_INTERVAL_TIME = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_splash);

        /*
           New Relic Code Added here don't modify
        */
        NewRelic.withApplicationToken(
                "AAd15196cb115e4343bd34c0675c6760a250a773ad"
        ).start(this.getApplication());

        TextView textview = (TextView) findViewById(R.id.tvBottomText);
        textview.setSelected(true);
        textview.setMovementMethod(new ScrollingMovementMethod());

        /**
         * 1. Check login status
         * 2. Check user is admin or nodal
         * 3. if user is admin then sync district with user details
         * 4. otherwise if user is nodal officer then just navigate to main screen
         * */
        int loginStatus = getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 1).getInt(Constants.SP_LOGIN_STATUS, 0);
        //if(loginStatus == 1){
        if(Util.getNetworkConnectivityStatus(SplashActivity.this)){
            //syncDistrictList();
            if (loginStatus == 1) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }, SPLASH_INTERVAL_TIME);
            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }, SPLASH_INTERVAL_TIME);
            }
        } else {
            if (loginStatus == 1) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }, SPLASH_INTERVAL_TIME);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this, R.style.AppCompatAlertDialogStyle);
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
        }
    }
}