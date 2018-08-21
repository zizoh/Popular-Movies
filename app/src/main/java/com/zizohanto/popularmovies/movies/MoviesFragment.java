package com.zizohanto.popularmovies.movies;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.zizohanto.popularmovies.R;
import com.zizohanto.popularmovies.data.database.database.Movie;
import com.zizohanto.popularmovies.databinding.MoviesFragBinding;
import com.zizohanto.popularmovies.utils.JsonUtils;

import org.json.JSONException;

import java.util.ArrayList;

public class MoviesFragment extends Fragment implements MovieAdapter.MovieItemClickListener {

    private Context mContext;
    private MoviesFragBinding mMoviesFragBinding;
    private MovieAdapter mMovieAdapter;

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

        // Set up tasks view
        // TODO: Check Fix of data binding in next line
        RecyclerView recyclerView = (RecyclerView) mMoviesFragBinding.rvMovies;

        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 4);
        recyclerView.setLayoutManager(layoutManager);

        mContext = getActivity();

        mMovieAdapter = new MovieAdapter(mContext, 20, this);

        recyclerView.setAdapter(mMovieAdapter);

        // Set up progress indicator
        final ScrollChildSwipeRefreshLayout swipeRefreshLayout =
                (ScrollChildSwipeRefreshLayout) root.findViewById(R.id.refresh_layout);
        swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(mContext, R.color.colorPrimary),
                ContextCompat.getColor(mContext, R.color.colorAccent),
                ContextCompat.getColor(mContext, R.color.colorPrimaryDark)
        );
        // Set the scrolling view in the custom SwipeRefreshLayout.
        swipeRefreshLayout.setScrollUpChild(recyclerView);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //mMovieAdapter.setMovieData(null);
            }
        });

        setHasOptionsMenu(true);

        loadMovies();

        return root;
    }

    private void loadMovies() {
        String[] moviesJSONResponse = mContext.getResources().getStringArray(R.array.popular_movies_details);
        Movie movie = null;
        ArrayList<Movie> moviesArray = new ArrayList<>();
        for (String movieJSONResponse : moviesJSONResponse) {
            try {
                movie = JsonUtils.parseMovieJson(movieJSONResponse);
            } catch (JSONException e) {
                // TODO: Check there is no error parsing JSON without escaping apostrophe (')
                e.printStackTrace();
            }
            if (movie == null) {
                // TODO: Handle error and remove Toast
                //closeOnError();
                Toast.makeText(mContext, "E didn't work at position: " +
                        String.valueOf(movieJSONResponse), Toast.LENGTH_LONG).show();
            }
            moviesArray.add(movie);
        }

        mMovieAdapter.setMovieData(moviesArray);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_filter:
                break;
            case R.id.menu_refresh:
                loadMovies();
                break;
        }
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.movies_fragment_menu, menu);
    }

    @Override
    public void onMovieClick(Movie clickedMovie) {
        Toast.makeText(mContext, clickedMovie.getTitle(), Toast.LENGTH_SHORT).show();
    }
}
