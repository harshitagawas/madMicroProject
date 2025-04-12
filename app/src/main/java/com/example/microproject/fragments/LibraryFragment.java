package com.example.microproject.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.microproject.R;
import com.example.microproject.adapters.CategoryAdapter;
import com.example.microproject.database.AppDatabase;
import com.example.microproject.models.Category;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LibraryFragment extends Fragment {

    private List<Category> categories = new ArrayList<>();
    private CategoryAdapter categoryAdapter;
    private RecyclerView categoryRecyclerView;
    private AppDatabase database;
    private ExecutorService executorService;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = AppDatabase.getInstance(requireContext());
        executorService = Executors.newSingleThreadExecutor();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.library_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Update title if needed
        TextView categoriesTitle = view.findViewById(R.id.categoriesTitle);
        if (categoriesTitle != null) {
            categoriesTitle.setText("Your Categories");
        }

        // Initialize Category RecyclerView
        categoryRecyclerView = view.findViewById(R.id.categoryRecyclerView);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        categoryAdapter = new CategoryAdapter(categories, getParentFragmentManager());
        categoryRecyclerView.setAdapter(categoryAdapter);

        // Initialize "Add Category" Button
        View addNewCategory = view.findViewById(R.id.add_new);
        if (addNewCategory != null) {
            addNewCategory.setOnClickListener(v -> showAddCategoryDialog());
        }

        // Load categories from database
        loadCategories();
    }
    
    private void loadCategories() {
        executorService.execute(() -> {
            try {
                List<Category> categoryList = database.categoryDao().getAllCategories();
                requireActivity().runOnUiThread(() -> {
                    categories.clear();
                    categories.addAll(categoryList);
                    categoryAdapter.notifyDataSetChanged();
                });
            } catch (Exception e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Error loading categories: " + e.getMessage(), 
                                  Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void showAddCategoryDialog() {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_layout);
        
        // Update dialog title for category
        dialog.setTitle("Add Category");
        
        // Get views
        EditText nameInput = dialog.findViewById(R.id.categoryInput);
        EditText descriptionInput = dialog.findViewById(R.id.descriptionInput);
        Button addButton = dialog.findViewById(R.id.addCategoryButton);
        
        // Hide description field if needed
        if (descriptionInput != null) {
            descriptionInput.setVisibility(View.GONE);
        }
        
        // Change hints and button text
        nameInput.setHint("Category Name");
        addButton.setText("Add Category");
        
        addButton.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            
            if (!name.isEmpty()) {
                // Create and save the new category
                Category category = new Category(name);
                saveCategory(category);
                dialog.dismiss();
            } else {
                Toast.makeText(getContext(), "Please enter a category name", 
                              Toast.LENGTH_SHORT).show();
            }
        });
        
        dialog.show();
    }
    
    private void saveCategory(Category category) {
        executorService.execute(() -> {
            try {
                long id = database.categoryDao().insertCategory(category);
                category.setId((int) id);  // Update the category with the new ID
                requireActivity().runOnUiThread(() -> {
                    loadCategories(); // Reload the list after adding
                    Toast.makeText(getContext(), "Category added successfully", 
                                  Toast.LENGTH_SHORT).show();
                });
            } catch (Exception e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Error saving category: " + e.getMessage(), 
                                  Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}