package com.zizohanto.popularmovies.data.network;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.zizohanto.popularmovies.AppExecutors;
import com.zizohanto.popularmovies.BuildConfig;
import com.zizohanto.popularmovies.data.database.movie.Movie;
import com.zizohanto.popularmovies.data.database.movie.MovieResponse;
import com.zizohanto.popularmovies.data.database.review.Review;
import com.zizohanto.popularmovies.data.database.review.ReviewResponse;
import com.zizohanto.popularmovies.data.database.video.Video;
import com.zizohanto.popularmovies.data.database.video.VideoResponse;
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
    private static final String MOVIE_ID_KEY = "MOVIE_ID_KEY";
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

    // LiveData storing the movies' videos data
    private final MutableLiveData<List<Video>> mDownloadedVideos;

    // LiveData storing the movies' reviews data
    private final MutableLiveData<List<Review>> mDownloadedReviews;

    private final MutableLiveData<NetworkState> networkState;

    private MovieNetworkDataSource(@NonNull Context context, AppExecutors executors) {
        mContext = context;
        mExecutors = executors;
        mDownloadedMovies = new MutableLiveData<>();
        networkState = new MutableLiveData<>();
        mDownloadedVideos = new MutableLiveData<>();
        mDownloadedReviews = new MutableLiveData<>();
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

    public LiveData<List<Movie>> getMoviesData() {
        return mDownloadedMovies;
    }

    public LiveData<List<Video>> getVideos() {
        return mDownloadedVideos;
    }

    public LiveData<List<Review>> getReviews() {
        return mDownloadedReviews;
    }

    public MutableLiveData<NetworkState> getNetworkState() {
        return networkState;
    }

    /**
     * Starts an intent service to fetch the movies.
     */
    public void startFetchMoviesService() {
        Intent intentToFetch = new Intent(mContext, PMMoviesSyncIntentService.class);
        intentToFetch.putExtra(CURRENT_SORTING_KEY, mMoviesSortType);
        intentToFetch.putExtra(PAGE_TO_LOAD_KEY, mPageToLoad);
        mContext.startService(intentToFetch);
        Timber.d("Movies Service created");
    }

    /**
     * Starts an intent service to fetch the videos.
     */
    public void startFetchVideosService(Integer id) {
        Intent intentToFetch = new Intent(mContext, PMVideosSyncIntentService.class);
        intentToFetch.putExtra(MOVIE_ID_KEY, id);
        mContext.startService(intentToFetch);
        Timber.d("Videos Service created");
    }

    /**
     * Starts an intent service to fetch the reviews.
     */
    public void startFetchReviewsService(Integer id) {
        Intent intentToFetch = new Intent(mContext, PMReviewsSyncIntentService.class);
        intentToFetch.putExtra(MOVIE_ID_KEY, id);
        mContext.startService(intentToFetch);
        Timber.d("Reviews Service created");
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
                            if (movies != null) {
                                Timber.d("Number of movies received: %s", movies.size());
                            }
                            mDownloadedMovies.postValue(movies);
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


    /**
     * Get videos
     */
    public void fetchVideos(Integer id) {
        Timber.d("Fetch videos started");
        mExecutors.networkIO().execute(new Runnable() {
            @Override
            public void run() {
                ApiInterface apiService = ApiClient.getClient();

                Call<VideoResponse> call = apiService.getVideos(id, API_KEY);
                call.enqueue(new Callback<VideoResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<VideoResponse> call, @NonNull Response<VideoResponse> response) {
                        Timber.d("got a response %s", response);
                        if (response.isSuccessful()) {
                            VideoResponse videoResponse = response.body();
                            List<Video> videos = videoResponse.getVideos();
                            if (videos == null) {
                                Timber.d("No videos available for selected movie");
                            } else {
                                Timber.d("Number of videos received: %s", videos.size());
                            }
                            for (int i = 0; i < videos.size(); i++) {
                                videos.get(i).setMovieId(videoResponse.getId());
                            }
                            mDownloadedVideos.postValue(videos);
                        } else {
                            Timber.d("%sUnknown error", String.valueOf(response.errorBody()));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<VideoResponse> call, @NonNull Throwable t) {
                        Timber.e(t.toString());
                    }
                });
            }
        });
    }


    /**
     * Get reviews
     */
    public void fetchReviews(Integer id) {
        Timber.d("Fetch reviews started");
        mExecutors.networkIO().execute(new Runnable() {
            @Override
            public void run() {
                ApiInterface apiService = ApiClient.getClient();

                Call<ReviewResponse> call = apiService.getReviews(id, API_KEY);
                call.enqueue(new Callback<ReviewResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<ReviewResponse> call, @NonNull Response<ReviewResponse> response) {
                        Timber.d("got a response %s", response);
                        if (response.isSuccessful()) {
                            ReviewResponse reviewResponse = response.body();
                            List<Review> reviews = reviewResponse.getReviews();
                            if (reviews == null) {
                                Timber.d("No review available for selected movie");
                            } else {
                                Timber.d("Number of reviews received: %s", reviews.size());
                            }
                            for (int i = 0; i < reviews.size(); i++) {
                                reviews.get(i).setMovieId(reviewResponse.getId());
                            }
                            mDownloadedReviews.postValue(reviews);
                        } else {
                            Timber.d("%sUnknown error", String.valueOf(response.errorBody()));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ReviewResponse> call, @NonNull Throwable t) {
                        Timber.e(t.toString());
                    }
                });
            }
        });
    }
}
