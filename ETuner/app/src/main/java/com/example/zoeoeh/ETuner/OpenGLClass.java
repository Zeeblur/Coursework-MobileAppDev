package com.example.zoeoeh.ETuner;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

public class OpenGLClass extends Fragment implements SensorEventListener {


    // declare vars for accelerometer
    private SensorManager mySensorMan;
    private Sensor myAccel;
    private MediaPlayer myPlayer;

    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 50;

    // declare reference notes
    // sounds from harri bionic http://freesound.org/people/harri/
    // under creative commons 3.0 attribute license http://creativecommons.org/licenses/by/3.0/
    private static final int testNoteELow = R.raw.referencenoteelower;
    private static final int testNoteA = R.raw.referencenotea;
    private static final int testNoteD = R.raw.referencenoted;
    private static final int testNoteG = R.raw.referencenoteg;
    private static final int testNoteB = R.raw.referencenoteb;
    private static final int testNoteEHigh = R.raw.referencenoteehigher;

    private static int currentSound = testNoteA;

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

        // switch handler for change events. controls looping of sound playing
        Switch loopSwitch = (Switch)myView.findViewById(R.id.loopSwitch);
        loopSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton btn, boolean isChecked) {
                Toast.makeText(TabSwitcher.getmContext(), "switchy", Toast.LENGTH_SHORT).show();
            }
        });

        Switch playSwitch = (Switch)myView.findViewById(R.id.playSwitch);
        playSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton btn, boolean isChecked) {
                playSound(TabSwitcher.getmContext(), currentSound);
            }
        });

        mySensorMan = (SensorManager) TabSwitcher.getmContext().getSystemService(Context.SENSOR_SERVICE);
        myAccel = mySensorMan.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mySensorMan.registerListener(this, myAccel, SensorManager.SENSOR_DELAY_NORMAL);


        // return this view
        return myView;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        myGLview.onResume();  // calls the surface view's onResume when the activity onResume
        mySensorMan.unregisterListener(this);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        myGLview.onPause();
        mySensorMan.registerListener(this, myAccel, SensorManager.SENSOR_DELAY_NORMAL);
    }

    // implementation of interface

    @Override
    public void onSensorChanged(SensorEvent event) {
        // this detects the gesture
        Sensor mySensor = event.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER)
        {
            float x = event.values[0]; // values for each axis
            float y = event.values[1];
            float z = event.values[2];

            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) > 300)
            {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                boolean right;
                String direction;

                float speed = (x - last_x);

                if (speed > 0)
                {
                    right = true;
                    direction = "Right";
                }
                else
                {
                    right = false;
                    direction = "left";
                }

                speed = Math.abs(speed + y + z - last_y - last_z)/ diffTime * 10000;

                if (speed > SHAKE_THRESHOLD)
                {
                    Toast.makeText(TabSwitcher.getmContext(), "YOU SHOOK ME" + direction, Toast.LENGTH_SHORT).show();
                }
                last_x = x;
                last_y = y;
                last_z = z;
            }

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    // play

    public void playSound(Context context, int soundID)
    {
        if (myPlayer != null)
        {
            myPlayer.reset();
            myPlayer.release();
        }

        myPlayer = MediaPlayer.create(context, soundID);

        myPlayer.setLooping(true);
        myPlayer.start();
    }

}
