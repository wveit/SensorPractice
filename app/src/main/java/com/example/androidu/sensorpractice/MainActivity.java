package com.example.androidu.sensorpractice;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button mSensorListButton;
    Button mSensorReadingsButton;
    Button mMapButton;
    Button mCamera1Button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSensorListButton = (Button) findViewById(R.id.btn_sensor_list);
        mSensorReadingsButton = (Button) findViewById(R.id.btn_sensor_readings);
        mMapButton = (Button) findViewById(R.id.btn_map);
        mCamera1Button = (Button) findViewById(R.id.btn_camera1);

        mSensorListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SensorListActivity.class);
                startActivity(intent);
            }
        });

        mSensorReadingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SensorReadingsActivity.class);
                startActivity(intent);
            }
        });

        mMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DisplayMapActivity.class);
                startActivity(intent);
            }
        });

        mCamera1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CameraActivity1.class);
                startActivity(intent);
            }
        });
    }
}
