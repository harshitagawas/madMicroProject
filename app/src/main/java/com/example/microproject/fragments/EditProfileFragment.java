package com.example.microproject.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.microproject.R;
import com.example.microproject.models.User;
import com.google.android.material.textfield.TextInputEditText;

public class EditProfileFragment extends Fragment {
    private TextInputEditText usernameEditText, bioEditText;
    private TextInputEditText totalBooksReadEditText, currentlyReadingEditText, yearlyReadEditText;
    private ImageView profileImageView;
    private Button saveButton, cancelButton, selectImageButton;
    private User user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.edit_profile_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize UI components
        usernameEditText = view.findViewById(R.id.editUsername);
        bioEditText = view.findViewById(R.id.editBio);
        totalBooksReadEditText = view.findViewById(R.id.editTotalBooksRead);
        currentlyReadingEditText = view.findViewById(R.id.editCurrentlyReading);
        yearlyReadEditText = view.findViewById(R.id.editYearlyRead);
        profileImageView = view.findViewById(R.id.editProfileImage);
        saveButton = view.findViewById(R.id.btnSaveProfile);
        cancelButton = view.findViewById(R.id.btnCancel);
        selectImageButton = view.findViewById(R.id.btnSelectImage);

        // Retrieve user data from bundle
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("user")) {
            user = bundle.getParcelable("user");
            populateFields();
        } else {
            user = new User("", "", 0, 0, 0);
        }

        // Set click listeners
        saveButton.setOnClickListener(v -> saveProfile());
        cancelButton.setOnClickListener(v -> navigateBack());
        selectImageButton.setOnClickListener(v -> selectImage());
    }

    private void populateFields() {
        usernameEditText.setText(user.getName());
        bioEditText.setText(user.getBio());
        totalBooksReadEditText.setText(String.valueOf(user.getTotalBooksRead()));
        currentlyReadingEditText.setText(String.valueOf(user.getCurrentlyReading()));
        yearlyReadEditText.setText(String.valueOf(user.getBooksReadThisYear()));
    }

    private void saveProfile() {
        // Update user object with new values
        user.setName(usernameEditText.getText().toString());
        user.setBio(bioEditText.getText().toString());

        try {
            user.setTotalBooksRead(Integer.parseInt(totalBooksReadEditText.getText().toString()));
            user.setCurrentlyReading(Integer.parseInt(currentlyReadingEditText.getText().toString()));
            user.setBooksReadThisYear(Integer.parseInt(yearlyReadEditText.getText().toString()));
        } catch (NumberFormatException e) {
            // Handle invalid number inputs
        }

        // Create a new ProfileFragment with updated user data
        ProfileFragment profileFragment = new ProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("user", user);
        profileFragment.setArguments(bundle);

        // Navigate back to ProfileFragment
        FragmentManager fragmentManager = getParentFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, profileFragment)
                .commit();
    }

    private void navigateBack() {
        // Go back to previous fragment
        FragmentManager fragmentManager = getParentFragmentManager();
        fragmentManager.popBackStack();
    }

    private void selectImage() {
        // Implement image selection functionality
        // This would typically involve launching an intent to select an image from the gallery
        // For now, we'll just show a placeholder implementation
    }
}