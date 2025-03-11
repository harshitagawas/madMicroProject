package com.example.microproject.models;

import android.content.Context;
import android.view.View;

import java.io.Serializable;

public abstract class ReviewElement implements Serializable {
    protected float x;
    protected float y;
    protected float width;
    protected float height;
    protected int zIndex;

    public ReviewElement(float x, float y) {
        this.x = x;
        this.y = y;
        this.zIndex = 0;
    }

    // Getters and setters
    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public int getZIndex() {
        return zIndex;
    }

    public void setZIndex(int zIndex) {
        this.zIndex = zIndex;
    }

    // Abstract methods to be implemented by subclasses
    public abstract View createView(Context context);
    public abstract void updateViewPosition(View view);
}
