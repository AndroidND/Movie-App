package com.example.basicactivitytest;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.basicactivitytest.data.MovieDao;
import com.example.basicactivitytest.data.MovieDatabase;
import com.example.basicactivitytest.model.ParcelableMovie;
import com.example.basicactivitytest.model.ParcelableReview;
import com.example.basicactivitytest.model.ParcelableTrailer;
import com.example.basicactivitytest.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    private TextView detail_title_tv;
    private ImageView detail_poster_iv;
    private TextView release_date_tv;
    private TextView vote_average_tv;
    private TextView overview_tv;
    private ImageView starred_iv;
    private RecyclerView trailers_rv;
    private RecyclerView reviews_rv;


    private TrailersAdapter trailersAdapter;
    private ReviewsAdapter reviewsAdapter;

    private RecyclerView trailerRecyclerView;
    private RecyclerView reviewRecyclerView;

    private List<ParcelableTrailer> trailerList;
    private List<ParcelableReview> reviewList;

    private View trailer_view;
    private boolean isFavorite;

    private ImageView iv_favorite;

    ParcelableMovie movie;
    ParcelableMovie favoriteMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        iv_favorite = (ImageView) findViewById(R.id.iv_favorite);

        isFavorite = true;

        iv_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    new UpdateFavoritesTask(DetailActivity.this).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        movie = intent.getParcelableExtra("movie");

        new FetchFavoriteMovieDetailTask(this).execute(movie);

        detail_title_tv = (TextView) findViewById(R.id.tv_movie_title);
        detail_poster_iv = (ImageView) findViewById(R.id.iv_detail_activity_poster);
        release_date_tv = (TextView) findViewById(R.id.tv_release_date);
        vote_average_tv = (TextView) findViewById(R.id.tv_vote_average);
        overview_tv = (TextView) findViewById(R.id.tv_overview);
        trailers_rv = (RecyclerView) findViewById(R.id.rv_trailers);
        reviews_rv = (RecyclerView) findViewById(R.id.rv_reviews);

        detail_title_tv.setText(movie.getTitle());


        String urlPath = "http://image.tmdb.org/t/p/w185/" + movie.getPoster_path();

        release_date_tv.setText(movie.getRelease_date());
        vote_average_tv.setText(Double.toString(movie.getVote_average()) + " / 10");
        overview_tv.setText(movie.getOverview());


        NetworkUtils networkUtils = new NetworkUtils(this);
        if (networkUtils.isNetworkAvailable()) {
            URL trailerUrl = NetworkUtils.buildTrailerUrl(String.valueOf(movie.getId()));
            URL reviewUrl = NetworkUtils.buildReviewUrl(String.valueOf(movie.getId()));

            trailerRecyclerView = (RecyclerView) findViewById(R.id.rv_trailers);
            trailersAdapter = new TrailersAdapter(this, null);
            trailerRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            trailerRecyclerView.setAdapter(trailersAdapter);

            reviewRecyclerView = (RecyclerView) findViewById(R.id.rv_reviews);
            reviewsAdapter = new ReviewsAdapter(this,null);
            reviewRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            reviewRecyclerView.setAdapter(reviewsAdapter);

            Picasso.get()
                    .load(urlPath)
                    .into(detail_poster_iv);
            detail_poster_iv.setAdjustViewBounds(true);

            try {
                new FetchTrailersTask().execute(trailerUrl);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                new FetchReviewsTask().execute(reviewUrl);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpTo(this, new Intent(this, MainActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public class FetchReviewsTask extends AsyncTask<URL, Integer, String> {

        @Override
        protected String doInBackground(URL... url) {
            String response;
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

                    reviewList = convertResponseToReviews(response);
                    reviewsAdapter = new ReviewsAdapter(DetailActivity.this, reviewList);
                    reviewRecyclerView.setAdapter(reviewsAdapter);

                } else {
//                    main_content_tv.setText("Http request returned no results. Please check your internet " +
//                            " connection or API key in utils->NetworkUtils->buildUrl->api_key.");
                }
            } catch (Exception e) {
                e.printStackTrace();
//                main_content_tv .setText("Error converting response to movieList.");
            }

        }


    }

    public List<ParcelableReview> convertResponseToReviews(String response) throws JSONException {

        JSONObject responseObject = new JSONObject(response);

        JSONArray reviewsArray = responseObject.getJSONArray("results");

        List<ParcelableReview> reviews = new ArrayList<>();

        for (int i = 0; i < reviewsArray.length(); i++) {
            JSONObject object = reviewsArray.getJSONObject(i);
            String author = object.getString("author");
            String content = object.getString("content");
            ParcelableReview review = new ParcelableReview(author, content);
            reviews.add(review);
        }
        return reviews;
    }


    public class FetchTrailersTask extends AsyncTask<URL, Integer, String> {

        @Override
        protected String doInBackground(URL... url) {
            String response;
            try {
                response = NetworkUtils.getResponseFromHttpUrl(url[0]);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

            return response;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // implement if have time: setProgressPercent(progress[0]
        }

        @Override
        protected void onPostExecute(String response) {

            try {
                if (response != null) {

                    trailerList = convertResponseToTrailersList(response);
                    trailersAdapter = new TrailersAdapter(DetailActivity.this, trailerList);
                    trailerRecyclerView.setAdapter(trailersAdapter);

                } else {
//                    main_content_tv.setText("Http request returned no results. Please check your internet " +
//                            " connection or API key in utils->NetworkUtils->buildUrl->api_key.");
                }
            } catch (Exception e) {
                e.printStackTrace();
//                main_content_tv .setText("Error converting response to movieList.");
            }

        }


    }

    public List<ParcelableTrailer> convertResponseToTrailersList(String response) throws JSONException {


        JSONObject responseObject = new JSONObject(response);

        JSONArray trailersArray = responseObject.getJSONArray("results");

        String name;
        String key;

        List<ParcelableTrailer> trailersList = new ArrayList<>();

        for (int i = 0; i < trailersArray.length(); i++) {

            JSONObject object = trailersArray.getJSONObject(i);
            name = object.getString("name");
            key = object.getString("key");

            ParcelableTrailer trailer = new ParcelableTrailer(
                    name,
                    key
            );

            trailersList.add(trailer);

        }

        return trailersList;
    }


    public class FetchFavoriteMovieDetailTask extends AsyncTask<ParcelableMovie, Void, Integer> {

        private WeakReference<Activity> weakActivity;

        public FetchFavoriteMovieDetailTask(Activity activity) {
            weakActivity = new WeakReference<>(activity);
        }

        @Override
        protected Integer doInBackground(ParcelableMovie... movies) {

            MovieDatabase db = Room.databaseBuilder(DetailActivity.this, MovieDatabase.class, "MovieDatabase")
                    .fallbackToDestructiveMigration()
                    .build();
            MovieDao dao = db.getMovieDao();

            if (dao.getCount() == 0) {
                return Color.WHITE;
            }

            ParcelableMovie movie = dao.getFavoriteMovie(movies[0].getId());

            Integer color;
            if (movie != null) {
                color = Color.RED;
            } else {
                color = Color.WHITE;
            }
            return color;
        }

        @Override
        protected void onPostExecute(Integer color) {
            super.onPostExecute(color);
            iv_favorite.setColorFilter(color);
        }
    }

    public class UpdateFavoritesTask extends AsyncTask<Void, Void, Integer> {

        private WeakReference<Activity> weakActivity;

        public UpdateFavoritesTask(Activity activity) {
            weakActivity = new WeakReference<>(activity);
        }
        @Override
        protected Integer doInBackground(Void... param) {
            MovieDatabase db = Room.databaseBuilder(DetailActivity.this, MovieDatabase.class, "MovieDatabase")
                    .fallbackToDestructiveMigration()
                    .build();
            MovieDao dao = db.getMovieDao();

            if (dao.getCount() == 0) {
                // Empty db, add and turn red
                dao.insertMovie(movie);
                return Color.RED;

            } else {
                favoriteMovie = dao.getFavoriteMovie(movie.getId());
                if (favoriteMovie != null) {
                    // Movie currently favorited - remove and un-favorite, turn white
                    dao.deleteMovie(favoriteMovie);
                    return Color.WHITE;
                } else {
                    // Not in db, add and turn red
                    dao.insertMovie(movie);
                    return Color.RED;
                }
            }
        }

        @Override
        protected void onPostExecute(Integer color) {
            iv_favorite.setColorFilter(color);
        }
    }



}