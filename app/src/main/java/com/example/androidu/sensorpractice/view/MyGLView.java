package com.example.androidu.sensorpractice.view;


import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyGLView extends GLSurfaceView {

    MyRenderer mRenderer;

    public MyGLView(Context context){
        super(context);
        setEGLContextClientVersion(2);
        mRenderer = new MyRenderer();
        setRenderer(mRenderer);
    }

    private void init(){
        GLES20.glClearColor(0.5f, 0, 0, 1);
    }

    private void resize(int width, int height){
        GLES20.glViewport(0, 0, width, height);
    }

    private void draw(){
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
    }


    class MyRenderer implements GLSurfaceView.Renderer{
        @Override
        public void onDrawFrame(GL10 gl) {
            draw();
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            init();
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            resize(width, height);
        }
    }

}
