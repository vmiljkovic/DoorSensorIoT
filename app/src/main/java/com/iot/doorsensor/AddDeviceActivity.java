package com.iot.doorsensor;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.android.gms.common.annotation.KeepName;
import com.google.android.gms.vision.barcode.Barcode;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;


/** Demo app showing the various features of ML Kit for Firebase. This class is used to
 * set up continuous frame processing on frames from a camera source. */
@KeepName
public final class AddDeviceActivity extends AppCompatActivity
        implements OnRequestPermissionsResultCallback,
        CompoundButton.OnCheckedChangeListener,
        NewFileDialog.NoticeNewFileDialogListener,
        BarcodeScanningProcessor.BarCodeDetectListener {
    private static final String BARCODE_DETECTION = "Barcode Detection";
    private static final String TAG = "LivePreviewActivity";
    private static final int PERMISSION_REQUESTS = 1;

    private CameraSource cameraSource = null;
    private CameraSourcePreview preview;
    private GraphicOverlay graphicOverlay;
    private String selectedModel = BARCODE_DETECTION;

    String mBarCodeValue;

    public static Stack<Class<?>> parents = new Stack<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Log.d(TAG, "onCreate");

        preview = findViewById(R.id.firePreview);
        if (preview == null) {
            Log.d(TAG, "Preview is null");
        }
        graphicOverlay = findViewById(R.id.fireFaceOverlay);
        if (graphicOverlay == null) {
            Log.d(TAG, "graphicOverlay is null");
        }

        if (allPermissionsGranted()) {
            createCameraSource(selectedModel);
        } else {
            getRuntimePermissions();
        }

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

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent parentActivityIntent = new Intent(this, parents.pop());
                NavUtils.navigateUpTo(this, parentActivityIntent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.d(TAG, "Set facing");
        if (cameraSource != null) {
            if (isChecked) {
                cameraSource.setFacing(CameraSource.CAMERA_FACING_FRONT);
            } else {
                cameraSource.setFacing(CameraSource.CAMERA_FACING_BACK);
            }
        }
        preview.stop();
        startCameraSource();
    }

    private void createCameraSource(String model) {
        // If there's no existing cameraSource, create one.
        if (cameraSource == null) {
            cameraSource = new CameraSource(this, graphicOverlay);
        }

        switch (model) {
            case BARCODE_DETECTION:
                Log.i(TAG, "Using Barcode Detector Processor");
                BarcodeScanningProcessor temp = new BarcodeScanningProcessor(DoorSensorIoT.mServiceCaller, this, getApplicationContext());
                cameraSource.setMachineLearningFrameProcessor(temp);
                temp.setActivityListener(AddDeviceActivity.this);
                break;
            default:
                Log.e(TAG, "Unknown model: " + model);
        }
    }

    /**
     * Starts or restarts the camera source, if it exists. If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() {
        if (cameraSource != null) {
            try {
                if (preview == null) {
                    Log.d(TAG, "resume: Preview is null");
                }
                if (graphicOverlay == null) {
                    Log.d(TAG, "resume: graphOverlay is null");
                }
                preview.start(cameraSource, graphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                cameraSource.release();
                cameraSource = null;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        startCameraSource();
    }

    /** Stops the camera. */
    @Override
    protected void onPause() {
        super.onPause();
        preview.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cameraSource != null) {
            cameraSource.release();
        }
    }

    private String[] getRequiredPermissions() {
        try {
            PackageInfo info =
                    this.getPackageManager()
                            .getPackageInfo(this.getPackageName(), PackageManager.GET_PERMISSIONS);
            String[] ps = info.requestedPermissions;
            if (ps != null && ps.length > 0) {
                return ps;
            } else {
                return new String[0];
            }
        } catch (Exception e) {
            return new String[0];
        }
    }

    private boolean allPermissionsGranted() {
        for (String permission : getRequiredPermissions()) {
            if (!isPermissionGranted(this, permission)) {
                return false;
            }
        }
        return true;
    }

    private void getRuntimePermissions() {
        List<String> allNeededPermissions = new ArrayList<>();
        for (String permission : getRequiredPermissions()) {
            if (!isPermissionGranted(this, permission)) {
                allNeededPermissions.add(permission);
            }
        }

        if (!allNeededPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(
                    this, allNeededPermissions.toArray(new String[0]), PERMISSION_REQUESTS);
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults) {
        Log.i(TAG, "Permission granted!");
        if (allPermissionsGranted()) {
            createCameraSource(selectedModel);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private static boolean isPermissionGranted(Context context, String permission) {
        if (ContextCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission granted: " + permission);
            return true;
        }
        Log.i(TAG, "Permission NOT granted: " + permission);
        return false;
    }

    @Override
    public void onNewFileDialogNegativeClick(DialogFragment dialog) {
        mBarCodeValue = null;
        startCameraSource();
    }

    @Override
    public void onNewFileDialogPositiveClick(DialogFragment dialog) {
        CallerTask mCallTask = new CallerTask(DoorSensorIoT.mServiceCaller, "add-device",
                new ServiceCaller.VolleyCallback() {
                    @Override
                    public void onSuccess(@NonNull String result) {
                        try {
                            Object json = new JSONTokener(result).nextValue();

                            startCameraSource();

                            if (json instanceof JSONObject) {
                                //you have an object
                                if (((JSONObject) json).has("message")) {
                                    String message = ((JSONObject) json).getString("message");
                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

                                    Intent parentActivityIntent = new Intent(AddDeviceActivity.this, AddDeviceActivity.parents.pop());
                                    NavUtils.navigateUpTo(AddDeviceActivity.this, parentActivityIntent);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable throwable) {
                        //do stuff here
                        Toast.makeText(getApplicationContext(), throwable.toString(), Toast.LENGTH_LONG).show();
                        startCameraSource();
                    }
                });

        mCallTask.execute(mBarCodeValue, ((NewFileDialog)dialog).mDeviceName);
        mBarCodeValue = null;
        dialog.dismiss();
    }

    @Override
    public void onBarCodeDetected(String barCodeValue) {
        preview.stop();
        if (mBarCodeValue == null) {
            mBarCodeValue = barCodeValue;
            NewFileDialog dialog = new NewFileDialog();
            dialog.show(getSupportFragmentManager(), "NewFileDialog");
        }
    }
}
