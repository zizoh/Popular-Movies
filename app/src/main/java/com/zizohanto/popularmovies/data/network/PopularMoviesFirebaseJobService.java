package com.zizohanto.popularmovies.data.network;

import android.os.Bundle;
import android.util.Log;

import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.firebase.jobdispatcher.RetryStrategy;
import com.zizohanto.popularmovies.utils.InjectorUtils;

public class PopularMoviesFirebaseJobService extends JobService {
    private static final String LOG_TAG = PopularMoviesFirebaseJobService.class.getSimpleName();
    public static final String CURRENT_SORTING_KEY = "CURRENT_SORTING_KEY";
    public static final String PAGE_TO_LOAD_KEY = "PAGE_TO_LOAD_KEY";

    /**
     * The entry point to your Job. Implementations should offload work to another thread of
     * execution as soon as possible.
     * <p>
     * This is called by the Job Dispatcher to tell us we should start our job. Keep in mind this
     * method is run on the application's main thread, so we need to offload work to a background
     * thread.
     *
     * @return whether there is more work remaining.
     */
    @Override
    public boolean onStartJob(final JobParameters jobParameters) {
        Log.d(LOG_TAG, "Job service started");
        Bundle bundle = jobParameters.getExtras();
        String moviesSortType = null;
        int pageToLoad = 0;
        if (null != bundle) {
            moviesSortType = bundle.getString(CURRENT_SORTING_KEY);
            pageToLoad = bundle.getInt(PAGE_TO_LOAD_KEY);
        }

        MovieNetworkDataSource networkDataSource =
                InjectorUtils.provideNetworkDataSource(this.getApplicationContext());
        networkDataSource.fetchMovies(moviesSortType, pageToLoad);

        jobFinished(jobParameters, false);

        return true;
    }

    /**
     * Called when the scheduling engine has decided to interrupt the execution of a running job,
     * most likely because the runtime constraints associated with the job are no longer satisfied.
     *
     * @return whether the job should be retried
     * @see Job.Builder#setRetryStrategy(RetryStrategy)
     * @see RetryStrategy
     */
    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return true;
    }
}
