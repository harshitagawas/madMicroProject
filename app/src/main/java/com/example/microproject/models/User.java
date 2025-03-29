package com.example.microproject.models;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    private String name;
    private String bio;
    private int totalBooksRead;
    private int currentlyReading;
    private int booksReadThisYear;
    private String profileImagePath;

    public User(String name) {
        this.name = name;
        this.bio = "Your Bio";
        this.totalBooksRead = 0;
        this.currentlyReading = 0;
        this.booksReadThisYear = 0;
        this.profileImagePath = "";
    }

    public User(String name, String bio, int totalBooksRead, int currentlyReading, int booksReadThisYear) {
        this.name = name;
        this.bio = bio;
        this.totalBooksRead = totalBooksRead;
        this.currentlyReading = currentlyReading;
        this.booksReadThisYear = booksReadThisYear;
        this.profileImagePath = "";
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public int getTotalBooksRead() {
        return totalBooksRead;
    }

    public void setTotalBooksRead(int totalBooksRead) {
        this.totalBooksRead = totalBooksRead;
    }

    public int getCurrentlyReading() {
        return currentlyReading;
    }

    public void setCurrentlyReading(int currentlyReading) {
        this.currentlyReading = currentlyReading;
    }

    public int getBooksReadThisYear() {
        return booksReadThisYear;
    }

    public void setBooksReadThisYear(int booksReadThisYear) {
        this.booksReadThisYear = booksReadThisYear;
    }

    public String getProfileImagePath() {
        return profileImagePath;
    }

    public void setProfileImagePath(String profileImagePath) {
        this.profileImagePath = profileImagePath;
    }

    // Parcelable implementation
    protected User(Parcel in) {
        name = in.readString();
        bio = in.readString();
        totalBooksRead = in.readInt();
        currentlyReading = in.readInt();
        booksReadThisYear = in.readInt();
        profileImagePath = in.readString();
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
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(bio);
        dest.writeInt(totalBooksRead);
        dest.writeInt(currentlyReading);
        dest.writeInt(booksReadThisYear);
        dest.writeString(profileImagePath);
    }
}