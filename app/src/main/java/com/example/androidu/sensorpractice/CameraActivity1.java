package com.example.androidu.sensorpractice;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.SensorEvent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.androidu.sensorpractice.sensor.MySensor;
import com.example.androidu.sensorpractice.view.MyCameraView;

public class CameraActivity1 extends AppCompatActivity {

    private final static int PERMISSION_REQUEST_CODE = 37;
    private final static String TAG = "wakaCameraActivity1";
    private MyCameraView mCameraView;
    private Camera mCamera;

    private MySensor mGravitySensor;
    private MySensor mMagnetSensor;

    private float[] mGravityVector = {0, 0, 0};
    private float[] mMagnetVector = {0, 0, 0};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_camera1);
        getSupportActionBar().hide();

        ViewGroup layout = (FrameLayout) findViewById(R.id.layout_camera);

        if(!cameraExists()){
            Log.d(TAG, "Camera does not exist");
        }
        else if(!haveCameraPermission()){
            Log.d(TAG, "Do not have camera permission");
            requestCameraPermission();
        }
        else{
            Log.d(TAG, "Setting up camera view");
            mCamera = getCamera();
            mCameraView = new MyCameraView(this, mCamera);
            layout.addView(mCameraView);
        }

        mGravitySensor = new MySensor(this, MySensor.GRAVITY);
        mGravitySensor.addListener(new GravityListener());
        mMagnetSensor = new MySensor(this, MySensor.MAGNETIC_FIELD);
        mMagnetSensor.addListener(new MagnetListener());

        int rotation = getWindowManager().getDefaultDisplay().getRotation();
    }

    @Override
    protected void onResume() {
        if(mGravitySensor.sensorExists() && mMagnetSensor.sensorExists()){
            mGravitySensor.start();
            mMagnetSensor.start();
        }

        super.onResume();
    }

    @Override
    protected void onPause() {
        if(mGravitySensor.sensorExists())
            mGravitySensor.stop();
        if(mMagnetSensor.sensorExists())
            mMagnetSensor.stop();

        super.onPause();
    }

    private void updateDirection(){
//        int direction = (int)magnitude(crossProduct(mMagnetVector, mGravityVector));
        int direction = (int)mGravityVector[0];
        mCameraView.setNumber(direction);
    }



    private static float magnitude(float[] vec){
        return (float) Math.sqrt(vec[0] * vec[0] + vec[1] * vec[1] + vec[2] * vec[2]);
    }


    private static float[] crossProduct(float[] a, float[] b){
        float[] c = {0, 0, 0};

        c[0] = a[1] * b[2] - a[2] * b[1];
        c[1] = a[2] * b[0] - a[0] * b[2];
        c[2] = a[0] * b[1] - a[1] * b[0];

        return c;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      Sensor Callbacks
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    class GravityListener implements MySensor.Listener{
        @Override
        public void onSensorEvent(SensorEvent event) {

            mGravityVector[0] = event.values[0];
            mGravityVector[1] = event.values[1];
            mGravityVector[2] = event.values[2];

            updateDirection();
        }
    }

    class MagnetListener implements MySensor.Listener{
        @Override
        public void onSensorEvent(SensorEvent event) {

            mMagnetVector[0] = event.values[0];
            mMagnetVector[1] = event.values[1];
            mMagnetVector[2] = event.values[2];
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      Camera manipulation methods
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private boolean cameraExists(){
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    private boolean haveCameraPermission(){
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
    }

    private Camera getCamera(){
        Camera c = null;
        try {
            c = Camera.open();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return c;
    }
}
