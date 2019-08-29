package com.iot.doorsensor;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

public class NotificationsPreferenceFragment extends PreferenceFragmentCompat {

    public static final String FRAGMENT_TAG = "my_preference_fragment";

    public NotificationsPreferenceFragment() {
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String rootKey) {
        setPreferencesFromResource(R.xml.pref_notification, rootKey);
    }

}

