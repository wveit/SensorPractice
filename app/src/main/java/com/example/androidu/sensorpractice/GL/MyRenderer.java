package com.example.androidu.sensorpractice.GL;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by bill on 9/12/17.
 */

public class MyRenderer implements GLSurfaceView.Renderer{
    MyTriangle mTriangle;

    @Override
    public void onDrawFrame(GL10 gl) {
        Log.d("MyRenderer", "frame rendered");
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        if(mTriangle != null) {
            Log.d("MyRenderer", "triangle rendered");
            mTriangle.draw();
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // init
        GLES20.glClearColor(0.5f, 0, 0, 1);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //resize
        GLES20.glViewport(0, 0, width, height);
    }

    public void add(MyTriangle triangle) {
        mTriangle = triangle;
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
}