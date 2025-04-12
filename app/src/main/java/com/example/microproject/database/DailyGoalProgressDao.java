package com.example.microproject.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.microproject.models.DailyGoalProgress;

import java.util.List;

@Dao
public interface DailyGoalProgressDao {
    @Insert
    void insertProgress(DailyGoalProgress progress);

    @Update
    void updateProgress(DailyGoalProgress progress);

    @Query("SELECT * FROM daily_goal_progress ORDER BY date DESC LIMIT 1")
    DailyGoalProgress getLatestProgress();

    @Query("SELECT * FROM daily_goal_progress ORDER BY date DESC")
    LiveData<List<DailyGoalProgress>> getAllProgress();

    @Query("SELECT * FROM daily_goal_progress WHERE date >= :startDate AND date <= :endDate ORDER BY date DESC")
    LiveData<List<DailyGoalProgress>> getProgressForDateRange(long startDate, long endDate);

    @Query("SELECT * FROM daily_goal_progress WHERE date = :date LIMIT 1")
    DailyGoalProgress getProgressForDate(long date);
} 