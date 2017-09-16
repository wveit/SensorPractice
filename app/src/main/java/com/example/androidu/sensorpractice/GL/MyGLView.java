package com.example.androidu.sensorpractice.GL;


import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyGLView extends GLSurfaceView {

    MyRenderer mRenderer;

    public MyGLView(Context context){
        super(context);
        setEGLContextClientVersion(2);
        mRenderer = new MyRenderer();
        mRenderer.add(new MyTriangle());
        setRenderer(mRenderer);
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        requestRender();
    }

}
