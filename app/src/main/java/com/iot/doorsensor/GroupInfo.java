package com.iot.doorsensor;

import java.io.Serializable;

public class GroupInfo implements Serializable {

    public int mGroupId;
    public String mGroupName;
    public int mIconId;

    public GroupInfo(int groupId, String name, int iconId) {
        mGroupId = groupId;
        mGroupName = name;
        mIconId = iconId;
    }
}