package com.example.microproject.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.microproject.R;
import com.example.microproject.models.User;

public class ProfileFragment extends Fragment {
    private TextView nameTextView, bioTextView, totalBooksReadTextView, totalBookCurrentTextView, yearlyReadTextView;
    private ImageView editPfp, profileImage;
    private User user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize UI components
        nameTextView = view.findViewById(R.id.username);
        bioTextView = view.findViewById(R.id.bio);
        profileImage = view.findViewById(R.id.profileImage);
        editPfp = view.findViewById(R.id.profileEdit);
        totalBooksReadTextView = view.findViewById(R.id.totalBooksRead);
        totalBookCurrentTextView = view.findViewById(R.id.totalBookCurrent);
        yearlyReadTextView = view.findViewById(R.id.yearlyRead);

        // Initialize user data
        user = new User("Harshita Gawas", "Book lover | Tech enthusiast", 12, 3, 8);
        updateUI();

        // Set click listener for edit profile button
        editPfp.setOnClickListener(v -> {
            // Create new EditProfileFragment instance
            EditProfileFragment editProfileFragment = new EditProfileFragment();

            // Pass user data using Bundle
            Bundle bundle = new Bundle();
            bundle.putParcelable("user", user);
            editProfileFragment.setArguments(bundle);

            // Navigate to EditProfileFragment
            FragmentManager fragmentManager = getParentFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, editProfileFragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    // Update UI with user data
    private void updateUI() {
        nameTextView.setText(user.getName());
        bioTextView.setText(user.getBio());
        totalBooksReadTextView.setText(String.valueOf(user.getTotalBooksRead()));
        totalBookCurrentTextView.setText(String.valueOf(user.getCurrentlyReading()));
        yearlyReadTextView.setText(String.valueOf(user.getBooksReadThisYear()));
    }
}