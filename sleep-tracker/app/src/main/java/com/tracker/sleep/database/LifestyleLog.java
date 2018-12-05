package com.tracker.sleep.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.sql.Timestamp;

@Entity
public class LifestyleLog {
    @PrimaryKey
    public Timestamp time;

    @ColumnInfo(name = "location")
    public double[] location;

    @ColumnInfo(name = "accelerometry")
    public double accelerometry;

    @ColumnInfo(name = "walk")
    public double walk;

    @ColumnInfo(name = "drive")
    public double drive;

    @ColumnInfo(name = "usingApps")
    public double usingApps;

}