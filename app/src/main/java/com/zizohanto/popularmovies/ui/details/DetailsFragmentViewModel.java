package com.zizohanto.popularmovies.ui.details;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.zizohanto.popularmovies.data.PopularMoviesRepository;
import com.zizohanto.popularmovies.data.database.Movie;

class DetailsFragmentViewModel extends ViewModel {

    private final PopularMoviesRepository mRepository;
    private final LiveData<Movie> mMovie;
    private final String mTitle;

    public DetailsFragmentViewModel(PopularMoviesRepository repository, String title) {
        mRepository = repository;
        mTitle = title;
        mMovie = mRepository.getMovieByTitle(mTitle);
    }

    public LiveData<Movie> getMovie() {
        return mMovie;
    }
}
