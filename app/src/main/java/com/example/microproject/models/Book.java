package com.example.microproject.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "books")
public class Book {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;
    private boolean isRead;

    // Constructor
    public Book(String name, boolean isRead) {
        this.name = name;
        this.isRead = isRead;
    }

   // Alternative constructor for backward compatibility
//    public Book(String name) {
//        this.name = name;
//        this.isRead = false;
//    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public boolean isRead() { return isRead; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setRead(boolean read) { isRead = read; }

    @Override
    public String toString() {
        return "Book{id=" + id + ", name='" + name + "', isRead=" + isRead + "}";
    }
}
