package com.example.microproject.models;

import android.net.Uri;

public class CategoryBook {
    private String name;
    private Uri imageUri;

    public CategoryBook(String name, Uri imageUri) {
        this.name = name;
        this.imageUri = imageUri;
    }

    public String getName() {
        return name;
    }

    public Uri getImageUri() {
        return imageUri;
    }
}
