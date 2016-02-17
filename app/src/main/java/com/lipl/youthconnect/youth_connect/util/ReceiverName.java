package com.lipl.youthconnect.youth_connect.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.replicator.Replication;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReceiverName extends BroadcastReceiver implements Replication.ChangeListener {
    private static final String TAG = "ReceiverName";
    public ReceiverName() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        if (cm == null)
            return;
        if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected()){

            int login_status = context.getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 1)
                    .getInt(Constants.SP_LOGIN_STATUS, 0);
            if(login_status == 1){
                try {
                    DatabaseUtil.startReplications(context, this, TAG);
                } catch(CouchbaseLiteException exception){
                    Log.e(TAG, "onReceive()", exception);
                } catch (IOException exception){
                    Log.e(TAG, "onReceive()", exception);
                } catch(Exception exception){
                    Log.e(TAG, "onReceive()", exception);
                }
            }

            return;
        }
    }

    @Override
    public void changed(Replication.ChangeEvent event) {

    }
}