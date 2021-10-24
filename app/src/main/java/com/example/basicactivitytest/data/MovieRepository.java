package com.example.basicactivitytest.data;

import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.room.Room;

import com.example.basicactivitytest.MainActivity;
import com.example.basicactivitytest.MoviesAdapter;
import com.example.basicactivitytest.model.ParcelableMovie;
import com.example.basicactivitytest.utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class MovieRepository {
    private MovieDao mMovieDao;
    private LiveData<List<ParcelableMovie>> mFavoriteMovies;
    private List<ParcelableMovie> mPopularMovies;
    private List<ParcelableMovie> mTopRatedMovies;

    private List<ParcelableMovie> mMovieList;

    private String category;

    private int favIconColor;

    // Note that in order to unit test the WordRepository, you have to remove the Application
    // dependency. This adds complexity and much more code, and this sample is not about testing.
    // See the BasicSample in the android-architecture-components repository at
    // https://github.com/googlesamples
    public MovieRepository (Application application) {
        MovieDatabase db = MovieDatabase.getDatabase(application);
        mMovieDao = db.getMovieDao();
        mFavoriteMovies = mMovieDao.getFavoriteMovies();
    }

    // Room executes all LiveData queries on a separate thread automatically, no async task required.
    // Observed LiveData will notify the observer when the data has changed.
    public LiveData<List<ParcelableMovie>> getFavoriteMovies() {
        return mFavoriteMovies;
    }

    public List<ParcelableMovie> fetchMoviesByCategory(String category) {
        return mMovieList;
    }

    public void insertMovie(ParcelableMovie movie) {
        insertMovieTask(movie);
    }

    public void deleteMovie(ParcelableMovie movie) {
        deleteMovieTask(movie);
    }

    private void insertMovieTask(final ParcelableMovie movie) {
        new AsyncTask<ParcelableMovie, Void, Integer>() {
            @Override
            protected Integer doInBackground(ParcelableMovie... movies) {
                try {
                    mMovieDao.insertMovie(movies[0]);
                    return Color.RED;
                } catch (Exception e) {
                    return Color.WHITE;
                }
            }

            @Override
            protected void onPostExecute(Integer color) {
                favIconColor = color;
            }

        }.execute();
    }

    public void deleteMovieTask(final ParcelableMovie movie) {
        new AsyncTask<ParcelableMovie, Void, Integer>() {
            @Override
            protected Integer doInBackground(ParcelableMovie... movies) {
                try {
                    mMovieDao.deleteMovie(movies[0]);
                    return Color.WHITE;
                } catch (Exception e) {
                    return Color.RED;
                }

            }

            @Override
            protected void onPostExecute(Integer color) {
                favIconColor = color;
            }

        }.execute();
    }


    public class FetchMoviesTask extends AsyncTask<String, Void, String> {

        private MovieRepository delegate = null;

        @Override
        protected String doInBackground(String... category) {
            String response;

            try {
                URL url = NetworkUtils.buildUrl(category[0]);
                response = NetworkUtils.getResponseFromHttpUrl(url);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

            return response;
        }


        @Override
        protected void onPostExecute(String response) {

            try {
                if (response != null) {
                    mMovieList = convertResponseToList(response);
                } else {
                    Log.d(TAG,"Http request returned no results. Please check your internet " +
                            " connection or API key in utils->NetworkUtils->buildUrl->api_key.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "Error converting response to movieList.");
            }

        }


    }

    public List<ParcelableMovie> convertResponseToList (String response) throws JSONException {

        JSONObject responseObject = new JSONObject(response);

        JSONArray moviesArray = responseObject.getJSONArray("results");

        String poster_path;
        int id;
        String title;
        double vote_average;
        String overview;
        String release_date;

        List<ParcelableMovie> moviesList = new ArrayList<>();

        for (int i=0; i < moviesArray.length(); i++) {

            JSONObject object = moviesArray.getJSONObject(i);
            poster_path = object.getString("poster_path");
            id = object.getInt("id");
            title = object.getString("title");
            vote_average = object.getDouble("vote_average");
            overview = object.getString("overview");
            release_date = object.getString("release_date");

            ParcelableMovie movie = new ParcelableMovie(
                    poster_path,
                    id,
                    title,
                    vote_average,
                    overview,
                    release_date
            );


            moviesList.add(movie);
            Log.d(TAG, "getResponseFromHttpUrl: " + movie.getPoster_path());

        }

        return moviesList;
    }

}

