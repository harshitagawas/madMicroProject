package com.example.microproject.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.microproject.R;
import com.example.microproject.database.AppDatabase;
import com.example.microproject.database.UserDao;
import com.example.microproject.models.User;
import com.google.android.material.textfield.TextInputEditText;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EditProfileFragment extends Fragment {
    private TextInputEditText usernameEditText, bioEditText;
    private TextInputEditText totalBooksReadEditText, currentlyReadingEditText, yearlyReadEditText;
    private ImageView profileImageView;
    private Button saveButton, cancelButton, selectImageButton;
    private User user;
    private UserDao userDao;
    private ExecutorService executorService;

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

        // Initialize Room database
        AppDatabase database = AppDatabase.getInstance(requireContext());
        userDao = database.userDao();
        executorService = Executors.newSingleThreadExecutor();

        // Retrieve user data from bundle
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("user")) {
            user = bundle.getParcelable("user");
            if (user != null) {
                populateFields();
            } else {
                Toast.makeText(requireContext(), "Error: User data not available", Toast.LENGTH_SHORT).show();
                navigateBack();
                return;
            }
        } else {
            // If no user data is provided, fetch the default user (ID 1) from the database
            executorService.execute(() -> {
                User defaultUser = userDao.getUserByIdSync(1);
                if (defaultUser != null) {
                    user = defaultUser;
                    requireActivity().runOnUiThread(this::populateFields);
                } else {
                    // Create a default user if none exists
                    User newUser = new User("User", "Add your bio here", 0, 0, 0);
                    newUser.setId(1); // Set ID to 1 for the default user
                    userDao.insert(newUser);
                    user = newUser;
                    requireActivity().runOnUiThread(this::populateFields);
                }
            });
        }

        // Set click listeners
        saveButton.setOnClickListener(v -> saveProfile());
        cancelButton.setOnClickListener(v -> navigateBack());
        selectImageButton.setOnClickListener(v -> {
            // Image selection functionality will be implemented later
            Toast.makeText(requireContext(), "Image selection not implemented yet", Toast.LENGTH_SHORT).show();
        });
    }

    private void populateFields() {
        if (user == null) return;
        
        usernameEditText.setText(user.getName());
        bioEditText.setText(user.getBio());
        totalBooksReadEditText.setText(String.valueOf(user.getTotalBooksRead()));
        currentlyReadingEditText.setText(String.valueOf(user.getCurrentlyReading()));
        yearlyReadEditText.setText(String.valueOf(user.getBooksReadThisYear()));
    }

    private void saveProfile() {
        if (user == null) {
            Toast.makeText(requireContext(), "Error: User data not available", Toast.LENGTH_SHORT).show();
            navigateBack();
            return;
        }

        // Get values from EditText fields safely
        String name = usernameEditText.getText() != null ? usernameEditText.getText().toString() : "";
        String bio = bioEditText.getText() != null ? bioEditText.getText().toString() : "";

        user.setName(name);
        user.setBio(bio);

        try {
            user.setTotalBooksRead(
                    !totalBooksReadEditText.getText().toString().isEmpty() ?
                            Integer.parseInt(totalBooksReadEditText.getText().toString()) : 0
            );
            user.setCurrentlyReading(
                    !currentlyReadingEditText.getText().toString().isEmpty() ?
                            Integer.parseInt(currentlyReadingEditText.getText().toString()) : 0
            );
            user.setBooksReadThisYear(
                    !yearlyReadEditText.getText().toString().isEmpty() ?
                            Integer.parseInt(yearlyReadEditText.getText().toString()) : 0
            );
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Error: Invalid number format", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update user in Room database
        executorService.execute(() -> {
            userDao.update(user);
            requireActivity().runOnUiThread(() -> {
                Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                navigateBack();
            });
        });
    }

    private void navigateBack() {
        FragmentManager fragmentManager = getParentFragmentManager();
        fragmentManager.popBackStack();
    }
}
