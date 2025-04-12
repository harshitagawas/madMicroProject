package com.example.microproject.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.microproject.R;
import com.example.microproject.models.Library;

import java.util.List;

public class LibraryAdapter extends RecyclerView.Adapter<LibraryAdapter.LibraryViewHolder> {

    private final List<Library> libraries;
    private final FragmentManager fragmentManager;

    public LibraryAdapter(List<Library> libraries, FragmentManager fragmentManager) {
        this.libraries = libraries;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public LibraryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.library_item, parent, false);
        return new LibraryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LibraryViewHolder holder, int position) {
        Library library = libraries.get(position);
        holder.libraryName.setText(library.getName());
        holder.libraryDescription.setText(library.getDescription());

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            // You can navigate to a detail fragment or perform other actions
            // Similar to what the CategoryAdapter does
            /*
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, LibraryDetailFragment.newInstance(library.getId()))
                    .addToBackStack(null)
                    .commit();
            */
        });
    }

    @Override
    public int getItemCount() {
        return libraries.size();
    }

    public static class LibraryViewHolder extends RecyclerView.ViewHolder {
        TextView libraryName;
        TextView libraryDescription;

        public LibraryViewHolder(@NonNull View itemView) {
            super(itemView);
            libraryName = itemView.findViewById(R.id.library_name);
            libraryDescription = itemView.findViewById(R.id.library_description);
        }
    }
} 