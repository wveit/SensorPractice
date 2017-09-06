package com.example.androidu.sensorpractice;


import android.hardware.SensorEvent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.androidu.sensorpractice.sensor.MyGps;
import com.example.androidu.sensorpractice.sensor.MySensor;


public class SensorReadingsActivity extends AppCompatActivity {

    private Button mFreezeButton;
    private TextView mLinearAccelerationTextView;
    private TextView mGravityTextView;
    private TextView mGyroscopeTextView;
    private TextView mMagneticFieldTextView;
    private TextView mRotationVectorTextView;
    private TextView mGpsTextView;

    private MySensor mLinearAcceleration;
    private MySensor mGravity;
    private MySensor mGyroscope;
    private MySensor mMagneticField;
    private MySensor mRotationVector;
    private MyGps mGps;

    private boolean mSensorsAreFrozen = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_readings);

        mFreezeButton = (Button) findViewById(R.id.btn_freeze);
        mLinearAccelerationTextView = (TextView) findViewById(R.id.tv_linear_acceleration);
        mGravityTextView = (TextView) findViewById(R.id.tv_gravity);
        mGyroscopeTextView = (TextView) findViewById(R.id.tv_gyroscope);
        mMagneticFieldTextView = (TextView) findViewById(R.id.tv_magnetometer);
        mRotationVectorTextView = (TextView) findViewById(R.id.tv_rotation_vector);
        mGpsTextView = (TextView) findViewById(R.id.tv_gps);

        mLinearAcceleration = new MySensor(this, MySensor.LINEAR_ACCELERATION);
        mGravity = new MySensor(this, MySensor.GRAVITY);
        mGyroscope = new MySensor(this, MySensor.GYROSCOPE);
        mMagneticField = new MySensor(this, MySensor.MAGNETIC_FIELD);
        mRotationVector = new MySensor(this, MySensor.ROTATION_VECTOR);
        mGps = new MyGps(this);

        mFreezeButton.setOnClickListener(mButtonClickListener);
        mLinearAcceleration.addListener(mLinearAccelerationListener);
        mGravity.addListener(mGravityListener);
        mGyroscope.addListener(mGyroscopeListener);
        mMagneticField.addListener(mMagneticFieldListener);
        mRotationVector.addListener(mRotationVectorListener);
        mGps.addListener(mGpsListener);
        handleButtonToggle();
    }

    @Override
    protected void onResume() {
        super.onResume();
        unfreezeSensors();
    }

    @Override
    protected void onPause() {
        super.onPause();
        freezeSensors();
    }

    private void handleButtonToggle(){
        if(mSensorsAreFrozen){
            mSensorsAreFrozen = false;
            mFreezeButton.setText("Freeze Sensors");
            unfreezeSensors();
        }
        else{
            mSensorsAreFrozen = true;
            mFreezeButton.setText("Unfreeze Sensors");
            freezeSensors();
        }
    }

    private void handleLinearAccelerationEvent(SensorEvent event){
        StringBuilder sb = new StringBuilder();
        sb.append("\nLinear Acceleration:\n");
        sb.append("x: ");
        sb.append(event.values[0]);
        sb.append("\ny: ");
        sb.append(event.values[1]);
        sb.append("\nz: ");
        sb.append(event.values[2]);
        mLinearAccelerationTextView.setText(sb);
    }

    private void handleGravityEvent(SensorEvent event){
        StringBuilder sb = new StringBuilder();
        sb.append("\nGravity:\n");
        sb.append("x: ");
        sb.append(event.values[0]);
        sb.append("\ny: ");
        sb.append(event.values[1]);
        sb.append("\nz: ");
        sb.append(event.values[2]);
        mGravityTextView.setText(sb);
    }

    private void handleGyroscopeEvent(SensorEvent event){
        StringBuilder sb = new StringBuilder();
        sb.append("\nGyroscope:\n");
        sb.append("x: ");
        sb.append(event.values[0]);
        sb.append("\ny: ");
        sb.append(event.values[1]);
        sb.append("\nz: ");
        sb.append(event.values[2]);
        mGyroscopeTextView.setText(sb);
    }

    private void handleMagneticFieldEvent(SensorEvent event){
        StringBuilder sb = new StringBuilder();
        sb.append("\nMagneticField:\n");
        sb.append("x: ");
        sb.append(event.values[0]);
        sb.append("\ny: ");
        sb.append(event.values[1]);
        sb.append("\nz: ");
        sb.append(event.values[2]);
        mMagneticFieldTextView.setText(sb);
    }

    private void handleRotationVectorEvent(SensorEvent event){
        StringBuilder sb = new StringBuilder();
        sb.append("\nRotationVector:\n");
        sb.append("x: ");
        sb.append(event.values[0]);
        sb.append("\ny: ");
        sb.append(event.values[1]);
        sb.append("\nz: ");
        sb.append(event.values[2]);
        mRotationVectorTextView.setText(sb);
    }

    private void handleGpsEvent(Location location){
        StringBuilder sb = new StringBuilder();
        sb.append("\nGPS:\n");
        sb.append("Provider: " + location.getProvider() + "\n");
        sb.append("Latitude: " + location.getLatitude() + "\n");
        sb.append("Longitude: " + location.getLongitude() + "\n");
        sb.append("Altitude: " + location.getAltitude() + "\n");
        sb.append("Bearing: " + location.getBearing() + "\n");
        sb.append("Accuracy: " + location.getAccuracy() + "\n");
        sb.append("Speed: " + location.getSpeed() + "\n");
        mGpsTextView.setText(sb);
    }

    private void freezeSensors(){
        mLinearAcceleration.stop();
        mGravity.stop();
        mGyroscope.stop();
        mMagneticField.stop();
        mRotationVector.stop();
        mGps.stop();
    }

    private void unfreezeSensors(){
        mLinearAcceleration.start();
        mGravity.start();
        mGyroscope.start();
        mMagneticField.start();
        mRotationVector.start();
        mGps.start();
    }


    ///////////////////////////////////////////////////////////////////////
    //
    //  Callback Classes and Callback Member Variables
    //
    //      * These classes and variables only have one purpose:
    //        to call this class' helper methods when
    //        an event occurs.
    //
    ///////////////////////////////////////////////////////////////////////

    View.OnClickListener mButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            handleButtonToggle();
        }
    };

    private MySensor.Listener mLinearAccelerationListener = new MySensor.Listener(){
        @Override
        public void onSensorEvent(SensorEvent event){
            handleLinearAccelerationEvent(event);
        }
    };

    private MySensor.Listener mGravityListener = new MySensor.Listener(){
        @Override
        public void onSensorEvent(SensorEvent event){
            handleGravityEvent(event);
        }
    };

    private MySensor.Listener mGyroscopeListener = new MySensor.Listener() {
        @Override
        public void onSensorEvent(SensorEvent event){
            handleGyroscopeEvent(event);
        }
    };

    private MySensor.Listener mMagneticFieldListener = new MySensor.Listener() {
        @Override
        public void onSensorEvent(SensorEvent event){
            handleMagneticFieldEvent(event);
        }
    };

    private MySensor.Listener mRotationVectorListener = new MySensor.Listener() {
        @Override
        public void onSensorEvent(SensorEvent event){
            handleRotationVectorEvent(event);
        }
    };

    private MyGps.Listener mGpsListener = new MyGps.Listener(){
        @Override
        public void handleLocation(Location location){
            handleGpsEvent(location);
        }
    };

}
