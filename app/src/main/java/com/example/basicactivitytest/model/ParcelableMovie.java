package com.example.basicactivitytest.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "movie")
public class ParcelableMovie implements Parcelable{

    private String poster_path;
    @PrimaryKey
    private int id;
    private String title;
    private double vote_average;
    private String overview, release_date;

    public ParcelableMovie(String  poster_path, int id,
                 String title, double vote_average, String overview,
                 String release_date) {
        this.poster_path = poster_path;
        this.id = id;
        this.title = title;
        this.vote_average = vote_average;
        this.overview = overview;
        this.release_date = release_date;

    }

    protected ParcelableMovie (Parcel in) {
        this.poster_path = in.readString();
        this.id = in.readInt();
        this.title = in.readString();
        this.vote_average = in.readDouble();
        this.overview = in.readString();
        this.release_date = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.poster_path);
        parcel.writeInt(this.id);
        parcel.writeString(this.title);
        parcel.writeDouble(this.vote_average);
        parcel.writeString(this.overview);
        parcel.writeString((this.release_date));

    }

    public static final Parcelable.Creator<ParcelableMovie> CREATOR
            = new Parcelable.Creator<ParcelableMovie>() {
        public ParcelableMovie createFromParcel(Parcel in) {
            return new ParcelableMovie(in);
        }
        public ParcelableMovie[] newArray(int size) {
            return new ParcelableMovie[size];
        }
    };


    public String getPoster_path() {
        return poster_path;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public double getVote_average() {
        return vote_average;
    }

    public String getOverview() {
        return overview;
    }

    public String getRelease_date() {
        return release_date;
    }

}

