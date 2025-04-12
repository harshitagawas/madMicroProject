package com.example.microproject.database;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.microproject.models.User;

public class UserViewModel extends AndroidViewModel {
    private final UserDao userDao;

    public UserViewModel(Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(application);
        userDao = database.userDao();
    }

    public LiveData<User> getUserById(int id) {
        return userDao.getUserById(id);
    }

    public void insertUser(User user) {
        new Thread(() -> userDao.insert(user)).start(); // Fixed method call
    }

    public void updateUser(User user) {
        new Thread(() -> userDao.update(user)).start(); // Fixed method call
    }

    public static class Factory implements ViewModelProvider.Factory {
        private final Application application;

        public Factory(Application application) {
            this.application = application;
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            if (modelClass.isAssignableFrom(UserViewModel.class)) {
                return (T) new UserViewModel(application);
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
