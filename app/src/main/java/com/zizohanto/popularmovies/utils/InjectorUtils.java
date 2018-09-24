package com.zizohanto.popularmovies.utils;

import android.content.Context;

import com.zizohanto.popularmovies.AppExecutors;
import com.zizohanto.popularmovies.data.PopularMoviesRepository;
import com.zizohanto.popularmovies.data.database.PopularMovieDatabase;
import com.zizohanto.popularmovies.data.network.MovieNetworkDataSource;
import com.zizohanto.popularmovies.ui.details.DetailsFragViewModelFactory;
import com.zizohanto.popularmovies.ui.favourites.FavouritesFragViewModelFactory;
import com.zizohanto.popularmovies.ui.movies.MoviesFragViewModelFactory;

/**
 * Provides static methods to inject the various classes needed for Popular Movies
 */
public class InjectorUtils {

    public static PopularMoviesRepository provideRepository(Context context) {
        PopularMovieDatabase database = PopularMovieDatabase.getInstance(context.getApplicationContext());
        AppExecutors executors = AppExecutors.getInstance();
        MovieNetworkDataSource networkDataSource =
                MovieNetworkDataSource.getInstance(context.getApplicationContext(), executors);
        return PopularMoviesRepository.getInstance(database.movieDao(),
                database.videoDao(),
                database.reviewDao(),
                database.favouriteMovieDao(),
                networkDataSource,
                executors);
    }

    public static MovieNetworkDataSource provideNetworkDataSource(Context context) {
        provideRepository(context.getApplicationContext());
        AppExecutors executors = AppExecutors.getInstance();
        return MovieNetworkDataSource.getInstance(context.getApplicationContext(), executors);
    }

    public static DetailsFragViewModelFactory provideDFViewModelFactory(Context context, Integer id) {
        PopularMoviesRepository repository = provideRepository(context.getApplicationContext());
        return new DetailsFragViewModelFactory(repository, id);
    }

    public static MoviesFragViewModelFactory provideMFViewModelFactory(Context context,
                                                                       String moviesSortType,
                                                                       Boolean isNotFirstPreferenceChange,
                                                                       int pageToLoad) {
        PopularMoviesRepository repository = provideRepository(context.getApplicationContext());

        return new MoviesFragViewModelFactory(repository, moviesSortType, isNotFirstPreferenceChange, pageToLoad);
    }

    public static FavouritesFragViewModelFactory provideFFViewModelFactory(Context context) {
        PopularMoviesRepository repository = provideRepository(context.getApplicationContext());

        return new FavouritesFragViewModelFactory(repository);
    }

}
