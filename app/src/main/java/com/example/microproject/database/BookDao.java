package com.example.microproject.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.microproject.models.Book;
import java.util.List;

@Dao
public interface BookDao {

    @Insert
    void insertBook(Book book);

    @Query("SELECT * FROM books")
    List<Book> getAllBooks();

    @Update
    void updateBook(Book book);  // <-- Add this method

    @Query("DELETE FROM books WHERE id = :bookId")
    void deleteBook(int bookId);
}
