package com.zizohanto.popularmovies.ui.favourites;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.zizohanto.popularmovies.data.PopularMoviesRepository;

/**
 * Factory method that to create a ViewModel with a constructor that takes a
 * {@link PopularMoviesRepository}
 */
public class FavouritesFragViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private final PopularMoviesRepository mRepository;

    public FavouritesFragViewModelFactory(PopularMoviesRepository repository) {
        mRepository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        //noinspection unchecked
        //return (T) new FavouritesFragViewModel(mRepository);
        return (T) new FavouritesFragViewModel(mRepository);
    }
}
