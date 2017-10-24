package com.example.androidu.sensorpractice.GL;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by billp on 9/16/17.
 */

public class GLSquare {

    private final String vertexShaderCode =
            "uniform mat4 uMVPMatrix;" +
            "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";

    private FloatBuffer vertexBuffer;
    private ShortBuffer drawListBuffer;

    // number of coordinates per vertex in this array
    private static final int COORDS_PER_VERTEX = 3;

    private short drawOrder[] = { 0, 1, 2, 0, 2, 3 }; // order to draw vertices

    // color values are stored in a float array
    private float color[] = { 0.63671875f, 0.76953125f, 0.22265625f, 0.167f };

    private int mPositionHandle;
    private int mColorHandle;

    private float mXAngle = 0.0f;
    private float mYAngle = 0.0f;
    private float mZAngle = 0.0f;

    private float distance[] = {0.0f, 0.0f, -8f};

    private int vertexCount = 0;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    private int mProgram;

    public GLSquare() {}

    public void init(double ratio) {
        float squareCoords[] = {
                -0.75f,  0.75f, 0.0f,  // top left
                -0.75f, -0.75f, 0.0f,  // bottom left
                0.75f, -0.75f, 0.0f,   // bottom right
                -0.75f,  0.75f, 0.0f,  // top left
                0.75f,  0.75f, 0.0f,   // top right
                0.75f, -0.75f, 0.0f }; // bottom right

        vertexCount = squareCoords.length / COORDS_PER_VERTEX;

        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 4 bytes per float)
                squareCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(squareCoords);
        vertexBuffer.position(0);

        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 2 bytes per short)
                drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);

        int vertexShader = GLRenderer.loadShader(GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);
        int fragmentShader = GLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);

        // create empty OpenGL ES Program
        mProgram = GLES20.glCreateProgram();

        // add the vertex shader to program
        GLES20.glAttachShader(mProgram, vertexShader);

        // add the fragment shader to program
        GLES20.glAttachShader(mProgram, fragmentShader);

        // creates OpenGL ES program executables
        GLES20.glLinkProgram(mProgram);
    }

    public void setXRotation(float xAngle) {
        mXAngle = xAngle;
    }

    public void setYRotation(float yAngle) {
        mYAngle = yAngle;
    }

    public void setZRotation(float zAngle) {
        mZAngle = zAngle;
    }

    public void setDistanceFromUser(float x, float y, float z) {
        distance[0] = x;
        distance[1] = y;
        distance[2] = z;
    }

    public void draw(float[] viewMatrix, float[] projectionMatrix, float ratio) {
        // Add program to OpenGL ES environment
        GLES20.glUseProgram(mProgram);

        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        // get handle to fragment shader's vColor member
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // Set color for drawing the triangle
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        float[] mModelMatrix = new float[16];
        float[] mTranslationMatrix = new float[16];
        float[] mRotationMatrix = new float[16];
        float[] mMVPMatrix = new float[16];

        Matrix.setIdentityM(mModelMatrix, 0);

        Matrix.setIdentityM(mRotationMatrix, 0);
        Matrix.setRotateM(mRotationMatrix, 0, mYAngle, 0, 1, 0);
        Matrix.multiplyMM(mModelMatrix, 0, mModelMatrix, 0, mRotationMatrix, 0);

        //Matrix.setIdentityM(mRotationMatrix, 0);
        //Matrix.setRotateM(mRotationMatrix, 0, mXAngle, 1, 0, 0);
        //Matrix.multiplyMM(mModelMatrix, 0, mModelMatrix, 0, mRotationMatrix, 0);

        Matrix.setIdentityM(mTranslationMatrix, 0);
        Matrix.translateM(mTranslationMatrix, 0, distance[0], distance[1] / ratio, distance[2]);
        Matrix.multiplyMM(mModelMatrix, 0, mModelMatrix, 0, mTranslationMatrix, 0);

        Matrix.setIdentityM(mRotationMatrix, 0);
        Matrix.setRotateM(mRotationMatrix, 0, mZAngle, 0, 0, 1);
        Matrix.multiplyMM(mModelMatrix, 0, mModelMatrix, 0, mRotationMatrix, 0);

        Matrix.multiplyMM(mMVPMatrix, 0, viewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, projectionMatrix, 0, mMVPMatrix, 0);

        // get handle to shape's transformation matrix
        int mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
        //GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, vertexCount);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }
}