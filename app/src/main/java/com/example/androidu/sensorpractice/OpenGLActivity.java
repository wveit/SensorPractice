package com.example.androidu.sensorpractice;

import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.androidu.sensorpractice.GL.GLRenderer;
import com.example.androidu.sensorpractice.GL.GLSquare;
import com.example.androidu.sensorpractice.GL.GLView;

public class OpenGLActivity extends AppCompatActivity {
    GLSurfaceView mGLView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();

        // Create a GLSurfaceView instance and set it
        // as the ContentView for this Activity.
        mGLView = new GLView(this);
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
