package com.example.basicactivitytest;

import android.os.AsyncTask;
import android.os.Bundle;

import com.example.basicactivitytest.data.MovieViewModel;
import com.example.basicactivitytest.model.ParcelableMovie;
import com.example.basicactivitytest.utils.NetworkUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {

    public TextView message_textview;

    Toolbar mToolbar;
    String title;
    String response;
    URL url;
    List<ParcelableMovie> movieList;
    ParcelableMovie movie;
    MoviesAdapter moviesAdapter;
    RecyclerView recyclerView;
    String category = "popular";
    String message;

    private MovieViewModel mMovieViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMovieViewModel = ViewModelProviders.of(this).get(MovieViewModel.class);

        message_textview = (TextView) findViewById(R.id.message_textview);

        mToolbar = findViewById(R.id.toolbar);

        toolbarSetup(R.string.popular);

        setSupportActionBar(mToolbar);

        recyclerView = (RecyclerView) findViewById(R.id.movie_recyclerview);
        moviesAdapter = new MoviesAdapter(this,null);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        recyclerView.setAdapter(moviesAdapter);

        displayMovies();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.popular:
                category = "popular";
                toolbarSetup(R.string.popular);
                break;
            case R.id.top_rated:
                category = "top_rated";
                toolbarSetup(R.string.top_rated);
                break;
            case R.id.favorite:
                category = "favorite";
                toolbarSetup(R.string.favorite);
                break;
            default:
                category = null;
                Toast.makeText(this, "Invalid category selected", Toast.LENGTH_SHORT).show();
                break;
        }

        if (category == "favorite") {
            observerSetup();
            recyclerView.setAdapter(moviesAdapter);
            setVisibilityFetchSuccess();
        } else {
            displayMovies();
        }

        return super.onOptionsItemSelected(item);
    }

    private void toolbarSetup(int category) {
        title = getResources().getString(category) + " " + getResources().getString(R.string.movies);
        mToolbar.setTitle(title);
        setSupportActionBar(mToolbar);
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

    public class FetchMoviesTask extends AsyncTask<URL, Integer, String> {

        @Override
        protected String doInBackground(URL... url) {
            try {
                response = NetworkUtils.getResponseFromHttpUrl(url[0]);
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

                    movieList = convertResponseToList(response);
                    moviesAdapter = new MoviesAdapter(MainActivity.this, movieList);
                    recyclerView.setAdapter(moviesAdapter);

                } else {
                    message = "Error fetching movie. Please check your internet connection and api key.";
                    message_textview.setText(message);
                    setVisibilityFetchFailed();
                }
            } catch (Exception e) {
                e.printStackTrace();
                message = "Error fetching movie. Please check your internet connection and api key.";
                message_textview.setText(message);
                setVisibilityFetchFailed();
            }

        }


    }

    private void displayMovies() {

        try {
            NetworkUtils networkUtils = new NetworkUtils(this);
            if (networkUtils.isNetworkAvailable()) {
                // category initialized as "popular" by default
                url = NetworkUtils.buildUrl(category);
                try {
                    new FetchMoviesTask().execute(url);
                    message = "Fetch successful";
                    setVisibilityFetchSuccess();
                } catch (Exception e) {
                    e.printStackTrace();
                    message = "Error fetching movie. Please check your api key.";
                    message_textview.setText(message);
                    setVisibilityFetchFailed();
                }
            } else {
                message = "No internet connection. Movie images cannot be displayed.";
                message_textview.setText(message);
                setVisibilityFetchFailed();
            }
        } catch (Exception e) {
            e.printStackTrace();
            message = "Error fetching movie. Please check your internet connection and api key.";
            message_textview.setText(message);
            setVisibilityFetchFailed();
        }
    }

    private void setVisibilityFetchSuccess() {
        message_textview.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void setVisibilityFetchFailed() {
        message_textview.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    private void observerSetup() {

        mMovieViewModel.getFavoriteMovies().observe(this, new Observer<List<ParcelableMovie>>() {
            @Override
            public void onChanged(@Nullable final List<ParcelableMovie> movies) {
                moviesAdapter.setFavoriteMovieList(movies);
            }
        });

    }

}

