package com.example.microproject.fragments;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

import java.util.ArrayList;
import java.util.List;
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

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        if (imagePreview != null) {
                            imagePreview.setImageURI(selectedImageUri);
                        }
                    }
                });
    }
    
    private void loadCategoryName(View view) {
        executorService.execute(() -> {
            Category category = database.categoryDao().getCategoryById(categoryId);
            if (category != null) {
                requireActivity().runOnUiThread(() -> {
                    // Update title if there's a TextView for it
                    TextView titleView = view.findViewById(R.id.category_title);
                    if (titleView != null) {
                        titleView.setText(category.getName() + " Books");
                    }
                });
            }
        });
    }
    
    private void loadCategoryBooks() {
        executorService.execute(() -> {
            List<CategoryBook> books = database.categoryBookDao()
                    .getCategoryBooksByCategoryId(categoryId);
            requireActivity().runOnUiThread(() -> {
                categoryBooks.clear();
                categoryBooks.addAll(books);
                adapter.notifyDataSetChanged();
            });
        });
    }

    private void showAddBookDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add Book");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_category_books, null);
        EditText bookNameInput = dialogView.findViewById(R.id.book_name_input);
        Button selectImageButton = dialogView.findViewById(R.id.select_image_button);
        imagePreview = dialogView.findViewById(R.id.image_preview);

        selectImageButton.setOnClickListener(v -> pickImage());

        builder.setView(dialogView);
        builder.setPositiveButton("Add", (dialog, which) -> {
            String bookName = bookNameInput.getText().toString().trim();
            if (!bookName.isEmpty() && selectedImageUri != null) {
                saveCategoryBook(new CategoryBook(bookName, selectedImageUri));
            } else {
                Toast.makeText(getContext(), "Enter book name and select an image", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
    
    private void saveCategoryBook(CategoryBook categoryBook) {
        // Set the category ID for this book
        categoryBook.setCategoryId(categoryId);
        
        executorService.execute(() -> {
            // Insert the book and get its ID
            long id = database.categoryBookDao().insertCategoryBook(categoryBook);
            
            // Reload the books list
            loadCategoryBooks();
        });
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}
