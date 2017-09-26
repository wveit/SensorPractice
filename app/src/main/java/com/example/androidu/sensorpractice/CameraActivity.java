package com.example.androidu.sensorpractice;

import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.example.androidu.sensorpractice.Camera.Camera2BasicFragment;
import com.example.androidu.sensorpractice.Camera.CameraOverlayView;
import com.example.androidu.sensorpractice.Sensors.MySensor;
import com.example.androidu.sensorpractice.Utils.MyMath;

public class CameraActivity extends AppCompatActivity {
    private final static String TAG = "CameraActivity";

    private CameraOverlayView mCamOverlay;
    private Camera2BasicFragment mCamFragment;

    private MySensor mAccelerometerSensor;
    private MySensor mGravitySensor;
    private MySensor mMagnetSensor;
    private MySensor mRotationVectorSensor;

    private float[] mAccelVector = {0, 0, 0};
    private float[] mGravityVector = {30, 20, 0};
    private float[] mMagnetVector = {1, 0, 0};
    private float[] mRotationVector = {0, 0, 0};
    private float[] mPhoneFrontVector = {0, 0, -1};
    private float[] mPhoneUpVector = {0, 1, 0};

    private float mBearing = 0.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        // keeps the screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // hide the action bar (gets fullscreen)
        getSupportActionBar().hide();

        // try to request full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // replace the camera container with a basic camera2 fragment
        if (null == savedInstanceState) {
            mCamFragment = Camera2BasicFragment.newInstance();
            mCamOverlay = new CameraOverlayView(this);
            // can't just add the view to a layout (since it may not exist yet before the camera is initialized)
            // so we will add the camera overlay whenever the camera preview is ready
            mCamFragment.setCamLayoutCallback(new CameraLayoutCallback() {
                @Override
                public void setUpLayout(LinearLayout layout) {
                    layout.addView(mCamOverlay);
                }
            });
            getFragmentManager().beginTransaction().replace(R.id.cam_container, mCamFragment).commit();
        }

        // initialize sensors
        mAccelerometerSensor = new MySensor(this, MySensor.LINEAR_ACCELERATION);
        mAccelerometerSensor.addListener(new MySensor.Listener() {
            @Override
            public void onSensorEvent(SensorEvent event) {
                if(mAccelVector == null)
                    mAccelVector = event.values.clone();
                else
                    MyMath.lowPass(event.values.clone(), mAccelVector);

                updateDirection();
            }
        });
        mGravitySensor = new MySensor(this, MySensor.GRAVITY);
        mGravitySensor.addListener(new GravityListener());
        mMagnetSensor = new MySensor(this, MySensor.MAGNETIC_FIELD);
        mMagnetSensor.addListener(new MagnetListener());
        mRotationVectorSensor = new MySensor(this, MySensor.ROTATION_VECTOR);
        mRotationVectorSensor.addListener(new RotationListener());

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
        if(mGravitySensor.sensorExists())
            mGravitySensor.start();
        if(mMagnetSensor.sensorExists())
            mMagnetSensor.start();
        if(mAccelerometerSensor.sensorExists())
            mAccelerometerSensor.start();

        super.onResume();
    }

    @Override
    protected void onPause() {
        if(mGravitySensor.sensorExists())
            mGravitySensor.stop();
        if(mMagnetSensor.sensorExists())
            mMagnetSensor.stop();
        if(mAccelerometerSensor.sensorExists())
            mAccelerometerSensor.stop();

        super.onPause();
    }

    private void updateDirection(){
        //Log.d("CameraActivity", "updating sensors info");
        //float bearing = MyMath.compassBearing(mGravityVector, mMagnetVector, mPhoneFrontVector);

        float tilt = MyMath.tiltAngle(mGravityVector, mPhoneUpVector);
        float bearing = MyMath.compassBearing(mMagnetVector, mGravityVector, tilt);
        if (Math.abs(bearing - mBearing) >= 1.10) {
            mBearing = bearing;
            mCamOverlay.setBearing(Math.round(bearing * 10) / 10.0f);
        }
        //mCamOverlay.setTilt((int)MyMath.landscapeTiltAngle(mGravityVector, mPhoneUpVector));
        mCamOverlay.setTilt(Math.round(MyMath.tiltAngle(mGravityVector, mPhoneUpVector) * 10) / 10.0f);
    }

    // public interface for when the camera layout is ready for callback
    public interface CameraLayoutCallback {
        public void setUpLayout(LinearLayout layout);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      Sensor Callbacks
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    class GravityListener implements MySensor.Listener{
        @Override
        public void onSensorEvent(SensorEvent event) {
            float[] vals = event.values.clone();

            for(int i=0; i<vals.length; i++)
                vals[i] = -vals[i];

            if(mGravityVector == null) {
                mGravityVector = vals;
                return;
            }

            MyMath.lowPass(vals, mGravityVector);

            if(mGravityVector != null && mMagnetVector != null)
                updateDirection();
        }
    }

    class MagnetListener implements MySensor.Listener{
        @Override
        public void onSensorEvent(SensorEvent event) {
            if(mMagnetVector == null) {
                mMagnetVector = event.values.clone();
                return;
            }

            MyMath.lowPass(event.values.clone(), mMagnetVector);
        }
    }

    class RotationListener implements MySensor.Listener{
        @Override
        public void onSensorEvent(SensorEvent event) {
            if(mRotationVector == null) {
                mRotationVector = event.values.clone();
                return;
            }

            MyMath.lowPass(event.values.clone(), mRotationVector);
        }
    }
}
