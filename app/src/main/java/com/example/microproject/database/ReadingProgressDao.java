package com.example.microproject.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.microproject.models.ReadingProgress;

import java.util.List;

@Dao
public interface ReadingProgressDao {
    @Insert
    void insertProgress(ReadingProgress progress);

    @Update
    void updateProgress(ReadingProgress progress);

    @Query("SELECT * FROM reading_progress ORDER BY timestamp DESC LIMIT 1")
    ReadingProgress getLatestProgress();

    @Query("SELECT * FROM reading_progress ORDER BY timestamp DESC")
    LiveData<List<ReadingProgress>> getAllProgress();

    @Query("DELETE FROM reading_progress WHERE id = :id")
    void deleteProgress(int id);
} 