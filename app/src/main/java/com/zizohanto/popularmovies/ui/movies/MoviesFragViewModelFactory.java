package com.zizohanto.popularmovies.ui.movies;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.zizohanto.popularmovies.data.PopularMoviesRepository;

/**
 * Factory method that to create a ViewModel with a constructor that takes a
 * {@link com.zizohanto.popularmovies.data.PopularMoviesRepository}
 */
public class MoviesFragViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private final PopularMoviesRepository mRepository;
    private String mMoviesSortType;
    private Boolean mIsNotFirstPreferenceChange;

    public MoviesFragViewModelFactory(PopularMoviesRepository repository,
                                      String moviesSortType, Boolean isNotFirstPreferenceChange) {
        mRepository = repository;
        mMoviesSortType = moviesSortType;
        mIsNotFirstPreferenceChange = isNotFirstPreferenceChange;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        //noinspection unchecked
        return (T) new MoviesFragViewModel(mRepository, mMoviesSortType, mIsNotFirstPreferenceChange);
    }
}
