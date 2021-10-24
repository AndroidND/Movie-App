package com.example.basicactivitytest.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.basicactivitytest.model.ParcelableMovie;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {ParcelableMovie.class}, version = 1, exportSchema = false)
public abstract class MovieDatabase extends RoomDatabase {

//    private static final int NUMBER_OF_THREADS = 4;
//    static final ExecutorService databaseWriteExecutor =
//            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    private static final String DB_NAME = "MovieDatabase";
    public abstract MovieDao getMovieDao();

    // singleton pattern to ensure only one instance of database
    private static volatile MovieDatabase INSTANCE;

    static MovieDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (MovieDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE =
                            Room.databaseBuilder(context.getApplicationContext(),
                                    MovieDatabase.class, DB_NAME)
                                    .build();
                }
            }
        }
        return INSTANCE;
    }

}
