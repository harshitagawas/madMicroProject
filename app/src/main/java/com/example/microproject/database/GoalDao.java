package com.example.microproject.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.microproject.models.Goal;

import java.util.List;

@Dao
public interface GoalDao {
    @Insert
    void insertGoal(Goal goal);

    @Update
    void updateGoal(Goal goal);

    @Delete
    void deleteGoal(Goal goal);

    @Query("SELECT * FROM goals ORDER BY timestamp DESC")
    LiveData<List<Goal>> getAllGoals();

    @Query("SELECT * FROM goals ORDER BY timestamp DESC")
    List<Goal> getAllGoalsSync();

    @Query("SELECT * FROM goals WHERE isCompleted = 1 ORDER BY timestamp DESC")
    LiveData<List<Goal>> getCompletedGoals();

    @Query("SELECT * FROM goals WHERE isRequired = 1 ORDER BY timestamp DESC")
    LiveData<List<Goal>> getRequiredGoals();

    @Query("DELETE FROM goals")
    void deleteAllGoals();
} 