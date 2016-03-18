package com.example.zoeoeh.inputaudio;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

// import sound pool
import android.media.SoundPool;

import java.util.HashMap;


// class uses SensorEventListener interface
public class PlayTune extends Activity implements SensorEventListener {

    private SensorManager senSensorManager;
    private Sensor senAccelerometer;

    private long lastUpdate =0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 600;

    private static final int testNote1 = R.raw.referencenoteg;
    private static final int testNote2 = R.raw.referencenotef;

   // private static SoundPool soundPool;
    private static HashMap soundMap = new HashMap(2);


    private static MediaPlayer mp;

    private int chosenString = 1; // default choice is 1

    public static void playSound(Context context, int soundID)
    {
        if (mp != null)
        {
            mp.reset();
            mp.release();
        }

        mp = MediaPlayer.create(context, soundID);
        mp.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_tune);

        /*To initialize the SensorManager instance, we invoke getSystemService to fetch the system's
        SensorManager instance, which we in turn use to access the system's sensors.
        The getSystemService method is used to get a reference to a service of the system b
        passing the name of the service. With the sensor manager at our disposal, we get a
        reference to the system's accelerometer by invoking getDefaultSensor on the sensor manage
        and passing the type of sensor we're interested in. We then register the sensor using on
        of the SensorManager's public methods, registerListener. This method accepts three arguments,
        the activity's context, a sensor, and the rate at which sensor events are delivered to us.
         */

        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        //soundMap.put(testNote1, soundPool.load(this, R.raw.referencenoteg, 1));
        //soundMap.put( testNote2, soundPool.load(this, R.raw.referencenotef, 2));

        soundMap.put(testNote1, R.raw.referencenotee);
        soundMap.put(testNote2, R.raw.referencenotef);

        final Context myActivity = this;
        Button playBtn = (Button)findViewById(R.id.playBtn);
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSound(myActivity, chosenString);
            }
        });

    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
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
                    chosenString = testNote1;
                }
                else
                {
                    right = false;
                    direction = "left";
                    chosenString = testNote2;
                }

                speed = Math.abs(speed + y + z - last_y - last_z)/ diffTime * 10000;

                if (speed > SHAKE_THRESHOLD)
                {
                    Toast.makeText(getBaseContext(), "YOU SHOOK ME" + direction + "choice is " + chosenString, Toast.LENGTH_SHORT).show();
                }
                last_x = x;
                last_y = y;
                last_z = z;
            }

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {

    }

    @Override
    protected void onPause()
    {
        super.onPause();
        senSensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }


}
