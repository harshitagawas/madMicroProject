package com.example.microproject.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.microproject.R;
import com.example.microproject.models.ImageElement;
import com.example.microproject.models.Review;
import com.example.microproject.models.ReviewElement;
import com.example.microproject.models.TextElement;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReviewEditorFragment extends Fragment {

    private RelativeLayout reviewCanvas;
    private List<ReviewElement> elements;
    private Review currentReview;
    private OnReviewSavedListener onReviewSavedListener;

    public interface OnReviewSavedListener {
        void onReviewSaved(Review review);
    }

    public void setOnReviewSavedListener(OnReviewSavedListener listener) {
        this.onReviewSavedListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.review_editor_layout, container, false);

        reviewCanvas = view.findViewById(R.id.reviewCanvas);
        elements = new ArrayList<>();
        currentReview = new Review();

        // Set up the toolbar
        Toolbar toolbar = view.findViewById(R.id.toolbarReviewEditor);
        if (getActivity() instanceof AppCompatActivity) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Set up buttons
        Button btnAddText = view.findViewById(R.id.btnAddText);
        Button btnAddImage = view.findViewById(R.id.btnAddImage);
        Button btnCancel = view.findViewById(R.id.btnCancel);
        Button btnSave = view.findViewById(R.id.btnSave);

        btnAddText.setOnClickListener(v -> showAddTextDialog());
        btnAddImage.setOnClickListener(v -> showImagePicker());

        btnCancel.setOnClickListener(v -> {
            // Go back to the previous fragment
            getParentFragmentManager().popBackStack();
        });

        btnSave.setOnClickListener(v -> saveReview());

        return view;
    }

    private void showAddTextDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add Text");

        // Set up the input
        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", (dialog, which) -> {
            String text = input.getText().toString();
            if (!text.isEmpty()) {
                addTextElement(text);
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void addTextElement(String text) {
        // Create a new text element at the center of the canvas
        int centerX = reviewCanvas.getWidth() / 2;
        int centerY = reviewCanvas.getHeight() / 2;

        TextElement textElement = new TextElement(centerX, centerY, text);
        elements.add(textElement);
        currentReview.addElement(textElement);

        // Create and add the text view to the canvas
        View textView = textElement.createView(getContext());
        reviewCanvas.addView(textView);
    }

    private void showImagePicker() {
        // Create an intent to pick an image from gallery
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            try {
                String imagePath = getPathFromUri(selectedImage);
                addImageElement(imagePath);
            } catch (Exception e) {
                Toast.makeText(getContext(), "Error loading image", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    private String getPathFromUri(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(column_index);
        cursor.close();
        return path;
    }

    private void addImageElement(String imagePath) {
        // Create a new image element at the center of the canvas
        int centerX = reviewCanvas.getWidth() / 2;
        int centerY = reviewCanvas.getHeight() / 2;

        ImageElement imageElement = new ImageElement(centerX, centerY, imagePath);
        elements.add(imageElement);
        currentReview.addElement(imageElement);

        // Create and add the image view to the canvas
        View imageView = imageElement.createView(getContext());
        reviewCanvas.addView(imageView);
    }

    private void saveReview() {
        // Ask for a title for the review
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Review Title");

        // Set up the input
        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Save", (dialog, which) -> {
            String title = input.getText().toString();
            if (!title.isEmpty()) {
                currentReview.setTitle(title);

                // Save the review canvas as an image
                saveCanvasAsImage();

                // Notify the listener that a review has been saved
                if (onReviewSavedListener != null) {
                    onReviewSavedListener.onReviewSaved(currentReview);
                }

                // Go back to the previous fragment
                getParentFragmentManager().popBackStack();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void saveCanvasAsImage() {
        // Create a bitmap of the review canvas
        Bitmap bitmap = Bitmap.createBitmap(
                reviewCanvas.getWidth(),
                reviewCanvas.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        reviewCanvas.draw(canvas);

        // Save the bitmap to internal storage
        try {
            File directory = new File(getActivity().getFilesDir(), "reviews");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String filename = "review_" + currentReview.getId() + ".png";
            File file = new File(directory, filename);

            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();

            // Set the path to the review image
            currentReview.setReviewImagePath(file.getAbsolutePath());

        } catch (IOException e) {
            Toast.makeText(getContext(), "Error saving review image", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}
