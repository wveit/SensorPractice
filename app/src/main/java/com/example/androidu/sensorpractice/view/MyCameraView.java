package com.example.androidu.sensorpractice.view;


import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


import com.example.androidu.sensorpractice.util.MyMath;

import java.io.IOException;

public class MyCameraView extends SurfaceView implements SurfaceHolder.Callback{
    private static final String TAG = "wakaMyCameraView";
    private Camera mCamera = null;
    private SurfaceHolder mSurfaceHolder;
    private Paint mPaint;
    private int mRotationCode = Surface.ROTATION_0;
    private int mBearing = 0;
    private int mTilt = 0;


    public MyCameraView(Activity activity, Camera camera){
        super(activity);
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mCamera = camera;

        mPaint = new Paint();
        mRotationCode = activity.getWindowManager().getDefaultDisplay().getRotation();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int canvasWidth = canvas.getWidth();
        Log.d(TAG, "canvasWidth: " + canvasWidth);

        mPaint.setColor(0x99FFFFFF);
        canvas.drawRect(0, 0, 200, 100, mPaint);
        canvas.drawRect(canvasWidth - 200, 0, canvasWidth, 100, mPaint);

        mPaint.setColor(0xFF000000);
        mPaint.setTextSize(100);
        canvas.drawText("" + mBearing, 0, 75, mPaint);
        canvas.drawText("" + mTilt, canvasWidth - 200, 75, mPaint);
        drawVerticalLine(mTilt, canvas);
        drawHorizontalLine(mTilt, canvas);
    }

    private void drawVerticalLine(float angle, Canvas canvas){
        angle = MyMath.degreesToRad(angle);
        int height = canvas.getHeight();
        int width = canvas.getWidth();

        int centerX = width / 2;
        int centerY = height / 2;

        int x1 = centerX + (int)(Math.sin(angle) * 2000);
        int y1 = centerY - (int)(Math.cos(angle) * 2000);
        int x2 = centerX - (int)(Math.sin(angle) * 2000);
        int y2 = centerY + (int)(Math.cos(angle) * 2000);

        mPaint.setColor(0x0FF0000FF);
        mPaint.setStrokeWidth(20);
        canvas.drawLine(x1, y1, x2, y2, mPaint);
    }

    private void drawHorizontalLine(float angle, Canvas canvas){
        angle = MyMath.degreesToRad(angle);
        int height = canvas.getHeight();
        int width = canvas.getWidth();

        int centerX = width / 2;
        int centerY = height / 2;

        int y2 = centerY + (int)(Math.sin(angle) * 2000);
        int x1 = centerX - (int)(Math.cos(angle) * 2000);
        int y1 = centerY - (int)(Math.sin(angle) * 2000);
        int x2 = centerX + (int)(Math.cos(angle) * 2000);

        mPaint.setColor(0x0FFFF0000);
        mPaint.setStrokeWidth(20);
        canvas.drawLine(x1, y1, x2, y2, mPaint);
    }


    public void setBearing(int bearing){
        mBearing = bearing;
        invalidate();
    }

    public void setTilt(int tilt){
        mTilt = tilt;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      SurfaceHolder.Callback methods
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mCamera.setPreviewDisplay(mSurfaceHolder);
            mCamera.startPreview();
            setWillNotDraw(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        mCamera.stopPreview();


        if(mRotationCode == Surface.ROTATION_90) {
            Log.d(TAG, "Rotation 90");
            //mCamera.setDisplayOrientation(0);
            mCamera.setDisplayOrientation(180);
        }
        else if(mRotationCode == Surface.ROTATION_180) {
            Log.d(TAG, "Rotation 180");
            //mCamera.setDisplayOrientation(270);
            mCamera.setDisplayOrientation(90);
        }
        else if(mRotationCode == Surface.ROTATION_270) {
            Log.d(TAG, "Rotation 270");
            //mCamera.setDisplayOrientation(180);
            mCamera.setDisplayOrientation(0);
        }
        else{
            Log.d(TAG, "Rotation 0");
            //mCamera.setDisplayOrientation(90);
            mCamera.setDisplayOrientation(270);
        }


        try{
            mCamera.setPreviewDisplay(mSurfaceHolder);
            mCamera.startPreview();
        }
        catch(IOException e){
            e.printStackTrace();
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mCamera.stopPreview();
    }
}
