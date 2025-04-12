package com.example.microproject.fragments;

import android.animation.Animator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.example.microproject.R;
import com.example.microproject.database.AppDatabase;
import com.example.microproject.database.ReadingProgressDao;
import com.example.microproject.database.StreakDao;
import com.example.microproject.fragments.QuotesFragment;
import com.example.microproject.fragments.ProfileFragment;
import com.example.microproject.models.ReadingProgress;
import com.example.microproject.models.Streak;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeFragment extends Fragment {

    private LinearLayout streakContainer;
    private FrameLayout streakResultContainer;
    private TextView streakQuestion;
    private Button btnYes, btnNo, trackBtn, addQuote;
    private LinearLayout trackBook;
    private EditText currentPage, totalPage;
    private FrameLayout progressContainer;

    private ImageView pfp;
    private int streakCount = 0; // Track streak count
    private StreakDao streakDao;
    private ReadingProgressDao readingProgressDao;
    private ExecutorService executorService;
    private Streak currentStreak;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);

        // Initialize database and DAO
        AppDatabase database = AppDatabase.getInstance(requireContext());
        streakDao = database.streakDao();
        readingProgressDao = database.readingProgressDao();
        executorService = Executors.newSingleThreadExecutor();

        // Initialize views
        streakContainer = view.findViewById(R.id.streakContainer);
        streakResultContainer = view.findViewById(R.id.streakResultContainer);
        streakQuestion = view.findViewById(R.id.streakQuestion);
        btnYes = view.findViewById(R.id.yes);
        btnNo = view.findViewById(R.id.no);
        currentPage = view.findViewById(R.id.currentPage);
        totalPage = view.findViewById(R.id.totalPage);
        trackBtn = view.findViewById(R.id.trackBtn);
        trackBook = view.findViewById(R.id.trackBook);
        progressContainer = view.findViewById(R.id.progressContainer);
        addQuote = view.findViewById(R.id.addQuote);
        pfp = view.findViewById(R.id.pfp);
        
        // Find the View Quote button
        Button viewQuoteBtn = view.findViewById(R.id.viewQuote);
        viewQuoteBtn.setOnClickListener(v -> openQuoteFragment());

        // Load current streak from database
        loadCurrentStreak();
        
        // Load latest reading progress
        loadLatestReadingProgress();

        // YES Button Click - Create animation dynamically
        btnYes.setOnClickListener(v -> {
            streakCount++;
            updateStreak(true);
            showStreakSuccess("Yay! You read for " + streakCount + " days in a row!!!");
        });

        // NO Button Click - Reset streak
        btnNo.setOnClickListener(v -> {
            streakCount = 0;
            updateStreak(false);
            showStreakFailure("Now cannot be any better time to read!!!");
        });

        trackBtn.setOnClickListener(v -> showProgressBar());
        addQuote.setOnClickListener(v -> openQuoteFragment());
        pfp.setOnClickListener(v -> showProfile());
        
        return view;
    }

    private void loadCurrentStreak() {
        executorService.execute(() -> {
            // Get the latest streak from the database
            currentStreak = streakDao.getLatestStreak();
            
            // Check if the streak is from today (within 24 hours)
            long currentTime = System.currentTimeMillis();
            long oneDayInMillis = 24 * 60 * 60 * 1000;
            
            if (currentStreak != null) {
                // If the streak is older than 24 hours, reset it
                if (currentTime - currentStreak.getLastUpdated() > oneDayInMillis) {
                    streakCount = 0;
                    // Delete old streaks
                    streakDao.deleteOldStreaks(currentTime - oneDayInMillis);
                } else {
                    streakCount = currentStreak.getStreakCount();
                }
            } else {
                // No streak exists yet
                streakCount = 0;
            }
            
            // Update UI on the main thread
            requireActivity().runOnUiThread(() -> {
                // Update UI based on streak count
                if (streakCount > 0) {
                    showStreakSuccess("Current streak: " + streakCount + " days!");
                }
            });
        });
    }

    private void updateStreak(boolean increment) {
        executorService.execute(() -> {
            long currentTime = System.currentTimeMillis();
            
            if (currentStreak == null) {
                // Create a new streak
                currentStreak = new Streak(increment ? 1 : 0, currentTime);
                streakDao.insertStreak(currentStreak);
            } else {
                // Update existing streak
                if (increment) {
                    currentStreak.setStreakCount(currentStreak.getStreakCount() + 1);
                } else {
                    currentStreak.setStreakCount(0);
                }
                currentStreak.setLastUpdated(currentTime);
                streakDao.updateStreak(currentStreak);
            }
        });
    }

    // Show success message with animation
    private void showStreakSuccess(String message) {
        // Hide the streak question part
        streakContainer.setVisibility(View.GONE);
        
        // Clear previous result
        streakResultContainer.removeAllViews();

        // Create new layout for result
        LinearLayout resultLayout = new LinearLayout(requireContext());
        resultLayout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        resultLayout.setOrientation(LinearLayout.VERTICAL);
        resultLayout.setPadding(20, 20, 20, 20);

        // Create and add message TextView
        TextView messageView = new TextView(requireContext());
        messageView.setText(message);
        messageView.setTextSize(18);
        messageView.setTypeface(null, android.graphics.Typeface.BOLD);
        messageView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        messageView.setGravity(android.view.Gravity.CENTER_HORIZONTAL);
        resultLayout.addView(messageView);

        // Create and add Lottie animation
        LottieAnimationView streakAnimation = new LottieAnimationView(requireContext());
        LinearLayout.LayoutParams animParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                (int) (200 * getResources().getDisplayMetrics().density)); // 200dp
        animParams.topMargin = (int) (getResources().getDisplayMetrics().density);
        streakAnimation.setLayoutParams(animParams);
        streakAnimation.setAnimation(R.raw.streak);
        streakAnimation.setRepeatCount(0); // Play only once
        resultLayout.addView(streakAnimation);

        // Add the entire result layout to the container
        streakResultContainer.addView(resultLayout);
        streakResultContainer.setVisibility(View.VISIBLE);

        // Play animation
        streakAnimation.playAnimation();

        // Set up animation listener to handle fade out
        streakAnimation.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                // Animation started
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // Animation ended - fade out
                fadeOutAnimation(streakAnimation);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                // Animation was cancelled
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                // Animation repeated
            }
        });
    }

    // Show failure message without animation
    private void showStreakFailure(String message) {
        // Hide the streak question part
        streakContainer.setVisibility(View.GONE);
        
        // Clear previous result
        streakResultContainer.removeAllViews();

        // Create and add message TextView
        TextView messageView = new TextView(requireContext());
        messageView.setText(message);
        messageView.setTextSize(18);
        messageView.setTypeface(null, android.graphics.Typeface.BOLD);
        messageView.setPadding(20, 20, 20, 20);
        messageView.setGravity(android.view.Gravity.CENTER_HORIZONTAL);

        // Add the message to the container
        streakResultContainer.addView(messageView);
        streakResultContainer.setVisibility(View.VISIBLE);
    }

    // Method to fade out the animation view
    private void fadeOutAnimation(View view) {
        AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
        fadeOut.setDuration(1000);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // Animation started
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Animation ended - hide the view
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // Animation repeated
            }
        });
        view.startAnimation(fadeOut);
    }

    private void loadLatestReadingProgress() {
        executorService.execute(() -> {
            ReadingProgress latestProgress = readingProgressDao.getLatestProgress();
            if (latestProgress != null) {
                requireActivity().runOnUiThread(() -> {
                    currentPage.setText(String.valueOf(latestProgress.getCurrentPage()));
                    totalPage.setText(String.valueOf(latestProgress.getTotalPages()));
                    
                    // Also display the progress bar with the loaded data
                    displayProgressBar(latestProgress.getCurrentPage(), latestProgress.getTotalPages());
                });
            }
        });
    }
    
    private void displayProgressBar(int currentPageNum, int totalPages) {
        // Clear the progress container
        progressContainer.removeAllViews();

        // Create text view for progress percentage
        TextView progressText = new TextView(requireContext());
        int progressPercentage = (currentPageNum * 100) / totalPages;
        progressText.setText("Progress: " + progressPercentage + "%");
        progressText.setTextSize(14);
        progressText.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        progressContainer.addView(progressText);

        // Create and configure ProgressBar
        ProgressBar progressBar = new ProgressBar(requireContext(), null, android.R.attr.progressBarStyleHorizontal);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = 60;
        progressBar.setLayoutParams(params);
        progressBar.setMax(totalPages);
        progressBar.setProgress(currentPageNum);

        // Add ProgressBar below the progress text
        progressContainer.addView(progressBar);
    }

    private void showProgressBar() {
        String currentStr = currentPage.getText().toString();
        String totalStr = totalPage.getText().toString();

        if (currentStr.isEmpty() || totalStr.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter both values!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int currentPageNum = Integer.parseInt(currentStr);
            int totalPages = Integer.parseInt(totalStr);

            if (currentPageNum < 1 || currentPageNum > totalPages) {
                Toast.makeText(requireContext(), "Invalid page numbers!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Save progress to database
            saveReadingProgress(currentPageNum, totalPages);

            // Display the progress bar
            displayProgressBar(currentPageNum, totalPages);

        } catch (NumberFormatException e) {
            Toast.makeText(requireContext(), "Please enter valid numbers!", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void saveReadingProgress(int currentPageNum, int totalPages) {
        executorService.execute(() -> {
            ReadingProgress progress = new ReadingProgress(currentPageNum, totalPages);
            readingProgressDao.insertProgress(progress);
        });
    }

    private void openQuoteFragment() {
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new QuotesFragment()) // Make sure fragment_container exists in activity_main.xml
                .addToBackStack(null) // Allows going back to HomeFragment when pressing back
                .commit();
    }

    private void showProfile() {
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new ProfileFragment()) // Make sure fragment_container exists in activity_main.xml
                .addToBackStack(null) // Allows going back to HomeFragment when pressing back
                .commit();
    }
}