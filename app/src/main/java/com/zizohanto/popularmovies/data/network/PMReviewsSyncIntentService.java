package com.zizohanto.popularmovies.data.network;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.zizohanto.popularmovies.utils.InjectorUtils;

import timber.log.Timber;

public class PMReviewsSyncIntentService extends IntentService {
    private static final String MOVIE_ID_KEY = "MOVIE_ID_KEY";
    private Integer mId;

    public PMReviewsSyncIntentService() {
        super("PMReviewsSyncIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Timber.d("Reviews Intent service started");
        if (null != intent) {
            mId = intent.getIntExtra(MOVIE_ID_KEY, 0);
            // TODO: catch instances when mId is 0 because no intent extra was passed
        }
        MovieNetworkDataSource networkDataSource =
                InjectorUtils.provideNetworkDataSource(this.getApplicationContext());
        networkDataSource.fetchReviews(mId);
    }
}
