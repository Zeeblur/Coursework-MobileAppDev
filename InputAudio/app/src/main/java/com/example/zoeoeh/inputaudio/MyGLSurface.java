package com.example.zoeoeh.inputaudio;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

/**
 * Created by Zoeoeh on 20/03/2016.
 */
public class MyGLSurface extends GLSurfaceView {

    // declare renderer
    private final TestRenderer myRenderer;

    // context constructor
    public MyGLSurface(Context context){
        super(context);

        // initialise renderer object
        myRenderer = new TestRenderer();
        initialise(context);
    }

    // attribute constructor (for XML initialisation)
    public MyGLSurface(Context context, AttributeSet attrs) {
        super(context, attrs);

        // initialise renderer object
        myRenderer = new TestRenderer();
        initialise(context);
    }

    public void initialise(Context context)
    {
        // add supporting checks for Opengl ES 2.0 here

        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(myRenderer);

    }


}
