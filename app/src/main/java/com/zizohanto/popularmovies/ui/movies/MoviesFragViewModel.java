package com.zizohanto.popularmovies.ui.movies;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.zizohanto.popularmovies.data.PopularMoviesRepository;
import com.zizohanto.popularmovies.data.database.Movie;

import java.util.List;

public class MoviesFragViewModel extends ViewModel {
    private final PopularMoviesRepository mRepository;
    private LiveData<List<Movie>> mMovies;
    private String mMoviesSortType;
    private Boolean mIsNotFirstPreferenceChange;

    public MoviesFragViewModel(PopularMoviesRepository repository, String moviesSortType,
                               Boolean isNotFirstPreferenceChange) {
        mRepository = repository;
        mMoviesSortType = moviesSortType;
        mIsNotFirstPreferenceChange = isNotFirstPreferenceChange;
        mRepository.setSortingCriteria(mMoviesSortType, mIsNotFirstPreferenceChange);
        mMovies = mRepository.getCurrentMovies();
    }

    public LiveData<List<Movie>> getMovies() {
        return mMovies;
    }

    public void getCurrentMovies(String moviesSortType, Boolean isNotFirstPreferenceChange) {
        mRepository.setSortingCriteria(moviesSortType, isNotFirstPreferenceChange);
        mMovies = mRepository.getCurrentMovies();
    }
}
