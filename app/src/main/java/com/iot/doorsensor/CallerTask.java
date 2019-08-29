package com.iot.doorsensor;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;

public class CallerTask extends AsyncTask<String, Void, Boolean> {

    private ServiceCaller mServiceCaller;
    private String mMethod;
    private ServiceCaller.VolleyCallback mCallback;

    CallerTask(ServiceCaller serviceCaller, String method, ServiceCaller.VolleyCallback callback) {
        mServiceCaller = serviceCaller;
        mMethod = method;
        mCallback = callback;
    }

    @Override
    protected Boolean doInBackground(String... params) {

        boolean result = false;
        // TODO: attempt authentication against a network service.

        try {
            mServiceCaller.CallMethod(mMethod, params, mCallback);
        } catch (JSONException e) {
            // If there is an error then output this to the logs.
            Log.e("Volley", "Invalid JSON Object.");
            return false;
        }

        return true;
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        //mCallTask = null;
        //showProgress(false);

        if (success) {

        } else {

        }
    }

    @Override
    protected void onCancelled() {
        //mCallTask = null;
        //showProgress(false);
    }
}