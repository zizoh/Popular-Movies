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

public class MoviesFragment extends Fragment implements MovieAdapter.MovieItemClickListener {
    public static final String CURRENT_SORTING_KEY = "CURRENT_SORTING_KEY";

    private Context mContext;
    private MoviesFragBinding mMoviesFragBinding;
    private int mPosition = RecyclerView.NO_POSITION;
    private int mMoviesSortType;
    private MovieAdapter mMovieAdapter;
    private MoviesFragmentViewModel mViewModel;
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
        RecyclerView recyclerView = (RecyclerView) mMoviesFragBinding.rvMovies;

        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 4);
        recyclerView.setLayoutManager(layoutManager);

        mContext = getActivity();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);

        mMovieAdapter = new MovieAdapter(mContext, this);

        recyclerView.setAdapter(mMovieAdapter);

        mRecyclerViewReadyCallback = new RecyclerViewReadyCallback() {
            @Override
            public void onLayoutReady() {
                setLoadingIndicator(false);
            }
        };

        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (null != mRecyclerViewReadyCallback) {
                    mRecyclerViewReadyCallback.onLayoutReady();
                } else {
                    recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });

        // Load previously saved state, if available.
        if (savedInstanceState != null) {
            mMoviesSortType = savedInstanceState.getInt(CURRENT_SORTING_KEY);
        }

        setupViewModel(recyclerView);

        setProgressIndicator(recyclerView);

        setHasOptionsMenu(true);

        return root;
    }

    private void setupViewModel(RecyclerView recyclerView) {
        MoviesFragmentViewModelFactory factory = InjectorUtils.
                provideMoviesFragmentViewModelFactory(mContext, mMoviesSortType);
        mViewModel = ViewModelProviders.of(this, factory).get(MoviesFragmentViewModel.class);

        mViewModel.getMovies().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> movies) {
                if (movies != null && movies.size() != 0) {
                    mMovieAdapter.setMovieData(movies);
                    if (mPosition == RecyclerView.NO_POSITION) {
                        mPosition = 0;
                    } else {
                        recyclerView.smoothScrollToPosition(mPosition);
                    }
                    //MoviesFragment.this.setLoadingIndicator(false);
                } else {
                    //MoviesFragment.this.setLoadingIndicator(true);
                }
            }
        });
    }

    private void showSortingPopUpMenu() {
        PopupMenu popup = new PopupMenu(mContext, getActivity().findViewById(R.id.menu_filter));
        popup.getMenuInflater().inflate(R.menu.sort_movies, popup.getMenu());

        String sortBy = sharedPreferences.getString(getString(R.string.pref_key_sort_by),
                getString(R.string.pref_sort_by_popularity_value));

        if (MoviesSortType.MOST_POPULAR_MOVIES.equals(sortBy)) {
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(CURRENT_SORTING_KEY, mMoviesSortType);
        //Toast.makeText(mContext, "Current sort type: " + String.valueOf(mMoviesSortType), Toast.LENGTH_SHORT).show();

        super.onSaveInstanceState(outState);
    }

    private void setProgressIndicator(RecyclerView recyclerView) {
        mSwipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(mContext, R.color.colorPrimary),
                ContextCompat.getColor(mContext, R.color.colorAccent),
                ContextCompat.getColor(mContext, R.color.colorPrimaryDark)
        );
        // Set the scrolling view in the custom SwipeRefreshLayout.
        mSwipeRefreshLayout.setScrollUpChild(recyclerView);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
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
                setLoadingIndicator(true);
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

    public interface RecyclerViewReadyCallback {
        void onLayoutReady();
    }
}
