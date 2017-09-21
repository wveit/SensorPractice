package com.example.androidu.sensorpractice.GL;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;

/**
 * Created by bill on 9/18/17.
 */

public class GLView extends GLSurfaceView {
    private GLRenderer mRenderer;
    private double mBearing;
    private double mTilt;

    public GLView(Context context) {
        super(context);
        setEGLContextClientVersion(2);
        mRenderer = new GLRenderer();
        setRenderer(mRenderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        //mRenderer.add(new GLTriangle());
        mRenderer.add(new GLSquare());
    }

    public void setBearing(double bearing) {
        mBearing = bearing;
        requestRender();
    }

    public void setTilt(double tilt) {
        mTilt = tilt;
        requestRender();
    }
}
