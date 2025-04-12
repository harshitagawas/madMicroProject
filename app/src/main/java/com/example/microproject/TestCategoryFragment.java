package com.example.microproject;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.microproject.database.AppDatabase;
import com.example.microproject.fragments.CategoryBooksFragment;
import com.example.microproject.models.Category;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestCategoryFragment extends AppCompatActivity {
    
    private static final String TAG = "TestCategoryFragment";
    private ExecutorService executorService;
    private AppDatabase database;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        database = AppDatabase.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();
        
        // Create a test category and save it to the database
        executorService.execute(() -> {
            // Create a new category for testing
            Category testCategory = new Category("Test Category");
            long categoryId = database.categoryDao().insertCategory(testCategory);
            
            // Log the category ID
            Log.d(TAG, "Created test category with ID: " + categoryId);
            
            // Open the CategoryBooksFragment with this ID
            runOnUiThread(() -> {
                Fragment fragment = CategoryBooksFragment.newInstance((int)categoryId);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .commit();
            });
        });
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
} 