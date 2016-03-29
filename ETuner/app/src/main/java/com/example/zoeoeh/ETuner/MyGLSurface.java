package com.example.zoeoeh.ETuner;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by Zoeoeh on 20/03/2016.
 *
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

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (event != null)
        {
            if (event.getAction() == MotionEvent.ACTION_DOWN)
            {
                if (myRenderer != null)
                {
                    queueEvent(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("Surface", "I've been touched");
                        }
                    });

                    return true;
                }
            }
        }

        return super.onTouchEvent(event);
    }

    // TODO write why I wont do raycasting....

}
