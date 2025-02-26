package com.example.microproject;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.microproject.fragments.GoalsFragment;
import com.example.microproject.fragments.HomeFragment;
import com.example.microproject.fragments.LibraryFragment;

import com.example.microproject.fragments.ReviewFragment;
import com.example.microproject.fragments.TbrFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);


        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }


        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            if (item.getItemId() == R.id.navigation_home) {
                selectedFragment = new HomeFragment();
            } else if (item.getItemId() == R.id.navigation_library) {
                selectedFragment = new LibraryFragment();
            } else if (item.getItemId() == R.id.navigation_review) {
                selectedFragment = new ReviewFragment();
            }
            else if(item.getItemId() == R.id.navigation_tbr){
                selectedFragment= new TbrFragment();
            }
            else if(item.getItemId() == R.id.navigation_goals){
                selectedFragment= new GoalsFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
            }

            return true;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
