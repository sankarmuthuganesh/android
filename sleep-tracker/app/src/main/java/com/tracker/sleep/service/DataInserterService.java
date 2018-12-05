package com.tracker.sleep.service;

import android.arch.persistence.room.Room;
import android.content.Context;

import com.tracker.sleep.database.InAppDatabase;

public class DataInserterService {

    InAppDatabase database;

    DataInserterService(Context context){
        database = Room.databaseBuilder(context,
                InAppDatabase.class, "database-name").build();
    }
    public boolean insert(){

        return true;
    }
}
