package com.iot.doorsensor;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.widget.TextView;

public class MyAccountPreferenceFragment extends PreferenceFragmentCompat {

    public static final String FRAGMENT_TAG = "my_preference_fragment";

    public MyAccountPreferenceFragment() {
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String rootKey) {
        setPreferencesFromResource(R.xml.pref_my_account, rootKey);

    }

//    @Override
//    public void onStart() {
//        super.onStart();
//
//        TextView numberDevices = getActivity().findViewById(R.id.pref_my_account_number_devices);
//
//    }

}

