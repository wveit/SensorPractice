package com.example.androidu.sensorpractice.GL;


import android.content.Context;
import android.opengl.GLSurfaceView;

public class GLView extends GLSurfaceView {

    GLRenderer mRenderer;

    public GLView(Context context){
        super(context);
        setEGLContextClientVersion(2);
        mRenderer = new GLRenderer();
        //mRenderer.add(new GLTriangle());
        mRenderer.add(new GLSquare());
        setRenderer(mRenderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        requestRender();
    }

}
