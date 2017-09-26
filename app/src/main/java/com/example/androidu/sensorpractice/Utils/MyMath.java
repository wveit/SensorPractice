package com.example.androidu.sensorpractice.Utils;

import android.util.Log;

public class MyMath {
    private static final String TAG = "wakaMyMath";

    public static float[] crossProduct(float[] a, float[] b){
        float[] c = new float[3];

        c[0] = a[1] * b[2] - a[2] * b[1];
        c[1] = a[2] * b[0] - a[0] * b[2];
        c[2] = a[0] * b[1] - a[1] * b[0];

        return c;
    }

    public static float dotProduct(float[] a, float[] b){
        return a[0] * b[0] + a[1] * b[1] + a[2] * b[2];
    }

    public static float magnitude(float[] vec){
        return (float) Math.sqrt(vec[0] * vec[0] + vec[1] * vec[1] + vec[2] * vec[2]);
    }

    public static float angle(float[] a, float[] b){
        float unitDotProduct = dotProduct(a, b) / magnitude(a) / magnitude(b);
        float angle = (float)Math.acos(unitDotProduct);

        return angle;
    }

    public static float[] normalize(float[] vec){
        float[] temp = new float[3];
        float mag = magnitude(vec);

        temp[0] = vec[0] / mag;
        temp[1] = vec[1] / mag;
        temp[2] = vec[2] / mag;

        return temp;
    }

    public static float radToDegrees(float radians){
        return (float)(radians / 2 / Math.PI * 360);
    }

    public static float degreesToRad(float degrees){
        return (float) (degrees / 360.0 * 2 * Math.PI);
    }

    public static float compassBearing(float[] gravityVec, float[] magnetVec, float[] cameraVec){
        // Information we have:
        //  + gravityVec: A vector representing the direction and magnitude of gravity in camera coordinate system
        //  + magnetVec: A vector representing the direction and magnitude of earth's magnetic field from camera coordinate system
        //  + cameraVec: A vector representing the direction of the camera in phone coordinate system

        // Algorithm for finding compass bearing:
        //  * Project magnetVec onto earth's xz plane -> xzMagnet
        //  * Project cameraVec onto earth's xz plane -> xzCamera
        //  * Use dot product to find angle between xzMagnet and xzCamera

        //float[] xzTemp = MyMath.crossProduct(magnetVec, gravityVec);
        //float[] xzMagnet = MyMath.crossProduct(gravityVec, xzTemp);
        // faster to just assign something, also this will give correct orientation
        float[] xzMagnet = {magnetVec[0], 0.0f, magnetVec[2]};

        //xzTemp = MyMath.crossProduct(cameraVec, gravityVec);
        //float[] xzCamera = MyMath.crossProduct(gravityVec, xzTemp);
        // faster to just assign something, also this will give correct orientation
        float[] xzCamera = {cameraVec[0], 0.0f, cameraVec[2]};

        float angle = MyMath.angle(xzCamera, xzMagnet);
        angle = MyMath.radToDegrees(angle);

        float[] xproduct = MyMath.crossProduct(xzMagnet, xzCamera);
        float direction = MyMath.dotProduct(xproduct, gravityVec);

        if(direction >=0)
            return angle;
        else
            return 360 - angle;
    }

    public static float compassBearing(float[] magnetVec, float[] cameraVec){
        // Information we have:
        //  + gravityVec: A vector representing the direction and magnitude of gravity in camera coordinate system
        //  + magnetVec: A vector representing the direction and magnitude of earth's magnetic field from camera coordinate system
        //  + cameraVec: A vector representing the direction of the camera in phone coordinate system

        // Algorithm for finding compass bearing:
        //  * Project magnetVec onto earth's xz plane -> xzMagnet
        //  * Project cameraVec onto earth's xz plane -> xzCamera
        //  * Use dot product to find angle between xzMagnet and xzCamera

        float dx = magnetVec[0] - cameraVec[0];
        float dy = magnetVec[2] - cameraVec[2];
        float angle = (float) Math.atan(dx / dy);
        angle = MyMath.radToDegrees(angle);

        if(magnetVec[1] < 0.0 && magnetVec[2] <= 0.0)
            return angle;
        else
            return 180 + angle;
    }

    public static float compassBearing(float[] magnetVec, float[] gravityVec, float tilt){
        // Information we have:
        //  + gravityVec: A vector representing the direction and magnitude of gravity in camera coordinate system
        //  + magnetVec: A vector representing the direction and magnitude of earth's magnetic field from camera coordinate system
        //  + cameraVec: A vector representing the direction of the camera in phone coordinate system

        // Algorithm for finding compass bearing:
        //  * Project magnetVec onto earth's xz plane -> xzMagnet
        //  * Project cameraVec onto earth's xz plane -> xzCamera
        //  * Use dot product to find angle between xzMagnet and xzCamera

        float[] zyGrav = {gravityVec[1], gravityVec[2]};
        float[] zyMag  = {magnetVec[1], magnetVec[2]};
        float zyAngle = (float) Math.acos(dotProduct2D(zyGrav, zyMag) / (magnitude2D(zyGrav) * magnitude2D(zyMag)));

        // need to correct this
        float[] xyA = {gravityVec[0], gravityVec[1]};
        float[] xyB  = {0.0f, gravityVec[1]};
        float xyAngle = (float) Math.acos(dotProduct2D(xyA, xyB) / (magnitude2D(xyA) * magnitude2D(xyB)));

        float[] xzA = {magnetVec[0], magnetVec[2]};
        float[] xzB = {0.0f, magnetVec[2]};
        float xzAngle = (float) Math.acos(dotProduct2D(xzA, xzB) / (magnitude2D(xzA) * magnitude2D(xzB)));

        // need to do more sensor research, Y-axis seems funky
        float t_x = magnetVec[0] * (float) Math.cos(xyAngle);// + magnetVec[1] * (float) Math.sin(xyAngle);
        float t_z = magnetVec[2] * (float) Math.cos(zyAngle);// + magnetVec[1] * (float) Math.sin(zyAngle);
        //Log.d("MyMath", "xyA=(" + xyA[0] + "," + xyA[1] + ") xyB=(" + xyB[0] + "," + xyB[1] + ") angle=" + Math.toDegrees(xyAngle) + " t_x=" + t_x + " magVec={" + magnetVec[0] + "," + magnetVec[1] + "} mAngle=" + Math.toDegrees(xymAngle));
        //Log.d("MyMath", "xz=" + Math.toDegrees(xzAngle) + " zy=" + Math.toDegrees(zyAngle) + " xy=" + Math.toDegrees(xyAngle));

        float dx = t_x;
        float dy = -t_z;
        float angle = (float) Math.atan(dx / dy);
        angle = (float)Math.toDegrees(angle);// - (float)Math.toDegrees(xzAngle);
        //angle = (angle - (float)Math.toDegrees(xzAngle)) / 2.0f;
        Log.d(TAG, "angle: " + angle);

        if(gravityVec[1] > 0.0 && magnetVec[2] < 0.0) // if upright
            return angle;
        else
            return 180 + angle;
    }

    public static String vec2String(float[] vec){
        StringBuilder sb = new StringBuilder();
        sb.append("Vec: (");
        sb.append(vec[0]);
        for(int i = 1; i < vec.length; i++){
            sb.append(", ");
            sb.append(vec[i]);
        }
        sb.append(")");
        return sb.toString();
    }

    public static float magnitude2D(float[] a) {
        return (float) Math.sqrt(a[0] * a[0] + a[1] * a[1]);
    }

    public static float dotProduct2D(float[] a, float[] b){
        return a[0] * b[0] + a[1] * b[1];
    }

    public static float landscapeTiltAngle(float[] gravityVec, float[] phoneUpVec){
        float[] xyGravityVec = {gravityVec[0], gravityVec[1], 0};
        float[] phoneFrontVec = {0, 0, -1};
        float unitDotProduct = dotProduct(phoneUpVec, xyGravityVec) / magnitude(xyGravityVec) / magnitude(phoneUpVec);
        float angle = (float)Math.acos(unitDotProduct);
        angle = radToDegrees(angle);
        float direction = dotProduct(crossProduct(phoneUpVec, xyGravityVec), phoneFrontVec);

        if(direction >= 0){
            return angle;
        }
        else
            return 360 - angle;
    }

    // simpler tilt angle math, currently only works with gravity vectors
    public static float tiltAngle(float[] gravityVec, float[] phoneUpVec) {
        float theta = (float) Math.atan(-gravityVec[1] / gravityVec[0]);
        theta = MyMath.radToDegrees(theta);

        if(gravityVec[0] < 0.0) theta += 180;

        return theta;
    }

    // taken from http://blog.thomnichols.org/2011/08/smoothing-sensor-data-with-a-low-pass-filter

    /*
    * time smoothing constant for low-pass filter
    * 0 ≤ alpha ≤ 1 ; a smaller value basically means more smoothing
    * See: http://en.wikipedia.org/wiki/Low-pass_filter#Discrete-time_realization
    */
    static final float ALPHA = 0.09f;

    /**
     * @see http://en.wikipedia.org/wiki/Low-pass_filter#Algorithmic_implementation
     * @see http://developer.android.com/reference/android/hardware/SensorEvent.html#values
     */
    public static float[] lowPass( float[] input, float[] output ) {
        if ( output == null ) return input;

        for ( int i=0; i<input.length; i++ ) {
            output[i] = (float)((input[i] * ALPHA) + (output[i] * (1.0 - ALPHA)));
        }

        return output;
    }
}
