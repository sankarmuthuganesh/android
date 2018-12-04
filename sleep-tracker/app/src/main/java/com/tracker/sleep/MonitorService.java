package com.tracker.sleep;

import android.app.KeyguardManager;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.display.DisplayManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
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

public class MonitorService extends Worker {

    /* Give the Job a Unique Id */
    private static final int JOB_ID = 1000;
    /** Last Sensor Change Event Time     */
    private static long lastMonitoredTime;
    /** Predicted Sleep Start Time     */
    private static String sleepStartTime  = "21:00:00";
    /** Predicted Sleep End Time     */
    private static String sleepEndtTime  = "06:00:00";

    private Context context;
    /** Current GPS Location of User */
    private static Location userCurrentLocation;
    /** The Android Sensor Manager */
    private SensorManager sensorManager;

    private Sensor sensor;
    /** The GoogleMap Api to Determine the device location, mode of transportation, if the device is moving,
     * Create and monitor predefined geographical regions, known as geofences,
     * Listen for location changes.
    */
//    private GoogleMap googleMap;
//    MonitorService(){
//
//    }
    MonitorService(@NonNull Context appContext, @NonNull WorkerParameters workerParams){
        super(appContext, workerParams);
    }

//    public static void enqueueWork(Context ctx, Intent intent) {
//        enqueueWork(ctx, MonitorService.class, JOB_ID, intent);
//    }
@NonNull
@Override
public Result doWork() {
    Log.i("Monitor***************************** isAudioPlaying: ", String.valueOf(isAudioPlaying()));
    registerSensorListeners();
//        locationMonitor();
    isScreenActive();

    long delay = TimeUnit.SECONDS.toMillis(10);
    Timer timer = new Timer();
    timer.schedule(new TimerTask()
    {
        public void run()
        {
            sensorManager.unregisterListener(sensorListener);
        }
    }, delay);

    return Result.SUCCESS;
}
//    @Override
//    protected void onHandleWork(Intent workIntent) {
//        Log.i("Monitor***************************** isAudioPlaying: ", String.valueOf(isAudioPlaying()));
//        registerSensorListeners();
////        locationMonitor();
//        isScreenActive();
//    }

    /** Listener for all Phone's Motion Sensor */
    private SensorEventListener sensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            long currentTime = event.timestamp;
//            if(currentTime - lastMonitoredTime < 200){
                if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
                    float[] values = event.values;
                    float x = values[0];
                    float y = values[1];
                    float z = values[2];
                    float accelerationSquareRoot = (x*x + y*y + z*z)/ (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
                    if(accelerationSquareRoot >= 1.2){
                        Log.i("Monitor***************************** Type: ", String.valueOf(event.sensor.getName()));
                        Log.i("Monitor***************************** Value: ", String.valueOf(accelerationSquareRoot));
                        // changes happened
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
                lastMonitoredTime = currentTime;
//            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int var) {
            Log.i("Accuracy Changed: ", String.valueOf(sensor.toString()));
        }

        };

    // Defining a listener that responds to location updates
    public static final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            // Called when a new location is found by the network location provider.
            userCurrentLocation = location;
//            toast("Location Changed: "+userCurrentLocation);
        }
        public void onStatusChanged(String provider, int status, Bundle extras) {}
        public void onProviderEnabled(String provider) {}
        public void onProviderDisabled(String provider) {}
    };

    private void locationMonitor(){
        LocationManager locationManager = (LocationManager) getApplicationContext()
                .getSystemService(Context.LOCATION_SERVICE);

//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
//                PackageManager.PERMISSION_GRANTED && ActivityCompat
//                .checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
//                PackageManager.PERMISSION_GRANTED) {
//            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
//            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//            Log.i("Monitor***************************** Location: ", String.valueOf(lastKnownLocation));
//            lastKnownLocation.distanceTo(lastKnownLocation);
//            return lastKnownLocation;
//        }
//        googleMap.setMyLocationEnabled(true);
//        Location currentLocation = LocationServices.FusedLocationApi.getLastLocation(googleMap);

//      else{
//            return null;
//        }
    }

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

//    final Handler mHandler = new Handler();
//
//    // Helper for showing tests
//    void toast(final CharSequence text) {
//        mHandler.post(new Runnable() {
//            @Override public void run() {
//                Toast.makeText(MonitorService.this, text, Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
}
