package com.example.zoeoeh.inputaudio;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class OpenGLClass extends Fragment {

    // create new instance of my custom surface view
    private MyGLSurface myGLview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        // Replaces setContentView - set the content to the XML activity containing the custom surface view
        View myView = inflater.inflate(R.layout.activity_open_glclass, container, false);

        // myGLview is my custom GL surface which sets the client version and initialises the renderer
        myGLview = (MyGLSurface) myView.findViewById(R.id.surfaceView);

        // return this view
        return myView;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        myGLview.onResume();  // calls the surface view's onResume when the activity onResume
    }

    @Override
    public void onPause()
    {
        super.onPause();
        myGLview.onPause();


    }
}
