package com.example.basicactivitytest.data;


import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.basicactivitytest.model.ParcelableMovie;

import java.util.List;

public class MovieViewModel extends AndroidViewModel {
    private MovieRepository repository;
    private LiveData<List<ParcelableMovie>> favoriteMovies;

    public MovieViewModel (Application application) {
        super(application);
        repository = new MovieRepository(application);
        favoriteMovies = repository.getFavoriteMovies();
    }

    public LiveData<List<ParcelableMovie>> getFavoriteMovies() {
        return favoriteMovies;
    }

    public void addFavorite(ParcelableMovie movie) {
        repository.insertMovie(movie);
    }

    public void deleteFavorite(ParcelableMovie movie) {
        repository.deleteMovie(movie);
    }

}