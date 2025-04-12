package com.example.microproject.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.microproject.R;
import com.example.microproject.database.AppDatabase;
import com.example.microproject.database.DailyGoalProgressDao;
import com.example.microproject.database.GoalDao;
import com.example.microproject.models.DailyGoalProgress;
import com.example.microproject.models.Goal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GoalsFragment extends Fragment {

    private TextView textViewDate;
    private ProgressBar progressBar;
    private TextView textViewProgress;
    private Button buttonSave;

    private List<CheckBox> goalCheckBoxes = new ArrayList<>();
    private List<Boolean> isRequiredGoal = new ArrayList<>();
    private List<Goal> goals = new ArrayList<>();

    private GoalDao goalDao;
    private DailyGoalProgressDao dailyGoalProgressDao;
    private ExecutorService executorService;

    public GoalsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.goals_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize database and DAO
        AppDatabase database = AppDatabase.getInstance(requireContext());
        goalDao = database.goalDao();
        dailyGoalProgressDao = database.dailyGoalProgressDao();
        executorService = Executors.newSingleThreadExecutor();

        // Initialize views
        textViewDate = view.findViewById(R.id.textViewDate);
        progressBar = view.findViewById(R.id.progressBar);
        textViewProgress = view.findViewById(R.id.textViewProgress);
        buttonSave = view.findViewById(R.id.buttonSave);

        // Set current date
        updateDateDisplay();

        // Initialize checkboxes
        initializeCheckboxes(view);

        // Set up checkbox listeners
        setupCheckboxListeners();

        // Load goals from database or create default goals if none exist
        loadOrCreateGoals();
        
        // Observe goals for changes
        observeGoals();

        // Set up save button
        buttonSave.setOnClickListener(v -> {
            saveGoalStates();
            updateStreakDays();
            Toast.makeText(requireContext(), "Progress saved!", Toast.LENGTH_SHORT).show();
        });

        // Update progress initially
        updateProgress();
    }

    private void loadOrCreateGoals() {
        executorService.execute(() -> {
            // Check if goals exist in the database using the synchronous method
            List<Goal> existingGoals = goalDao.getAllGoalsSync();
            
            if (existingGoals == null || existingGoals.isEmpty()) {
                // Create default goals if none exist
                createDefaultGoals();
            } else {
                // Load existing goals
                goals = existingGoals;
                requireActivity().runOnUiThread(() -> {
                    // Update checkboxes based on loaded goals
                    for (int i = 0; i < goals.size() && i < goalCheckBoxes.size(); i++) {
                        goalCheckBoxes.get(i).setChecked(goals.get(i).isCompleted());
                    }
                });
            }
            
            // Load today's progress if it exists
            loadTodayProgress();
        });
    }
    
    private void createDefaultGoals() {
        // Create default goals
        goals.clear();
        goals.add(new Goal("Read for 20 Minutes", true, false));
        goals.add(new Goal("Add a New Book", true, false));
        goals.add(new Goal("Write a Review", false, false));
        goals.add(new Goal("Finish a Chapter", true, false));
        goals.add(new Goal("Share a Quote", true, false));
        
        // Save goals to database
        for (Goal goal : goals) {
            goalDao.insertGoal(goal);
        }
        
        requireActivity().runOnUiThread(() -> {
            // Update checkboxes based on created goals
            for (int i = 0; i < goals.size() && i < goalCheckBoxes.size(); i++) {
                goalCheckBoxes.get(i).setChecked(goals.get(i).isCompleted());
            }
        });
    }
    
    private void loadTodayProgress() {
        executorService.execute(() -> {
            // Get today's date at midnight
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            long todayStart = calendar.getTimeInMillis();
            
            // Get progress for today
            DailyGoalProgress todayProgress = dailyGoalProgressDao.getProgressForDate(todayStart);
            
            if (todayProgress != null) {
                requireActivity().runOnUiThread(() -> {
                    // Update progress bar
                    int progressPercentage = (todayProgress.getCompletedGoals() * 100) / todayProgress.getTotalGoals();
                    progressBar.setProgress(progressPercentage);
                    textViewProgress.setText(progressPercentage + "% Complete");
                });
            }
        });
    }

    private void initializeCheckboxes(View view) {
        // Add all checkboxes to our list for easier management
        goalCheckBoxes.add(view.findViewById(R.id.checkBoxGoal1));  // Read for 20 Minutes
        goalCheckBoxes.add(view.findViewById(R.id.checkBoxGoal2));  // Add a New Book
        goalCheckBoxes.add(view.findViewById(R.id.checkBoxGoal3));  // Write a Review
        goalCheckBoxes.add(view.findViewById(R.id.checkBoxGoal4));  // Finish a Chapter
        goalCheckBoxes.add(view.findViewById(R.id.checkBoxGoal5));  // Share a Quote

        // Mark which goals are required (bold ones in the XML)
        isRequiredGoal.add(true);   // Read for 20 Minutes - required
        isRequiredGoal.add(true);   // Add a New Book - required
        isRequiredGoal.add(false);  // Write a Review - optional
        isRequiredGoal.add(true);   // Finish a Chapter - required
        isRequiredGoal.add(true);   // Share a Quote - required
    }

    private void setupCheckboxListeners() {
        for (int i = 0; i < goalCheckBoxes.size(); i++) {
            final int index = i;
            goalCheckBoxes.get(i).setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (goals.size() > index) {
                    goals.get(index).setCompleted(isChecked);
                    updateProgress();
                }
            });
        }
    }

    private void updateProgress() {
        int totalGoals = goalCheckBoxes.size();
        int completedGoals = 0;
        int requiredGoalsCompleted = 0;
        int totalRequiredGoals = 0;

        for (int i = 0; i < goalCheckBoxes.size(); i++) {
            if (goalCheckBoxes.get(i).isChecked()) {
                completedGoals++;
                if (isRequiredGoal.get(i)) {
                    requiredGoalsCompleted++;
                }
            }
            if (isRequiredGoal.get(i)) {
                totalRequiredGoals++;
            }
        }

        int progressPercentage = (totalGoals > 0) ? (completedGoals * 100) / totalGoals : 0;

        progressBar.setProgress(progressPercentage);
        textViewProgress.setText(progressPercentage + "% Complete");
    }

    private void updateDateDisplay() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());
        textViewDate.setText(currentDate);
    }

    private void saveGoalStates() {
        // Update goals in the database
        executorService.execute(() -> {
            // First, make sure we have goals in the database
            if (goals.isEmpty()) {
                createDefaultGoals();
            }
            
            // Update goal completion status
            for (int i = 0; i < goals.size() && i < goalCheckBoxes.size(); i++) {
                goals.get(i).setCompleted(goalCheckBoxes.get(i).isChecked());
                goalDao.updateGoal(goals.get(i));
            }
            
            // Calculate progress metrics
            final int totalGoals = goalCheckBoxes.size();
            final int completedGoals = calculateCompletedGoals();
            final int requiredGoalsCompleted = calculateRequiredGoalsCompleted();
            final int totalRequiredGoals = calculateTotalRequiredGoals();
            
            // Get current streak
            final int streakDays = getStreakDays();
            
            // Get today's date at midnight
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            final long todayStart = calendar.getTimeInMillis();
            
            // Check if we already have progress for today
            DailyGoalProgress existingProgress = dailyGoalProgressDao.getProgressForDate(todayStart);
            
            if (existingProgress != null) {
                // Update existing progress
                existingProgress.setCompletedGoals(completedGoals);
                existingProgress.setTotalGoals(totalGoals);
                existingProgress.setRequiredGoalsCompleted(requiredGoalsCompleted);
                existingProgress.setTotalRequiredGoals(totalRequiredGoals);
                existingProgress.setStreakDays(streakDays);
                dailyGoalProgressDao.updateProgress(existingProgress);
            } else {
                // Create new progress
                DailyGoalProgress progress = new DailyGoalProgress(
                        completedGoals, 
                        totalGoals, 
                        requiredGoalsCompleted, 
                        totalRequiredGoals, 
                        streakDays
                );
                dailyGoalProgressDao.insertProgress(progress);
            }
            
            // Update UI on main thread
            requireActivity().runOnUiThread(() -> {
                int progressPercentage = (totalGoals > 0) ? (completedGoals * 100) / totalGoals : 0;
                progressBar.setProgress(progressPercentage);
                textViewProgress.setText(progressPercentage + "% Complete");
            });
        });
    }
    
    private int calculateCompletedGoals() {
        int count = 0;
        for (int i = 0; i < goalCheckBoxes.size(); i++) {
            if (goalCheckBoxes.get(i).isChecked()) {
                count++;
            }
        }
        return count;
    }
    
    private int calculateRequiredGoalsCompleted() {
        int count = 0;
        for (int i = 0; i < goalCheckBoxes.size(); i++) {
            if (goalCheckBoxes.get(i).isChecked() && isRequiredGoal.get(i)) {
                count++;
            }
        }
        return count;
    }
    
    private int calculateTotalRequiredGoals() {
        int count = 0;
        for (int i = 0; i < goalCheckBoxes.size(); i++) {
            if (isRequiredGoal.get(i)) {
                count++;
            }
        }
        return count;
    }

    private void updateStreakDays() {
        executorService.execute(() -> {
            // Get today's date at midnight
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            long todayStart = calendar.getTimeInMillis();
            
            // Get yesterday's date at midnight
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            long yesterdayStart = calendar.getTimeInMillis();
            
            // Get latest progress
            DailyGoalProgress latestProgress = dailyGoalProgressDao.getLatestProgress();
            int streakDays = 0;
            
            if (latestProgress != null) {
                streakDays = latestProgress.getStreakDays();
            }
            
            // Check if all required goals are completed
            boolean allRequiredGoalsCompleted = true;
            for (int i = 0; i < goalCheckBoxes.size(); i++) {
                if (isRequiredGoal.get(i) && !goalCheckBoxes.get(i).isChecked()) {
                    allRequiredGoalsCompleted = false;
                    break;
                }
            }
            
            if (allRequiredGoalsCompleted) {
                // Check if we have progress for yesterday
                DailyGoalProgress yesterdayProgress = dailyGoalProgressDao.getProgressForDate(yesterdayStart);
                
                if (yesterdayProgress != null && yesterdayProgress.getRequiredGoalsCompleted() == yesterdayProgress.getTotalRequiredGoals()) {
                    // Consecutive day, increment streak
                    streakDays++;
                } else if (yesterdayProgress == null || yesterdayProgress.getRequiredGoalsCompleted() < yesterdayProgress.getTotalRequiredGoals()) {
                    // Missed a day or no progress yesterday, reset streak
                    streakDays = 1;
                }
                // If we have progress for today already, don't change streak
            }
            
            // Update streak in the latest progress
            if (latestProgress != null) {
                latestProgress.setStreakDays(streakDays);
                dailyGoalProgressDao.updateProgress(latestProgress);
            }
        });
    }

    public int getStreakDays() {
        // This is a synchronous call that should be run on a background thread
        // For simplicity, we'll return 0 here and update it asynchronously
        return 0;
    }

    private void observeGoals() {
        goalDao.getAllGoals().observe(getViewLifecycleOwner(), updatedGoals -> {
            if (updatedGoals != null && !updatedGoals.isEmpty()) {
                goals = updatedGoals;
                // Update checkboxes based on loaded goals
                for (int i = 0; i < goals.size() && i < goalCheckBoxes.size(); i++) {
                    goalCheckBoxes.get(i).setChecked(goals.get(i).isCompleted());
                }
                updateProgress();
            }
        });
    }
}