package com.example.microproject.utils;

import com.example.microproject.models.ReviewElement;
import com.example.microproject.models.TextElement;
import com.example.microproject.models.ImageElement;
import com.google.gson.*;

import java.lang.reflect.Type;

public class ReviewElementAdapter implements JsonDeserializer<ReviewElement>, JsonSerializer<ReviewElement> {
    @Override
    public ReviewElement deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        if (jsonObject.has("text")) {
            return context.deserialize(jsonObject, TextElement.class);
        } else if (jsonObject.has("imagePath")) {
            return context.deserialize(jsonObject, ImageElement.class);
        }

        throw new JsonParseException("Unknown subtype of ReviewElement");
    }

    @Override
    public JsonElement serialize(ReviewElement src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        
        // Add common properties
        jsonObject.addProperty("x", src.getX());
        jsonObject.addProperty("y", src.getY());
        jsonObject.addProperty("width", src.getWidth());
        jsonObject.addProperty("height", src.getHeight());
        jsonObject.addProperty("zIndex", src.getZIndex());
        
        // Add specific properties based on the type
        if (src instanceof TextElement) {
            TextElement textElement = (TextElement) src;
            jsonObject.addProperty("text", textElement.getText());
            jsonObject.addProperty("textSize", textElement.getTextSize());
            jsonObject.addProperty("textColor", textElement.getTextColor());
        } else if (src instanceof ImageElement) {
            ImageElement imageElement = (ImageElement) src;
            jsonObject.addProperty("imagePath", imageElement.getImagePath());
        }
        
        return jsonObject;
    }
}
