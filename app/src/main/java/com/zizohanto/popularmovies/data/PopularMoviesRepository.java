package com.zizohanto.popularmovies.data;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;
import android.util.Log;

import com.zizohanto.popularmovies.AppExecutors;
import com.zizohanto.popularmovies.data.database.Movie;
import com.zizohanto.popularmovies.data.database.MovieDao;
import com.zizohanto.popularmovies.data.network.MovieNetworkDataSource;

import java.util.List;

/**
 * Handles data operations in Popular movies. Acts as a mediator between {@link MovieNetworkDataSource}
 * and {@link MovieDao}
 */
public class PopularMoviesRepository {
    private static final String LOG_TAG = PopularMoviesRepository.class.getSimpleName();

    private int mMoviesSortType;

    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static PopularMoviesRepository sInstance;
    private final MovieDao mMovieDao;
    private final MovieNetworkDataSource mMovieNetworkDataSource;
    private final AppExecutors mExecutors;
    private boolean mInitialized = false;

    private PopularMoviesRepository(MovieDao movieDao,
                                    MovieNetworkDataSource movieNetworkDataSource,
                                    AppExecutors executors) {
        mMovieDao = movieDao;
        mMovieNetworkDataSource = movieNetworkDataSource;
        mExecutors = executors;

        // As long as the repository exists, observe the network LiveData.
        // If that LiveData changes, update the database.
        LiveData<Movie[]> networkData = mMovieNetworkDataSource.getTodaysMoviesData();

        networkData.observeForever(new Observer<Movie[]>() {
            @Override
            public void onChanged(@Nullable Movie[] newMoviesFromNetwork) {
                mExecutors.diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        // Deletes old historical data
                        PopularMoviesRepository.this.deleteOldData();
                        Log.d(LOG_TAG, "Old movies deleted");
                        // Insert our new movie data into Popular Movie's database
                        mMovieDao.bulkInsert(newMoviesFromNetwork);
                        Log.d(LOG_TAG, "New values inserted");
                    }
                });
            }
        });
    }

    public synchronized static PopularMoviesRepository getInstance(
            MovieDao movieDao, MovieNetworkDataSource movieNetworkDataSource,
            AppExecutors executors) {
        Log.d(LOG_TAG, "Getting the repository");
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new PopularMoviesRepository(movieDao, movieNetworkDataSource,
                        executors);
                Log.d(LOG_TAG, "Made new repository");
            }
        }
        return sInstance;
    }

    /**
     * Creates periodic sync tasks and checks to see if an immediate sync is required. If an
     * immediate sync is required, this method will take care of making sure that sync occurs.
     */
    private synchronized void initializeData(int moviesSortType) {

        // Only perform initialization once per app lifetime. If initialization has already been
        // performed, nothing is done in this method.
        //if (mInitialized) return;
        mInitialized = true;

        // This method call triggers Popular Movies to create its task to synchronize movie data
        // periodically.
        mMovieNetworkDataSource.scheduleRecurringFetchMoviesSync();

        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                startFetchMoviesService(moviesSortType);
            }
        });
    }

    /**
     * Database related operations
     **/

    public LiveData<List<Movie>> getCurrentMovies(int moviesSortType) {
        mMoviesSortType = moviesSortType;
        initializeData(mMoviesSortType);
        return mMovieDao.getAll();
    }

    public LiveData<Movie> getMovieByTitle(String title) {
        initializeData(mMoviesSortType);
        return mMovieDao.getMovieByTitle(title);
    }

    /**
     * Deletes old movies data
     */
    private void deleteOldData() {
        mMovieDao.deleteAll();
    }

    /**
     * Network related operation
     */

    private void startFetchMoviesService(int moviesSortType) {
        mMovieNetworkDataSource.startFetchMoviesService(moviesSortType);
    }

}
