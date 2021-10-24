package com.example.basicactivitytest;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.nfc.Tag;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.basicactivitytest.model.ParcelableTrailer;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

import static android.content.ContentValues.TAG;

// Create the basic adapter extending from RecyclerView.Adapter
public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder {
        // Holder declares all variables required for view
        private TextView tv_trailer_name;
        private ImageView iv_trailer;

        // Constructor used to set view variables
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tv_trailer_name = (TextView) itemView.findViewById(R.id.tv_trailer_name);
            this.iv_trailer = (ImageView) itemView.findViewById(R.id.iv_trailer);
        }
    }

    // Declare movies parcelable variable
    private Context context;
    private List<ParcelableTrailer> trailerList;

    // Constructor used to set trailerList
    public TrailersAdapter(Context context, List<ParcelableTrailer> trailerList) {
        this.context = context;
        this.trailerList = trailerList;
    }

    @NonNull
    @Override
    public TrailersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();

        // Inflate view
        View reviewView = inflater.inflate(R.layout.trailer_view, parent, false);

        // Return holder
        ViewHolder viewHolder = new ViewHolder(reviewView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TrailersAdapter.ViewHolder holder, int position) {

        final ParcelableTrailer trailer = trailerList.get(position);

        TextView tv_trailer_name = holder.tv_trailer_name;
        ImageView iv_trailer = holder.iv_trailer;
        tv_trailer_name.setText(trailer.getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String video_id = trailer.getKey();
                Intent youtubeIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://www.youtube.com/watch?v=" + video_id));

                try {
                    context.startActivity(youtubeIntent);
                } catch (Exception e) {
                    Log.d(TAG, "Error youtube trailer launch intent.");
                }
            }
        });
    }



    @Override
    public int getItemCount() {
        if (trailerList != null && !trailerList.isEmpty()) {
            return trailerList.size();
        } else {
            return 0;
        }
    }



}