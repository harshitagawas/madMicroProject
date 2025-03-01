package com.example.microproject.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.microproject.R;
import com.example.microproject.adapters.CategoryAdapter;
import java.util.ArrayList;
import java.util.List;

public class LibraryFragment extends Fragment {

    private List<String> categories = new ArrayList<>();
    private CategoryAdapter adapter;
    private RecyclerView categoryRecyclerView;

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

        // Initialize RecyclerView
        categoryRecyclerView = view.findViewById(R.id.categoryRecyclerView);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new CategoryAdapter(categories);
        categoryRecyclerView.setAdapter(adapter);

        // Initialize "Add New" Button
        View addNewCategory = view.findViewById(R.id.add_new);
        addNewCategory.setOnClickListener(v -> showAddCategoryDialog());
    }

    private void showAddCategoryDialog() {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_layout);

        EditText categoryInput = dialog.findViewById(R.id.categoryInput);
        Button addCategoryButton = dialog.findViewById(R.id.addCategoryButton);

        addCategoryButton.setOnClickListener(v -> {
            String categoryName = categoryInput.getText().toString().trim();
            if (!categoryName.isEmpty()) {
                categories.add(categoryName); // Add category to list
                adapter.notifyDataSetChanged(); // Refresh RecyclerView
                dialog.dismiss(); // Close dialog
            }
        });

        dialog.show();
    }
}
