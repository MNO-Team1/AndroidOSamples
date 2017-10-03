package com.samsung.mno.androidosampleapp;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
    }

    @OnClick(R.id.start_bg_service)
    public void onStartServiceButtonClick(View view){
        Toast.makeText(this, "Start Service", Toast.LENGTH_SHORT).show();

        //Start the Demo service
        Intent i= new Intent(this, DemoService.class);
        //Start the Radio Service
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //startForegroundService(i);
            startService(i);
        } else {
            startService(i);
        }
    }

    @OnClick(R.id.stop_bg_service)
    public void onStopServiceButtonClick(View view){
        Toast.makeText(this, "Stop Service", Toast.LENGTH_SHORT).show();

        //Stop the Demo service
        Intent i= new Intent(this, DemoService.class);
        this.stopService(i);
    }
}
