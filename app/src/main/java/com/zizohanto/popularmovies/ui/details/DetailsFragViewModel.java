package com.zizohanto.popularmovies.ui.details;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.zizohanto.popularmovies.data.PopularMoviesRepository;
import com.zizohanto.popularmovies.data.database.Movie;

class DetailsFragViewModel extends ViewModel {

    private final PopularMoviesRepository mRepository;
    private final LiveData<Movie> mMovie;
    private final String mTitle;

    public DetailsFragViewModel(PopularMoviesRepository repository, String title) {
        mRepository = repository;
        mTitle = title;
        mMovie = mRepository.getMovieByTitle(mTitle);
    }

    public LiveData<Movie> getMovie() {
        return mMovie;
    }
}
