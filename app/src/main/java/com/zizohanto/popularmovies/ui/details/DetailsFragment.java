package com.zizohanto.popularmovies.ui.details;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;
import com.zizohanto.popularmovies.R;
import com.zizohanto.popularmovies.data.database.Movie;
import com.zizohanto.popularmovies.databinding.DetailsFragBinding;
import com.zizohanto.popularmovies.utils.InjectorUtils;

public class DetailsFragment extends Fragment {
    public static final String MOVIE_TITLE_EXTRA = "MOVIE_TITLE_EXTRA";

    private DetailsFragBinding mDetailsFragBinding;
    private DetailsFragViewModel mViewModel;
    private Context mContext;

    public DetailsFragment() {
        // Requires empty public constructor
    }

    public static DetailsFragment newInstance() {
        return new DetailsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@Nullable LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mDetailsFragBinding = DataBindingUtil.inflate(inflater, R.layout.details_frag, container, false);
        View root = mDetailsFragBinding.getRoot();
        mContext = getActivity();

        String title = getArguments().getString(MOVIE_TITLE_EXTRA);

        setupViewModel(title);

        mViewModel.getMovie().observe(this, new Observer<Movie>() {
            @Override
            public void onChanged(@Nullable Movie movie) {
                if (null != movie) {
                    String posterUrl = movie.getPosterUrl();
                    Picasso.with(mContext)
                            .load(posterUrl)
                            .into(mDetailsFragBinding.ivMoviePoster);

                    String title = movie.getTitle();
                    mDetailsFragBinding.tvTitle.setText(title);

                    String releaseDate = movie.getReleaseDate();
                    mDetailsFragBinding.tvReleaseDate.setText(releaseDate);

                    String voteAverage = String.valueOf(movie.getVoteAverage());
                    mDetailsFragBinding.tvVoteAverage.setText(voteAverage);

                    String popularity = String.valueOf(movie.getPopularity());
                    mDetailsFragBinding.tvPopularity.setText(popularity);

                    String synopsis = movie.getPlotSynopsis();
                    mDetailsFragBinding.tvPlotSynopsis.setText(synopsis);
                }
            }
        });

        return root;
    }

    private void setupViewModel(String title) {
        DetailsFragViewModelFactory factory = InjectorUtils.
                provideDFViewModelFactory(mContext.getApplicationContext(), title);
        mViewModel = ViewModelProviders.of(this, factory).get(DetailsFragViewModel.class);
    }
}
