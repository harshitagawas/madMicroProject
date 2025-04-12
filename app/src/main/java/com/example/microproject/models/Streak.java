package com.example.microproject.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "streaks")
public class Streak {
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    private int streakCount;
    private long lastUpdated;
    
    // Constructor
    public Streak(int streakCount, long lastUpdated) {
        this.streakCount = streakCount;
        this.lastUpdated = lastUpdated;
    }
    
    // Getters
    public int getId() { return id; }
    public int getStreakCount() { return streakCount; }
    public long getLastUpdated() { return lastUpdated; }
    
    // Setters
    public void setId(int id) { this.id = id; }
    public void setStreakCount(int streakCount) { this.streakCount = streakCount; }
    public void setLastUpdated(long lastUpdated) { this.lastUpdated = lastUpdated; }
} 