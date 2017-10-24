package com.example.androidu.sensorpractice.GL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by bill on 9/12/17.
 */

public class GLRenderer implements GLSurfaceView.Renderer{
    GLTriangle mTriangle;
    GLSquare mSquare;
    double mBearing = 0.0;
    double mTilt = 0.0;
    float mRatio = 0.0f;

    // the view matrix of this view
    float[] mViewMatrix = new float[16];
    // the projection matrix of this view
    float[] mProjectionMatrix = new float[16];

    @Override
    public void onDrawFrame(GL10 gl) {
        Log.d("GLRenderer", "frame rendered");
        //gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        if(mTriangle != null) {
            Log.d("GLRenderer", "triangle rendered");
            mTriangle.draw();
        }

        if(mSquare != null) {
            mSquare.draw(mViewMatrix, mProjectionMatrix, mRatio);
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // set background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        //gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        //GLES20.glClearColor();

        // Position the eye behind the origin.
        final float eyeX = 0.0f;
        final float eyeY = 0.0f;
        final float eyeZ = 0.0f;

        // We are looking toward the distance
        final float lookX = 0.0f;
        final float lookY = 0.0f;
        final float lookZ = -25.0f;

        // Set our up vector. This is where our head would be pointing were we holding the camera.
        final float upX = 0.0f;
        final float upY = 1.0f;
        final float upZ = 0.0f;

        // Set the view matrix. This matrix can be said to represent the camera position.
        // NOTE: In OpenGL 1, a ModelView matrix is used, which is a combination of a model and
        // view matrix. In OpenGL 2, we can keep track of these matrices separately if we choose.
        Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //resize
        Log.d("GLRenderer", "surface changed to " + width + "x" + height);
        GLES20.glViewport(0, 0, width, height);
        mRatio = width * 1.0f / height;

        if(mTriangle != null)
            mTriangle.init(mRatio);
        if(mSquare != null)
            mSquare.init(mRatio);

        // Create a new perspective projection matrix. The height will stay the same
        // while the width will vary as per aspect ratio.
        final float ratio = mRatio;
        final float left = -ratio;
        final float right = ratio;
        final float bottom = -1.0f;
        final float top = 1.0f;
        final float near = 1.0f;
        final float far = 10.0f;

        Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
    }

    public void add(GLTriangle triangle) {
        mTriangle = triangle;
    }
    public void add(GLSquare square) {
        mSquare = square;
    }

    public void setBearing(double bearing) {
        mBearing = bearing;
        Log.d("GLRenderer", "set bearing: " + bearing);
        mSquare.setYRotation((float)-bearing);
    }

    public void setViewTilt(double vTilt) {
        mSquare.setXRotation((float)vTilt);
    }

    public void setTilt(double tilt) {
        mTilt = tilt;
        mSquare.setZRotation((float)-tilt);
    }

    public static int loadShader(int type, String shaderCode){
        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    public static int loadTexture(final Context context, final int resourceId)
    {
        final int[] textureHandle = new int[1];

        GLES20.glGenTextures(1, textureHandle, 0);

        if (textureHandle[0] != 0)
        {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;   // No pre-scaling

            // Read in the resource
            final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);

            // Bind to the texture in OpenGL
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

            // Set filtering
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

            // Load the bitmap into the bound texture.
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

            // Recycle the bitmap, since its data has been loaded into OpenGL.
            bitmap.recycle();
        }

        if (textureHandle[0] == 0)
        {
            throw new RuntimeException("Error loading texture.");
        }

        return textureHandle[0];
    }
}