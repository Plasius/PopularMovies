package com.plasius.popularmovies;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.Toast;

import com.plasius.popularmovies.data.Movie;
import com.plasius.popularmovies.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Movie[]>{
    private static final int MOVIE_LOADER_ID = 3204;
    private static final String LOAD_TYPE_EXTRA = "whattoload";
    private static final String LOAD_TOP_RATED = "top_rated";
    private static final String LOAD_POPULAR = "popular";
    private static final String LOAD_FAVORITED = "favorites";


    Movie[] movies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);

        final Spinner spinner= findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!Utils.isOnline(getBaseContext()) && position!=2){
                    Toast.makeText(getBaseContext(), "Please get a connection.", Toast.LENGTH_SHORT).show();
                    spinner.setSelection(2);
                    return;
                }
                switch (position){
                    case 0:
                        initLoader(LOAD_POPULAR);
                        break;
                    case 1:
                        initLoader(LOAD_TOP_RATED);
                        break;
                    case 2:
                        initLoader(LOAD_FAVORITED);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if(!Utils.isOnline(this)){
            Toast.makeText(this, "Please get a connection.", Toast.LENGTH_SHORT).show();
            initLoader(LOAD_FAVORITED);
            spinner.setSelection(2);
        }else{
            initLoader(LOAD_POPULAR);
        }

        //request permission to store images
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 3204);
        }
    }


    //called with the data by processAPIResponse to build our GridView
    private void initGridView(){
        GridView gridview = findViewById(R.id.gridview);
        if(movies==null){
            gridview.setAdapter(null);
            Toast.makeText(this, "You have no favorites.", Toast.LENGTH_SHORT).show();
            return;
            
        }
        gridview.setAdapter(new IconAdapter(movies, this));
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Intent intent= new Intent(getApplicationContext(), DetailActivity.class);
                intent.putExtra(DetailActivity.INTENT_EXTRA, movies[position]);
                startActivity(intent);


            }
        });


    }


    //called at the start and when the spinner changed
    private void initLoader(String whattoload) {
        Bundle bundle = new Bundle();
        bundle.putString(LOAD_TYPE_EXTRA, whattoload);

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> loader = loaderManager.getLoader(MOVIE_LOADER_ID);
        if(loader==null){
            loaderManager.initLoader(MOVIE_LOADER_ID, bundle, this);
        }else{
            loaderManager.restartLoader(MOVIE_LOADER_ID, bundle, this);
        }

    }




    //LoaderManager
    @Override
    public Loader<Movie[]> onCreateLoader(int id,  Bundle args) {
        return new MoviesAsyncLoader(this, args);
    }

    @Override
    public void onLoadFinished(Loader<Movie[]> loader, Movie[] data) {

    }

    @Override
    public void onLoaderReset(Loader<Movie[]> loader) {

    }

    static class MoviesAsyncLoader extends AsyncTaskLoader<Movie[]>{
        MainActivity context;
        Bundle args;
        private MoviesAsyncLoader(MainActivity c, Bundle a){
            super(c);
            context = c;
            args= a;
        }

        @Override
        public Movie[] loadInBackground() {
            if(args.getString(LOAD_TYPE_EXTRA) == LOAD_FAVORITED){
                Movie[] movies = null;
                Cursor c = context.getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,null,null,null,null);
                if(c!= null){
                    if(c.getCount()>0 && c.moveToFirst()){
                        movies = new Movie[c.getCount()];
                        int i=0;
                        do{
                            movies[i] = new Movie(
                                    c.getLong(c.getColumnIndex(MovieContract.MovieEntry.COL_ID)),
                                    c.getString(c.getColumnIndex(MovieContract.MovieEntry.COL_TITLE)),
                                    c.getString(c.getColumnIndex(MovieContract.MovieEntry.COL_RELEASE)),
                                    c.getString(c.getColumnIndex(MovieContract.MovieEntry.COL_IMAGE)),
                                    c.getString(c.getColumnIndex(MovieContract.MovieEntry.COL_OVERVIEW)),
                                    c.getDouble(c.getColumnIndex(MovieContract.MovieEntry.COL_VOTE_AVERAGE))
                            );
                            i++;
                        }while(c.moveToNext());
                    }
                }

                return movies;
            }

            HttpURLConnection urlConnection = null;
            try {
                //get movie api response
                URL url = new URL("http://api.themoviedb.org/3/movie/"+args.getString(LOAD_TYPE_EXTRA)+"?api_key="+  BuildConfig.API_KEY);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();

                Scanner scanner = new Scanner(in);
                scanner.useDelimiter("\\A");

                if (scanner.hasNext()) {
                    return processResponse(scanner.next());
                } else {
                    return null;
                }

            }catch(Exception e){
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }

            return null;
        }


        private Movie[] processResponse(String data){
            Movie movies [] = new Movie[20];
            String baseURL = "http://image.tmdb.org/t/p/"+"w185/";
            try {
                JSONObject jObject = new JSONObject(data);
                JSONArray jsonImages = jObject.getJSONArray("results");
                for(int i=0; i<jsonImages.length(); i++){
                    movies[i] = new Movie(
                            jsonImages.getJSONObject(i).getLong("id"),
                            jsonImages.getJSONObject(i).getString("title"),
                            jsonImages.getJSONObject(i).getString("release_date"),
                            baseURL+ jsonImages.getJSONObject(i).getString("poster_path"),
                            jsonImages.getJSONObject(i).getString("overview"),
                            jsonImages.getJSONObject(i).getDouble("vote_average")
                    );
                }

            }catch (Exception e){
                e.printStackTrace();
            }
            return movies;
        }

        @Override
        protected void onStartLoading() {

            forceLoad();
        }

        @Override
        public void deliverResult(Movie[] data) {
            context.movies = data;
            context.initGridView();
        }
    }

}