package com.tracker.sleep.database;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface LifestyleDao {

    @Query("SELECT * FROM lifestylelog")
    List<LifestyleLog> getAll();

    @Insert
    void insertAll(LifestyleLog... users);

    @Delete
    void delete(LifestyleLog user);
}

