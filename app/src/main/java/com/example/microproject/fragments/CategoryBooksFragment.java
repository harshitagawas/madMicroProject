package com.example.microproject.fragments;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.microproject.R;
import com.example.microproject.adapters.CategoryBooksAdapter;
import com.example.microproject.database.AppDatabase;
import com.example.microproject.models.Category;
import com.example.microproject.models.CategoryBook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CategoryBooksFragment extends Fragment {
    private static final String ARG_CATEGORY_ID = "category_id";
    
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private List<CategoryBook> categoryBooks = new ArrayList<>();
    private CategoryBooksAdapter adapter;
    private RecyclerView recycler;
    private Uri selectedImageUri;
    private ImageView imagePreview;
    private int categoryId;
    private AppDatabase database;
    private ExecutorService executorService;
    private String savedImagePath;
    private static final String TAG = "CategoryBooksFragment";
    
    public static CategoryBooksFragment newInstance(int categoryId) {
        CategoryBooksFragment fragment = new CategoryBooksFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CATEGORY_ID, categoryId);
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            categoryId = getArguments().getInt(ARG_CATEGORY_ID);
        }
        database = AppDatabase.getInstance(requireContext());
        executorService = Executors.newSingleThreadExecutor();
        
        // Initialize image picker launcher
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        if (imagePreview != null && selectedImageUri != null) {
                            imagePreview.setImageURI(selectedImageUri);
                            imagePreview.setVisibility(View.VISIBLE);
                            
                            // Log the selected image URI for debugging
                            Log.d(TAG, "Selected image URI: " + selectedImageUri.toString());
                            
                            // Copy the image to app's private storage
                            savedImagePath = copyImageToPrivateStorage(selectedImageUri);
                            if (savedImagePath != null) {
                                Log.d(TAG, "Image saved to: " + savedImagePath);
                            } else {
                                Log.e(TAG, "Failed to save image");
                            }
                        }
                    }
                });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_category_book_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recycler = view.findViewById(R.id.addBookRecycler);
        recycler.setLayoutManager(new GridLayoutManager(getContext(), 2));

        adapter = new CategoryBooksAdapter(getContext(), categoryBooks);
        recycler.setAdapter(adapter);

        Button btn = view.findViewById(R.id.add_book);
        btn.setOnClickListener(v -> showAddBookDialog());

        // Load category title
        loadCategoryName(view);
        
        // Load books for this category
        loadCategoryBooks();
    }
    
    private void loadCategoryName(View view) {
        TextView categoryTitle = view.findViewById(R.id.category_title);
        if (categoryTitle != null) {
            executorService.execute(() -> {
                try {
                    Category category = database.categoryDao().getCategoryById(categoryId);
                    if (category != null) {
                        requireActivity().runOnUiThread(() -> {
                            categoryTitle.setText("Books in " + category.getName());
                        });
                    } else {
                        requireActivity().runOnUiThread(() -> {
                            categoryTitle.setText("Category Not Found");
                            Toast.makeText(getContext(), "Category not found", Toast.LENGTH_SHORT).show();
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Error loading category: " + e.getMessage(), 
                                      Toast.LENGTH_SHORT).show();
                    });
                }
            });
        }
    }
    
    private void loadCategoryBooks() {
        executorService.execute(() -> {
            try {
                List<CategoryBook> books = database.categoryBookDao().getCategoryBooksByCategoryId(categoryId);
                requireActivity().runOnUiThread(() -> {
                    categoryBooks.clear();
                    categoryBooks.addAll(books);
                    adapter.notifyDataSetChanged();
                });
            } catch (Exception e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Error loading books: " + e.getMessage(), 
                                Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
    
    private void showAddBookDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_book, null);
        
        EditText bookNameInput = dialogView.findViewById(R.id.book_name_input);
        Button selectImageButton = dialogView.findViewById(R.id.select_image_button);
        imagePreview = dialogView.findViewById(R.id.image_preview);
        
        // Reset selected image
        selectedImageUri = null;
        savedImagePath = null;
        if (imagePreview != null) {
            imagePreview.setVisibility(View.GONE);
        }
        
        builder.setView(dialogView)
               .setTitle("Add New Book")
               .setPositiveButton("Add", (dialog, which) -> {
                   String bookName = bookNameInput.getText().toString().trim();
                   if (!bookName.isEmpty()) {
                       CategoryBook book;
                       if (savedImagePath != null) {
                           // Create book with saved image path
                           book = new CategoryBook(bookName, savedImagePath, categoryId);
                       } else {
                           // Create book without image
                           book = new CategoryBook(bookName, (String) null, categoryId);
                       }
                       saveCategoryBook(book);
                   } else {
                       Toast.makeText(getContext(), "Please enter a book name", 
                                     Toast.LENGTH_SHORT).show();
                   }
               })
               .setNegativeButton("Cancel", null);
        
        AlertDialog dialog = builder.create();
        
        selectImageButton.setOnClickListener(v -> pickImage());
        
        dialog.show();
    }
    
    private void saveCategoryBook(CategoryBook categoryBook) {
        executorService.execute(() -> {
            try {
                // Make sure categoryId is set
                categoryBook.setCategoryId(categoryId);
                
                // Ensure imageUriString is set from the Uri if available
                if (categoryBook.getImageUri() != null && 
                    (categoryBook.getImageUriString() == null || categoryBook.getImageUriString().isEmpty())) {
                    categoryBook.setImageUriString(categoryBook.getImageUri().toString());
                }
                
                // Log the image URI string for debugging
                Log.d("CategoryBooksFragment", "Saving book with image URI string: " + categoryBook.getImageUriString());
                
                // Insert the book and get its ID
                long id = database.categoryBookDao().insertCategoryBook(categoryBook);
                categoryBook.setId((int) id);
                
                requireActivity().runOnUiThread(() -> {
                    loadCategoryBooks();
                    Toast.makeText(getContext(), "Book added successfully", 
                                  Toast.LENGTH_SHORT).show();
                });
            } catch (Exception e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Error saving book: " + e.getMessage(), 
                                  Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
    
    private void pickImage() {
        try {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error selecting image: " + e.getMessage(), 
                          Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Copies the selected image to the app's private storage and returns the path
     */
    private String copyImageToPrivateStorage(Uri sourceUri) {
        try {
            // Create a unique filename
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String imageFileName = "BOOK_IMG_" + timeStamp + ".jpg";
            
            // Get the app's private directory
            File storageDir = requireContext().getFilesDir();
            File imageFile = new File(storageDir, imageFileName);
            
            // Copy the image
            ContentResolver contentResolver = requireContext().getContentResolver();
            InputStream inputStream = contentResolver.openInputStream(sourceUri);
            OutputStream outputStream = new FileOutputStream(imageFile);
            
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            
            inputStream.close();
            outputStream.close();
            
            // Return the absolute path to the saved image
            return imageFile.getAbsolutePath();
        } catch (IOException e) {
            Log.e(TAG, "Error copying image: " + e.getMessage(), e);
            return null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}
