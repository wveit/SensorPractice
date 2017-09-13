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
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.example.androidu.sensorpractice.sensor.MySensor;
import com.example.androidu.sensorpractice.util.MyMath;
import com.example.androidu.sensorpractice.view.MyCameraView;

public class CameraActivity1 extends AppCompatActivity {

    private final static int PERMISSION_REQUEST_CODE = 37;
    private final static String TAG = "wakaCameraActivity1";
    private MyCameraView mCameraView;
    private Camera mCamera;

    private MySensor mGravitySensor;
    private MySensor mMagnetSensor;
    private MySensor mRotationVectorSensor;

    private float[] mGravityVector = {30, 20, 0};
    private float[] mMagnetVector = {1, 0, 0};
    private float[] mRotationVector = {0, 0, 0};
    private float[] mPhoneFrontVector = {0, 0, -1};
    private float[] mPhoneUpVector = {1, 0, 0};

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
        mRotationVectorSensor = new MySensor(this, MySensor.ROTATION_VECTOR);
        mRotationVectorSensor.addListener(new RotationListener());

        int rotation = getWindowManager().getDefaultDisplay().getRotation();

        Log.d(TAG, "Angle: " + MyMath.angle(mGravityVector, mMagnetVector));
    }

    private void logVec(float[] vec){
        StringBuilder sb = new StringBuilder();
        sb.append("Vec: (");
        sb.append(vec[0]);
        for(int i = 1; i < vec.length; i++){
            sb.append(", ");
            sb.append(vec[i]);
        }
        sb.append(")");
        Log.d(TAG, sb.toString());
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
        mCameraView.setBearing((int)MyMath.compassBearing(mGravityVector, mMagnetVector, mPhoneFrontVector));

        mCameraView.setTilt((int)MyMath.landscapeTiltAngle(mGravityVector, mPhoneUpVector));
    }





    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      Sensor Callbacks
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    class GravityListener implements MySensor.Listener{
        @Override
        public void onSensorEvent(SensorEvent event) {

            mGravityVector[0] = -event.values[0];
            mGravityVector[1] = -event.values[1];
            mGravityVector[2] = -event.values[2];

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

    class RotationListener implements MySensor.Listener{
        @Override
        public void onSensorEvent(SensorEvent event) {

            mRotationVector[0] = event.values[0];
            mRotationVector[1] = event.values[1];
            mRotationVector[2] = event.values[2];
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
