package com.zizohanto.popularmovies.ui.movies;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.zizohanto.popularmovies.R;
import com.zizohanto.popularmovies.data.database.Movie;
import com.zizohanto.popularmovies.databinding.MoviesFragBinding;
import com.zizohanto.popularmovies.ui.details.DetailsActivity;
import com.zizohanto.popularmovies.utils.InjectorUtils;

import java.util.List;

public class MoviesFragment extends Fragment implements MovieAdapter.MovieItemClickListener,
        SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String LOG_TAG = MoviesFragment.class.getSimpleName();

    private Context mContext;
    private MoviesFragBinding mMoviesFragBinding;
    private int mPosition = RecyclerView.NO_POSITION;
    private static final int PAGE_SIZE = 20;
    public static final String MOST_POPULAR_MOVIES = "movie/popular";
    private String mMoviesSortType;
    private boolean mIsNotPreferenceChange = true;
    private boolean isLoading;
    private int mPageToLoad = 1;
    private MovieAdapter mMovieAdapter;
    private MoviesFragViewModel mViewModel;
    private RecyclerView mRecyclerView;
    private ScrollChildSwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerViewReadyCallback mRecyclerViewReadyCallback;
    private SharedPreferences sharedPreferences;


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
                (ScrollChildSwipeRefreshLayout) root.findViewById(R.id.refresh_layout);

        // Set up tasks view
        mRecyclerView = (RecyclerView) mMoviesFragBinding.rvMovies;

        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 2);
        mRecyclerView.setLayoutManager(layoutManager);

        mContext = getActivity();

        setupSharedPreferences();

        mMovieAdapter = new MovieAdapter(mContext, this);

        mRecyclerView.setAdapter(mMovieAdapter);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();


                if (isMoreFetchNeeded(visibleItemCount, totalItemCount, firstVisibleItemPosition) && !isLoading) {
                    mPageToLoad++;
                    Log.e(LOG_TAG, "fetch more movies called");
                    fetchMoreMovies(mPageToLoad);
                    isLoading = true;
                }
            }
        });

        mRecyclerViewReadyCallback = new RecyclerViewReadyCallback() {
            @Override
            public void onLayoutReady() {
                setLoadingIndicator(false);
            }
        };

        mRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (null != mRecyclerViewReadyCallback) {
                    mRecyclerViewReadyCallback.onLayoutReady();
                } else {
                    mRecyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });

        setupViewModel();

        setProgressIndicator();

        setHasOptionsMenu(true);

        return root;
    }

    private void fetchFirstMovies() {
        mPageToLoad = 1;
        setLoadingIndicator(true);
        mViewModel.getCurrentMovies(mMoviesSortType, false, mPageToLoad);
    }

    private void fetchMoreMovies(int pageToLoad) {
        setLoadingIndicator(true);
        mViewModel.getCurrentMovies(mMoviesSortType, false, pageToLoad);
    }

    private boolean isMoreFetchNeeded(int visibleItemCount, int totalItemCount, int firstVisibleItemPosition) {
        return (visibleItemCount + firstVisibleItemPosition) >= totalItemCount &&
                firstVisibleItemPosition >= 0 && totalItemCount >= PAGE_SIZE;
    }

    private void setupSharedPreferences() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        mMoviesSortType = sharedPreferences.getString(getString(R.string.pref_key_sort_by),
                getString(R.string.pref_sort_by_popularity_value));
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    private void setupViewModel() {
        MoviesFragViewModelFactory factory =
                InjectorUtils.provideMFViewModelFactory(mContext,
                        mMoviesSortType, mIsNotPreferenceChange, mPageToLoad);
        mViewModel = ViewModelProviders.of(this, factory).get(MoviesFragViewModel.class);

        mViewModel.getMovies().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> movies) {
                if (movies != null && movies.size() != 0) {
                    isLoading = false;
                    mMovieAdapter.setMovieData(movies);
                    //mSwipeRefreshLayout.setRefreshing(false);
                    if (mPosition == RecyclerView.NO_POSITION) {
                        mPosition = 0;
                    } else {
                        //mRecyclerView.smoothScrollToPosition(mPosition);
                    }
                    //MoviesFragment.this.setLoadingIndicator(false);
                } else {
                    //setLoadingIndicator(true);
                }
            }
        });
    }

    private void showSortingPopUpMenu() {
        PopupMenu popup = new PopupMenu(mContext, getActivity().findViewById(R.id.menu_filter));
        popup.getMenuInflater().inflate(R.menu.sort_movies, popup.getMenu());

        if (MOST_POPULAR_MOVIES.equals(mMoviesSortType)) {
            popup.getMenu().findItem(R.id.most_popular).setChecked(true);
        } else {
            popup.getMenu().findItem(R.id.top_rated).setChecked(true);
        }

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                switch (item.getItemId()) {
                    case R.id.top_rated:
                        editor.putString(getString(R.string.pref_key_sort_by),
                                getString(R.string.pref_sort_by_top_rated_value));
                        break;
                    default:
                        editor.putString(getString(R.string.pref_key_sort_by),
                                getString(R.string.pref_sort_by_popularity_value));
                        break;
                }
                editor.apply();
                return true;
            }
        });

        popup.show();
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
                //fetchFirstMovies();
            }
        });
    }

    public void setLoadingIndicator(final boolean active) {
        // setRefreshing() is called after the layout is done with everything else.
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(active);
            }
        });
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
    public void onMovieClick(String title) {
        Intent movieDetailIntent = new Intent(getActivity(), DetailsActivity.class);
        movieDetailIntent.putExtra(DetailsActivity.MOVIE_TITLE_EXTRA, title);
        startActivity(movieDetailIntent);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_key_sort_by))) {
            mMoviesSortType = sharedPreferences.getString(key,
                    getString(R.string.pref_sort_by_popularity_value));
        }
        //mIsNotPreferenceChange = false;
        /*mNumberOfPreferenceChange ++;
        if (mNumberOfPreferenceChange == 1) {

        } else {
            mIsNotPreferenceChange = true;
        }*/
        fetchFirstMovies();
    }

    public interface RecyclerViewReadyCallback {
        void onLayoutReady();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }
}
