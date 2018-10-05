package com.zizohanto.popularmovies.ui.movies;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.zizohanto.popularmovies.data.PopularMoviesRepository;
import com.zizohanto.popularmovies.data.database.favouritemovie.FavouriteMovie;
import com.zizohanto.popularmovies.data.database.movie.Movie;
import com.zizohanto.popularmovies.utils.NetworkState;

import java.util.List;

public class MoviesFragViewModel extends ViewModel {
    private final PopularMoviesRepository mRepository;
    private LiveData<List<Movie>> mMovies;
    private LiveData<NetworkState> mNetworkState;

    MoviesFragViewModel(PopularMoviesRepository repository, String moviesSortType,
                        Boolean isNotPreferenceChange, int pageToLoad) {
        mRepository = repository;
        mRepository.setFetchMoviesCriteria(moviesSortType, isNotPreferenceChange, pageToLoad);
        mMovies = mRepository.getCurrentMovies();
        mNetworkState = mRepository.getNetworkState();
    }

    public LiveData<List<Movie>> getMovies() {
        return mMovies;
    }

    public void getCurrentMovies(String moviesSortType, Boolean isNotPreferenceChange, int pageToLoad) {
        mRepository.setFetchMoviesCriteria(moviesSortType, isNotPreferenceChange, pageToLoad);
        mMovies = mRepository.getCurrentMovies();
    }

    public LiveData<NetworkState> getNetworkState() {
        return mNetworkState;
    }

    public LiveData<List<FavouriteMovie>> getAllFavouriteMovies() {
        return mRepository.getAllFavouriteMovies();
    }

    public LiveData<List<Movie>> getFavouriteMoviesByIds(int[] ids) {
        return mRepository.getMoviesByIds(ids);
    }
}