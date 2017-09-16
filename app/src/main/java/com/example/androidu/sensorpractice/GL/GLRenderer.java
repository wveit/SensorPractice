package com.example.androidu.sensorpractice.GL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by bill on 9/12/17.
 */

public class GLRenderer implements GLSurfaceView.Renderer{
    GLTriangle mTriangle;
    GLSquare mSquare;

    @Override
    public void onDrawFrame(GL10 gl) {
        Log.d("GLRenderer", "frame rendered");
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        if(mTriangle != null) {
            Log.d("GLRenderer", "triangle rendered");
            mTriangle.draw();
        }

        if(mSquare != null) {
            mSquare.draw();
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // set background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        if(mTriangle != null)
            mTriangle.init();

        if(mSquare != null)
            mSquare.init();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //resize
        GLES20.glViewport(0, 0, width, height);
    }

    public void add(GLTriangle triangle) {
        mTriangle = triangle;
    }
    public void add(GLSquare square) {
        mSquare = square;
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