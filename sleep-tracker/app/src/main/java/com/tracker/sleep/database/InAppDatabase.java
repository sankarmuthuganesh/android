package com.tracker.sleep.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {LifestyleLog.class}, version = 1)
public abstract class InAppDatabase extends RoomDatabase {
    public abstract LifestyleDao userDao();
}
