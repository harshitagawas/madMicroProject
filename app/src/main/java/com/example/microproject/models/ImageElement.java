package com.example.microproject.models;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class ImageElement extends ReviewElement {
    private String imagePath;

    public ImageElement(float x, float y, String imagePath) {
        super(x, y);
        this.imagePath = imagePath;
    }

    // Getters and setters
    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public View createView(Context context) {
        ImageView imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

        // Load the image
        if (imagePath != null && !imagePath.isEmpty()) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
                // Set default width and height based on the bitmap
                width = bitmap.getWidth();
                height = bitmap.getHeight();

                // Scale down large images to a reasonable size
                float maxWidth = 500;
                float maxHeight = 500;

                if (width > maxWidth || height > maxHeight) {
                    float scaleFactor = Math.min(maxWidth / width, maxHeight / height);
                    width = width * scaleFactor;
                    height = height * scaleFactor;
                }
            }
        }

        // Set the layout parameters
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                width > 0 ? (int) width : 200,
                height > 0 ? (int) height : 200);
        params.leftMargin = (int) x;
        params.topMargin = (int) y;
        imageView.setLayoutParams(params);

        // Make the image view draggable
        setupDraggable(imageView);

        return imageView;
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
            private int lastAction;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        dX = v.getX() - event.getRawX();
                        dY = v.getY() - event.getRawY();
                        v.bringToFront(); // Bring the view to front when touched
                        lastAction = MotionEvent.ACTION_DOWN;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        x = event.getRawX() + dX;
                        y = event.getRawY() + dY;
                        updateViewPosition(v);
                        lastAction = MotionEvent.ACTION_MOVE;
                        break;
                    case MotionEvent.ACTION_UP:
                        // If it was just a tap (not a drag), trigger click listeners
                        if (lastAction == MotionEvent.ACTION_DOWN) {
                            v.performClick();
                        }
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });

        // Handle long clicks for additional options
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // Show options to resize, delete, etc.
                // This can be implemented later
                return true;
            }
        });
    }
}