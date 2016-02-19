package com.lipl.youthconnect.youth_connect.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;
import android.widget.Toast;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.replicator.Replication;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ReceiverName extends BroadcastReceiver implements Replication.ChangeListener {
    private static final String TAG = "ReceiverName";
    private Context mContex;
    public ReceiverName() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mContex = context;
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
        Replication replication = event.getSource();
        com.couchbase.lite.util.Log.i(TAG, "Replication : " + replication + "changed.");
        if (!replication.isRunning()) {
            String msg = String.format("Replicator %s not running", replication);
            com.couchbase.lite.util.Log.i(TAG, msg);
        } else {
            Set<String> pending_docIds = replication.getPendingDocumentIDs();
            List<String> doc_ids = replication.getDocIds();

            int processed = replication.getCompletedChangesCount();
            int total = replication.getChangesCount();
            String msg = String.format("Replicator processed %d / %d", processed, total);
            com.couchbase.lite.util.Log.i(TAG, msg);
        }

        if (event.getError() != null) {
            showError("Sync error", event.getError());
        }

        if (mContex != null){
            Intent intent_answered = new Intent(Constants.BROADCAST_ACTION_REPLICATION_CHANGE);
            mContex.sendBroadcast(intent_answered);
        }
    }

    public void showError(final String errorMessage, final Throwable throwable){

        String msg = String.format("%s: %s", errorMessage, throwable);
        com.couchbase.lite.util.Log.e(TAG, msg, throwable);
    }
}