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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

public class OpenGLClass extends Fragment implements SensorEventListener {

    private String TAG = "OpenGLClassFrag";

    // declare vars for accelerometer
    private SensorManager mySensorMan;
    private Sensor myAccel;
    private static MediaPlayer myPlayer;

    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private final int SHAKE_THRESHOLD = 600;

    // declare reference notes
    // sounds from harri bionic http://freesound.org/people/harri/
    // under creative commons 3.0 attribute license http://creativecommons.org/licenses/by/3.0/
    private static final int testNoteELow = R.raw.refnote_el;
    private static final int testNoteA = R.raw.refnote_a;
    private static final int testNoteD = R.raw.refnote_d;
    private static final int testNoteG = R.raw.refnote_g;
    private static final int testNoteB = R.raw.refnote_b;
    private static final int testNoteEHigh = R.raw.refnote_eh;

    private static int currentSoundIndex = 3;

    private static boolean loopingChecked;
    private static boolean playChecked;

    // initialise an array to store string id
    private static final int[] myStrings = {testNoteELow, testNoteA, testNoteD, testNoteG, testNoteB, testNoteEHigh};

    // create new instance of my custom surface view
    private MyGLSurface myGLview;

    private boolean shakeDirty = false;

    private Switch playSwitch;
    private Switch loopSwitch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        // Replaces setContentView - set the content to the XML activity containing the custom surface view
        View myView = inflater.inflate(R.layout.activity_open_glclass, container, false);

        // myGLview is my custom GL surface which sets the client version and initialises the renderer
        myGLview = (MyGLSurface) myView.findViewById(R.id.surfaceView);

        // switch handler for change events. controls looping of sound playing
        loopSwitch = (Switch)myView.findViewById(R.id.loopSwitch);
        loopSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton btn, boolean isChecked) {

                // bool used for setting looping state of media player
                loopingChecked = isChecked;

                updatePlay();
            }
        });

        playSwitch = (Switch)myView.findViewById(R.id.playSwitch);
        playSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton btn, boolean isChecked) {
                playChecked = isChecked;
                if (isChecked)
                {
                    playSound(TabSwitcher.getmContext(), currentSoundIndex);
                }
                else
                {
                    // if unchecked and player isn't null stop it.
                    if (myPlayer != null)
                    {
                        stopPlay();
                        // stop vibrating of string
                        TestRenderer.setChosenString(-1);
                    }
                }
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

        mySensorMan.registerListener(this, myAccel, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        myGLview.onPause();
        mySensorMan.unregisterListener(this);
    }

    // implementation of interface

    @Override
    public void onSensorChanged(SensorEvent event) {

        // this detects the gesture
        Sensor mySensor = event.sensor;

        // increment for strings, changes with direction
        int increment = 0;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER)
        {
            float x = event.values[0]; // values for each axis
            float y = event.values[1];
            float z = event.values[2];

            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) > 200)
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
                    increment = 1;
                }
                else
                {
                    right = false;
                    direction = "left";
                    increment = -1;
                }

                speed = Math.abs(speed + y + z - last_y - last_z)/ diffTime * 10000;

                if (speed > SHAKE_THRESHOLD)
                {
                    // either positive or negative depending on the direction.
                    currentSoundIndex += increment;

                    if (currentSoundIndex < 0)
                    {
                        currentSoundIndex = myStrings.length - 1;
                    }
                    else if (currentSoundIndex > myStrings.length -1)
                    {
                        currentSoundIndex = 0;
                    }

                    Toast.makeText(TabSwitcher.getmContext(), "YOU SHOOK ME" + direction + " sound " + currentSoundIndex, Toast.LENGTH_SHORT).show();
                    shakeDirty = true;
                    if (playChecked)
                    {
                        updatePlay();
                    }
                }

            }
            last_x = x;
            last_y = y;
            last_z = z;

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void updatePlay()
    {
        // if player is playing, stop and start with new looping set
        if (myPlayer != null)
        {
            while (myPlayer.isPlaying())
            {
                // stop playback once the loop has completed (current position of track is at zero)
                if (myPlayer.getCurrentPosition() == 0)
                {
                    stopPlay();
                    break;
                }
            }

            // if stopped because shake has occured, start playing with new sound
            if (shakeDirty)
            {
                playSound(TabSwitcher.getmContext(), currentSoundIndex);
            }

        }

        if (playChecked && loopingChecked || playChecked && shakeDirty)    // if it IS null and been checked start playing again
        {
            playSound(TabSwitcher.getmContext(), currentSoundIndex);
        }

    }

    // play

    public void playSound(Context context, int soundID)
    {
        Log.d(TAG, "sound playing sound ID " + soundID + " current sound " + currentSoundIndex);
        if (myPlayer != null)
        {
            myPlayer.reset();
            myPlayer.release();
        }

        myPlayer = MediaPlayer.create(context, myStrings[soundID]);

        TestRenderer.setChosenString(soundID);

        myPlayer.setLooping(loopingChecked);

        myPlayer.start();
        shakeDirty = false;

    }

    // stop playback and release media player/pointer if playing
    public void stopPlay()
    {
        if (myPlayer.isPlaying())
        {
            myPlayer.stop();
            myPlayer.reset();
            myPlayer.release();
            myPlayer = null;
        }
    }

    // if tab is invisible ensure to stop playback
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (this.isVisible())
        {
            if (!isVisibleToUser)
            {
                // set switches to false
                playSwitch.setChecked(false);
                loopSwitch.setChecked(false);

            }
        }
    }

}
