package com.tracker.sleep;

import android.app.KeyguardManager;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.display.DisplayManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Display;

import com.google.common.collect.ImmutableSet;

import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class SensorMonitor extends Worker {

    /** The Android Sensor Manager */
    private SensorManager sensorManager;

    private Sensor sensor;

    SensorMonitor(@NonNull Context appContext, @NonNull WorkerParameters workerParams){
        super(appContext, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.i("Monitor***************************** isAudioPlaying: ", String.valueOf(isAudioPlaying()));
        registerSensorListeners();
        isScreenActive();

        long delay = TimeUnit.MINUTES.toMillis(10);
        Timer timer = new Timer();
        timer.schedule(new TimerTask(){
        public void run() {
            sensorManager.unregisterListener(sensorListener);
        }
        }, delay);

        return Result.SUCCESS;
    }


    /** Listener for all Phone's Motion Sensor */
    private SensorEventListener sensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
                if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
                    float[] values = event.values;
                    float x = values[0];
                    float y = values[1];
                    float z = values[2];
                    float accelerationSquareRoot = (x*x + y*y + z*z)/ (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
                    if(accelerationSquareRoot >= 1.2){
                        Log.i("Monitor***************************** Type: ", String.valueOf(event.sensor.getName()));
                        Log.i("Monitor***************************** Value: ", String.valueOf(accelerationSquareRoot));
                    }
                }
                if(event.sensor.getType() == Sensor.TYPE_STEP_COUNTER){
                    // the number of steps taken by the user since the last reboot while the sensor was activated
                    Sensor sensor = event.sensor;
                    float[] values = event.values;
                    int value = -1;

                    if (values.length > 0) {
                        value = (int) values[0];
                    }
                    // walking
                    Log.i("Monitor***************************** Type: ", String.valueOf(event.sensor.getName()));
                }
                if(event.sensor.getType() == Sensor.TYPE_PROXIMITY){
                    // in a call
                    Log.i("Monitor***************************** Type: ", String.valueOf(event.sensor.getName()));
                }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int var) {
            Log.i("Accuracy Changed: ", String.valueOf(sensor.toString()));
        }

        };

    private boolean isScreenActive(){
        KeyguardManager kgMgr = (KeyguardManager) getApplicationContext().getSystemService(Context.KEYGUARD_SERVICE);
        boolean isPhoneLocked = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && kgMgr.isKeyguardLocked())
                || (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN && kgMgr.inKeyguardRestrictedInputMode());
        Log.i("Monitor***************************** isPhoneLocked: ", String.valueOf(isPhoneLocked));
        boolean screenOn = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            DisplayManager dm = (DisplayManager) getApplicationContext().getSystemService(Context.DISPLAY_SERVICE);
            for (Display display : dm.getDisplays()) {
                if (display.getState() != Display.STATE_OFF) {
                    screenOn = true;
                }
            }
            Log.i("Monitor***************************** screenOn: ", String.valueOf(screenOn));
            // screenOn;
        } else {
            PowerManager powerManager = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
            boolean  isScreenAwake = (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT_WATCH?
                    powerManager.isScreenOn():powerManager.isInteractive());
            Log.i("Monitor***************************** isScreenAwake: ", String.valueOf(isScreenAwake));
        }

        return false;
    }

    public boolean isAudioPlaying(){
        AudioManager manager = (AudioManager)getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        if(manager.isMusicActive()){
            return true;
        }else{
            return false;
        }
    }
    public void registerSensorListeners(){
        sensorManager = (SensorManager) getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
        Set<Integer> requiredSensors = ImmutableSet.of(Sensor.TYPE_ACCELEROMETER, Sensor.TYPE_STEP_COUNTER,
                Sensor.TYPE_PROXIMITY);
        for(int sensorType : requiredSensors){
            sensor = sensorManager.getDefaultSensor(sensorType);
            sensorManager.registerListener(sensorListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

}
