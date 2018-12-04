package com.tracker.sleep;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

public class SleepTrackerActivity extends AppCompatActivity {
    /** Sensor Data Monitoring Service  */
    private MonitorService monitorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_tracker);
//        monitorService = new MonitorService(this);
//        if (!isMyServiceRunning(monitorService.getClass())) {
//            JobIntentService.enqueueWork(this, monitorService.getClass(), 1000,
//                    new Intent(this, monitorService.getClass()));
//        }
//        startService(new Intent(this, LocationApi.class));
        Toast.makeText(this, "Started Monitoring Your Sleep!!!", Toast.LENGTH_SHORT).show();

        Data source = new Data.Builder()
                .putString("workType", "PeriodicSensorMonitor")
                .build();

        Constraints monitorRequirements = new Constraints.Builder()
//                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .setRequiresStorageNotLow(true)
                .build();

        PeriodicWorkRequest lifeStyleMonitor =new PeriodicWorkRequest
                .Builder(MonitorService.class, 30, TimeUnit.SECONDS)
//                .setInputData(source)
                .setConstraints(monitorRequirements).build();
        PeriodicWorkRequest locationMonitor =new PeriodicWorkRequest
                .Builder(LocationApi.class, 30, TimeUnit.SECONDS)
//                .setInputData(source)
                .setConstraints(monitorRequirements).build();

        WorkManager.getInstance().cancelAllWorkByTag("");
//        List<WorkRequest> sensorsToMonitor = Arrays.asList(lifeStyleMonitor, locationMonitor);
        WorkManager.getInstance().enqueueUniquePeriodicWork("actionSensors",
                ExistingPeriodicWorkPolicy.REPLACE, lifeStyleMonitor);
        WorkManager.getInstance().enqueueUniquePeriodicWork("locationTracker",
                ExistingPeriodicWorkPolicy.REPLACE, locationMonitor);
//        WorkManager.getInstance().enqueueUniquePeriodicWork(tag, ExistingPeriodicWorkPolicy.KEEP , request);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("isMyServiceRunning?", true+"");
                return true;
            }
        }
        Log.i ("isMyServiceRunning?", false+"");
        return false;
    }


}
