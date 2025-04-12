package com.example.microproject.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.microproject.models.Library;
import java.util.List;

@Dao
public interface LibraryDao {

    @Insert
    void insertLibrary(Library library);

    @Query("SELECT * FROM libraries")
    List<Library> getAllLibraries();

    @Update
    void updateLibrary(Library library);

    @Query("DELETE FROM libraries WHERE id = :libraryId")
    void deleteLibrary(int libraryId);
    
    @Query("SELECT * FROM libraries WHERE id = :libraryId")
    Library getLibraryById(int libraryId);
} 