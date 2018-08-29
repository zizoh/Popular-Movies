package com.zizohanto.popularmovies.ui.details;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.zizohanto.popularmovies.data.PopularMoviesRepository;

public class DetailsFragViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private final PopularMoviesRepository mRepository;
    private final String mTitle;

    public DetailsFragViewModelFactory(PopularMoviesRepository repository, String title) {
        mRepository = repository;
        mTitle = title;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        //noinspection unchecked
        return (T) new DetailsFragViewModel(mRepository, mTitle);
    }
}
