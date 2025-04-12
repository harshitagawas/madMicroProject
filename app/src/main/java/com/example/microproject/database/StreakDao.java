package com.example.microproject.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.microproject.models.Streak;

@Dao
public interface StreakDao {
    @Insert
    long insertStreak(Streak streak);

    @Update
    void updateStreak(Streak streak);

    @Query("SELECT * FROM streaks ORDER BY id DESC LIMIT 1")
    Streak getLatestStreak();
    
    @Query("DELETE FROM streaks WHERE lastUpdated < :timestamp")
    void deleteOldStreaks(long timestamp);
} 