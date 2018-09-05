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
import com.zizohanto.popularmovies.data.database.MovieResponse;

import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Provides an API for doing all operations with the server data
 */
public class MovieNetworkDataSource {

    private static final String LOG_TAG = MovieNetworkDataSource.class.getSimpleName();
    public static final String CURRENT_SORTING_KEY = "CURRENT_SORTING_KEY";
    private static final String API_KEY = "***REMOVED***";
    private static int pageNumber = 1;

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
    private final MutableLiveData<List<Movie>> mDownloadedMovies;
    private final AppExecutors mExecutors;

    private MovieNetworkDataSource(@NonNull Context context, AppExecutors executors) {
        mContext = context;
        mExecutors = executors;
        mDownloadedMovies = new MutableLiveData<List<Movie>>();
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

    public LiveData<List<Movie>> getTodaysMoviesData() {
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
                ApiInterface apiService = ApiClient.getClient();
                Call<MovieResponse> call;

                if (moviesSortType.equals("movie/popular")) {
                    call = apiService.getPopularMovies(API_KEY, pageNumber);
                } else {
                    call = apiService.getTopRatedMovies(API_KEY, pageNumber);
                }
                call.enqueue(new Callback<MovieResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                        Log.d(LOG_TAG, "got a response " + response);
                        if (response.isSuccessful()) {
                            List<Movie> movies = response.body().getMovies();
                            mDownloadedMovies.postValue(response.body().getMovies());
                            Log.d(LOG_TAG, "Number of movies received: " + movies.size());
                        } else {
                            Log.d(LOG_TAG, String.valueOf(response.errorBody()) + "Unknown error");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                        // Log error here since request failed
                        Log.e(LOG_TAG, t.toString());
                    }
                });
            }
        });
    }

}
