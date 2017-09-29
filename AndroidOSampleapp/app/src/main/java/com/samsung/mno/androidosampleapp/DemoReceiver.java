package com.samsung.mno.androidosampleapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class DemoReceiver extends BroadcastReceiver {

    private static final String TAG = "DemoReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive");
        if (intent == null) {
            Log.e(TAG, "Received Intent is null");
            return;
        }
        String action = intent.getAction();
        if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
            Log.d(TAG, "ACTION_BOOT_COMPLETED");

            //Start the Demo service
            Intent i= new Intent(context, DemoService.class);
            context.startService(i);
        }

    }
}
