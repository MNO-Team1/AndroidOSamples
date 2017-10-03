package com.samsung.mno.androidosampleapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import java.util.stream.Stream;

public class DemoService extends Service {

    private static final String TAG="DemoService";
    private long mServiceStartTime;
    private long mServiceEndTime;

    public DemoService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        sendNotification();
        return Service.START_STICKY;
    }

    @Override
    public void onCreate() {
        mServiceStartTime = System.currentTimeMillis();
        Log.d(TAG,"onCreate - " + " mServiceStartTime= " + mServiceStartTime);
        sendNotification();
    }

    @Override
    public void onDestroy() {
        mServiceEndTime = System.currentTimeMillis();
        Log.d(TAG,"onDestroy - " + " mServiceEndTime= " + mServiceEndTime +
                " Service Running Time= " + (mServiceEndTime - mServiceStartTime));
    }


    private void sendNotification() {
        Log.d(TAG, "sendNotification");
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //Create Notification Channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("PRIMARY_CHANNEL",
                    getString(R.string.app_name), NotificationManager.IMPORTANCE_LOW);
            channel.enableLights(true);
            channel.setLightColor(Color.BLUE);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            notificationManager.createNotificationChannel(channel);
        }

        Notification.Builder notificationBuilder = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Android O Sample App")
                .setContentText("Notification from DemoService")
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationBuilder.setChannelId("PRIMARY_CHANNEL");
        }
        //notificationManager.notify(0, notificationBuilder.build());
        Notification notification = notificationBuilder.build();
        Log.d(TAG, "startForeground");
        startForeground(0, notification);
    }

}
