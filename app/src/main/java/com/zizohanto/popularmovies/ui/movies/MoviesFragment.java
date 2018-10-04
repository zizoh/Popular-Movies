package com.zizohanto.popularmovies.ui.movies;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.zizohanto.popularmovies.R;
import com.zizohanto.popularmovies.data.database.favouritemovie.FavouriteMovie;
import com.zizohanto.popularmovies.data.database.movie.Movie;
import com.zizohanto.popularmovies.databinding.MoviesFragBinding;
import com.zizohanto.popularmovies.ui.details.DetailsActivity;
import com.zizohanto.popularmovies.utils.InjectorUtils;
import com.zizohanto.popularmovies.utils.NetworkState;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import timber.log.Timber;

public class MoviesFragment extends Fragment implements MovieAdapter.MovieItemClickListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String KEY_PAGE_TO_LOAD = "PAGE_TO_LOAD";
    private static final String KEY_IS_LOADING = "IS_LOADING";
    private static final String KEY_IS_FIRST_TIME_FETCH = "IS_FIRST_TIME_FETCH";

    private MoviesSortType mCurrentSortType;

    private int mPageToLoad = 1;
    private boolean isFirstTimeFetch;
    private boolean isLoading;
    private boolean isFavouriteView;
    private String mMoviesSortType;

    private Context mContext;
    private MoviesFragBinding mMoviesFragBinding;
    private MovieAdapter mMovieAdapter;
    private MoviesFragViewModel mViewModel;
    private RecyclerView mRecyclerView;
    private ScrollChildSwipeRefreshLayout mSwipeRefreshLayout;
    private SharedPreferences mSharedPreference;

    public MoviesFragment() {
        // Requires empty public constructor
    }

    public static MoviesFragment newInstance() {
        return new MoviesFragment();
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
        setScrollListener(layoutManager);

        isFirstTimeFetch = true;

        if (savedInstanceState != null) {
            mPageToLoad = savedInstanceState.getInt(KEY_PAGE_TO_LOAD);
            isLoading = savedInstanceState.getBoolean(KEY_IS_LOADING);
            isFirstTimeFetch = savedInstanceState.getBoolean(KEY_IS_FIRST_TIME_FETCH);
        }
        setupSharedPreferences();
        setActionBarTitle();
        setupViewModel();
        observeMovies();
        observeNetworkState();
        observeFavouriteMovies();

        return root;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(KEY_PAGE_TO_LOAD, mPageToLoad);
        outState.putBoolean(KEY_IS_LOADING, isLoading);
        outState.putBoolean(KEY_IS_FIRST_TIME_FETCH, isFirstTimeFetch);

        super.onSaveInstanceState(outState);
    }

    private void setMoviesToAdapter(@Nullable List<Movie> movies) {
        loading(false);
        if (movies != null && movies.size() != 0) {
            Timber.e("First movie type: " + String.valueOf(movies.get(0).getListType()));
            mMovieAdapter.setMovieData(movies);
        }
    }

    public void setLoadingIndicator(final boolean active) {
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(active);
            }
        });
    }

    private void setProgressIndicator() {
        mSwipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(mContext, R.color.colorPrimary),
                ContextCompat.getColor(mContext, R.color.colorAccent),
                ContextCompat.getColor(mContext, R.color.colorPrimaryDark)
        );
        // Set the scrolling view in the custom SwipeRefreshLayout.
        mSwipeRefreshLayout.setScrollUpChild(mRecyclerView);

        setLoadingIndicator(true);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!isLoading) {
                    fetchFirstMovies();
                }
            }
        });
    }

    private void setScrollListener(GridLayoutManager layoutManager) {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (!isFavouriteView) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    boolean isMoreFetchNeeded = isMoreFetchNeeded(visibleItemCount, totalItemCount,
                            firstVisibleItemPosition);

                    if (isMoreFetchNeeded && !isLoading) {
                        mPageToLoad++;
                        fetchMoreMovies();
                    }
                }
            }
        });
    }

    private void setupSharedPreferences() {
        mSharedPreference = PreferenceManager.getDefaultSharedPreferences(mContext);
        mMoviesSortType = mSharedPreference.getString(getString(R.string.pref_key_sort_by),
                getString(R.string.pref_sort_by_popularity_value));

        if (mMoviesSortType.equals(getString(R.string.pref_sort_by_popularity_value))) {
            mCurrentSortType = MoviesSortType.POPULAR_MOVIES;
        } else if (mMoviesSortType.equals(getString(R.string.pref_sort_by_top_rated_value))) {
            mCurrentSortType = MoviesSortType.TOP_RATED_MOVIES;
        } else if (mMoviesSortType.equals(getString(R.string.pref_sort_by_favorite_value))) {
            isFavouriteView = true;
            mCurrentSortType = MoviesSortType.FAVORITE_MOVIES;
        }
        mSharedPreference.registerOnSharedPreferenceChangeListener(this);
    }

    private void setActionBarTitle() {
        MoviesActivity moviesActivity = (MoviesActivity) getActivity();
        String activityTitle;
        switch (mCurrentSortType) {
            case FAVORITE_MOVIES:
                activityTitle = getString(R.string.movies_act_title_favorite);
                break;
            case TOP_RATED_MOVIES:
                activityTitle = getString(R.string.movies_act_title_top_rated);
                break;
            default:
                activityTitle = getString(R.string.movies_act_title_popular);
        }
        moviesActivity.setActionBarTitle(activityTitle);
    }

    private void setupViewModel() {
        MoviesFragViewModelFactory factory =
                InjectorUtils.provideMFViewModelFactory(mContext,
                        mMoviesSortType, true, mPageToLoad);
        mViewModel = ViewModelProviders.of(this, factory).get(MoviesFragViewModel.class);
    }

    private void observeMovies() {
        mViewModel.getMovies().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> movies) {
                setMoviesToAdapter(movies);
            }
        });
    }

    private void observeNetworkState() {
        mViewModel.getNetworkState().observe(this, new Observer<NetworkState>() {
            @Override
            public void onChanged(@Nullable NetworkState networkState) {
                if (networkState != null && !isFavouriteView) {
                    if (networkState.getStatus() == NetworkState.Status.RUNNING) {
                        loading(true);
                    } else if (networkState.getStatus() == NetworkState.Status.FAILED) {
                        loading(false);
                        Toast.makeText(mContext, networkState.getMsg(), Toast.LENGTH_SHORT).show();
                        //Snackbar.make(mTitle, networkState.getMsg(), Snackbar.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    public void loading(boolean loading) {
        isLoading = loading;
        setLoadingIndicator(loading);
    }

    private void observeFavouriteMovies() {
        loading(true);
        mViewModel.getAllFavouriteMovies().observe(this, new Observer<List<FavouriteMovie>>() {
            @Override
            public void onChanged(@Nullable List<FavouriteMovie> favouriteMovies) {
                if (isFavouriteView) {
                    if (favouriteMovies != null && favouriteMovies.size() != 0) {
                        //getMoviesFromFavourites(favouriteMovies);
                        List<Movie> movies = convertFavMoviesToMovies(favouriteMovies);
                        setMoviesToAdapter(movies);
                    }
                }
            }
        });
    }

    private List<Movie> convertFavMoviesToMovies(List<FavouriteMovie> favouriteMovies) {
        List<Movie> movies = new ArrayList<>();
        for (int i = 0; i < favouriteMovies.size(); i++) {
            FavouriteMovie favouriteMovie = favouriteMovies.get(i);
            Movie movie = new Movie();

            movie.setTitle(favouriteMovie.getTitle());
            movie.setId(favouriteMovie.getId());
            movie.setVoteCount(favouriteMovie.getVoteCount());
            movie.setVideo(favouriteMovie.getVideo());
            movie.setPopularity(favouriteMovie.getPopularity());
            movie.setPosterPath(favouriteMovie.getPosterPath());
            movie.setOriginalTitle(favouriteMovie.getOriginalTitle());
            movie.setBackdropPath(favouriteMovie.getBackdropPath());
            movie.setOverview(favouriteMovie.getOverview());
            movie.setReleaseDate(favouriteMovie.getReleaseDate());

            movies.add(movie);
        }
        return movies;
    }

    private boolean isMoreFetchNeeded(int visibleItemCount, int totalItemCount, int firstVisibleItemPosition) {
        return (visibleItemCount + firstVisibleItemPosition) >= totalItemCount &&
                firstVisibleItemPosition >= 0;
    }

    private void fetchFirstMovies() {
        loading(true);
        mPageToLoad = 1;
        isFirstTimeFetch = true;
        getMovies();
    }

    private void fetchMoreMovies() {
        loading(true);
        getMovies();
    }

    private void getMovies() {
        mViewModel.getCurrentMovies(mMoviesSortType, false, mPageToLoad);
    }

    private void showSortingPopUpMenu() {
        PopupMenu popup = new PopupMenu(mContext, Objects.requireNonNull(getActivity()).findViewById(R.id.menu_filter));
        popup.getMenuInflater().inflate(R.menu.sort_movies, popup.getMenu());

        checkAppropriateButton(popup);

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                SharedPreferences.Editor editor = mSharedPreference.edit();
                switch (item.getItemId()) {
                    case R.id.favorite:
                        editor.putString(getString(R.string.pref_key_sort_by),
                                getString(R.string.pref_sort_by_favorite_value));
                        isFavouriteView = true;
                        mCurrentSortType = MoviesSortType.FAVORITE_MOVIES;
                        break;
                    case R.id.top_rated:
                        editor.putString(getString(R.string.pref_key_sort_by),
                                getString(R.string.pref_sort_by_top_rated_value));
                        isFavouriteView = false;
                        mCurrentSortType = MoviesSortType.TOP_RATED_MOVIES;
                        break;
                    default:
                        editor.putString(getString(R.string.pref_key_sort_by),
                                getString(R.string.pref_sort_by_popularity_value));
                        isFavouriteView = false;
                        mCurrentSortType = MoviesSortType.POPULAR_MOVIES;
                        break;
                }
                editor.apply();
                setActionBarTitle();
                return true;
            }
        });
        popup.show();
    }

    /*
     *  Method to mark the appropriate checkbox based on the existing preference
     */
    private void checkAppropriateButton(PopupMenu popup) {
        if (getString(R.string.pref_sort_by_popularity_value).equals(mMoviesSortType)) {
            popup.getMenu().findItem(R.id.most_popular).setChecked(true);
        } else if (getString(R.string.pref_sort_by_top_rated_value).equals(mMoviesSortType)) {
            popup.getMenu().findItem(R.id.top_rated).setChecked(true);
        } else if (getString(R.string.pref_sort_by_favorite_value).equals(mMoviesSortType)) {
            popup.getMenu().findItem(R.id.favorite).setChecked(true);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_key_sort_by))) {
            mMoviesSortType = sharedPreferences.getString(key,
                    getString(R.string.pref_sort_by_popularity_value));
            if (isFavouriteView) {
                observeFavouriteMovies();
            } else {
                fetchFirstMovies();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_filter:
                showSortingPopUpMenu();
                break;
            case R.id.menu_refresh:
                fetchFirstMovies();
                break;
        }
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.movies_fragment_menu, menu);
    }

    @Override
    public void onMovieClick(Movie movie) {
        Intent movieDetailIntent = new Intent(getActivity(), DetailsActivity.class);
        movieDetailIntent.putExtra(DetailsActivity.MOVIE_ID_EXTRA, movie.getId());
        startActivity(movieDetailIntent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mSharedPreference.unregisterOnSharedPreferenceChangeListener(this);
    }
}
