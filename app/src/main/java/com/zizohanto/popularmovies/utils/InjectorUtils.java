package com.zizohanto.popularmovies.utils;

import android.content.Context;

import com.zizohanto.popularmovies.AppExecutors;
import com.zizohanto.popularmovies.data.PopularMoviesRepository;
import com.zizohanto.popularmovies.data.database.PopularMovieDatabase;
import com.zizohanto.popularmovies.data.network.MovieNetworkDataSource;
import com.zizohanto.popularmovies.ui.details.DetailsFragmentViewModelFactory;
import com.zizohanto.popularmovies.ui.movies.MoviesFragmentViewModelFactory;

/**
 * Provides static methods to inject the various classes needed for Sunshine
 */
public class InjectorUtils {

    public static PopularMoviesRepository provideRepository(Context context) {
        PopularMovieDatabase database = PopularMovieDatabase.getInstance(context.getApplicationContext());
        AppExecutors executors = AppExecutors.getInstance();
        MovieNetworkDataSource networkDataSource =
                MovieNetworkDataSource.getInstance(context.getApplicationContext(), executors);
        return PopularMoviesRepository.getInstance(database.movieDao(), networkDataSource, executors);
    }

    public static MovieNetworkDataSource provideNetworkDataSource(Context context) {
        provideRepository(context.getApplicationContext());
        AppExecutors executors = AppExecutors.getInstance();
        return MovieNetworkDataSource.getInstance(context.getApplicationContext(), executors);
    }

    public static DetailsFragmentViewModelFactory provideDetailsFragmentViewModelFactory(Context context, String title) {
        PopularMoviesRepository repository = provideRepository(context.getApplicationContext());
        return new DetailsFragmentViewModelFactory(repository, title);
    }

    public static MoviesFragmentViewModelFactory provideMoviesFragmentViewModelFactory(Context context, int moviesSortType) {
        PopularMoviesRepository repository = provideRepository(context.getApplicationContext());
        return new MoviesFragmentViewModelFactory(repository, moviesSortType);
    }

}