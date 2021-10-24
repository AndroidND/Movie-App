package com.example.basicactivitytest.data;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.basicactivitytest.model.ParcelableMovie;

import java.util.List;

@Dao
public interface MovieDao {

    @Query("SELECT * FROM movie")
    public LiveData<List<ParcelableMovie>> getFavoriteMovies();

    @Query("SELECT * FROM movie WHERE id = :id")
    public ParcelableMovie getFavoriteMovie(int id);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public void insertMovie(ParcelableMovie favoriteMovie);

    @Delete
    public void deleteMovie(ParcelableMovie favoriteMovie);

    @Query("SELECT COUNT(id) FROM movie")
    public int getCount();

}
