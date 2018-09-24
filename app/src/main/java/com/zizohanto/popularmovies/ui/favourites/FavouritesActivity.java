package com.zizohanto.popularmovies.ui.favourites;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.zizohanto.popularmovies.R;
import com.zizohanto.popularmovies.utils.ActivityUtils;

public class FavouritesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favourites_act);

        addFragmentToActivity();
    }

    private void addFragmentToActivity() {
        FavouritesFragment favouritesFragment =
                (FavouritesFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (favouritesFragment == null) {
            // Create the fragment
            favouritesFragment = FavouritesFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), favouritesFragment, R.id.contentFrame);
        }
    }
}