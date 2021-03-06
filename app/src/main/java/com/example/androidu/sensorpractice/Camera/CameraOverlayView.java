package com.example.androidu.sensorpractice.Camera;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;


import com.example.androidu.sensorpractice.Utils.MyMath;

public class CameraOverlayView extends View {
    private static final String TAG = "CameraOverlayView";
    private Paint mPaint;
    private float mBearing = 0.0f;
    private float mTilt = 0.0f;

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
        canvas.drawRect(0, 0, 220, 100, mPaint);
        canvas.drawRect(canvasWidth - 220, 0, canvasWidth, 100, mPaint);

        mPaint.setColor(0xFF000000);
        mPaint.setTextSize(100);
        canvas.drawText("" + mBearing, 0, 75, mPaint);
        canvas.drawText("" + mTilt, canvasWidth - 220, 75, mPaint);
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


    public void setBearing(float bearing){
        mBearing = bearing;
        invalidate();
    }

    public void setTilt(float tilt){
        mTilt = tilt;
        invalidate();
    }
}
