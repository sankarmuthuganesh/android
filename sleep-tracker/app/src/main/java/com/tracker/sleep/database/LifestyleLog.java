package com.tracker.sleep.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class LifestyleLog {
    @PrimaryKey
    public int time;

    @ColumnInfo(name = "first_name")
    public double[] location;

    @ColumnInfo(name = "last_name")
    public String lastName;
}