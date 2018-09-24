package com.zizohanto.popularmovies.ui.favourites;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zizohanto.popularmovies.R;
import com.zizohanto.popularmovies.data.database.favouritemovie.FavouriteMovie;
import com.zizohanto.popularmovies.data.database.movie.Movie;
import com.zizohanto.popularmovies.databinding.MoviesFragBinding;
import com.zizohanto.popularmovies.ui.details.DetailsActivity;
import com.zizohanto.popularmovies.ui.movies.MovieAdapter;
import com.zizohanto.popularmovies.ui.movies.ScrollChildSwipeRefreshLayout;
import com.zizohanto.popularmovies.utils.InjectorUtils;

import java.util.List;

public class FavouritesFragment extends Fragment implements MovieAdapter.MovieItemClickListener {

    private boolean isLoading;

    private Context mContext;
    private MoviesFragBinding mMoviesFragBinding;
    private MovieAdapter mMovieAdapter;
    private FavouritesFragViewModel mViewModel;
    private RecyclerView mRecyclerView;
    private ScrollChildSwipeRefreshLayout mSwipeRefreshLayout;

    public FavouritesFragment() {
    }

    public static FavouritesFragment newInstance() {
        return new FavouritesFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@Nullable LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMoviesFragBinding = DataBindingUtil.inflate(inflater, R.layout.movies_frag, container, false);
        View root = mMoviesFragBinding.getRoot();
        mSwipeRefreshLayout =
                root.findViewById(R.id.refresh_layout);

        // Set up movies view
        mRecyclerView = mMoviesFragBinding.rvMovies;

        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 2);
        mRecyclerView.setLayoutManager(layoutManager);

        mContext = getActivity();

        mMovieAdapter = new MovieAdapter(mContext, this);

        mRecyclerView.setAdapter(mMovieAdapter);

        setProgressIndicator();

        setHasOptionsMenu(true);

        //setScrollListener(layoutManager);

        setupViewModel();

        observeMovies();

        return root;
    }

    private void setProgressIndicator() {
        mSwipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(mContext, R.color.colorPrimary),
                ContextCompat.getColor(mContext, R.color.colorAccent),
                ContextCompat.getColor(mContext, R.color.colorPrimaryDark)
        );
        // Set the scrolling view in the custom SwipeRefreshLayout.
        mSwipeRefreshLayout.setScrollUpChild(mRecyclerView);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshFavouriteMovies();
            }
        });
    }

    private void refreshFavouriteMovies() {
        loading(true);
        mViewModel.refreshFavouriteMovies();
    }

    private void setupViewModel() {
        FavouritesFragViewModelFactory factory =
                InjectorUtils.provideFFViewModelFactory(mContext);
        mViewModel = ViewModelProviders.of(this, factory).get(FavouritesFragViewModel.class);
    }

    private void observeMovies() {
        loading(true);
        mViewModel.getAllFavouriteMovies().observe(this, new Observer<List<FavouriteMovie>>() {
            @Override
            public void onChanged(@Nullable List<FavouriteMovie> favouriteMovies) {
                if (favouriteMovies != null && favouriteMovies.size() != 0) {
                    getMoviesFromFavourites(favouriteMovies);
                }
            }
        });
    }

    private void getMoviesFromFavourites(List<FavouriteMovie> favouriteMovies) {
        int[] ids = new int[favouriteMovies.size()];
        for (int i = 0; i < favouriteMovies.size(); i++) {
            ids[i] = favouriteMovies.get(i).getId();
        }
        mViewModel.getFavouriteMoviesByIds(ids).observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> movies) {
                loading(false);
                mMovieAdapter.setMovieData(movies);
            }
        });
    }

    private void loading(boolean loading) {
        isLoading = loading;
        setLoadingIndicator(loading);
    }

    public void setLoadingIndicator(final boolean active) {
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(active);
            }
        });
    }

    @Override
    public void onMovieClick(Movie movie) {
        Intent movieDetailIntent = new Intent(getActivity(), DetailsActivity.class);
        movieDetailIntent.putExtra(DetailsActivity.MOVIE_TITLE_EXTRA, movie.getTitle());
        movieDetailIntent.putExtra(DetailsActivity.MOVIE_ID_EXTRA, movie.getId());
        startActivity(movieDetailIntent);
    }

}
