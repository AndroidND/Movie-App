package com.example.basicactivitytest.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ParcelableReview implements Parcelable {

    private String author;
    private String content;

    public ParcelableReview(String  author,
                             String content) {
        this.author = author;
        this.content = content;
    }

    protected ParcelableReview (Parcel in) {
        this.author = in.readString();
        this.content = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.author);
        parcel.writeString(this.content);
    }

    public static final Parcelable.Creator<ParcelableReview> CREATOR
            = new Parcelable.Creator<ParcelableReview>() {
        public ParcelableReview createFromParcel(Parcel in) {
            return new ParcelableReview(in);
        }
        public ParcelableReview[] newArray(int size) {
            return new ParcelableReview[size];
        }
    };


    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }


}
