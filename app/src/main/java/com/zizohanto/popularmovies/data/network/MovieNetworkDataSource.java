package com.zizohanto.popularmovies.data.network;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;
import com.zizohanto.popularmovies.AppExecutors;
import com.zizohanto.popularmovies.data.database.Movie;

import java.net.URL;
import java.util.concurrent.TimeUnit;

/**
 * Provides an API for doing all operations with the server data
 */
public class MovieNetworkDataSource {

    private static final String LOG_TAG = MovieNetworkDataSource.class.getSimpleName();
    public static final String CURRENT_SORTING_KEY = "CURRENT_SORTING_KEY";

    // Interval at which to sync with data.
    private static final int SYNC_INTERVAL_HOURS = 12;
    private static final int SYNC_INTERVAL_SECONDS = (int) TimeUnit.MINUTES.toSeconds(SYNC_INTERVAL_HOURS);
    private String mMoviesSortType;
    private static final int SYNC_FLEXTIME_SECONDS = SYNC_INTERVAL_SECONDS / 3;
    private static final String POPULAR_MOVIES_SYNC_TAG = "popular-movies-sync";

    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static MovieNetworkDataSource sInstance;
    private final Context mContext;

    // LiveData storing the latest downloaded movies data
    private final MutableLiveData<Movie[]> mDownloadedMovies;
    private final AppExecutors mExecutors;

    private MovieNetworkDataSource(@NonNull Context context, AppExecutors executors) {
        mContext = context;
        mExecutors = executors;
        mDownloadedMovies = new MutableLiveData<Movie[]>();
    }

    /**
     * Get the singleton for this class
     */
    public static MovieNetworkDataSource getInstance(Context context, AppExecutors executors) {
        Log.d(LOG_TAG, "Getting the network data source");
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new MovieNetworkDataSource(context.getApplicationContext(), executors);
                Log.d(LOG_TAG, "Made new network data source");
            }
        }
        return sInstance;
    }

    public LiveData<Movie[]> getTodaysMoviesData() {
        return mDownloadedMovies;
    }

    /**
     * Starts an intent service to fetch the movies.
     */
    public void startFetchMoviesService() {
        Intent intentToFetch = new Intent(mContext, PopularMoviesSyncIntentService.class);
        intentToFetch.putExtra(CURRENT_SORTING_KEY, mMoviesSortType);
        mContext.startService(intentToFetch);
        Log.d(LOG_TAG, "Service created");
    }

    public void setSortingCriteria(String moviesSortType) {
        mMoviesSortType = moviesSortType;
    }

    /**
     * Schedules a repeating job service which fetches the movies.
     */
    public void scheduleRecurringFetchMoviesSync() {
        Driver driver = new GooglePlayDriver(mContext);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        Bundle bundle = new Bundle();
        bundle.putString(CURRENT_SORTING_KEY, mMoviesSortType);

        Job syncPopularMoviesJob = dispatcher.newJobBuilder()
                .setService(PopularMoviesFirebaseJobService.class)
                .setTag(POPULAR_MOVIES_SYNC_TAG)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(
                        SYNC_INTERVAL_SECONDS,
                        SYNC_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS))
                .setReplaceCurrent(true)
                .setExtras(bundle)
                .build();

        // Schedule the Job with the dispatcher
        dispatcher.schedule(syncPopularMoviesJob);
        Log.d(LOG_TAG, "Job scheduled");
    }

    /**
     * Gets the newest movies
     */
    void fetchMovies(String moviesSortType) {
        Log.d(LOG_TAG, "Fetch movies started");
        mExecutors.networkIO().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // The getUrl method will return the URL that we need to get the movies JSON for the
                    // movies. It will create the URL based off of the endpoint selected
                    // by the user: most popular or highest rated
                    URL moviesRequestUrl = NetworkUtils.getUrl(moviesSortType);

                    // Use the URL to retrieve the JSON
                    String jsonMovieResponse = NetworkUtils.getResponseFromHttpUrl(moviesRequestUrl);

                    // Parse the JSON into a list of movies
                    MovieResponse response = JsonUtils.parseMovieJson(jsonMovieResponse);
                    Log.d(LOG_TAG, "JSON Parsing finished");

                    // As long as there are movies, update the LiveData storing the most recent
                    // movies. This will trigger PopularMoviesRepository - the observer of that LiveData
                    if (response != null && response.getMovies().length != 0) {
                        Log.d(LOG_TAG, "JSON not null and has " + response.getMovies().length
                                + " values");

                        // postValue used to posts the update to the main thread since off main thread
                        mDownloadedMovies.postValue(response.getMovies());
                    }
                } catch (Exception e) {
                    // Server probably invalid
                    e.printStackTrace();
                }
            }
        });
    }

}
