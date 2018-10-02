package com.zizohanto.popularmovies.ui.details;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.zizohanto.popularmovies.data.PopularMoviesRepository;
import com.zizohanto.popularmovies.data.database.favouritemovie.FavouriteMovie;
import com.zizohanto.popularmovies.data.database.movie.Movie;
import com.zizohanto.popularmovies.data.database.review.Review;
import com.zizohanto.popularmovies.data.database.video.Video;

import java.util.List;

class DetailsFragViewModel extends ViewModel {

    private final PopularMoviesRepository mRepository;
    private final LiveData<Movie> mMovie;
    private final LiveData<List<Video>> mVideos;
    private final LiveData<List<Review>> mRevies;
    private final LiveData<FavouriteMovie> mFavouriteMovie;
    private final Integer mId;

    public DetailsFragViewModel(PopularMoviesRepository repository, Integer id) {
        mRepository = repository;
        mId = id;
        mRepository.setFetchMoviesCriteria(mId);
        mMovie = mRepository.getMovie();
        mVideos = mRepository.getVideos();
        mRevies = mRepository.getReviews();
        mFavouriteMovie = mRepository.getFavouriteMovie(mId);

    }

    public LiveData<Movie> getMovie() {
        return mMovie;
    }

    public LiveData<List<Video>> getVideos() {
        return mVideos;
    }

    public LiveData<List<Review>> getReviews() {
        return mRevies;
    }

    public void saveFavouriteMovie(FavouriteMovie favouriteMovie) {
        mRepository.saveFavouriteMovie(favouriteMovie);
    }

    public LiveData<FavouriteMovie> getFavouriteMovie() {
        return mFavouriteMovie;
    }

    public void deleteFavouriteMovie() {
        mRepository.deleteFavouriteMovie();
    }


}
