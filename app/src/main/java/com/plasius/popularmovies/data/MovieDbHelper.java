package com.plasius.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.plasius.popularmovies.data.MovieContract.MovieEntry;

/**
 * Created by PlasiusPC on 24.02.2018.
 */

public class MovieDbHelper extends SQLiteOpenHelper{
    private static String DATABASE_NAME = "movies.db";

    private static final int VERSION = 2;

    MovieDbHelper(Context context){
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID                      + " INTEGER PRIMARY KEY, " +
                MovieEntry.COL_ID                   + " INTEGER NOT NULL, " +
                MovieEntry.COL_TITLE                + " TEXT NOT NULL, " +
                MovieEntry.COL_RELEASE              + " TEXT NOT NULL, " +
                MovieEntry.COL_VOTE_AVERAGE         + " REAL NOT NULL, " +
                MovieEntry.COL_IMAGE                + " TEXT NOT NULL, " +
                MovieEntry.COL_OVERVIEW             + " TEXT NOT NULL);";

        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        onCreate(db);
    }
}
