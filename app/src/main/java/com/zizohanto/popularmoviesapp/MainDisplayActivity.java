package com.zizohanto.popularmoviesapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainDisplayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maindisplay_act);
    }
    /* Add Common Project Requirements */
    // TODO 1: Movies are displayed in the main layout via a grid of their corresponding movie
    // poster thumbnails.
    // TODO 2: UI contains an element (i.e a spinner or settings menu) to toggle the sort order of
    // the movies by: most popular, highest rated.
    // TODO 3: UI contains a screen for displaying the details for a selected movie.
    // TODO 4: Movie details layout contains title, release date, movie poster, vote average, and
    // plot synopsis.
    // TODO 5: App utilizes stable release versions of all libraries, Gradle, and Android Studio.
    // TODO 6: When a user changes the sort criteria (“most popular and highest rated”) the main
    // view gets updated correctly.
    // TODO 7: When a movie poster thumbnail is selected, the movie details screen is launched.
    // TODO 8: In a background thread, app queries the /movie/popular or /movie/top_rated API for
    // the sort criteria specified in the settings menu.
}
