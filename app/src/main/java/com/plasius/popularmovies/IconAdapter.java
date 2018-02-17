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
    Context context;
    String[] images;
    long[] ids;
    public IconAdapter(String[] images, long[] ids, Context c){
        context= c;
        this.images = images;
        this.ids= ids;
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v;
        if (convertView == null) {
            v = inflater.inflate(R.layout.item_icon, null);
        }else{
            v= convertView;
        }

        v.setTag(ids[position]);
        ImageView img = v.findViewById(R.id.item_icon_iv);
        Picasso.with(context).load(images[position]).into(img);
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