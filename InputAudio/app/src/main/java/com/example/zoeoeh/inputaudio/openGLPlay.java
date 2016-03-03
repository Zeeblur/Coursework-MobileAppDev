package com.example.zoeoeh.inputaudio;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

public class openGLPlay extends Activity {

    private GLSurfaceView myGLview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myGLview = new GLSurfaceView(this); // create a surface view


        // add supporting checks for OPENGL ES 2.0 HERE



        myGLview.setEGLContextClientVersion(2);    // use openGL ES 2.0
        myGLview.setRenderer(new TestRenderer());  // set renderer

        setContentView(myGLview);  // tell android activity should be the openGL surface
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        myGLview.onResume();  // calls the surface view's onResume when the activity onResume
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        myGLview.onPause();
    }
}
