package com.plasius.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by PlasiusPC on 22.02.2018.
 */

public class Movie implements Parcelable {
    String title;
    String release;
    String imagePath;
    String overview;
    Double average;




    Movie(String t, String r, String iP, String o, Double a){
        title=t;
        release=r;
        imagePath=iP;
        overview=o;
        average=a;
    }


    //parcel
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    private Movie(Parcel in){
        title = in.readString();
        release=in.readString();
        imagePath=in.readString();
        overview=in.readString();
        average=in.readDouble();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(release);
        dest.writeString(imagePath);
        dest.writeString(overview);
        dest.writeDouble(average);
    }
}