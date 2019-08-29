package com.iot.doorsensor;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.util.Stack;

public class MainActivity extends AppCompatActivity implements LoadFileDialog.NoticeLoadFileDialogListener {

    public DeviceInfo mDevice = null;

    ActionBar mActionBar = null;
    TextView mTitleText = null;
    TextView mSubTitleText = null;

    ImageView mMainSensor;
//    ServiceCaller mServiceCaller;
//    static String mUserName;
//    static String mPassword;

    boolean isInitiate = true;

    SwitchCompat mLockButton;
    ProgressBar mBatteryBar;
    TextView mTemperatureLabel;

    public static Stack<Class<?>> parents = new Stack<>();

    MenuItem actionSettings = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mActionBar = getSupportActionBar();

        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(R.layout.action_bar_custom, null);
        mTitleText = mCustomView.findViewById(R.id.title_text);
        //mSubTitleText = (TextView) mCustomView.findViewById(R.id.subtitle_text);

        mBatteryBar = findViewById(R.id.batteryBar);
        mTemperatureLabel = findViewById(R.id.temperatureLabel);
        mLockButton = findViewById(R.id.lockButton);
        mMainSensor = findViewById(R.id.mainSensor);
        mLockButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                if (isInitiate) {
                    isInitiate = false;
                    return;
                }
                CallerTask mCallTask = new CallerTask(DoorSensorIoT.mServiceCaller,"set-device-status",
                        new ServiceCaller.VolleyCallback(){
                            @Override
                            public void onSuccess(@NonNull String result){
                                try {
                                    Object json = new JSONTokener(result).nextValue();

                                    if (json instanceof JSONObject) {
                                        //you have an object
                                        if (((JSONObject) json).has("message")) {
                                            String message = ((JSONObject) json).getString("message");
                                            if (message.contains(Constants.SActive)) {
                                                mDevice.mStatus = Constants.IActive;
                                                mMainSensor.setBackground(getResources().getDrawable(R.drawable.main_sensor_green));
                                            } else if (message.contains(Constants.SInactive)) {
                                                mDevice.mStatus = Constants.IInactive;
                                                mMainSensor.setBackground(getResources().getDrawable(R.drawable.main_sensor_gray));
                                            }
                                            //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            @Override
                            public void onError(@NonNull Throwable throwable){
                                //do stuff here
                                Toast.makeText(getApplicationContext(), throwable.toString(), Toast.LENGTH_LONG).show();
                                isInitiate = true;
                                mLockButton.setChecked(!isChecked);
                                isInitiate = false;
                            }
                        });

                mCallTask.execute(String.valueOf(mDevice.mDeviceId), isChecked ? Constants.SActive : Constants.SInactive);
            }
        });

        mActionBar.setDisplayShowTitleEnabled(false);
        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);

        if(SaveSharedPreference.getUserName(DoorSensorIoT.getContext()).length() == 0) {
            goToLogin();
        }
    }

    private void goToLogin() {
        DoorSensorIoT.mServiceCaller.mUserName = null;
        DoorSensorIoT.mServiceCaller.mPassword = null;
        SaveSharedPreference.clearUserCredentials(DoorSensorIoT.getContext());
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (getIntent().hasExtra("mainContext")) {
            mDevice = (DeviceInfo) getIntent().getSerializableExtra("mainContext");
            mTitleText.setText(mDevice.mDeviceName);
            isInitiate = true;
            if (mDevice.mStatus == Constants.IInactive) {
                mLockButton.setChecked(false);
                mMainSensor.setBackground(getResources().getDrawable(R.drawable.main_sensor_gray));
            }
            else if (mDevice.mStatus == Constants.IActive) {
                mLockButton.setChecked(true);
                mMainSensor.setBackground(getResources().getDrawable(R.drawable.main_sensor_green));
            }
            mBatteryBar.setProgress(mDevice.mBatteryStatus);
            mTemperatureLabel.setText(String.valueOf(mDevice.mTemperature) + " C");
            isInitiate = false;
        }

        mActionBar.setDisplayShowCustomEnabled(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

//        if (id == R.id.action_settings) {
//            SettingsActivity.parents.push(getClass());
//            Intent intent = new Intent(this, SettingsActivity.class);
//            startActivity(intent);
//            return true;
        if (id == android.R.id.home) {
            Intent parentActivityIntent = new Intent(this, parents.pop());
            NavUtils.navigateUpTo(this, parentActivityIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLoadFileDialogPositiveClick(DialogFragment dialog, int which) {
        File file = getSelectedFile(which);
        if (file != null) {

        }
    }

    private File getSelectedFile(int which) {
        File appFolder = getFilesDir();
        File[] files = appFolder.listFiles();
        int i = 0;
        for (File inFile : files) {
            if (!inFile.isDirectory() && !inFile.getName().startsWith("rList-com.prizma.prizsalt.")) {
                if (i == which)
                    return inFile;
                else
                    i++;
            }
        }

        return null;
    }

}
