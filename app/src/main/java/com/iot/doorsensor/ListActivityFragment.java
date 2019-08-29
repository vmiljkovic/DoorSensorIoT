package com.iot.doorsensor;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

//import static com.iot.doorsensor.DeviceGroupActivity.mDeviceList;

/**
 * A placeholder fragment containing a simple view.
 */
public class ListActivityFragment extends Fragment implements BTDevice.OnItemClickListener, ListActivity.ListenFromActivity {

    private RecyclerView mRecyclerView;

    SwitchCompat mLockButton;

    boolean mIsInitiate = true;
    boolean mIsEditMode;

    public ListActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((ListActivity) getActivity()).setActivityListener(ListActivityFragment.this);
    }

    @Override
    public void onStart() {
        super.onStart();

//        if (mAllDeviceList == null) {
//            if (getActivity().getIntent().hasExtra("mainSubContext")) {
//                currentGroup = (GroupInfo) getActivity().getIntent().getSerializableExtra("mainSubContext");
//                ListActivity.mTitleText.setText(currentGroup.mGroupName);
//            }
//
//            mGroupDeviceList.clear();
//            if (getActivity().getIntent().hasExtra("mainContext")) {
//                mAllDeviceList = (ArrayList<DeviceInfo>) getActivity().getIntent().getSerializableExtra("mainContext");
//                for (DeviceInfo device : mAllDeviceList) {
//                    if (device.mParentGroupId == currentGroup.mGroupId)
//                        mGroupDeviceList.add(device);
//                }
//
//                mRecyclerView.setAdapter(new BTDevice(mGroupDeviceList, this));
//            }
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);

        mRecyclerView = rootView.findViewById(R.id.devices);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mLockButton = rootView.findViewById(R.id.lockButton);
        mLockButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                if (mIsInitiate) {
                    mIsInitiate = false;
                    return;
                }
                CallerTask mCallTask = new CallerTask(DoorSensorIoT.mServiceCaller,"set-group-status",
                        new ServiceCaller.VolleyCallback(){
                            @Override
                            public void onSuccess(@NonNull String result){
                                try {
                                    Object json = new JSONTokener(result).nextValue();

                                    if (json instanceof JSONObject) {
                                        //you have an object
                                        if (((JSONObject) json).has("message")) {
                                            String message = ((JSONObject) json).getString("message");
                                            //Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG).show();

                                            for (DeviceInfo device : ListActivity.mGroupDeviceList) {
                                                device.mStatus = isChecked ? Constants.IActive : Constants.IInactive;
                                            }
                                            mRecyclerView.getAdapter().notifyDataSetChanged();
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            @Override
                            public void onError(@NonNull Throwable throwable){
                                //do stuff here
                                Toast.makeText(getActivity().getApplicationContext(), throwable.toString(), Toast.LENGTH_LONG).show();
                                mIsInitiate = true;
                                mLockButton.setChecked(!isChecked);
                                mIsInitiate = false;
                                mRecyclerView.getAdapter().notifyDataSetChanged();
                            }
                        });

                mCallTask.execute(String.valueOf(ListActivity.currentGroup.mGroupId), isChecked ? Constants.SActive : Constants.SInactive);
            }
        });

        return rootView;
    }

    @Override
    public void onClick(View view, int position) {
        selectItem(position);
    }

    private void selectItem(int position) {
        if (mIsEditMode) {
            ListActivity.mAllDeviceList.get(position).mIsSelected = !ListActivity.mAllDeviceList.get(position).mIsSelected;
            mRecyclerView.getAdapter().notifyItemChanged(position);
            return;
        }
        MainActivity.parents.push(getActivity().getClass());
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.putExtra("mainContext", ListActivity.mGroupDeviceList.get(position));

        startActivity(intent);
    }

    @Override
    public void RefreshList() {
        if (ListActivity.mAllDeviceList.size() > 0) {
            mIsEditMode = ListActivity.mAllDeviceList.get(0).mIsEditMode;
        }

        if (mIsEditMode)
            mRecyclerView.setAdapter(new BTDevice(ListActivity.mAllDeviceList, this));
        else
            mRecyclerView.setAdapter(new BTDevice(ListActivity.mGroupDeviceList, this));

        if (ListActivity.mGroupDeviceList.size() > 0) {
            mLockButton.setVisibility(!mIsEditMode ? View.VISIBLE : View.INVISIBLE);

            mIsInitiate = true;
            boolean isThereInactive = false;
            for (int i = 0; i < ListActivity.mGroupDeviceList.size(); i++) {
                DeviceInfo temp = ListActivity.mGroupDeviceList.get(i);
                if (temp.mStatus  == Constants.IInactive) {
                    isThereInactive = true;
                    break;
                }
            }

            if (isThereInactive)
                mLockButton.setChecked(false);
            else
                mLockButton.setChecked(true);

            mIsInitiate = false;
        }
        else
            mLockButton.setVisibility(View.INVISIBLE);

        mRecyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void AddDeviceToGroup(final ArrayList<DeviceInfo> devices) {
        for (int i = 0; i < devices.size(); i++) {
            final DeviceInfo device = devices.get(i);
            if (device.mIsSelected && device.mParentGroupId == -1) {
                CallerTask mCallTask = new CallerTask(DoorSensorIoT.mServiceCaller, "add-device-to-group",
                        new ServiceCaller.VolleyCallback() {
                            @Override
                            public void onSuccess(@NonNull String result) {
                                try {
                                    Object json = new JSONTokener(result).nextValue();

                                    if (json instanceof JSONObject) {
                                        //you have an object
                                        if (((JSONObject) json).has("message")) {
                                            String message = ((JSONObject) json).getString("message");
                                            //Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(@NonNull Throwable throwable) {
                                //do stuff here
                                Toast.makeText(getActivity().getApplicationContext(), throwable.toString(), Toast.LENGTH_LONG).show();
                                mRecyclerView.getAdapter().notifyDataSetChanged();
                            }
                        });

                mCallTask.execute(String.valueOf(ListActivity.currentGroup.mGroupId), String.valueOf(device.mDeviceId));

                if (device.mIsSelected) {
                    ListActivity.mGroupDeviceList.add(device);
                    device.mParentGroupId = ListActivity.currentGroup.mGroupId;
                }
            }

            device.mIsEditMode = false;
            mIsEditMode = false;
            mLockButton.setVisibility((!mIsEditMode && (ListActivity.mGroupDeviceList.size() > 0)) ? View.VISIBLE : View.INVISIBLE);
            mRecyclerView.getAdapter().notifyDataSetChanged();
        }
    }
}
