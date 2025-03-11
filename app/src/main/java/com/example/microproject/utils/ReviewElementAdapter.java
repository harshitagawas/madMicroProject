package com.example.microproject.utils;

import com.example.microproject.models.ReviewElement;
import com.example.microproject.models.TextElement; // Add other subclasses when needed
import com.google.gson.*;

import java.lang.reflect.Type;

public class ReviewElementAdapter implements JsonDeserializer<ReviewElement>, JsonSerializer<ReviewElement> {
    @Override
    public ReviewElement deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        if (jsonObject.has("text")) { // Check for a property unique to a subclass
            return context.deserialize(jsonObject, TextElement.class);
        }

        throw new JsonParseException("Unknown subtype of ReviewElement");
    }

    @Override
    public JsonElement serialize(ReviewElement src, Type typeOfSrc, JsonSerializationContext context) {
        return context.serialize(src);
    }
}
