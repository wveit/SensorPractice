package com.example.androidu.sensorpractice.Camera;


import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;


import com.example.androidu.sensorpractice.util.MyMath;

import java.io.IOException;

public class CameraOverlayView extends View {
    private static final String TAG = "CameraOverlayView";
    private Paint mPaint;
    private int mBearing = 0;
    private int mTilt = 0;

    public CameraOverlayView(Context context) {
        super(context);
        Log.d(TAG, "setup with Context");
        setWillNotDraw(false);

        mPaint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int canvasWidth = canvas.getWidth();

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
}
