package com.example.androidu.sensorpractice;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;


public class SensorReadingsActivity extends AppCompatActivity {

    private Button mFreezeButton;
    private TextView mLinearAccelerationTextView;
    private TextView mGyroscopeTextView;
    private TextView mMagnetometerTextView;
    private TextView mRotationVectorTextView;
    private TextView mGpsTextView;

    private SensorManager mSensorManager;
    private Sensor mLinearAcceleration;
    private Sensor mGyroscope;
    private Sensor mMagnetometer;
    private Sensor mRotationVector;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private boolean mSensorsAreFrozen = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_readings);

        mFreezeButton = (Button) findViewById(R.id.btn_freeze);
        mLinearAccelerationTextView = (TextView) findViewById(R.id.tv_linear_acceleration);
        mGyroscopeTextView = (TextView) findViewById(R.id.tv_gyroscope);
        mMagnetometerTextView = (TextView) findViewById(R.id.tv_magnetometer);
        mRotationVectorTextView = (TextView) findViewById(R.id.tv_rotation_vector);
        mGpsTextView = (TextView) findViewById(R.id.tv_gps);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mLinearAcceleration = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mRotationVector = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        mFreezeButton.setOnClickListener(mButtonClickListener);
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

    private void handleMagnetometerEvent(SensorEvent event){
        StringBuilder sb = new StringBuilder();
        sb.append("\nMagnetometer:\n");
        sb.append("x: ");
        sb.append(event.values[0]);
        sb.append("\ny: ");
        sb.append(event.values[1]);
        sb.append("\nz: ");
        sb.append(event.values[2]);
        mMagnetometerTextView.setText(sb);
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
        mSensorManager.unregisterListener(mLinearAccelerationListener);
        mSensorManager.unregisterListener(mGyroscopeListener);
        mSensorManager.unregisterListener(mMagnetometerListener);
        mSensorManager.unregisterListener(mRotationVectorListener);
        stopGps();
    }

    private void unfreezeSensors(){
        mSensorManager.registerListener(mLinearAccelerationListener, mLinearAcceleration, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(mGyroscopeListener, mGyroscope, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(mMagnetometerListener, mMagnetometer, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(mRotationVectorListener, mRotationVector, SensorManager.SENSOR_DELAY_FASTEST);
        startGps();
    }

    private void startGps(){
        /*
            Method description:

            This method first checks to see if this app has permission to use Location
                + If have permission, request initial location (result sent to mGpsSuccessListener)
                    and set up callback for recurring location updates (updates sent to
                    mLocationUpdatesCallback)
                + If do not have permission, asks for permission so that gps can be started next time
                    (GPS will not be started this time)
         */

        boolean havePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        if(havePermission){
            try {
                mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, mGpsSuccessListener);

                LocationRequest locationRequest = new LocationRequest();
                locationRequest.setInterval(5000);
                locationRequest.setFastestInterval(5000);
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

                mFusedLocationProviderClient.requestLocationUpdates(locationRequest, mLocationUpdatesCallback, null);
            }
            catch(SecurityException e){
                e.printStackTrace();
            }
        }
        else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 3);
        }
    }

    private void stopGps(){
        mFusedLocationProviderClient.removeLocationUpdates(mLocationUpdatesCallback);
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

    private SensorEventListener mLinearAccelerationListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            handleLinearAccelerationEvent(event);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    private SensorEventListener mGyroscopeListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            handleGyroscopeEvent(event);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    private SensorEventListener mMagnetometerListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            handleMagnetometerEvent(event);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    private SensorEventListener mRotationVectorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            handleRotationVectorEvent(event);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    private OnSuccessListener<Location> mGpsSuccessListener = new OnSuccessListener<Location>(){
        @Override
        public void onSuccess(Location location) {
            if(location != null){
                handleGpsEvent(location);
            }
        }
    };

    private LocationCallback mLocationUpdatesCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            for(Location location : locationResult.getLocations()){
                handleGpsEvent(location);
            }
        }
    };

}
