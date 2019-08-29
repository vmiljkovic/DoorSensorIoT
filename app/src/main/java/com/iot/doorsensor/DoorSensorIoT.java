package com.iot.doorsensor;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

public class DoorSensorIoT extends Application {
    public static final String TAG = DoorSensorIoT.class
            .getSimpleName();

    public static DoorSensorIoT mInstance;

    public static ServiceCaller mServiceCaller;

    public static Context mContext;


    @Override
    public void onCreate() {

        super.onCreate();

        mInstance = this; //return the singleton
        mContext = getApplicationContext();

        if (mServiceCaller == null) {
            mServiceCaller = new ServiceCaller(mContext);
        }
    }

    public static synchronized DoorSensorIoT getInstance() {
        return mInstance;
    }

    public static Context getContext() {
        return mContext;
    }
}