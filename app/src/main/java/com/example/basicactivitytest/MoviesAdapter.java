package com.example.basicactivitytest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.basicactivitytest.model.ParcelableMovie;
import com.example.basicactivitytest.utils.GlideApp;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

// Create the basic adapter extending from RecyclerView.Adapter
public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder {
        // Holder declares all variables required for view, only poster image here
        public ImageView poster_iv;
        public TextView title_tv;

        // Constructor used to set view variables
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.poster_iv = (ImageView) itemView.findViewById(R.id.poster_iv);
            this.title_tv = (TextView) itemView.findViewById(R.id.title_tv);

        }
    }

    // Declare movies parcelable variable
    private Context context;
    private List<ParcelableMovie> movieList;
    private LiveData<List<ParcelableMovie>> favoriteMovieList;

    public void setFavoriteMovieList(List<ParcelableMovie> movies) {
        movieList = movies;
        notifyDataSetChanged();
    }

    // Constructor used to set movieList
    public MoviesAdapter(Context context, List<ParcelableMovie> movieList) {
        this.context = context;
        this.movieList = movieList;
    }

    @NonNull
    @Override
    public MoviesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();

        // Inflate view
        View movieView = inflater.inflate(R.layout.movie_poster_itemview, parent, false);

        // Return holder
        ViewHolder viewHolder = new ViewHolder(movieView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MoviesAdapter.ViewHolder holder, int position) {

        // Get movie
        final ParcelableMovie movie = movieList.get(position);

        ImageView poster_iv = holder.poster_iv;
        TextView title_tv = holder.title_tv;

        final String urlPath = "http://image.tmdb.org/t/p/w185/" + movie.getPoster_path();

        Picasso.get()
                .load(urlPath)
                .into(poster_iv, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d("Picasso", urlPath);
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.d("Picasso", e.getMessage());
                    }
                });
        poster_iv.setAdjustViewBounds(true);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (context instanceof MainActivity) {
                    Log.d("onclick",movie.getTitle());
                    Intent intent = new Intent(context, DetailActivity.class);
                    intent.putExtra("movie", movie);
                    context.startActivity(intent);
                }
            }
        });



    }

    @Override
    public int getItemCount() {
        if (movieList != null && !movieList.isEmpty()) {
            return movieList.size();
        } else {
            return 0;
        }
    }

}

