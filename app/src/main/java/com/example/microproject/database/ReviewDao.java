package com.example.microproject.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.microproject.models.Review;
import java.util.List;

@Dao
public interface ReviewDao {
    @Insert
    void insertReview(Review review);

    @Update
    void updateReview(Review review);

    @Delete
    void deleteReview(Review review);

    @Query("SELECT * FROM reviews ORDER BY timestamp DESC")
    List<Review> getAllReviews();

    @Query("SELECT * FROM reviews WHERE bookId = :bookId ORDER BY timestamp DESC")
    List<Review> getReviewsByBookId(String bookId);

    @Query("SELECT * FROM reviews WHERE id = :reviewId")
    Review getReviewById(String reviewId);
} 