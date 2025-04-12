package com.example.microproject.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "libraries")
public class Library {
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    private String name;
    private String description;
    
    // Constructor
    public Library(String name, String description) {
        this.name = name;
        this.description = description;
    }
    
    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    
    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    
    @Override
    public String toString() {
        return "Library{id=" + id + ", name='" + name + "', description='" + description + "'}";
    }
} 