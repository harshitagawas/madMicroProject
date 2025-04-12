package com.example.microproject.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "categories")
public class Category {
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    private String name;
    
    // Constructor
    public Category(String name) {
        this.name = name;
    }
    
    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    
    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    
    @Override
    public String toString() {
        return "Category{id=" + id + ", name='" + name + "'}";
    }
} 