package com.example.androidu.sensorpractice.GL;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
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
        // extra insurance
        setBackgroundColor(Color.TRANSPARENT);

        // set GL ES version to 2.0
        setEGLContextClientVersion(2);

        // set up for transparent background
        setZOrderOnTop(true);
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        getHolder().setFormat(PixelFormat.RGBA_8888);

        mRenderer = new GLRenderer();
        setRenderer(mRenderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        //mRenderer.add(new GLTriangle());
        mRenderer.add(new GLSquare());
    }

    public void setBearing(double bearing) {
        mBearing = bearing;
        mRenderer.setBearing(bearing);
        requestRender();
    }

    public void setTilt(double tilt) {
        mTilt = tilt;
        mRenderer.setTilt(tilt);
        requestRender();
    }
}
