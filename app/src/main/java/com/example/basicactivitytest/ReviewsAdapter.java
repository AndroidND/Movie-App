package com.example.basicactivitytest;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.basicactivitytest.model.ParcelableReview;
import com.example.basicactivitytest.model.ParcelableTrailer;

import java.util.List;

// Create the basic adapter extending from RecyclerView.Adapter
public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder {
        // Holder declares all variables required for view
        private TextView tv_author;
        private TextView tv_content;

        // Constructor used to set view variables
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tv_author = (TextView) itemView.findViewById(R.id.author_tv);
            this.tv_content = (TextView) itemView.findViewById(R.id.content_tv);
        }
    }

    // Declare movies parcelable variable
    private Context context;
    private List<ParcelableReview> reviewList;

    // Constructor used to set trailerList
    public ReviewsAdapter(Context context, List<ParcelableReview> reviewList) {
        this.context = context;
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public ReviewsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();

        // Inflate view
        View reviewView = inflater.inflate(R.layout.review_view, parent, false);

        // Return holder
        ViewHolder viewHolder = new ViewHolder(reviewView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewsAdapter.ViewHolder holder, int position) {

        if (reviewList != null) {
            final ParcelableReview review = reviewList.get(position);

            TextView tv_author = holder.tv_author;
            TextView tv_content = holder.tv_content;
            tv_author.setText("author: " + review.getAuthor());
            tv_content.setText(review.getContent());
        }


    }

    @Override
    public int getItemCount() {
        return reviewList == null ? 0 : reviewList.size();

    }

}