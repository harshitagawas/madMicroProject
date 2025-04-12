package com.example.microproject.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "quotes")
public class Quote {
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    private String quote;
    private long timestamp;
    
    // Constructor
    public Quote(String quote) {
        this.quote = quote;
        this.timestamp = System.currentTimeMillis();
    }
    
    // Getters
    public int getId() { return id; }
    public String getQuote() { return quote; }
    public long getTimestamp() { return timestamp; }
    
    // Setters
    public void setId(int id) { this.id = id; }
    public void setQuote(String quote) { this.quote = quote; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
