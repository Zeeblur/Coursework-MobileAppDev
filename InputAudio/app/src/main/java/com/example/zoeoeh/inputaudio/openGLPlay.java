package com.example.zoeoeh.inputaudio;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class openGLPlay extends AppCompatActivity {

    // create new instance of my custom surface view
    private MyGLSurface myGLview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set the content to the XML activity containing the custom surface view - allows menu
        setContentView(R.layout.activity_open_glplay);

        // set view to surfaceView within XML
        myGLview = (MyGLSurface) findViewById(R.id.surfaceView);
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
