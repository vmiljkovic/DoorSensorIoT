package com.iot.doorsensor;


import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class ServiceCaller {

    public interface VolleyCallback {
        void onSuccess(@NonNull String result);

        void onError(@NonNull Throwable throwable);
    }

    private RequestQueue mRequestQueue;

    public String mUserName;
    public String mPassword;
    private Context mContext;

    ServiceCaller(Context context) {
        mContext = context;
        mRequestQueue = Volley.newRequestQueue(context);
    }

    void CallMethod(String method, String[] params, final VolleyCallback callbacks) throws JSONException {
        String url = Constants.URL + method;

        JSONObject requestJsonObject = new JSONObject();
        JSONObject userJsonObject = new JSONObject();
        JSONObject groupJsonObject = new JSONObject();
        JSONObject deviceJsonObject = new JSONObject();

        switch (method) {
            case "add-device":
                userJsonObject.put("userName", mUserName);
                userJsonObject.put("password", mPassword);

                deviceJsonObject.put("devEUI", params[0]);
                deviceJsonObject.put("name", params[1]);
                deviceJsonObject.put("groupId", null);

                requestJsonObject.put("user", userJsonObject);
                requestJsonObject.put("device", deviceJsonObject);
                break;
            case "create-group":
                userJsonObject.put("userName", mUserName);
                userJsonObject.put("password", mPassword);

                groupJsonObject.put("name", params[0]);
                groupJsonObject.put("iconId", params[1]);

                requestJsonObject.put("user", userJsonObject);
                requestJsonObject.put("group", groupJsonObject);
                break;
            case "add-device-to-group":

                userJsonObject.put("userName", mUserName);
                userJsonObject.put("password", mPassword);

                groupJsonObject.put("id", params[0]);
                deviceJsonObject.put("id", params[1]);

                requestJsonObject.put("user", userJsonObject);
                requestJsonObject.put("group", groupJsonObject);
                requestJsonObject.put("device", deviceJsonObject);
                break;
            case "set-device-status":

                userJsonObject.put("userName", mUserName);
                userJsonObject.put("password", mPassword);

                deviceJsonObject.put("id", params[0]);
                deviceJsonObject.put("status", params[1]);

                requestJsonObject.put("user", userJsonObject);
                requestJsonObject.put("device", deviceJsonObject);
                break;
            case "set-group-status":

                userJsonObject.put("userName", mUserName);
                userJsonObject.put("password", mPassword);

                groupJsonObject.put("id", params[0]);
                deviceJsonObject.put("status", params[1]);

                requestJsonObject.put("user", userJsonObject);
                requestJsonObject.put("group", groupJsonObject);
                requestJsonObject.put("device", deviceJsonObject);
                break;
            default:
                requestJsonObject.put("userName", mUserName);
                requestJsonObject.put("password", mPassword);
                break;
        }


        CustomRequest jsonObjReq = new CustomRequest(Request.Method.POST, url, requestJsonObject.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if (response.length() > 0) {
                    if (callbacks != null)
                        callbacks.onSuccess(response);
                }
            }
        },

        new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // If there a HTTP error then add a note to our repo list.
                //setRepoListText("Error while calling REST API");
                Log.e("Volley", error.toString());
                Toast.makeText(mContext, error.toString(), Toast.LENGTH_LONG).show();
                if (callbacks != null)
                    callbacks.onError(error);
            }
        });

        mRequestQueue.add(jsonObjReq);
    }
}


