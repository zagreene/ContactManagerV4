package com.example.aaron.maptest2;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.FloatMath;

/**
 * Created by Zachary Aaron Greene on 4/12/2018.
 *      credits located below.
 *
 * The majority of the code here is designed with help from:
 *      http://jasonmcreynolds.com/?p=388
 *
 * The following help was used:
 *      https://stackoverflow.com/questions/5271448/how-to-detect-shake-event-with-android
 *      https://developer.android.com/guide/topics/sensors/sensors_overview.html
 *      https://developer.android.com/guide/topics/sensors/sensors_motion.html
 *
 */

public class ShakeDetection implements SensorEventListener
{
    // globals

    private static final float SHAKE_THRESHOLD_GRAVITY = 2.7F;      // not sure what this does; tweak this to get rid of gravity.
    private static final int SHAKE_SLOP_TIME_MS = 1000;               // minimum time between shakes that is acceptable.

    private OnShakeListener listener;
    private long timeStamp;

    // constructor
    public void setOnShakeListener(OnShakeListener listener)
    {
        this.listener = listener;
    }

    // custom interface for shaking
    public interface OnShakeListener
    {
        public void onShake();
    }


    // mandatory method we need to override. Called when a given sensor's accuracy changes.
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {
        // We are not expecting any accuracy changes in the accelerometer, nor would they really impact this program anyway. Thus, we leave this blank.
    }

    // mandatory method we need to override. Called when a sensor sends an event indicating its value changed.
    @Override
    public void onSensorChanged (SensorEvent event)
    {
        // We are expecting sensor values to change and we are going to react to them, so we implement code here.

        if(listener != null)    // if sensor exists
        {
            // get coordinates from event, removing earth's gravity in the process
            float x = event.values[0] / SensorManager.GRAVITY_EARTH;
            float y = event.values[1] / SensorManager.GRAVITY_EARTH;
            float z = event.values[2] / SensorManager.GRAVITY_EARTH;

            // get the acceleration vector
            float shake = (float) Math.sqrt(x * x + y * y + z * z);

            // if shaking is greating than gravity threshhold, shake is detected.
            if(shake > SHAKE_THRESHOLD_GRAVITY)
            {
                long temp = System.currentTimeMillis();     //temp slot for system time

                // the following code ignores any events that are gotten within half a second (500 ms) of each other.
                // simply put, it just exits if they were too close to each other; onShake is never called.
                if(timeStamp + SHAKE_SLOP_TIME_MS > temp)
                {
                    return;
                }

                timeStamp = temp;       // set current timestamp for reference as of current check

                listener.onShake();     // call the onShake interface
            }
        }
    }
}