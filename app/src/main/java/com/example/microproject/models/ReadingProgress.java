package com.example.microproject.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "reading_progress")
public class ReadingProgress {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int currentPage;
    private int totalPages;
    private long timestamp;

    public ReadingProgress(int currentPage, int totalPages) {
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.timestamp = System.currentTimeMillis();
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
} 