package com.example.androidu.sensorpractice.Sensors;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.ArrayList;

public class MySensor {
    public static final int ACCELEROMETER = Sensor.TYPE_ACCELEROMETER;
    public static final int LINEAR_ACCELERATION = Sensor.TYPE_LINEAR_ACCELERATION;
    public static final int GRAVITY = Sensor.TYPE_GRAVITY;
    public static final int GYROSCOPE = Sensor.TYPE_GYROSCOPE;
    public static final int MAGNETIC_FIELD = Sensor.TYPE_MAGNETIC_FIELD;
    public static final int ROTATION_VECTOR = Sensor.TYPE_ROTATION_VECTOR;

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private ArrayList<Listener> mListenerList = new ArrayList<Listener>();
    private boolean mRunning;

    public MySensor(Context context, int sensorCode){
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(sensorCode);
        mRunning = false;
    }

    public boolean sensorExists(){
        return mSensor != null;
    }

    public boolean running() {
        return mRunning;
    }

    public void start(){
        if(sensorExists()) {
            mSensorManager.registerListener(mListener, mSensor, SensorManager.SENSOR_DELAY_FASTEST);
            mRunning = true;
        }
    }

    public void stop(){
        if(sensorExists()) {
            mSensorManager.unregisterListener(mListener);
            mRunning = false;
        }
    }

    public void addListener(Listener listener){
        mListenerList.add(listener);
    }

    public interface Listener{
        public void onSensorEvent(SensorEvent event);
    }




    /////////////////////////////////////////////////////////////////////////////

    private SensorEventListener mListener = new SensorEventListener(){
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            for(Listener listener : mListenerList)
                listener.onSensorEvent(event);
        }
    };

}


