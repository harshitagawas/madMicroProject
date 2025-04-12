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
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.example.microproject.R;
import com.example.microproject.database.AppDatabase;
import com.example.microproject.database.UserDao;
import com.example.microproject.models.User;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProfileFragment extends Fragment {
    private TextView nameTextView, bioTextView, totalBooksReadTextView, totalBookCurrentTextView, yearlyReadTextView;
    private ImageView editPfp;
    private UserDao userDao;
    private ExecutorService executorService;
    private User currentUser;

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
        editPfp = view.findViewById(R.id.profileEdit);
        totalBooksReadTextView = view.findViewById(R.id.totalBooksRead);
        totalBookCurrentTextView = view.findViewById(R.id.totalBookCurrent);
        yearlyReadTextView = view.findViewById(R.id.yearlyRead);

        // Initialize database and DAO
        AppDatabase database = AppDatabase.getInstance(requireContext());
        userDao = database.userDao();
        executorService = Executors.newSingleThreadExecutor();

        // Fetch user data from Room database
        LiveData<User> userLiveData = userDao.getUserById(1); // Fetch user with ID 1
        userLiveData.observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (user != null) {
                    currentUser = user;
                    updateUI(user);
                }
            }
        });

        // Set click listener for edit profile button
        editPfp.setOnClickListener(v -> openEditProfileFragment());
    }

    // Open EditProfileFragment and pass current user data
    private void openEditProfileFragment() {
        EditProfileFragment editProfileFragment = new EditProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("user", currentUser);
        editProfileFragment.setArguments(bundle);

        FragmentManager fragmentManager = getParentFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, editProfileFragment)
                .addToBackStack(null)
                .commit();
    }

    // Update UI with user data
    private void updateUI(User user) {
        nameTextView.setText(user.getName());
        bioTextView.setText(user.getBio());
        totalBooksReadTextView.setText(String.valueOf(user.getTotalBooksRead()));
        totalBookCurrentTextView.setText(String.valueOf(user.getCurrentlyReading()));
        yearlyReadTextView.setText(String.valueOf(user.getBooksReadThisYear()));
    }
}
