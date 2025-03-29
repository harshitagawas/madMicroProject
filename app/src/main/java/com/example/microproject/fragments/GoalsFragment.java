package com.example.microproject.fragments;

import android.content.Context;
import android.content.SharedPreferences;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GoalsFragment extends Fragment {

    private TextView textViewDate;
    private ProgressBar progressBar;
    private TextView textViewProgress;
    private Button buttonSave;

    private List<CheckBox> goalCheckBoxes = new ArrayList<>();
    private List<Boolean> isRequiredGoal = new ArrayList<>();

    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "ReadingGoalsPrefs";
    private static final String LAST_SAVED_DATE = "LastSavedDate";
    private static final String STREAK_DAYS = "StreakDays";

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

        sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

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

        // Load saved data
        loadSavedGoalStates();

        // Set up save button
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveGoalStates();
                updateStreakDays();
                Toast.makeText(requireContext(), "Progress saved!", Toast.LENGTH_SHORT).show();
            }
        });

        // Update progress initially
        updateProgress();
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
        for (CheckBox checkBox : goalCheckBoxes) {
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    updateProgress();
                }
            });
        }
    }

    private void updateProgress() {
        int totalGoals = goalCheckBoxes.size();
        int completedGoals = 0;

        for (CheckBox checkBox : goalCheckBoxes) {
            if (checkBox.isChecked()) {
                completedGoals++;
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
        SharedPreferences.Editor editor = sharedPreferences.edit();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());

        // Save date of last save
        editor.putString(LAST_SAVED_DATE, currentDate);

        // Save each checkbox state
        for (int i = 0; i < goalCheckBoxes.size(); i++) {
            editor.putBoolean("goal_" + i + "_" + currentDate, goalCheckBoxes.get(i).isChecked());
        }

        editor.apply();
    }

    private void loadSavedGoalStates() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());

        // Check if we have saved goals for today
        for (int i = 0; i < goalCheckBoxes.size(); i++) {
            boolean isChecked = sharedPreferences.getBoolean("goal_" + i + "_" + currentDate, false);
            goalCheckBoxes.get(i).setChecked(isChecked);
        }
    }

    private void updateStreakDays() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());
        String lastSavedDate = sharedPreferences.getString(LAST_SAVED_DATE, "");
        int streakDays = sharedPreferences.getInt(STREAK_DAYS, 0);

        // Check if all required goals are completed
        boolean allRequiredGoalsCompleted = true;
        for (int i = 0; i < goalCheckBoxes.size(); i++) {
            if (isRequiredGoal.get(i) && !goalCheckBoxes.get(i).isChecked()) {
                allRequiredGoalsCompleted = false;
                break;
            }
        }

        if (allRequiredGoalsCompleted) {
            // If yesterday's date + 1 equals today, or if this is the first completion, increment streak
            try {
                Date savedDate = dateFormat.parse(lastSavedDate);
                Date today = dateFormat.parse(currentDate);

                if (savedDate == null || today == null) {
                    streakDays = 1; // Reset if there's an issue with dates
                } else {
                    // Add one day to saved date
                    long dayDifference = (today.getTime() - savedDate.getTime()) / (24 * 60 * 60 * 1000);

                    if (dayDifference == 1) {
                        // Consecutive day, increment streak
                        streakDays++;
                    } else if (dayDifference > 1) {
                        // Missed a day, reset streak
                        streakDays = 1;
                    }
                    // If dayDifference == 0, it's the same day, don't change streak
                }
            } catch (Exception e) {
                streakDays = 1; // Reset if there's an error
            }

            // Save updated streak
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(STREAK_DAYS, streakDays);
            editor.apply();
        }
    }

    public int getStreakDays() {
        return sharedPreferences.getInt(STREAK_DAYS, 0);
    }
}