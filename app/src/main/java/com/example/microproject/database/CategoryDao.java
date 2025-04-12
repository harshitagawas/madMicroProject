package com.example.microproject.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.microproject.models.Category;
import java.util.List;

@Dao
public interface CategoryDao {

    @Insert
    long insertCategory(Category category);

    @Query("SELECT * FROM categories")
    List<Category> getAllCategories();

    @Update
    void updateCategory(Category category);

    @Query("DELETE FROM categories WHERE id = :categoryId")
    void deleteCategory(int categoryId);
    
    @Query("SELECT * FROM categories WHERE id = :categoryId")
    Category getCategoryById(int categoryId);
} 