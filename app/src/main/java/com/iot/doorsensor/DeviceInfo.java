package com.iot.doorsensor;


import java.io.Serializable;

public class DeviceInfo implements Serializable {

    public int mDeviceId;
    public int mParentGroupId;
    public int mStatus;
    public int mBatteryStatus;
    public String mTemperature;
    public String mDeviceName;
    public boolean mIsSelected;
    public boolean mIsEditMode;

    public DeviceInfo(int deviceId, String name, int parentId, String status, int batteryStatus, String temperature, boolean isSelected, boolean isEditMode) {
        mDeviceId = deviceId;
        mDeviceName = name;
        mParentGroupId = parentId;
        mIsSelected = isSelected;
        mIsEditMode = isEditMode;
        mBatteryStatus = batteryStatus;
        mTemperature = temperature;
        if (status.equals(Constants.SActive)) {
            mStatus = Constants.IActive;
        } else if (status.equals(Constants.SInactive)) {
            mStatus = Constants.IInactive;
        }
    }
}
