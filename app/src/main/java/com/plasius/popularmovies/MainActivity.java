package com.plasius.popularmovies;

import android.annotation.SuppressLint;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String>{
    private static final int MOVIE_LOADER_ID = 3204;
    private static final String LOAD_TYPE_EXTRA = "whattoload";
    private static final String LOAD_TOP_RATED = "top_rated";
    private static String cache = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initLoader();

    }

    private void initLoader() {
        Bundle bundle = new Bundle();
        bundle.putString(LOAD_TYPE_EXTRA, LOAD_TOP_RATED);

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> loader = loaderManager.getLoader(MOVIE_LOADER_ID);
        if(loader==null){
            loaderManager.initLoader(MOVIE_LOADER_ID, bundle, this);
        }else{
            loaderManager.restartLoader(MOVIE_LOADER_ID, bundle, this);
        }

    }

    private void initGridView(String[] images){
        GridView gridview = findViewById(R.id.gridview);
        gridview.setAdapter(new IconAdapter(images, this));
    }

    private  String[] formatAPIResponse(String data){
        String[] images= new String[20];
        String baseURL = "http://image.tmdb.org/t/p/"+"w185/";
        try {
            JSONObject jObject = new JSONObject(data);
            JSONArray jsonImages = jObject.getJSONArray("results");
            for(int i=0; i<jsonImages.length(); i++){
                images[i] = baseURL + jsonImages.getJSONObject(i).getString("poster_path");
            }

        }catch (Exception e){
            e.printStackTrace();
        }


        Log.v("MainActivity", data);
        return images;
    }

    //LoaderManager
    @SuppressLint("StaticFieldLeak")
    @Override
    public Loader<String> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<String>(this) {
            @Override
            public String loadInBackground() {


                HttpURLConnection urlConnection = null;
                try {
                    URL url = new URL("http://api.themoviedb.org/3/movie/"+args.getString(LOAD_TYPE_EXTRA)+"?api_key="+ "6c07d7cb591067d42a5814368f770c5d");
                    urlConnection = (HttpURLConnection) url.openConnection();
                    InputStream in = urlConnection.getInputStream();

                    Scanner scanner = new Scanner(in);
                    scanner.useDelimiter("\\A");

                    boolean hasInput = scanner.hasNext();
                    if (hasInput) {
                        return scanner.next();
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

            @Override
            protected void onStartLoading() {
                if(cache != null){
                    deliverResult(cache);
                    return;
                }

                forceLoad();
            }

            @Override
            public void deliverResult(String data) {
                cache= data;
                initGridView(formatAPIResponse(data));
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {

    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }
}
