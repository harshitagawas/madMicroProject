package com.example.microproject.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.microproject.R;
import com.example.microproject.models.CategoryBook;

import java.io.File;
import java.util.List;

public class CategoryBooksAdapter extends RecyclerView.Adapter<CategoryBooksAdapter.CategoryBooksViewHolder> {
    private static final String TAG = "CategoryBooksAdapter";
    private final List<CategoryBook> categoryBooks;
    private final Context context;

    public CategoryBooksAdapter(Context context, List<CategoryBook> categoryBooks) {
        this.context = context;
        this.categoryBooks = categoryBooks;
    }

    @NonNull
    @Override
    public CategoryBooksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.individual_book, parent, false);
        return new CategoryBooksViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryBooksViewHolder holder, int position) {
        CategoryBook book = categoryBooks.get(position);
        holder.bookTitle.setText(book.getName());

        try {
            // Log the book details for debugging
            Log.d(TAG, "Binding book: " + book.getName() + ", Image path: " + book.getImageUriString());
            
            // Check if we have a file path
            String imagePath = book.getImageUriString();
            if (imagePath != null && !imagePath.isEmpty()) {
                // Try to load the image from the file path
                File imageFile = new File(imagePath);
                if (imageFile.exists()) {
                    Log.d(TAG, "Loading image from file: " + imagePath);
                    Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                    if (bitmap != null) {
                        holder.bookImage.setImageBitmap(bitmap);
                        holder.bookImage.setVisibility(View.VISIBLE);
                    } else {
                        Log.e(TAG, "Failed to decode bitmap from file: " + imagePath);
                        holder.bookImage.setVisibility(View.GONE);
                    }
                } else {
                    Log.e(TAG, "Image file does not exist: " + imagePath);
                    holder.bookImage.setVisibility(View.GONE);
                }
            } else {
                Log.d(TAG, "No image path available for book: " + book.getName());
                holder.bookImage.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading image for book: " + book.getName(), e);
            holder.bookImage.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return categoryBooks.size();
    }

    public static class CategoryBooksViewHolder extends RecyclerView.ViewHolder {
        TextView bookTitle;
        ImageView bookImage;

        public CategoryBooksViewHolder(@NonNull View itemView) {
            super(itemView);
            bookTitle = itemView.findViewById(R.id.book_title);
            bookImage = itemView.findViewById(R.id.book_image);
        }
    }
}
