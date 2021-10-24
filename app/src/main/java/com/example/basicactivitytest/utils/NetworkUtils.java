package com.example.basicactivitytest.utils;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class NetworkUtils {

    private static Context context;
    private static String api_key = "46cbb4ecac729ecdb49d8ff5a4cd5d7f";

    public NetworkUtils(Context context) {
        this.context = context;
    }

    public static URL buildUrl(String movieCategory) {
        // Build uri given movie category
        // sample url: https://api.themoviedb.org/3/movie/popular?api_key=

        Uri.Builder builder = new Uri.Builder();
        Uri builtUri = builder.scheme("https")
                .authority("api.themoviedb.org")
                .appendPath("3")
                .appendPath("movie")
                .appendPath(movieCategory)
                .appendQueryParameter("api_key", api_key)
                .build();
        URL url = null;

        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }

        return url;
    }

    public static URL buildTrailerUrl(String movieId) {
        // Build uri given movie category
        // sample url: https://api.themoviedb.org/3/movie/popular?api_key=

        Uri.Builder builder = new Uri.Builder();
        Uri builtUri = builder.scheme("https")
                .authority("api.themoviedb.org")
                .appendPath("3")
                .appendPath("movie")
                .appendPath(movieId)
                .appendPath("videos")
                .appendQueryParameter("api_key", api_key)
                .build();
        URL url = null;

        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }

        return url;
    }

    public static URL buildReviewUrl(String movieId) {
        // Build uri given movie category
        // sample url: https://api.themoviedb.org/3/movie/popular?api_key=

        Uri.Builder builder = new Uri.Builder();
        Uri builtUri = builder.scheme("https")
                .authority("api.themoviedb.org")
                .appendPath("3")
                .appendPath("movie")
                .appendPath(movieId)
                .appendPath("reviews")
                .appendQueryParameter("api_key", api_key)
                .build();
        URL url = null;

        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }

        return url;
    }

    //    public static List<Movie> getResponseFromHttpUrl(URL url) throws IOException {
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        String response;
        BufferedReader reader = null;
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.connect();

        try {
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();

            if (inputStream == null) {
                return  null;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;

            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return null;
            }

            response = buffer.toString();

            return response;

        } catch (Exception e) {
           continue;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }

            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    e.printStackTrace();
                }

            }
        }

        return null;
    }


    // got code from https://stackoverflow.com/questions/4238921/detect-whether-there-is-an-internet-connection-available-on-android
    public static boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
