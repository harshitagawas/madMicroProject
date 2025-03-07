package com.example.microproject.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.microproject.R;
import com.example.microproject.fragments.CategoryBooksFragment;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private final List<String> categories;
    private final FragmentManager fragmentManager;

    public CategoryAdapter(List<String> categories, FragmentManager fragmentManager) {
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
        holder.categoryName.setText(categories.get(position));

        // Set click listener on the shelf
        holder.shelf.setOnClickListener(v -> {
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, new CategoryBooksFragment()) // R.id.fragment_container is the container where fragments are swapped
                    .addToBackStack(null) // Allows back navigation
                    .commit();
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
