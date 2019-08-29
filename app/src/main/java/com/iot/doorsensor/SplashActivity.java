package com.iot.doorsensor;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    private Handler mHandler = new Handler();
    boolean mToGo = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        if(SaveSharedPreference.getUserName(DoorSensorIoT.getContext()).length() == 0)
        {
            // call Login Activity
            mHandler.postDelayed(runnable, 2000);
        }
        else
        {
            DoorSensorIoT.mServiceCaller.mUserName = SaveSharedPreference.getUserName(DoorSensorIoT.getContext());
            DoorSensorIoT.mServiceCaller.mPassword = SaveSharedPreference.getPassword(DoorSensorIoT.getContext());
            Intent intent = new Intent(getBaseContext(), DeviceGroupActivity.class);
            startActivity(intent);
        }
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (mToGo) {
                SplashActivity.this.finish();
                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                startActivity(intent);
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();

        mToGo = false;
        mHandler.removeCallbacks(runnable);
    }
}
