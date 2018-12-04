package com.tracker.sleep;

import android.content.Context;
import android.support.annotation.NonNull;

import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class SensorData extends Worker{

    SensorData(@NonNull Context appContext, @NonNull WorkerParameters workerParams){
        super(appContext, workerParams);
    }

    private static final String TAG = "SensorData";
    @NonNull
    @Override
    public Result doWork() {

        Data sleepTimeGuessed = new Data.Builder()
                .putString("sleepStartTime", "")
                .putString("sleepEndTime", "")
                .build();
        setOutputData(sleepTimeGuessed);

        return Result.SUCCESS;
    }
}
