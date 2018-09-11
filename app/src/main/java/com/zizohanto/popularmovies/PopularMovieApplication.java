package com.zizohanto.popularmovies;

import android.app.Application;

import timber.log.Timber;

public class PopularMovieApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }
}
