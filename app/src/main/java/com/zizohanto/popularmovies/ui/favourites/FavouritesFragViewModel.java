package com.zizohanto.popularmovies.ui.favourites;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.zizohanto.popularmovies.data.PopularMoviesRepository;
import com.zizohanto.popularmovies.data.database.favouritemovie.FavouriteMovie;
import com.zizohanto.popularmovies.data.database.movie.Movie;

import java.util.List;

public class FavouritesFragViewModel extends ViewModel {
    private final PopularMoviesRepository mRepository;
    private LiveData<List<FavouriteMovie>> mFavouriteMovies;

    FavouritesFragViewModel(PopularMoviesRepository repository) {
        mRepository = repository;
        mFavouriteMovies = mRepository.getAllFavouriteMovies();
    }

    public LiveData<List<FavouriteMovie>> getAllFavouriteMovies() {
        return mFavouriteMovies;
    }

    public void refreshFavouriteMovies() {
        mFavouriteMovies = mRepository.getAllFavouriteMovies();
    }

    public LiveData<List<Movie>> getFavouriteMoviesByIds(int[] ids) {
        return mRepository.getMoviesByIds(ids);
    }
}
