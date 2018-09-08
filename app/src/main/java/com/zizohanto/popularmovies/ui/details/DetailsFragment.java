package com.zizohanto.popularmovies.ui.details;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.DrawableCompat;
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

    private static String buildCompleteBackdropUrl(String filePath) {

        final String baseUrl = "http://image.tmdb.org/t/p/";

        final String backdropImageSize = "w500/";

        return String.format("%s%s%s", baseUrl, backdropImageSize, filePath);
    }

    private void setupViewModel(String title) {
        DetailsFragViewModelFactory factory = InjectorUtils.
                provideDFViewModelFactory(mContext, title);
        mViewModel = ViewModelProviders.of(this, factory).get(DetailsFragViewModel.class);
    }

    @Override
    public View onCreateView(@Nullable LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        assert inflater != null;
        mDetailsFragBinding = DataBindingUtil.inflate(inflater, R.layout.details_frag, container, false);
        View root = mDetailsFragBinding.getRoot();
        mContext = getActivity();
        String title = null;
        if (null != getArguments()) {
            title = getArguments().getString(MOVIE_TITLE_EXTRA);
        }

        setupViewModel(title);

        mViewModel.getMovie().observe(this, new Observer<Movie>() {
            @Override
            public void onChanged(@Nullable Movie movie) {
                if (null != movie) {
                    String posterUrl = movie.getPosterPath();
                    Picasso.with(mContext)
                            .load(buildCompleteBackdropUrl(posterUrl))
                            .error(mContext.getResources().getDrawable(R.drawable.no_image))
                            .placeholder(mContext.getResources().getDrawable(R.drawable.poster_placeholder))
                            .into(mDetailsFragBinding.ivBackdropImage);

                    String title = movie.getTitle();
                    mDetailsFragBinding.tvTitle.setText(title);

                    LayerDrawable layerDrawable = (LayerDrawable) mDetailsFragBinding.rating.getProgressDrawable();
                    DrawableCompat.setTint(DrawableCompat.wrap(layerDrawable.getDrawable(0)), Color.WHITE);
                    DrawableCompat.setTint(DrawableCompat.wrap(layerDrawable.getDrawable(1)), Color.YELLOW);
                    DrawableCompat.setTint(DrawableCompat.wrap(layerDrawable.getDrawable(2)), Color.YELLOW);

                    Double voteAverage = movie.getVoteAverage();
                    float d = (float) (voteAverage / 2);

                    mDetailsFragBinding.rating.setRating(d);

                    String popularity = String.valueOf(movie.getPopularity());
                    mDetailsFragBinding.tvPopularity.setText(
                            String.format("(%s)", popularity));

                    String releaseDate = movie.getReleaseDate();
                    String releaseYear = releaseDate.substring(0, 4);
                    mDetailsFragBinding.tvReleaseYear.setText(String.format("Released: %s", releaseYear));

                    String synopsis = movie.getOverview();
                    mDetailsFragBinding.tvPlotSynopsis.setText(synopsis);
                }
            }
        });

        return root;
    }
}
