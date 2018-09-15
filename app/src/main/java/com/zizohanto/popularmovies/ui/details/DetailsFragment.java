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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;
import com.zizohanto.popularmovies.R;
import com.zizohanto.popularmovies.data.database.movie.Movie;
import com.zizohanto.popularmovies.data.database.video.Video;
import com.zizohanto.popularmovies.databinding.DetailsFragBinding;
import com.zizohanto.popularmovies.utils.InjectorUtils;

import java.util.List;

public class DetailsFragment extends Fragment {
    public static final String MOVIE_TITLE_EXTRA = "MOVIE_TITLE_EXTRA";
    public static final String MOVIE_ID_EXTRA = "MOVIE_ID_EXTRA";

    private DetailsFragBinding mDetailsFragBinding;
    private DetailsFragViewModel mViewModel;
    private VideoAdapter mVideoAdapter;
    private RecyclerView mRecyclerView;
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

        final String backdropImageSize = "w185/";

        return String.format("%s%s%s", baseUrl, backdropImageSize, filePath);
    }

    @Override
    public View onCreateView(@Nullable LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        assert inflater != null;
        mDetailsFragBinding = DataBindingUtil.inflate(inflater, R.layout.details_frag, container, false);
        View root = mDetailsFragBinding.getRoot();
        mContext = getActivity();
        String title = null;
        Integer id = 0;
        if (null != getArguments()) {
            title = getArguments().getString(MOVIE_TITLE_EXTRA);
            id = getArguments().getInt(MOVIE_ID_EXTRA, 0);
        }

        // Set up videos view
        mRecyclerView = mDetailsFragBinding.rvVideos;

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        mRecyclerView.setLayoutManager(layoutManager);

        mVideoAdapter = new VideoAdapter(mContext);

        mRecyclerView.setAdapter(mVideoAdapter);

        setupViewModel(title, id);

        observeMovies();

        observeVideos();

        return root;
    }

    private void setupViewModel(String title, Integer id) {
        DetailsFragViewModelFactory factory = InjectorUtils.
                provideDFViewModelFactory(mContext, title, id);
        mViewModel = ViewModelProviders.of(this, factory).get(DetailsFragViewModel.class);
    }

    private void observeMovies() {
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
    }

    private void observeVideos() {
        mViewModel.getVideos().observe(this, new Observer<List<Video>>() {
            @Override
            public void onChanged(@Nullable List<Video> videos) {
                if (videos != null && videos.size() != 0) {
                    mVideoAdapter.setVideoData(videos);
                }
            }
        });
    }
}
