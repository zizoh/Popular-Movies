package com.zizohanto.popularmovies.ui.movies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.zizohanto.popularmovies.R;
import com.zizohanto.popularmovies.utils.ActivityUtils;

public class MoviesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movies_act);

        addFragmentToActivity();
    }

    private void addFragmentToActivity() {
        MoviesFragment tasksFragment =
                (MoviesFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (tasksFragment == null) {
            // Create the fragment
            tasksFragment = MoviesFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), tasksFragment, R.id.contentFrame);
        }
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }
}
