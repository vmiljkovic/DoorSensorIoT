package com.iot.doorsensor;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ListActivity extends AppCompatActivity {

    public interface ListenFromActivity {
        void RefreshList();
        void AddDeviceToGroup(ArrayList<DeviceInfo> devices);
    }

    public static Stack<Class<?>> parents = new Stack<>();
    ActionBar mActionBar = null;
    public static TextView mTitleText = null;
    TextView mSubTitleText = null;

    MenuItem mActionEdit;
    MenuItem mActionCancel;
    MenuItem mActionSave;

    public ListenFromActivity activityListener;

    public static ArrayList<DeviceInfo> mGroupDeviceList = new ArrayList<DeviceInfo>();
    public static ArrayList<DeviceInfo> mAllDeviceList = new ArrayList<DeviceInfo>();
    public static GroupInfo currentGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mActionBar = getSupportActionBar();

        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(R.layout.action_bar_custom, null);
        mTitleText = mCustomView.findViewById(R.id.title_text);
        //mSubTitleText = (TextView) mCustomView.findViewById(R.id.subtitle_text);

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list, menu);
        mActionEdit = menu.getItem(0);
        mActionCancel = menu.getItem(1);
        mActionSave = menu.getItem(2);
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
            parentActivityIntent.putExtra(DeviceGroupActivity.PlaceholderFragment.ARG_SECTION_NUMBER, 1);
            NavUtils.navigateUpTo(this, parentActivityIntent);

            return true;
        } else if (id == R.id.action_edit) {
            mActionEdit.setVisible(false);
            mActionCancel.setVisible(true);
            mActionSave.setVisible(true);
            for (int i = 0; i < mGroupDeviceList.size(); i++) {
                DeviceInfo temp = mGroupDeviceList.get(i);
                temp.mIsEditMode = true;
                temp.mIsSelected = true;
            }
            for (int i = 0; i < mAllDeviceList.size(); i++) {
                DeviceInfo temp = mAllDeviceList.get(i);
                temp.mIsEditMode = true;
            }
            if (null != activityListener) {
                activityListener.RefreshList();
            }
        } else if (id == R.id.action_cancel) {
            mActionCancel.setVisible(false);
            mActionEdit.setVisible(true);
            mActionSave.setVisible(false);
            for (int i = 0; i < mAllDeviceList.size(); i++) {
                DeviceInfo temp = mAllDeviceList.get(i);
                temp.mIsEditMode = false;
                temp.mIsSelected = false;
            }
            for (int i = 0; i < mGroupDeviceList.size(); i++) {
                DeviceInfo temp = mGroupDeviceList.get(i);
                temp.mIsSelected = true;
            }

            if (null != activityListener) {
                activityListener.RefreshList();
            }
        } else if (id == R.id.action_save) {
            mActionCancel.setVisible(false);
            mActionSave.setVisible(false);
            mActionEdit.setVisible(true);

            if (null != activityListener) {
                activityListener.AddDeviceToGroup(mAllDeviceList);
                activityListener.RefreshList();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (getIntent().hasExtra("mainContext")) {
            currentGroup = (GroupInfo) getIntent().getSerializableExtra("mainContext");
        }

        if (currentGroup != null)
            ListActivity.mTitleText.setText(currentGroup.mGroupName);

        getDevices();
    }

    public void getDevices() {
        //showProgress(true);
        CallerTask mCallTask = new CallerTask(DoorSensorIoT.mServiceCaller,"find-all-devices",
                new ServiceCaller.VolleyCallback(){
                    @Override
                    public void onSuccess(@NonNull String result){
                        try {
                            Object json = new JSONTokener(result).nextValue();

                            if (json instanceof JSONObject) {
                                //you have an object
                                if (((JSONObject) json).has("message")) {
                                    String message = ((JSONObject) json).getString("message");
                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                }
                            }
                            else if (json instanceof JSONArray) {
                                JSONArray resultJSON = (JSONArray)json;
                                if (resultJSON.length() > 0) {
                                    mAllDeviceList.clear();
                                    mGroupDeviceList.clear();
                                    for (int i = 0; i < resultJSON.length(); i++) {
                                        JSONObject elementJson = resultJSON.getJSONObject(i);
                                        DeviceInfo temp = new DeviceInfo(elementJson.getInt("id"), elementJson.getString("name"), elementJson.optInt("groupId", -1), elementJson.getString("status"), elementJson.getInt("batteryStatus"), elementJson.getString("temperature"),false, false);
                                        mAllDeviceList.add(temp);
                                    }

                                    for (DeviceInfo device : mAllDeviceList) {
                                        if (device.mParentGroupId == currentGroup.mGroupId) {
                                            mGroupDeviceList.add(device);
                                        }
                                    }
                                }

                                if (null != activityListener) {
                                    activityListener.RefreshList();
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(@NonNull Throwable throwable){
                        //do stuff here
                        Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        mCallTask.execute();
    }

    public void setActivityListener(ListenFromActivity activityListener) {
        this.activityListener = activityListener;
    }
}
