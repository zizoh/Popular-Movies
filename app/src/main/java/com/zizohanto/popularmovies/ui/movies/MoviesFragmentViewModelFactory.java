package com.zizohanto.popularmovies.ui.movies;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.zizohanto.popularmovies.data.PopularMoviesRepository;

/**
 * Factory method that to create a ViewModel with a constructor that takes a
 * {@link com.zizohanto.popularmovies.data.PopularMoviesRepository}
 */
public class MoviesFragmentViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private final PopularMoviesRepository mRepository;
    private int mMoviesSortType;

    public MoviesFragmentViewModelFactory(PopularMoviesRepository repository, int moviesSortType) {
        mRepository = repository;
        mMoviesSortType = moviesSortType;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        //noinspection unchecked
        return (T) new MoviesFragmentViewModel(mRepository, mMoviesSortType);
    }
}
