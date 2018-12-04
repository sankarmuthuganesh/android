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

    @Query("SELECT * FROM lifestylelog WHERE uid IN (:userIds)")
    List<LifestyleLog> loadAllByIds(int[] userIds);

    @Query("SELECT * FROM lifestylelog WHERE first_name LIKE :first AND " +
            "last_name LIKE :last LIMIT 1")
    LifestyleLog findByName(String first, String last);

    @Insert
    void insertAll(LifestyleLog... users);

    @Delete
    void delete(LifestyleLog user);
}

