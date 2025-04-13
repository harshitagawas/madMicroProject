package com.example.microproject.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.microproject.models.CategoryBook;
import java.util.List;

@Dao
public interface CategoryBookDao {

    @Insert
    long insertCategoryBook(CategoryBook categoryBook);

    @Query("SELECT * FROM category_books")
    List<CategoryBook> getAllCategoryBooks();
    
    @Query("SELECT * FROM category_books WHERE categoryId = :categoryId")
    List<CategoryBook> getCategoryBooksByCategoryId(int categoryId);

    @Update
    void updateCategoryBook(CategoryBook categoryBook);

    @Delete
    void deleteCategoryBook(CategoryBook categoryBook);
    
    @Query("DELETE FROM category_books WHERE id = :bookId")
    void deleteCategoryBookById(int bookId);
    
    @Query("DELETE FROM category_books WHERE categoryId = :categoryId")
    void deleteAllBooksForCategory(int categoryId);
    
    @Query("SELECT * FROM category_books WHERE id = :bookId")
    CategoryBook getCategoryBookById(int bookId);
} 