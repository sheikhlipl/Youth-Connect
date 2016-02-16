package com.lipl.youthconnect.youth_connect.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.lipl.youthconnect.youth_connect.R;
import com.lipl.youthconnect.youth_connect.activity.MainActivity;

/**
 * Created by luminousinfoways on 08/02/16.
 */
public class PushReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent) {
        // Create a test notification
        // (Use deprecated notification API for demonstration
        // purposes, to avoid having to import AppCompat into your project)

        if(context == null){
            return;
        }

        int login_status = context.getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 1).getInt(Constants.SP_LOGIN_STATUS, 0);
        if (login_status == 1) {

            String notificationTitle = "Pushy";
            String notificationDesc = "Test notification";

            // Attempt to grab the message property from the payload
            if (intent.getStringExtra("message") != null) {
                notificationDesc = intent.getStringExtra("message");
            }

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context.getApplicationContext())
                            .setSmallIcon(R.mipmap.ic_launcher)
                                    //.setSound(uri)
                            .setContentTitle(context.getApplicationContext().getResources().getString(R.string.app_name))
                            .setContentText(notificationDesc)
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setAutoCancel(true);

            Intent resultIntent = new Intent(context.getApplicationContext(), MainActivity.class);
            resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            context.getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 2).edit().putInt(Constants.SP_FROM_NOTIFICATION_PANEL, 1).commit();

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context.getApplicationContext());
            stackBuilder.addParentStack(MainActivity.class);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_ONE_SHOT
                    );
            mBuilder.setContentIntent(resultPendingIntent);
            NotificationManager mNotificationManager =
                    (NotificationManager) context.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notif = mBuilder.build();
            notif.flags |= Notification.FLAG_AUTO_CANCEL;
            mNotificationManager.notify(0, notif);
        }
    }
}