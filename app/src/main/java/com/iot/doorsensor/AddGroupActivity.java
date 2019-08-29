package com.iot.doorsensor;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.Stack;

public class AddGroupActivity extends AppCompatActivity {

    ActionBar mActionBar = null;

    public String mGroupNameString;
    public int mIconId;

    ToggleButton iconHouse;
    ToggleButton iconOffice;
    ToggleButton iconCottage;

    public static Stack<Class<?>> parents = new Stack<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mActionBar = getSupportActionBar();

        mActionBar.setDisplayShowTitleEnabled(false);
        mActionBar.setDisplayHomeAsUpEnabled(true);

//        if (mServiceCaller == null) {
//            mServiceCaller = new ServiceCaller(getApplication());
//        }
//
//        if (mUserName == null && getIntent().hasExtra("userName")) {
//            mUserName = getIntent().getExtras().getString("userName");
//        }
//
//        mServiceCaller.mUserName = mUserName;
//
//        if (mPassword == null && getIntent().hasExtra("password")) {
//            mPassword = getIntent().getExtras().getString("password");
//        }
//
//        mServiceCaller.mPassword = mPassword;

        iconHouse = findViewById(R.id.iconHouse);
        iconOffice = findViewById(R.id.iconOffice);
        iconCottage = findViewById(R.id.iconCottage);

        final EditText groupName = findViewById(R.id.group_name);

        final Button savebtn = findViewById(R.id.savebtn);
        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGroupNameString = groupName.getText().toString();
                if (iconHouse.isChecked())
                    mIconId = Constants.iconHouse;
                else if (iconOffice.isChecked())
                    mIconId = Constants.iconOffice;
                else if (iconCottage.isChecked())
                    mIconId = Constants.iconCottage;

                CallerTask mCallTask = new CallerTask(DoorSensorIoT.mServiceCaller,"create-group",
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
                                            Intent parentActivityIntent = new Intent(AddGroupActivity.this, parents.pop());
                                            parentActivityIntent.putExtra(DeviceGroupActivity.PlaceholderFragment.ARG_SECTION_NUMBER, 1);
                                            NavUtils.navigateUpTo(AddGroupActivity.this, parentActivityIntent);
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            @Override
                            public void onError(@NonNull Throwable throwable){
                                //do stuff here
                            }
                        });

                mCallTask.execute(mGroupNameString, String.valueOf(mIconId));
            }
        });

        final Button cancelbtn = findViewById(R.id.cancelbtn);
        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent parentActivityIntent = new Intent(AddGroupActivity.this, parents.pop());
                parentActivityIntent.putExtra(DeviceGroupActivity.PlaceholderFragment.ARG_SECTION_NUMBER, 1);
                NavUtils.navigateUpTo(AddGroupActivity.this, parentActivityIntent);
                clearColorFilter();
            }
        });

        savebtn.setVisibility(View.INVISIBLE);

        iconHouse.getBackground().setColorFilter(getResources().getColor(R.color.colorDisabled),PorterDuff.Mode.SRC_ATOP);
        iconOffice.getBackground().setColorFilter(getResources().getColor(R.color.colorDisabled),PorterDuff.Mode.SRC_ATOP);
        iconCottage.getBackground().setColorFilter(getResources().getColor(R.color.colorDisabled),PorterDuff.Mode.SRC_ATOP);

        groupName.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() > 0 && (iconHouse.isChecked() || iconOffice.isChecked() || iconCottage.isChecked()))
                    savebtn.setVisibility(View.VISIBLE);
                else
                    savebtn.setVisibility(View.INVISIBLE);
            }
        });
        iconHouse.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    iconHouse.getBackground().setColorFilter(getResources().getColor(R.color.colorPrimary),PorterDuff.Mode.SRC_ATOP);
                    iconCottage.setChecked(false);
                    iconOffice.setChecked(false);
                    if(groupName.getText().length() > 0)
                        savebtn.setVisibility(View.VISIBLE);
                } else {
                    iconHouse.getBackground().setColorFilter(getResources().getColor(R.color.colorDisabled),PorterDuff.Mode.SRC_ATOP);
                    if(!iconHouse.isChecked() && !iconOffice.isChecked() && !iconCottage.isChecked())
                        savebtn.setVisibility(View.INVISIBLE);
                }
            }
        });

        iconOffice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    iconOffice.getBackground().setColorFilter(getResources().getColor(R.color.colorPrimary),PorterDuff.Mode.SRC_ATOP);
                    iconHouse.setChecked(false);
                    iconCottage.setChecked(false);
                    if(groupName.getText().length() > 0)
                        savebtn.setVisibility(View.VISIBLE);
                } else {
                    iconOffice.getBackground().setColorFilter(getResources().getColor(R.color.colorDisabled),PorterDuff.Mode.SRC_ATOP);
                    if(!iconHouse.isChecked() && !iconOffice.isChecked() && !iconCottage.isChecked())
                        savebtn.setVisibility(View.INVISIBLE);
                }
            }
        });

        iconCottage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    iconCottage.getBackground().setColorFilter(getResources().getColor(R.color.colorPrimary),PorterDuff.Mode.SRC_ATOP);
                    iconHouse.setChecked(false);
                    iconOffice.setChecked(false);
                    if(groupName.getText().length() > 0)
                        savebtn.setVisibility(View.VISIBLE);
                } else {
                    iconCottage.getBackground().setColorFilter(getResources().getColor(R.color.colorDisabled),PorterDuff.Mode.SRC_ATOP);
                    if(!iconHouse.isChecked() && !iconOffice.isChecked() && !iconCottage.isChecked())
                        savebtn.setVisibility(View.INVISIBLE);
                }
            }
        });

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

    public void clearColorFilter(){
        iconHouse.getBackground().clearColorFilter();
        iconOffice.getBackground().clearColorFilter();
        iconCottage.getBackground().clearColorFilter();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent parentActivityIntent = new Intent(AddGroupActivity.this, parents.pop());
                parentActivityIntent.putExtra(DeviceGroupActivity.PlaceholderFragment.ARG_SECTION_NUMBER, 1);
                NavUtils.navigateUpTo(AddGroupActivity.this, parentActivityIntent);
                clearColorFilter();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
