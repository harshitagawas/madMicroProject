package com.example.microproject.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

@Entity(tableName = "users")
public class User implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String bio;
    private int totalBooksRead;
    private int currentlyReading;
    private int booksReadThisYear;

    // Constructor
    public User(String name, String bio, int totalBooksRead, int currentlyReading, int booksReadThisYear) {
        this.name = name;
        this.bio = bio;
        this.totalBooksRead = totalBooksRead;
        this.currentlyReading = currentlyReading;
        this.booksReadThisYear = booksReadThisYear;
    }

    // Parcelable implementation
    protected User(Parcel in) {
        id = in.readInt();
        name = in.readString();
        bio = in.readString();
        totalBooksRead = in.readInt();
        currentlyReading = in.readInt();
        booksReadThisYear = in.readInt();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(bio);
        dest.writeInt(totalBooksRead);
        dest.writeInt(currentlyReading);
        dest.writeInt(booksReadThisYear);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // ✅ GETTERS
    public int getId() { return id; }
    public String getName() { return name; }
    public String getBio() { return bio; }
    public int getTotalBooksRead() { return totalBooksRead; }
    public int getCurrentlyReading() { return currentlyReading; }
    public int getBooksReadThisYear() { return booksReadThisYear; }

    // ✅ SETTERS (Add these methods)
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setBio(String bio) { this.bio = bio; }
    public void setTotalBooksRead(int totalBooksRead) { this.totalBooksRead = totalBooksRead; }
    public void setCurrentlyReading(int currentlyReading) { this.currentlyReading = currentlyReading; }
    public void setBooksReadThisYear(int booksReadThisYear) { this.booksReadThisYear = booksReadThisYear; }
}
