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
        MoviesFragment moviesFragment =
                (MoviesFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (moviesFragment == null) {
            // Create the fragment
            moviesFragment = MoviesFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), moviesFragment, R.id.contentFrame);
        }
    }
    /* Add Common Project Requirements */
    // TODOC 1: Movies are displayed in the main layout via a grid of their corresponding movie
    // poster thumbnails.
    // TODOC 2: UI contains an element (i.e a spinner or settings menu) to toggle the sort order of
    // the movies by: most popular, highest rated.
    // TODOC 3: UI contains a screen for displaying the details for a selected movie.
    // TODOC 4: Movie details layout contains title, release date, movie poster, vote average, and
    // plot synopsis.
    // TODOC 5: App utilizes stable release versions of all libraries, Gradle, and Android Studio.
    // TODOC 6: When a user changes the sort criteria (“most popular and highest rated”) the main
    // view gets updated correctly.
    // TODOC 7: When a movie poster thumbnail is selected, the movie details screen is launched.
    // TODOC 8: In a background thread, app queries the /movie/popular or /movie/top_rated API for
    // the sort criteria specified in the settings menu.

    // TODOC 9: Remove API Key before sharing code publicly
    // TODOC 10: App does not crash when there is no network connection
    // TODO 11: Use timber for logging
}
