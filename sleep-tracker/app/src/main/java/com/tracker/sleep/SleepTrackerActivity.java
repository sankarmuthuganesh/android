package com.tracker.sleep;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

public class SleepTrackerActivity extends AppCompatActivity {

    private static final int REQUEST_LOCATION = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_tracker);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        } else {
            Log.i("PERMISSION", "Location Permission Already Granted" +
                    ", ***...***...***Monitoring Started***...***...***");
            monitorSleep();
        }
    }

    @Override

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode == REQUEST_LOCATION) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i("PERMISSION", ", ***...***...***Monitoring Started***...***...***");
                monitorSleep();
            }else{
                this.finish();
            }
        }
    }

    private void monitorSleep(){
        // Montoring Constraints
        Constraints monitorRequirements = new Constraints.Builder()
                //.setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .setRequiresStorageNotLow(true)
                .build();

        // Sensor Data Monitoring Work
        PeriodicWorkRequest lifeStyleMonitor = new PeriodicWorkRequest
                .Builder(SensorMonitor.class, 30, TimeUnit.MINUTES)
                .addTag("SENSOR_MONITOR").setConstraints(monitorRequirements).build();

        // Location Changes Monitoring Work
        LocationApi.context = this;
        PeriodicWorkRequest locationMonitor = new PeriodicWorkRequest
                .Builder(LocationApi.class, 3, TimeUnit.HOURS)
                .addTag("LOCATION_MONITOR").setConstraints(monitorRequirements).build();

        WorkManager.getInstance().cancelAllWorkByTag("SENSOR_MONITOR");
        WorkManager.getInstance().cancelAllWorkByTag("LOCATION_MONITOR");

        WorkManager.getInstance().enqueueUniquePeriodicWork("sensorMonitor",
                ExistingPeriodicWorkPolicy.REPLACE, lifeStyleMonitor);
        WorkManager.getInstance().enqueueUniquePeriodicWork("locationMonitor",
                ExistingPeriodicWorkPolicy.REPLACE, locationMonitor);

        Toast.makeText(this, "Started Monitoring Your Sleep!!!", Toast.LENGTH_SHORT).show();
    }

}
