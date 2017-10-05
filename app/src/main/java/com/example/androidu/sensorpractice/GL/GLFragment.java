package com.example.androidu.sensorpractice.GL;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by bill on 10/3/17.
 */

public class GLFragment extends Fragment
{
    private GLView mGLView;

    public GLFragment()
    {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mGLView = new GLView(this.getActivity().getApplicationContext());
        return mGLView;
    }
}