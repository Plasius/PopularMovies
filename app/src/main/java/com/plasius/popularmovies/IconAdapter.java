package com.plasius.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by PlasiusPC on 17.02.2018.
 */

public class IconAdapter extends BaseAdapter{
    private Context context;
    Movie[] movies;

    IconAdapter(Movie[] movies, Context c){
        context= c;
        this.movies = movies;
    }

    @Override
    public int getCount() {
        return movies.length;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v;
        if (convertView == null) {
            v = inflater.inflate(R.layout.item_icon, null);
        }else{
            v= convertView;
        }

        ImageView img = v.findViewById(R.id.item_icon_iv);
        Picasso.with(context).load(movies[position].imagePath).resize(185,278).into(img);
        return v;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
}
