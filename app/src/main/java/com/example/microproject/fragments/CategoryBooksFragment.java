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
        
        // Initialize image picker launcher
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        if (imagePreview != null && selectedImageUri != null) {
                            imagePreview.setImageURI(selectedImageUri);
                            imagePreview.setVisibility(View.VISIBLE);
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
        
        builder.setView(dialogView)
               .setTitle("Add New Book")
               .setPositiveButton("Add", (dialog, which) -> {
                   String bookName = bookNameInput.getText().toString().trim();
                   if (!bookName.isEmpty()) {
                       CategoryBook book;
                       if (selectedImageUri != null) {
                           book = new CategoryBook(bookName, selectedImageUri);
                       } else {
                           book = new CategoryBook(bookName, (String) null, categoryId);
                       }
                       book.setCategoryId(categoryId);
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
