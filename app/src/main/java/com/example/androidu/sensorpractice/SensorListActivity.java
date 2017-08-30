package com.example.androidu.sensorpractice;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.List;

public class SensorListActivity extends AppCompatActivity {

    TextView mSensorTextView;
    SensorManager mSensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_list);

        mSensorTextView = (TextView) findViewById(R.id.tv_sensor_list);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        List<Sensor> sensorList = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        StringBuilder s = new StringBuilder();
        for(Sensor sensor : sensorList){
            s.append(sensor.getName());
            s.append("\n");
        }
        mSensorTextView.setText(s);
    }
}
