package com.zizohanto.popularmovies.data.network;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.zizohanto.popularmovies.utils.InjectorUtils;

public class PopularMoviesSyncIntentService extends IntentService {
    private static final String LOG_TAG = PopularMoviesSyncIntentService.class.getSimpleName();

    public PopularMoviesSyncIntentService() {
        super("PopularMoviesSyncIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d(LOG_TAG, "Intent service started");
        MovieNetworkDataSource networkDataSource =
                InjectorUtils.provideNetworkDataSource(this.getApplicationContext());
        networkDataSource.fetchMovies();
    }
}
