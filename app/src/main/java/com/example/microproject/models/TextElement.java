package com.example.microproject.models;

import android.content.Context;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TextElement extends ReviewElement {
    private String text;
    private float textSize;
    private int textColor;

    public TextElement(float x, float y, String text) {
        super(x, y);
        this.text = text;
        this.textSize = 16f; // Default text size
        this.textColor = Color.BLACK; // Default text color
    }

    // Getters and setters
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    @Override
    public View createView(Context context) {
        TextView textView = new TextView(context);
        textView.setText(text);
        textView.setTextSize(textSize);
        textView.setTextColor(textColor);

        // Set the layout parameters
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = (int) x;
        params.topMargin = (int) y;
        textView.setLayoutParams(params);

        // Make the text view draggable
        setupDraggable(textView);

        return textView;
    }

    @Override
    public void updateViewPosition(View view) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
        params.leftMargin = (int) x;
        params.topMargin = (int) y;
        view.setLayoutParams(params);
    }

    private void setupDraggable(final View view) {
        view.setOnTouchListener(new View.OnTouchListener() {
            private float dX, dY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        dX = v.getX() - event.getRawX();
                        dY = v.getY() - event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        x = event.getRawX() + dX;
                        y = event.getRawY() + dY;
                        updateViewPosition(v);
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });
    }
}