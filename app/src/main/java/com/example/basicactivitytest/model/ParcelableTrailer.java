package com.example.basicactivitytest.model;


import android.os.Parcel;
import android.os.Parcelable;

public class ParcelableTrailer implements Parcelable {

    private String name;
    private String key;

    public ParcelableTrailer(String  name,
                             String key) {
        this.name = name;
        this.key = key;
    }

    protected ParcelableTrailer (Parcel in) {
        this.name = in.readString();
        this.key = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.name);
        parcel.writeString(this.key);
    }

    public static final Parcelable.Creator<ParcelableTrailer> CREATOR
            = new Parcelable.Creator<ParcelableTrailer>() {
        public ParcelableTrailer createFromParcel(Parcel in) {
            return new ParcelableTrailer(in);
        }
        public ParcelableTrailer[] newArray(int size) {
            return new ParcelableTrailer[size];
        }
    };


    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }


}
