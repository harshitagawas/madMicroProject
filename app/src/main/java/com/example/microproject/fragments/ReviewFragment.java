package com.example.microproject.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.microproject.R;
import com.example.microproject.adapters.ReviewAdapter;
import com.example.microproject.database.AppDatabase;
import com.example.microproject.models.Review;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReviewFragment extends Fragment {

    private RecyclerView reviewRecyclerView;
    private ReviewAdapter reviewAdapter;
    private List<Review> reviewList;
    private AppDatabase database;
    private ExecutorService executorService;
    private Spinner sortSpinner;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = AppDatabase.getInstance(requireContext());
        executorService = Executors.newSingleThreadExecutor();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.review_fragment, container, false);

        reviewRecyclerView = view.findViewById(R.id.reviewRecyclerView);
        reviewRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        sortSpinner = view.findViewById(R.id.sortSpinner);
        setupSortSpinner();

        reviewList = new ArrayList<>();
        reviewAdapter = new ReviewAdapter(reviewList);
        reviewRecyclerView.setAdapter(reviewAdapter);

        // Set up click listeners
        reviewAdapter.setOnReviewClickListener(this::openReviewEditor);
        reviewAdapter.setOnReviewDeleteListener(this::confirmDeleteReview);

        Button btnAddReview = view.findViewById(R.id.btnAddReview);
        btnAddReview.setOnClickListener(v -> openReviewEditor(null));

        // Load reviews from database
        loadReviews();

        return view;
    }

    private void setupSortSpinner() {
        String[] sortOptions = {"Newest First", "Oldest First", "Highest Rated", "Lowest Rated"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, sortOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(adapter);
        sortSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                sortReviews(position);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
    }

    private void sortReviews(int sortOption) {
        switch (sortOption) {
            case 0: // Newest First
                reviewList.sort((r1, r2) -> Long.compare(r2.getTimestamp(), r1.getTimestamp()));
                break;
            case 1: // Oldest First
                reviewList.sort((r1, r2) -> Long.compare(r1.getTimestamp(), r2.getTimestamp()));
                break;
            case 2: // Highest Rated
                reviewList.sort((r1, r2) -> Float.compare(r2.getRating(), r1.getRating()));
                break;
            case 3: // Lowest Rated
                reviewList.sort((r1, r2) -> Float.compare(r1.getRating(), r2.getRating()));
                break;
        }
        reviewAdapter.notifyDataSetChanged();
    }

    private void loadReviews() {
        executorService.execute(() -> {
            List<Review> reviews = database.reviewDao().getAllReviews();
            requireActivity().runOnUiThread(() -> {
                reviewList.clear();
                reviewList.addAll(reviews);
                // Apply current sort
                sortReviews(sortSpinner.getSelectedItemPosition());
            });
        });
    }

    private void openReviewEditor(Review review) {
        ReviewEditorFragment editorFragment;
        if (review != null) {
            editorFragment = ReviewEditorFragment.newInstance(review.getId());
        } else {
            editorFragment = new ReviewEditorFragment();
        }

        editorFragment.setOnReviewSavedListener(savedReview -> {
            executorService.execute(() -> {
                if (review != null) {
                    database.reviewDao().updateReview(savedReview);
                } else {
                    database.reviewDao().insertReview(savedReview);
                }
                loadReviews();
                
                // Show success message
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "Review saved successfully!", Toast.LENGTH_SHORT).show();
                });
            });
        });

        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, editorFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void confirmDeleteReview(Review review) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Review")
                .setMessage("Are you sure you want to delete this review?")
                .setPositiveButton("Delete", (dialog, which) -> deleteReview(review))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteReview(Review review) {
        executorService.execute(() -> {
            database.reviewDao().deleteReview(review);
            loadReviews();
            
            // Show success message
            requireActivity().runOnUiThread(() -> {
                Toast.makeText(requireContext(), "Review deleted successfully!", Toast.LENGTH_SHORT).show();
            });
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
