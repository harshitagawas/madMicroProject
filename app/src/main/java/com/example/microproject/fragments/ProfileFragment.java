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
import com.example.microproject.database.DailyGoalProgressDao;
import com.example.microproject.models.User;
import com.example.microproject.models.DailyGoalProgress;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Calendar;

import com.airbnb.lottie.LottieAnimationView;
import android.widget.FrameLayout;

public class ProfileFragment extends Fragment {
    private TextView nameTextView, bioTextView, totalBooksReadTextView, totalBookCurrentTextView, yearlyReadTextView;
    private ImageView editPfp;
    private UserDao userDao;
    private DailyGoalProgressDao dailyGoalProgressDao;
    private ExecutorService executorService;
    private User currentUser;
    private FrameLayout medalContainer, trophyContainer;
    private LottieAnimationView medalAnimation, trophyAnimation;

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
        medalContainer = view.findViewById(R.id.medalContainer);
        trophyContainer = view.findViewById(R.id.trophyContainer);
        medalAnimation = view.findViewById(R.id.medalAnimation);
        trophyAnimation = view.findViewById(R.id.trophyAnimation);

        // Initialize database and DAO
        AppDatabase database = AppDatabase.getInstance(requireContext());
        userDao = database.userDao();
        dailyGoalProgressDao = database.dailyGoalProgressDao();
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
        
        // Load achievements
        loadAchievements();

        // Set up medal animation
        medalAnimation.setAnimation(R.raw.medal);
        medalAnimation.setRepeatCount(1); // Repeat once
        medalAnimation.playAnimation();
        
        // Set up trophy animation
        trophyAnimation.setAnimation(R.raw.trophy);
        trophyAnimation.setRepeatCount(1); // Repeat once
        trophyAnimation.playAnimation();
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

    private void loadAchievements() {
        executorService.execute(() -> {
            // Get today's date at midnight
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            long todayStart = calendar.getTimeInMillis();
            
            // Get today's progress
            DailyGoalProgress todayProgress = dailyGoalProgressDao.getProgressForDate(todayStart);
            
            if (todayProgress != null) {
                final int completedGoals = todayProgress.getCompletedGoals();
                final int totalGoals = todayProgress.getTotalGoals();
                
                requireActivity().runOnUiThread(() -> {
                    if (completedGoals >= 3 && completedGoals < totalGoals) {
                        // Show medal for completing 3 or more goals
                        medalContainer.setVisibility(View.VISIBLE);
                        trophyContainer.setVisibility(View.GONE);
                        medalAnimation.playAnimation();
                    } else if (completedGoals == totalGoals) {
                        // Show trophy for completing all goals
                        medalContainer.setVisibility(View.GONE);
                        trophyContainer.setVisibility(View.VISIBLE);
                        trophyAnimation.playAnimation();
                    } else {
                        // Hide both if not enough goals completed
                        medalContainer.setVisibility(View.GONE);
                        trophyContainer.setVisibility(View.GONE);
                    }
                });
            }
        });
    }
}
