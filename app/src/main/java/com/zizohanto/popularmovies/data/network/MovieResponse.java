package com.zizohanto.popularmovies.data.network;

import android.support.annotation.NonNull;

import com.zizohanto.popularmovies.data.database.Movie;

/**
 * Movie response from the backend. Contains the movie.
 */
class MovieResponse {

    @NonNull
    private final Movie[] mMovies;

    public MovieResponse(@NonNull final Movie[] movies) {
        mMovies = movies;
    }

    public Movie[] getMovies() {
        return mMovies;
    }
}