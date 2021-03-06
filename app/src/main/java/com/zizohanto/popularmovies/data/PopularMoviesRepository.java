package com.zizohanto.popularmovies.data;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;

import com.zizohanto.popularmovies.AppExecutors;
import com.zizohanto.popularmovies.data.database.favouritemovie.FavouriteMovie;
import com.zizohanto.popularmovies.data.database.favouritemovie.FavouriteMovieDao;
import com.zizohanto.popularmovies.data.database.movie.Movie;
import com.zizohanto.popularmovies.data.database.movie.MovieDao;
import com.zizohanto.popularmovies.data.database.review.Review;
import com.zizohanto.popularmovies.data.database.review.ReviewDao;
import com.zizohanto.popularmovies.data.database.video.Video;
import com.zizohanto.popularmovies.data.database.video.VideoDao;
import com.zizohanto.popularmovies.data.network.MovieNetworkDataSource;
import com.zizohanto.popularmovies.utils.NetworkState;

import java.util.List;

import timber.log.Timber;

/**
 * Handles data operations in Popular movies. Acts as a mediator between {@link MovieNetworkDataSource}
 * and {@link MovieDao}
 */
public class PopularMoviesRepository {

    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static PopularMoviesRepository sInstance;
    private final MovieDao mMovieDao;
    private final VideoDao mVideoDao;
    private final ReviewDao mReviewDao;
    private final FavouriteMovieDao mFavouriteMovieDao;
    private final MovieNetworkDataSource mMovieNetworkDataSource;
    private final AppExecutors mExecutors;

    private int mMovieId;
    private int mListType;
    private int mPageToLoad;
    private boolean mInitialized = false;
    private boolean mIsNotPreferenceChange;
    private String mMoviesSortType;

    private PopularMoviesRepository(MovieDao movieDao,
                                    VideoDao videoDao,
                                    ReviewDao reviewDao,
                                    FavouriteMovieDao favouriteMovieDao,
                                    MovieNetworkDataSource movieNetworkDataSource,
                                    AppExecutors executors) {
        mMovieDao = movieDao;
        mVideoDao = videoDao;
        mReviewDao = reviewDao;
        mFavouriteMovieDao = favouriteMovieDao;
        mMovieNetworkDataSource = movieNetworkDataSource;
        mExecutors = executors;

        // As long as the repository exists, observe the network LiveData.
        // If that LiveData changes, update the database.
        LiveData<List<Movie>> networkData = mMovieNetworkDataSource.getMoviesData();

        networkData.observeForever(new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> newMoviesFromNetwork) {
                mExecutors.diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (mPageToLoad <= 1) {
                            // Deletes old historical data
                            deleteOldMovieData();
                            Timber.d("Old movies deleted");
                        }

                        // Insert our new movie data into PopularMovie's database
                        mMovieDao.bulkInsert(newMoviesFromNetwork);
                        Timber.d("New values inserted");
                    }
                });
            }
        });

        LiveData<List<Video>> networkVideoData = mMovieNetworkDataSource.getVideos();

        networkVideoData.observeForever(new Observer<List<Video>>() {
            @Override
            public void onChanged(@Nullable List<Video> videos) {
                mExecutors.diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        // Deletes old historical data
                        deleteOldVideoData();
                        Timber.d("Old videos deleted");

                        // Insert our new movie data into PopularMovie's database
                        mVideoDao.bulkInsert(videos);
                        Timber.d("New video values inserted");
                    }
                });
            }
        });

        LiveData<List<Review>> networkReviewData = mMovieNetworkDataSource.getReviews();

        networkReviewData.observeForever(new Observer<List<Review>>() {
            @Override
            public void onChanged(@Nullable List<Review> reviews) {
                mExecutors.diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        // Deletes old historical data
                        deleteOldReviewData();
                        Timber.d("Old reviews deleted");

                        // Insert our new movie data into PopularMovie's database
                        mReviewDao.bulkInsert(reviews);
                        Timber.d("New review values inserted");
                    }
                });
            }
        });
    }

    public synchronized static PopularMoviesRepository getInstance(
            MovieDao movieDao,
            VideoDao videoDao,
            ReviewDao reviewDao,
            FavouriteMovieDao favouriteMovieDao,
            MovieNetworkDataSource movieNetworkDataSource,
            AppExecutors executors) {
        Timber.d("Getting the repository");
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new PopularMoviesRepository(movieDao, videoDao, reviewDao, favouriteMovieDao,
                        movieNetworkDataSource,
                        executors);
                Timber.d("Made new repository");
            }
        }
        return sInstance;
    }

    /**
     * Deletes old movies data after new movies are fetched successfully
     */
    private void deleteOldMovieData() {
//        mMovieDao.deleteMoviesByListType(mListType);
        mMovieDao.deleteAllMovies();
    }

    /**
     * Deletes old videos data
     */
    private void deleteOldVideoData() {
        mVideoDao.deleteVideosOfMovie(mMovieId);
    }

    /**
     * Deletes old reviews data
     */
    private void deleteOldReviewData() {
        mReviewDao.deleteReviewsOfMovie(mMovieId);
    }

    /**
     * Starts service that fetches data
     */
    private synchronized void initializeData() {
        // Only perform initialization once per app lifetime. If initialization has already been
        // performed, nothing is done in this method.
        if (mInitialized && mIsNotPreferenceChange) {
            return;
        }
        mInitialized = true;
        mMovieNetworkDataSource.setFetchCriteria(mMoviesSortType, mPageToLoad);

        startFetchMoviesService();
    }

    /**
     * Network related operation
     */
    private void startFetchMoviesService() {
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mMovieNetworkDataSource.startFetchMoviesService();
            }
        });
    }

    private void startFetchVideosService(Integer id) {
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mMovieNetworkDataSource.startFetchVideosService(id);
            }
        });
    }

    private void startFetchReviewsService(Integer id) {
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mMovieNetworkDataSource.startFetchReviewsService(id);
            }
        });
    }

    public void setFetchMoviesCriteria(String moviesSortType, Boolean isNotPreferenceChange,
                                       int pageToLoad) {
        mMoviesSortType = moviesSortType;
        mIsNotPreferenceChange = isNotPreferenceChange;
        mPageToLoad = pageToLoad;

        if (mMoviesSortType.equals("popular")) {
            mListType = 1;
        } else if (mMoviesSortType.equals("top_rated")) {
            mListType = 2;
        }
    }

    public void setFetchMoviesCriteria(Integer movieId) {
        mMovieId = movieId;
    }

    public LiveData<NetworkState> getNetworkState() {
        return mMovieNetworkDataSource.getNetworkState();
    }

    /*
     * Movies database related operations
     */
    public LiveData<List<Movie>> getCurrentMovies() {
        initializeData();
//        return mMovieDao.getMoviesByType(mListType);
        return mMovieDao.getAllMovies();
    }

    public LiveData<Movie> getMovie() {
        initializeData();
        return mMovieDao.getMovieById(mMovieId);
    }

    /*
     *  Returns a list of movies from the ids of favorite movies
     */
    public LiveData<List<Movie>> getMoviesByIds(int[] ids) {
        return mMovieDao.getMoviesByIds(ids, 0);
    }

    /*
     *  Trailers database operations
     */
    public LiveData<List<Video>> getVideos() {
        Timber.d("Getting videos for movie with id: %s", String.valueOf(mMovieId));
        startFetchVideosService(mMovieId);
        return mVideoDao.getVideosOfMovie(mMovieId);
    }

    /*
     *  Reviews database operations
     */
    public LiveData<List<Review>> getReviews() {
        Timber.d("Getting reviews for movie with id: %s", String.valueOf(mMovieId));
        startFetchReviewsService(mMovieId);
        return mReviewDao.getReviewsOfMovie(mMovieId);
    }

    /*
     *  Favourite movies database operations
     */
    public LiveData<List<FavouriteMovie>> getAllFavouriteMovies() {
        Timber.d("Getting all favourite movies");
        return mFavouriteMovieDao.getAllFavouriteMovies();
    }

    public LiveData<FavouriteMovie> getFavouriteMovie() {
        Timber.d("Getting favourite movie with id: %s", mMovieId);
        return mFavouriteMovieDao.getFavouriteMovieWithId(mMovieId);
    }

    public void saveFavouriteMovie(FavouriteMovie favouriteMovie) {
        Timber.d("Inserting favourite movie");
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mFavouriteMovieDao.insertFavouriteMovie(favouriteMovie);
            }
        });
    }

    public void deleteFavouriteMovie() {
        Timber.d("Deleting favourite movie with id: %s", mMovieId);
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mFavouriteMovieDao.deleteFavouriteMovie(mMovieId);
            }
        });
    }

}
