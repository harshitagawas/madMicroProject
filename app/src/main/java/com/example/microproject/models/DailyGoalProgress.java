package com.example.microproject.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "daily_goal_progress")
public class DailyGoalProgress {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int completedGoals;
    private int totalGoals;
    private int requiredGoalsCompleted;
    private int totalRequiredGoals;
    private long date;
    private int streakDays;

    public DailyGoalProgress(int completedGoals, int totalGoals, int requiredGoalsCompleted, int totalRequiredGoals, int streakDays) {
        this.completedGoals = completedGoals;
        this.totalGoals = totalGoals;
        this.requiredGoalsCompleted = requiredGoalsCompleted;
        this.totalRequiredGoals = totalRequiredGoals;
        this.streakDays = streakDays;
        this.date = System.currentTimeMillis();
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCompletedGoals() {
        return completedGoals;
    }

    public void setCompletedGoals(int completedGoals) {
        this.completedGoals = completedGoals;
    }

    public int getTotalGoals() {
        return totalGoals;
    }

    public void setTotalGoals(int totalGoals) {
        this.totalGoals = totalGoals;
    }

    public int getRequiredGoalsCompleted() {
        return requiredGoalsCompleted;
    }

    public void setRequiredGoalsCompleted(int requiredGoalsCompleted) {
        this.requiredGoalsCompleted = requiredGoalsCompleted;
    }

    public int getTotalRequiredGoals() {
        return totalRequiredGoals;
    }

    public void setTotalRequiredGoals(int totalRequiredGoals) {
        this.totalRequiredGoals = totalRequiredGoals;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getStreakDays() {
        return streakDays;
    }

    public void setStreakDays(int streakDays) {
        this.streakDays = streakDays;
    }
} 