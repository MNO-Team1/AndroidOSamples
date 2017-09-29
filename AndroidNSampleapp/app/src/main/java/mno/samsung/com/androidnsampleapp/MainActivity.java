package mno.samsung.com.androidnsampleapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends Activity {

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
        this.startService(i);
    }

    @OnClick(R.id.stop_bg_service)
    public void onStopServiceButtonClick(View view){
        Toast.makeText(this, "Stop Service", Toast.LENGTH_SHORT).show();

        //Stop the Demo service
        Intent i= new Intent(this, DemoService.class);
        this.stopService(i);
    }
}
