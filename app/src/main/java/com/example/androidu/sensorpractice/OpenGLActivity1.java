package com.example.androidu.sensorpractice;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.androidu.sensorpractice.view.MyGLView;

public class OpenGLActivity1 extends AppCompatActivity {

    MyGLView mMyGLView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMyGLView = new MyGLView(this);
        setContentView(mMyGLView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMyGLView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMyGLView.onPause();
    }
}
