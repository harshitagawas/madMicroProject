package com.example.microproject.models;

public class Book {
    private String name;
    private boolean isRead;

    // Constructor
    public Book(String name) {
        this.name = name;
        this.isRead = false; // Default: book is unread
    }

    // Getters
    public String getName() { return name; }
    public boolean isRead() { return isRead; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setRead(boolean read) { isRead = read; }

    @Override
    public String toString() {
        return "Book{name='" + name + "', isRead=" + isRead + "}";
    }
}
