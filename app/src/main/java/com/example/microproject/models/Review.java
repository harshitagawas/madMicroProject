package com.example.microproject.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Review implements Serializable {
    private String id;
    private long timestamp;
    private String title;
    private String bookId;
    private String reviewImagePath; // Path to the saved review image
    private List<ReviewElement> elements; // Elements that make up the review

    public Review() {
        this.id = UUID.randomUUID().toString();
        this.timestamp = System.currentTimeMillis();
        this.elements = new ArrayList<>();
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getReviewImagePath() {
        return reviewImagePath;
    }

    public void setReviewImagePath(String reviewImagePath) {
        this.reviewImagePath = reviewImagePath;
    }

    public List<ReviewElement> getElements() {
        return elements;
    }

    public void setElements(List<ReviewElement> elements) {
        this.elements = elements;
    }

    public void addElement(ReviewElement element) {
        this.elements.add(element);
    }
}
