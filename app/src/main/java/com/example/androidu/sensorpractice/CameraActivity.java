package com.example.androidu.sensorpractice;

import android.hardware.SensorEvent;
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
        setContentView(R.layout.activity_camera);

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
        //Log.d("CameraActivity", "updating sensors info");
        mCamOverlay.setBearing((int)MyMath.compassBearing(mGravityVector, mMagnetVector, mPhoneFrontVector));
        mCamOverlay.setTilt((int)MyMath.landscapeTiltAngle(mGravityVector, mPhoneUpVector));
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
