package com.plasius.popularmovies;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.plasius.popularmovies.data.Movie;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String[]>{
    public static final String INTENT_EXTRA = "movieextra";
    private static final int MOVIE_LOADER_ID = 3205;

    @BindView(R.id.detail_title_tv) TextView tv_title;
    @BindView(R.id.detail_rating_tv) TextView tv_rating;
    @BindView(R.id.detail_release_tv) TextView tv_release;
    @BindView(R.id.detail_overview_tv) TextView tv_overview;
    @BindView(R.id.detail_iv) ImageView iv;

    private static Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        movie = getIntent().getExtras().getParcelable(INTENT_EXTRA);
        initMovieLoader();
        processIntent(movie);
    }

    private void initMovieLoader() {
        if(!Utils.isOnline(this)) {
            Toast.makeText(this, "Please get a connection.", Toast.LENGTH_SHORT).show();
            return;
        }

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String[]> loader = loaderManager.getLoader(MOVIE_LOADER_ID);
        if(loader==null){
            loaderManager.initLoader(MOVIE_LOADER_ID, null, this);
        }else{
            loaderManager.restartLoader(MOVIE_LOADER_ID, null, this);
        }


    }

    //called with json to populate UI
    private void processIntent(Movie movie){
            tv_title.setText(movie.getTitle());
            tv_rating.setText(Double.toString(movie.getAverage()));
            tv_release.setText(movie.getRelease());
            tv_overview.setText(movie.getOverview());
            Picasso.with(this).load(movie.getImagePath()).resize(185,278).into(iv);

    }

    //LoaderManager
    @Override
    public Loader<String[]> onCreateLoader(int id, Bundle args) {
        return new MovieAsyncLoader(this, args);
    }

    @Override
    public void onLoadFinished(Loader<String[]> loader, String[] data) {

    }

    @Override
    public void onLoaderReset(Loader<String[]> loader) {

    }


    static class MovieAsyncLoader extends AsyncTaskLoader<String[]> {
        DetailActivity context;
        Bundle args;
        private MovieAsyncLoader(DetailActivity c, Bundle a){
            super(c);
            context = c;
            args= a;
        }

        @Override
        public String[] loadInBackground() {
            HttpURLConnection urlConnection = null;
            String[] response = new String[2];
            try {
                URL url = new URL("http://api.themoviedb.org/3/movie/"+movie.getId()+"/videos?api_key="+ BuildConfig.API_KEY);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();

                Scanner scanner = new Scanner(in);
                scanner.useDelimiter("\\A");

                boolean hasInput = scanner.hasNext();
                if (hasInput) {
                    response[0]= scanner.next();
                } else {
                    response[0] = null;
                }



                urlConnection.disconnect();


                url = new URL("http://api.themoviedb.org/3/movie/"+movie.getId()+"/reviews?api_key="+ BuildConfig.API_KEY);
                urlConnection = (HttpURLConnection) url.openConnection();
                in = urlConnection.getInputStream();

                scanner = new Scanner(in);
                scanner.useDelimiter("\\A");

                hasInput = scanner.hasNext();
                if (hasInput) {
                    response[1]= scanner.next();
                } else {
                    response[1] = null;
                }
            }catch(Exception e){
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }
            return response;
        }

        @Override
        protected void onStartLoading() {

            forceLoad();
        }

        @Override
        public void deliverResult(String[] data) {
            Toast.makeText(getContext(), data[0], Toast.LENGTH_LONG).show();
            Toast.makeText(getContext(), data[1], Toast.LENGTH_LONG).show();
        }
    }
}
