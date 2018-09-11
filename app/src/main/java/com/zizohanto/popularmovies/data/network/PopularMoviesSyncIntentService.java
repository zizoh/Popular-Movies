package com.zizohanto.popularmovies.data.network;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.zizohanto.popularmovies.utils.InjectorUtils;

import timber.log.Timber;

public class PopularMoviesSyncIntentService extends IntentService {
    public static final String CURRENT_SORTING_KEY = "CURRENT_SORTING_KEY";
    public static final String PAGE_TO_LOAD_KEY = "PAGE_TO_LOAD_KEY";
    private String mMoviesSortType;
    private int mPageToLoad;

    public PopularMoviesSyncIntentService() {
        super("PopularMoviesSyncIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Timber.d("Intent service started");
        if (null != intent) {
            mMoviesSortType = intent.getStringExtra(CURRENT_SORTING_KEY);
            mPageToLoad = intent.getIntExtra(PAGE_TO_LOAD_KEY, 1);
        }
        MovieNetworkDataSource networkDataSource =
                InjectorUtils.provideNetworkDataSource(this.getApplicationContext());
        networkDataSource.fetchMovies(mMoviesSortType, mPageToLoad);
    }
}
