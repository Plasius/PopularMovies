package com.plasius.popularmovies;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String>{
    public static final String INTENT_EXTRA = "movieextra";
    private static final int MOVIE_LOADER_ID = 3205;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        initMovieLoader();
    }

    private void initMovieLoader() {
        if(!Utils.isOnline(this)) {
            Toast.makeText(this, "Please get a connection.", Toast.LENGTH_SHORT).show();
            return;
        }

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> loader = loaderManager.getLoader(MOVIE_LOADER_ID);
        if(loader==null){
            loaderManager.initLoader(MOVIE_LOADER_ID, null, this);
        }else{
            loaderManager.restartLoader(MOVIE_LOADER_ID, null, this);
        }


    }

    //called with json to populate UI
    private void processAPIResponse(String data){
        String baseURL = "http://image.tmdb.org/t/p/"+"w342/";
        try{
            JSONObject jsonObject = new JSONObject(data);
            ((TextView)findViewById(R.id.detail_title_tv)).setText(jsonObject.getString("title"));
            ((TextView)findViewById(R.id.detail_rating_tv)).setText(Double.toString(jsonObject.getDouble("vote_average")));
            ((TextView)findViewById(R.id.detail_release_tv)).setText(jsonObject.getString("release_date"));
            ((TextView)findViewById(R.id.detail_overview_tv)).setText(jsonObject.getString("overview"));
            ImageView iv = ((ImageView)findViewById(R.id.detail_iv));
            Picasso.with(this).load(baseURL+jsonObject.getString("poster_path")).resize(185,278).into(iv);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    //LoaderManager
    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        return new MovieAsyncLoader(this, args);
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {

    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }


    static class MovieAsyncLoader extends AsyncTaskLoader<String> {
        DetailActivity context;
        Bundle args;
        private MovieAsyncLoader(DetailActivity c, Bundle a){
            super(c);
            context = c;
            args= a;
        }

        @Override
        public String loadInBackground() {


            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL("http://api.themoviedb.org/3/movie/"+context.getIntent().getStringExtra(INTENT_EXTRA)+"?api_key="+ context.getString(R.string.API_KEY));
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
            context.processAPIResponse(data);
        }
    }
}
