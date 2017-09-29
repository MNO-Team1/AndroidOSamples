package mno.samsung.com.androidnsampleapp;

import android.app.Service;
import android.content.Intent;
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

}
