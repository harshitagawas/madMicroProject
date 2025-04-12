package com.example.microproject.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.microproject.models.Quote;
import java.util.List;

@Dao
public interface QuoteDao {
    @Insert
    long insertQuote(Quote quote);

    @Delete
    void deleteQuote(Quote quote);

    @Query("SELECT * FROM quotes ORDER BY timestamp DESC")
    List<Quote> getAllQuotes();
    
    @Query("SELECT * FROM quotes WHERE id = :quoteId")
    Quote getQuoteById(int quoteId);
} 