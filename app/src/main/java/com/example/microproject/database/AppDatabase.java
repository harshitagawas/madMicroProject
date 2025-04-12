package com.example.microproject.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import com.example.microproject.models.Book;
import com.example.microproject.models.Category;
import com.example.microproject.models.CategoryBook;
import com.example.microproject.models.DailyGoalProgress;
import com.example.microproject.models.Goal;
import com.example.microproject.models.Library;
import com.example.microproject.models.Quote;
import com.example.microproject.models.ReadingProgress;
import com.example.microproject.models.Review;
import com.example.microproject.models.Streak;
import com.example.microproject.models.User;

@Database(entities = {Book.class, User.class, Library.class, Category.class, CategoryBook.class, Review.class, Streak.class, Quote.class, ReadingProgress.class, Goal.class, DailyGoalProgress.class}, 
          version = 8, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase INSTANCE;

    public abstract BookDao bookDao();
    public abstract UserDao userDao();
    public abstract LibraryDao libraryDao();
    public abstract CategoryDao categoryDao();
    public abstract CategoryBookDao categoryBookDao();
    public abstract ReviewDao reviewDao();
    public abstract StreakDao streakDao();
    public abstract QuoteDao quoteDao();
    public abstract ReadingProgressDao readingProgressDao();
    public abstract GoalDao goalDao();
    public abstract DailyGoalProgressDao dailyGoalProgressDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "book_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
