package com.zizohanto.popularmovies.ui.movies;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.zizohanto.popularmovies.data.PopularMoviesRepository;
import com.zizohanto.popularmovies.data.database.Movie;

import java.util.List;

public class MoviesFragmentViewModel extends ViewModel {
    private final PopularMoviesRepository mRepository;
    private final LiveData<List<Movie>> mMovies;

    public MoviesFragmentViewModel(PopularMoviesRepository repository) {
        mRepository = repository;
        mMovies = mRepository.getCurrentMovies();
    }

    public LiveData<List<Movie>> getMovies() {
        return mMovies;
    }
}
