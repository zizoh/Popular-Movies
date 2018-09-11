package com.zizohanto.popularmovies.data;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;

import com.zizohanto.popularmovies.AppExecutors;
import com.zizohanto.popularmovies.data.database.Movie;
import com.zizohanto.popularmovies.data.database.MovieDao;
import com.zizohanto.popularmovies.data.network.MovieNetworkDataSource;

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
    private final MovieNetworkDataSource mMovieNetworkDataSource;
    private final AppExecutors mExecutors;

    private String mMoviesSortType;
    private int mPageToLoad = 0;
    private boolean mInitialized = false;
    private boolean mIsNotPreferenceChange;

    private PopularMoviesRepository(MovieDao movieDao,
                                    MovieNetworkDataSource movieNetworkDataSource,
                                    AppExecutors executors,
                                    MovieNetworkDataSource.OnResponseListener onResponseListener) {
        mMovieDao = movieDao;
        mMovieNetworkDataSource = movieNetworkDataSource;
        mExecutors = executors;
        MovieNetworkDataSource.OnResponseListener mOnResponseListener = onResponseListener;

        // As long as the repository exists, observe the network LiveData.
        // If that LiveData changes, update the database.
        LiveData<List<Movie>> networkData = mMovieNetworkDataSource.getTodaysMoviesData();

        networkData.observeForever(new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> newMoviesFromNetwork) {
                mExecutors.diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (mPageToLoad <= 1) {
                            // Deletes old historical data
                            PopularMoviesRepository.this.deleteOldData();
                            Timber.d("Old movies deleted");
                        }
                        // Insert our new movie data into PopularMovie's database
                        mMovieDao.bulkInsert(newMoviesFromNetwork);
                        Timber.d("New values inserted");
                    }
                });
            }
        });
    }

    /**
     * Deletes old movies data
     */
    private void deleteOldData() {
        mMovieDao.deleteAll();
    }

    public synchronized static PopularMoviesRepository getInstance(
            MovieDao movieDao, MovieNetworkDataSource movieNetworkDataSource,
            AppExecutors executors,
            MovieNetworkDataSource.OnResponseListener onResponseListener) {
        Timber.d("Getting the repository");
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new PopularMoviesRepository(movieDao, movieNetworkDataSource,
                        executors, onResponseListener);
                Timber.d("Made new repository");
            }
        }
        return sInstance;
    }

    /**
     * Creates periodic sync tasks and checks to see if an immediate sync is required. If an
     * immediate sync is required, this method will take care of making sure that sync occurs.
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
    public void startFetchMoviesService() {
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mMovieNetworkDataSource.startFetchMoviesService();
            }
        });
    }

    /**
     * Database related operations
     */
    public LiveData<List<Movie>> getCurrentMovies() {
        Timber.d("Getting current movies: ");
        initializeData();
        return mMovieDao.getAll();
    }

    public LiveData<Movie> getMovieByTitle(String title) {
        initializeData();
        return mMovieDao.getMovieByTitle(title);
    }

    public void setFetchCriteria(String moviesSortType, Boolean isNotPreferenceChange, int pageToLoad) {
        mMoviesSortType = moviesSortType;
        mIsNotPreferenceChange = isNotPreferenceChange;
        mPageToLoad = pageToLoad;
    }
}
