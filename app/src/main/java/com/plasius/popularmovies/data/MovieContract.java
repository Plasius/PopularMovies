package com.plasius.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by PlasiusPC on 23.02.2018.
 */

public class MovieContract {
    public static final String AUTHORITY = "com.plasius.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_MOVIES = "movies";

    public static final class MovieEntry implements BaseColumns{
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static String TABLE_NAME = "movies";
        public static final String COL_ID = "movieid";
        public static final String COL_TITLE = "title";
        public static final String COL_RELEASE = "release";
        public static final String COL_IMAGE = "image";
        public static final String COL_VOTE_AVERAGE = "average";
        public static final String COL_OVERVIEW = "overview";
    }
}
