package com.zizohanto.popularmovies.data.network;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.zizohanto.popularmovies.AppExecutors;
import com.zizohanto.popularmovies.BuildConfig;
import com.zizohanto.popularmovies.data.database.Movie;
import com.zizohanto.popularmovies.data.database.MovieResponse;
import com.zizohanto.popularmovies.utils.NetworkState;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Provides an API for doing all operations with the server data
 */
public class MovieNetworkDataSource {

    private static final String CURRENT_SORTING_KEY = "CURRENT_SORTING_KEY";
    private static final String PAGE_TO_LOAD_KEY = "PAGE_TO_LOAD_KEY";
    private static final String API_KEY = BuildConfig.ApiKey;
    private int mPageToLoad;
    private String mMoviesSortType;

    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static MovieNetworkDataSource sInstance;
    private final Context mContext;
    private final AppExecutors mExecutors;

    // LiveData storing the latest downloaded movies data
    private final MutableLiveData<List<Movie>> mDownloadedMovies;

    private final MutableLiveData<NetworkState> networkState;

    private MovieNetworkDataSource(@NonNull Context context, AppExecutors executors) {
        mContext = context;
        mExecutors = executors;
        mDownloadedMovies = new MutableLiveData<List<Movie>>();
        networkState = new MutableLiveData<NetworkState>();
    }

    /**
     * Get the singleton for this class
     */
    public static MovieNetworkDataSource getInstance(Context context, AppExecutors executors) {
        Timber.d("Getting the network data source");
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new MovieNetworkDataSource(context.getApplicationContext(), executors);
                Timber.d("Made new network data source");
            }
        }
        return sInstance;
    }

    public LiveData<List<Movie>> getTodaysMoviesData() {
        return mDownloadedMovies;
    }

    public MutableLiveData<NetworkState> getNetworkState() {
        return networkState;
    }

    /**
     * Starts an intent service to fetch the movies.
     */
    public void startFetchMoviesService() {
        Intent intentToFetch = new Intent(mContext, PopularMoviesSyncIntentService.class);
        intentToFetch.putExtra(CURRENT_SORTING_KEY, mMoviesSortType);
        intentToFetch.putExtra(PAGE_TO_LOAD_KEY, mPageToLoad);
        mContext.startService(intentToFetch);
        Timber.d("Service created");
    }

    public void setFetchCriteria(String moviesSortType, int pageToLoad) {
        mMoviesSortType = moviesSortType;
        mPageToLoad = pageToLoad;
    }

    /**
     * Get movies
     */
    void fetchMovies(@NonNull String moviesSortType, int pageToLoad) {
        Timber.d("Fetch movies started");
        mExecutors.networkIO().execute(new Runnable() {
            @Override
            public void run() {
                networkState.postValue(NetworkState.LOADING);

                ApiInterface apiService = ApiClient.getClient();
                Call<MovieResponse> call;

                call = getMovieResponseCall(apiService, moviesSortType, pageToLoad);
                call.enqueue(new Callback<MovieResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                        Timber.d("got a response %s", response);
                        if (response.isSuccessful()) {
                            List<Movie> movies = response.body().getMovies();
                            Timber.d("Number of movies received: %s", movies.size());
                            mDownloadedMovies.postValue(response.body().getMovies());
                            networkState.postValue(NetworkState.LOADED);
                        } else {
                            Timber.d("%sUnknown error", String.valueOf(response.errorBody()));
                            networkState.postValue(new NetworkState(NetworkState.Status.FAILED, response.message()));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                        Timber.e(t.toString());
                        networkState.postValue(new NetworkState(NetworkState.Status.FAILED, t.getMessage()));
                    }
                });
            }
        });
    }

    private Call<MovieResponse> getMovieResponseCall(ApiInterface apiService,
                                                     @NonNull String moviesSortType, int pageToLoad) {
        Call<MovieResponse> call;
        if (moviesSortType.equals("movie/popular")) {
            call = apiService.getPopularMovies(API_KEY, pageToLoad);
        } else {
            call = apiService.getTopRatedMovies(API_KEY, pageToLoad);
        }
        return call;
    }
}
