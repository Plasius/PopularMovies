package com.plasius.popularmovies.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by PlasiusPC on 22.02.2018.
 */

public class Movie implements Parcelable {
    //parcel
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
    private Long id;
    private String title;
    private String release;
    private String imagePath;
    private String overview;
    private Double average;

    public Movie(long iD, String t, String r, String iP, String o, Double a) {
        title = t;
        release = r;
        imagePath = iP;
        overview = o;
        average = a;
        id = iD;
    }

    private Movie(Parcel in) {
        id = in.readLong();
        title = in.readString();
        release = in.readString();
        imagePath = in.readString();
        overview = in.readString();
        average = in.readDouble();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRelease() {
        return release;
    }

    public void setRelease(String release) {
        this.release = release;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public Double getAverage() {
        return average;
    }

    public void setAverage(Double average) {
        this.average = average;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeString(release);
        dest.writeString(imagePath);
        dest.writeString(overview);
        dest.writeDouble(average);
    }

}
