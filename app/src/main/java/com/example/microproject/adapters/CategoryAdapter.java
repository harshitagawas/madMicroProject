package com.example.microproject.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.microproject.R;
import com.example.microproject.fragments.CategoryBooksFragment;
import com.example.microproject.models.Category;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private final List<Category> categories;
    private final FragmentManager fragmentManager;

    public CategoryAdapter(List<Category> categories, FragmentManager fragmentManager) {
        this.categories = categories;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_item, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.categoryName.setText(category.getName());

        // Set click listener on the shelf
        holder.shelf.setOnClickListener(v -> {
            try {
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, 
                                 CategoryBooksFragment.newInstance(category.getId()))
                        .addToBackStack(null) // Allows back navigation
                        .commit();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(v.getContext(), 
                              "Error opening category: " + e.getMessage(), 
                              Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName;
        View shelf;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.category_name);
            shelf = itemView.findViewById(R.id.shelf);
        }
    }
}