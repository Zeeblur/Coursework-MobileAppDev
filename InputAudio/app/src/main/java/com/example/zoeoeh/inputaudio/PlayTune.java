package com.example.zoeoeh.inputaudio;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.hardware.SensorEventListener;
import android.widget.Toast;


// class uses SensorEventListener interface
public class PlayTune extends Activity implements SensorEventListener {

    private SensorManager senSensorManager;
    private Sensor senAccelerometer;

    private long lastUpdate =0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 600;

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

            if ((curTime - lastUpdate) > 100)
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
                    Toast.makeText(getBaseContext(), "YOU SHOOK ME" + direction, Toast.LENGTH_SHORT).show();
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
