package mno.samsung.com.androidnsampleapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

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

//        //Create Notification Channel
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel("PRIMARY_CHANNEL",
//                    getString(R.string.app_name), NotificationManager.IMPORTANCE_LOW);
//            channel.enableLights(true);
//            channel.setLightColor(Color.BLUE);
//            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
//            notificationManager.createNotificationChannel(channel);
//        }

        Notification.Builder notificationBuilder = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Android O Sample App")
                .setContentText("Notification from DemoService")
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            notificationBuilder.setChannelId("PRIMARY_CHANNEL");
//        }

        Notification notification = notificationBuilder.build();
        Log.d(TAG, "startForeground");
        notificationManager.notify(0, notification);
    }

}
