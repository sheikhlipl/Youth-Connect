package com.lipl.youthconnect.youth_connect.util;

import android.app.Application;
import android.content.Intent;

import com.lipl.youthconnect.youth_connect.pojo.CurrentLocation;
import com.lipl.youthconnect.youth_connect.pojo.PendingFileToUpload;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by luminousinfoways on 14/10/15.
 */
public class MyApplication extends Application {

    private static MyApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        int login_status = getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 1).getInt(Constants.SP_LOGIN_STATUS, 0);
        if(login_status == 1) {
            startService(new Intent(this, NotificationService.class));
        }
    }

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    public interface UpdateLocationListener{
        public void updateLocation(CurrentLocation currentLocationList);
    }
}