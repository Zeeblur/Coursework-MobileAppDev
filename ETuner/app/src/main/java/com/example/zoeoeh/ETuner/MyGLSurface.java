package com.example.zoeoeh.ETuner;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by Zoe Wall on 20/03/2016.
 * Custom GLSurface class used for initialising and listening to touch events
 */
public class MyGLSurface extends GLSurfaceView {

    // declare renderer
    private final TestRenderer myRenderer;

    // attribute constructor (for XML initialisation)
    public MyGLSurface(Context context, AttributeSet attrs) {
        super(context, attrs);

        // initialise renderer object
        myRenderer = new TestRenderer();
        initialise(context);
    }

    private void initialise(Context context)
    {
        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(myRenderer);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        // show touch event in log.
        // beginning of ray casting implementation - not complete
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

}
