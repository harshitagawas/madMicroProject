package com.example.microproject.models;

public class Book {
    private String name;
    private boolean isRead;

    public Book(String name) {
        this.name = name;
        this.isRead = false;
    }

    public String getName() { return name; }
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
}
