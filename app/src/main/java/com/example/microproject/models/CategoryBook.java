package com.example.microproject.models;

import android.net.Uri;
import android.util.Log;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Ignore;

@Entity(tableName = "category_books")
public class CategoryBook {
    private static final String TAG = "CategoryBook";
    
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    private String name;
    private String imageUriString;
    private int categoryId; // Foreign key to associate with a category
    
    @Ignore
    private Uri imageUri; // Transient field, not stored in database
    
    // Constructor for Room
    public CategoryBook(String name, String imageUriString, int categoryId) {
        this.name = name;
        this.imageUriString = imageUriString;
        this.categoryId = categoryId;
    }
    
    // Constructor for UI with Uri (annotated with @Ignore so Room doesn't use it)
    @Ignore
    public CategoryBook(String name, Uri imageUri) {
        this.name = name;
        this.imageUri = imageUri;
        this.imageUriString = imageUri != null ? imageUri.toString() : null;
    }
    
    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getImageUriString() { return imageUriString; }
    public int getCategoryId() { return categoryId; }
    
    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setImageUriString(String imageUriString) { this.imageUriString = imageUriString; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
    
    // Helper methods for Uri conversion
    public Uri getImageUri() {
        try {
            if (imageUri == null && imageUriString != null && !imageUriString.isEmpty()) {
                imageUri = Uri.parse(imageUriString);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing URI: " + imageUriString, e);
        }
        return imageUri;
    }
    
    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
        this.imageUriString = imageUri != null ? imageUri.toString() : null;
    }
}
