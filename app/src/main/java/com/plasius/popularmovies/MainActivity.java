package com.plasius.popularmovies;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
    private static final String LOAD_POPULAR = "popular";
    private static final String LOAD_FAVORITED = "favorites";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Spinner spinner= findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        initQueryLoader(LOAD_POPULAR);
                        break;
                    case 1:
                        initQueryLoader(LOAD_TOP_RATED);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        initQueryLoader(LOAD_POPULAR);
    }

    //called at the start and when the spinner changed
    private void initQueryLoader(String whattoload) {
        if(!isOnline())
            Toast.makeText(this, "Please get a connection.", Toast.LENGTH_SHORT).show();

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

    //checks whether we have an access to the internet
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    //called with the data by processAPIResponse to build our GridView
    private void initGridView(String[] images, long[] ids){
        GridView gridview = findViewById(R.id.gridview);
        gridview.setAdapter(new IconAdapter(images, ids, this));
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Intent intent= new Intent(getApplicationContext(), DetailActivity.class);
                intent.putExtra(DetailActivity.INTENT_EXTRA, v.getTag().toString());
                startActivity(intent);

            }
        });
    }

    //called when we have data, calls initGridView()
    private void processAPIResponse(String data){
        String[] images= new String[20];
        long[] ids= new long[20];
        String baseURL = "http://image.tmdb.org/t/p/"+"w185/";
        try {
            JSONObject jObject = new JSONObject(data);
            JSONArray jsonImages = jObject.getJSONArray("results");
            for(int i=0; i<jsonImages.length(); i++){
                images[i] = baseURL + jsonImages.getJSONObject(i).getString("poster_path");
                ids[i] = jsonImages.getJSONObject(i).getLong("id");
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        initGridView(images, ids);
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

                forceLoad();
            }

            @Override
            public void deliverResult(String data) {
                processAPIResponse(data);
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
