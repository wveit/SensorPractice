package com.example.androidu.sensorpractice.view;


import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


import java.io.IOException;

public class MyCameraView extends SurfaceView implements SurfaceHolder.Callback{
    private static final String TAG = "wakaMyCameraView";
    private Camera mCamera = null;
    private SurfaceHolder mSurfaceHolder;
    private Paint mPaint;
    private int mRotationCode = Surface.ROTATION_0;
    private int mNumber = 0;


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

        mPaint.setColor(0x99FFFFFF);
        canvas.drawRect(0, 0, 200, 100, mPaint);

        mPaint.setColor(0xFF000000);
        mPaint.setTextSize(100);
        canvas.drawText("" + mNumber, 0, 75, mPaint);
    }


    public void setNumber(int number){
        mNumber = number;
        invalidate();
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
            mCamera.setDisplayOrientation(0);
        }
        else if(mRotationCode == Surface.ROTATION_180) {
            Log.d(TAG, "Rotation 180");
            mCamera.setDisplayOrientation(270);
        }
        else if(mRotationCode == Surface.ROTATION_270) {
            Log.d(TAG, "Rotation 270");
            mCamera.setDisplayOrientation(180);
        }
        else{
            mCamera.setDisplayOrientation(90);
            Log.d(TAG, "Rotation 0");
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
