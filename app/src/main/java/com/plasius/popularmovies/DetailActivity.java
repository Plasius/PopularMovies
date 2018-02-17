package com.plasius.popularmovies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class DetailActivity extends AppCompatActivity {
    public static final String INTENT_EXTRA = "movieextra";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toast.makeText(this, getIntent().getStringExtra(INTENT_EXTRA), Toast.LENGTH_SHORT).show();
    }
}
