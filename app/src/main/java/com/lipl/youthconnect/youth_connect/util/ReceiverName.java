package com.lipl.youthconnect.youth_connect.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ReceiverName extends BroadcastReceiver {
    public ReceiverName() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        if (cm == null)
            return;
        if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected()){
            return;
        }
    }
}