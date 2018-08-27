package com.zizohanto.popularmovies.ui.movies;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.zizohanto.popularmovies.data.PopularMoviesRepository;
import com.zizohanto.popularmovies.data.database.Movie;

import java.util.List;

public class MoviesFragmentViewModel extends ViewModel {
    private final PopularMoviesRepository mRepository;
    private LiveData<List<Movie>> mMovies;
    private int mMoviesSortType;

    public MoviesFragmentViewModel(PopularMoviesRepository repository, int moviesSortType) {
        mRepository = repository;
        mMoviesSortType = moviesSortType;
        mMovies = mRepository.getCurrentMovies(mMoviesSortType);
    }

    public LiveData<List<Movie>> getMovies() {
        return mMovies;
    }
}
