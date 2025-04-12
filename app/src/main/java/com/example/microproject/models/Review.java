package com.example.microproject.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import com.example.microproject.database.Converters;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity(tableName = "reviews")
@TypeConverters(Converters.class)
public class Review implements Serializable {
    @PrimaryKey
    @NonNull
    private String id;
    private long timestamp;
    private String title;
    private String bookId;
    private String reviewImagePath; // Path to the saved review image
    private List<ReviewElement> elements; // Elements that make up the review
    private float rating; // 0-5 stars

    public Review() {
        this.id = UUID.randomUUID().toString();
        this.timestamp = System.currentTimeMillis();
        this.elements = new ArrayList<>();
        this.rating = 0f;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
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

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        if (rating >= 0 && rating <= 5) {
            this.rating = rating;
        }
    }
}
