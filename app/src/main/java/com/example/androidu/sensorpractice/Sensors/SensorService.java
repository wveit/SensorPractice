package com.example.androidu.sensorpractice.Sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.ArrayList;

/**
 * Created by billp on 9/16/17.
 */

public class SensorService {
    public static final int LINEAR_ACCELERATION = Sensor.TYPE_LINEAR_ACCELERATION;
    public static final int GRAVITY = Sensor.TYPE_GRAVITY;
    public static final int GYROSCOPE = Sensor.TYPE_GYROSCOPE;
    public static final int MAGNETIC_FIELD = Sensor.TYPE_MAGNETIC_FIELD;
    public static final int ROTATION_VECTOR = Sensor.TYPE_ROTATION_VECTOR;

    private SensorManager mSensorManager;
    private Sensor[] mSensors;
    private Callback mCallback;

    public SensorService(Context context, Callback callback, int[] sensorCodes){
        mCallback = callback;
        mSensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
        mSensors = new Sensor[sensorCodes.length];

        for(int i=0; i<sensorCodes.length; i++) {
            mSensors[i] = mSensorManager.getDefaultSensor(sensorCodes[i]);
        }
    }

    public boolean sensorExists(int i){
        return mSensors[i] != null;
    }

    public void start(){
        for(int i=0; i<mSensors.length; i++) {
            if(sensorExists(i))
                mSensorManager.registerListener(mListener, mSensors[i], SensorManager.SENSOR_DELAY_FASTEST);
        }
    }

    public void stop(){
        mSensorManager.unregisterListener(mListener);
    }

    public interface Callback {
        void sensorServiceCallback(SensorEvent event);
    }

    /////////////////////////////////////////////////////////////////////////////

    private SensorEventListener mListener = new SensorEventListener(){
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // do something
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            mCallback.sensorServiceCallback(event);
        }
    };
}
