package com.example.androidu.sensorpractice;

import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.androidu.sensorpractice.GL.GLRenderer;
import com.example.androidu.sensorpractice.GL.GLSquare;

public class OpenGLActivity extends AppCompatActivity {
    GLSurfaceView mGLView;
    GLRenderer mRenderer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create a GLSurfaceView instance and set it
        // as the ContentView for this Activity.
        mGLView = new GLSurfaceView(this);
        mGLView.setEGLContextClientVersion(2);
        mRenderer = new GLRenderer();
        mGLView.setRenderer(mRenderer);
        mGLView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        setContentView(mGLView);

        mRenderer.add(new GLSquare());
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
