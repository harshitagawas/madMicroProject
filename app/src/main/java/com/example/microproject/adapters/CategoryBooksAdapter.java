package com.example.microproject.adapters;

import android.content.Context;
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
            // First try to get the Uri directly
            Uri imageUri = book.getImageUri();
            
            // If that's null, try to parse the string
            if (imageUri == null && book.getImageUriString() != null && !book.getImageUriString().isEmpty()) {
                imageUri = Uri.parse(book.getImageUriString());
                // Update the book's Uri for future use
                book.setImageUri(imageUri);
            }
            
            if (imageUri != null) {
                Log.d(TAG, "Loading image from URI: " + imageUri.toString());
                holder.bookImage.setImageURI(imageUri);
                holder.bookImage.setVisibility(View.VISIBLE);
            } else {
                Log.d(TAG, "No image URI available for book: " + book.getName());
                // Set a default image or hide the ImageView
                holder.bookImage.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading image for book: " + book.getName(), e);
            // Handle image loading error
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
