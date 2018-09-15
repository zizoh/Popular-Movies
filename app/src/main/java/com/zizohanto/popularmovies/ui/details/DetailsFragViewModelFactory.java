package com.zizohanto.popularmovies.ui.details;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.zizohanto.popularmovies.data.PopularMoviesRepository;

public class DetailsFragViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private final PopularMoviesRepository mRepository;
    private final String mTitle;
    private final Integer mId;

    public DetailsFragViewModelFactory(PopularMoviesRepository repository, String title, Integer id) {
        mRepository = repository;
        mTitle = title;
        mId = id;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        //noinspection unchecked
        return (T) new DetailsFragViewModel(mRepository, mTitle, mId);
    }
}
