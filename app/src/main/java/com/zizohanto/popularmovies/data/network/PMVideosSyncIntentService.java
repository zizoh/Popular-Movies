package com.zizohanto.popularmovies.data.network;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.zizohanto.popularmovies.utils.InjectorUtils;

import timber.log.Timber;

public class PMVideosSyncIntentService extends IntentService {

    private static final String MOVIE_ID_KEY = "MOVIE_ID_KEY";
    private Integer mId;

    public PMVideosSyncIntentService() {
        super("PMVideosSyncIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Timber.d("Intent service started");
        if (null != intent) {
            mId = intent.getIntExtra(MOVIE_ID_KEY, 0);
        }
        MovieNetworkDataSource networkDataSource =
                InjectorUtils.provideNetworkDataSource(this.getApplicationContext());
        networkDataSource.fetchVideos(mId);
    }
}
