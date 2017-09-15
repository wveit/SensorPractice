package com.example.androidu.sensorpractice;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.androidu.sensorpractice.GL.MyGLView;

public class OpenGLActivity1 extends AppCompatActivity {

    MyGLView mGLView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create a GLSurfaceView instance and set it
        // as the ContentView for this Activity.
        mGLView = new MyGLView(this);
        setContentView(mGLView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLView.onPause();
    }
}
