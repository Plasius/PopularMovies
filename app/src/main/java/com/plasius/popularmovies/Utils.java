package com.plasius.popularmovies;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by PlasiusPC on 17.02.2018.
 */

public class Utils {

    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static String getStorageLocation(final String movieid, String movieImage, final Context c) {
        Picasso.with(c)
                .load(movieImage)
                .into(new Target() {
                          @Override
                          public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                              try {

                                  String name = movieid + ".jpg";
                                  FileOutputStream out = c.openFileOutput(name, Context.MODE_PRIVATE);
                                  bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);

                                  out.flush();
                                  out.close();

                              } catch (Exception e) {
                                  e.printStackTrace();
                              }
                          }

                          @Override
                          public void onBitmapFailed(Drawable errorDrawable) {
                          }

                          @Override
                          public void onPrepareLoad(Drawable placeHolderDrawable) {
                          }
                      }
                );


        return "file:" + c.getFilesDir() + "/" + movieid + ".jpg";
    }

    public static void deleteImage(String movieid, Context c) {
        File fdelete = new File(c.getFilesDir(), movieid + ".jpg");
        if (fdelete.exists()) {
            fdelete.delete();
        }
    }

}
