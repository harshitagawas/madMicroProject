package com.example.microproject.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.microproject.R;
import com.example.microproject.adapters.ReviewAdapter;
import com.example.microproject.models.Review;

import java.util.ArrayList;
import java.util.List;

public class ReviewFragment extends Fragment {

    private RecyclerView reviewRecyclerView;
    private ReviewAdapter reviewAdapter;
    private List<Review> reviewList; // No Gson, just a list

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.review_fragment, container, false);

        reviewRecyclerView = view.findViewById(R.id.reviewRecyclerView);
        reviewRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize the review list
        reviewList = new ArrayList<>();

        reviewAdapter = new ReviewAdapter(reviewList);
        reviewRecyclerView.setAdapter(reviewAdapter);

        Button btnAddReview = view.findViewById(R.id.btnAddReview);
        btnAddReview.setOnClickListener(v -> openReviewEditor());

        return view;
    }

    private void openReviewEditor() {
        // Create and show the ReviewEditorFragment
        ReviewEditorFragment editorFragment = new ReviewEditorFragment();
        editorFragment.setOnReviewSavedListener(review -> {
            // Add the new review to the list
            reviewList.add(review);
            reviewAdapter.notifyItemInserted(reviewList.size() - 1);
        });

        // Replace the fragment
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, editorFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
