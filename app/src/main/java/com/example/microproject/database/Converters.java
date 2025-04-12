package com.example.microproject.database;

import androidx.room.TypeConverter;
import com.example.microproject.models.ReviewElement;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;

public class Converters {
    @TypeConverter
    public static String fromReviewElementList(List<ReviewElement> elements) {
        if (elements == null) {
            return null;
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<ReviewElement>>() {}.getType();
        return gson.toJson(elements, type);
    }

    @TypeConverter
    public static List<ReviewElement> toReviewElementList(String elementsString) {
        if (elementsString == null) {
            return null;
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<ReviewElement>>() {}.getType();
        return gson.fromJson(elementsString, type);
    }
} 