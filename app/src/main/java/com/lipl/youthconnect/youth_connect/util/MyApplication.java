package com.lipl.youthconnect.youth_connect.util;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.replicator.Replication;
import com.lipl.youthconnect.youth_connect.pojo.CurrentLocation;
import com.lipl.youthconnect.youth_connect.pojo.PendingFileToUpload;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by luminousinfoways on 14/10/15.
 */
public class MyApplication extends Application implements Replication.ChangeListener {

    private static MyApplication mInstance;
    private static final String TAG = "MyApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        int login_status = getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 1).getInt(Constants.SP_LOGIN_STATUS, 0);
        if(login_status == 1) {
            startService(new Intent(this, NotificationService.class));
            try {
                DatabaseUtil.startReplications(this, this, TAG);
            } catch(CouchbaseLiteException exception){
                Log.e(TAG, "onReceive()", exception);
            } catch (IOException exception){
                Log.e(TAG, "onReceive()", exception);
            } catch(Exception exception){
                Log.e(TAG, "onReceive()", exception);
            }
        }
    }

    @Override
    public void changed(Replication.ChangeEvent event) {
        Replication replication = event.getSource();
        com.couchbase.lite.util.Log.i(TAG, "Replication : " + replication + "changed.");
        if (!replication.isRunning()) {
            String msg = String.format("Replicator %s not running", replication);
            com.couchbase.lite.util.Log.i(TAG, msg);
        } else {
            int processed = replication.getCompletedChangesCount();
            int total = replication.getChangesCount();
            String msg = String.format("Replicator processed %d / %d", processed, total);
            com.couchbase.lite.util.Log.i(TAG, msg);
        }

        if (event.getError() != null) {
            showError("Sync error", event.getError());
        }

        Intent intent_answered = new Intent(Constants.BROADCAST_ACTION_REPLICATION_CHANGE);
        sendBroadcast(intent_answered);
    }

    public void showError(final String errorMessage, final Throwable throwable){

        String msg = String.format("%s: %s", errorMessage, throwable);
        com.couchbase.lite.util.Log.e(TAG, msg, throwable);
    }

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }
}