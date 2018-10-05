package com.zizohanto.popularmovies.ui.details;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.squareup.picasso.Picasso;
import com.zizohanto.popularmovies.R;
import com.zizohanto.popularmovies.data.database.favouritemovie.FavouriteMovie;
import com.zizohanto.popularmovies.data.database.movie.Movie;
import com.zizohanto.popularmovies.data.database.review.Review;
import com.zizohanto.popularmovies.data.database.video.Video;
import com.zizohanto.popularmovies.databinding.DetailsFragBinding;
import com.zizohanto.popularmovies.utils.InjectorUtils;

import java.util.List;

public class DetailsFragment extends Fragment implements View.OnClickListener,
        VideoAdapter.VideoItemClickListener {
    public static final String MOVIE_ID_EXTRA = "MOVIE_ID_EXTRA";
    private static final String YOUTUBE_VIDEO_URL = "https://www.youtube.com/watch?v=";

    private int mListTpye;
    private Integer mId;
    private String mTitle;
    private Movie mMovie;

    private DetailsFragBinding mDetailsFragBinding;
    private Context mContext;
    private DetailsFragViewModel mViewModel;
    private RecyclerView mRecyclerViewVideos;
    private RecyclerView mRecyclerViewReviews;
    private VideoAdapter mVideoAdapter;
    private ReviewAdapter mReviewAdapter;

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
        setActionBarTitle();

        mContext = getActivity();

        if (null != getArguments()) {
            mId = getArguments().getInt(MOVIE_ID_EXTRA, 0);
        }


        mDetailsFragBinding.clDetailsFragTop.cbFavourite.setOnClickListener(this);

        setUpVideosView();

        setUpReviewsView();

        setupViewModel(mId);

        observeMovie();

        observeFavourite();

        observeVideos();

        observeReviews();

        return root;
    }

    private void setActionBarTitle() {
        DetailsActivity detailsActivity = (DetailsActivity) getActivity();
        detailsActivity.getSupportActionBar().setTitle(getString(R.string.details_act_title));
    }

    private void setUpVideosView() {
        mRecyclerViewVideos = mDetailsFragBinding.llDetailsFragBottom.rvVideos;

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        mRecyclerViewVideos.setLayoutManager(layoutManager);

        mVideoAdapter = new VideoAdapter(mContext, this);

        mRecyclerViewVideos.setAdapter(mVideoAdapter);
    }

    private void setUpReviewsView() {
        mRecyclerViewReviews = mDetailsFragBinding.llDetailsFragBottom.rvReviews;

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerViewReviews.setLayoutManager(layoutManager);

        mReviewAdapter = new ReviewAdapter(mContext);

        mRecyclerViewReviews.setAdapter(mReviewAdapter);
    }

    private void setupViewModel(Integer id) {
        DetailsFragViewModelFactory factory = InjectorUtils.
                provideDFViewModelFactory(mContext, id);
        mViewModel = ViewModelProviders.of(this, factory).get(DetailsFragViewModel.class);
    }

    private void observeMovie() {
        mViewModel.getMovie().observe(this, new Observer<Movie>() {
            @Override
            public void onChanged(@Nullable Movie movie) {
                if (null != movie) {
                    String posterUrl = movie.getPosterPath();
                    Picasso.with(mContext)
                            .load(buildCompleteBackdropUrl(posterUrl))
                            .error(mContext.getResources().getDrawable(R.drawable.no_image))
                            .placeholder(mContext.getResources().getDrawable(R.drawable
                                    .im_poster_placeholder))
                            .into(mDetailsFragBinding.clDetailsFragTop.ivBackdropImage);

                    mTitle = movie.getTitle();
                    mDetailsFragBinding.clDetailsFragTop.tvTitle.setText(mTitle);

                    LayerDrawable layerDrawable = (LayerDrawable) mDetailsFragBinding
                            .clDetailsFragTop.rating.getProgressDrawable();
                    DrawableCompat.setTint(DrawableCompat.wrap(layerDrawable.getDrawable(0)),
                            Color.WHITE);
                    DrawableCompat.setTint(DrawableCompat.wrap(layerDrawable.getDrawable(1)),
                            Color.YELLOW);
                    DrawableCompat.setTint(DrawableCompat.wrap(layerDrawable.getDrawable(2)),
                            Color.YELLOW);

                    Double voteAverage = movie.getVoteAverage();
                    float d = (float) (voteAverage / 2);

                    mDetailsFragBinding.clDetailsFragTop.rating.setRating(d);

                    String popularity = String.valueOf(movie.getPopularity());
                    mDetailsFragBinding.clDetailsFragTop.tvPopularity.setText(
                            String.format("(%s)", popularity));

                    String releaseDate = movie.getReleaseDate();
                    String releaseYear = releaseDate.substring(0, 4);
                    mDetailsFragBinding.clDetailsFragTop.tvReleaseYear.setText(String
                            .format("Released: %s", releaseYear));

                    String synopsis = movie.getOverview();
                    mDetailsFragBinding.clDetailsFragTop.tvPlotSynopsis.setText(synopsis);

                    mMovie = movie;

                }
            }
        });
    }

    private void observeFavourite() {
        mViewModel.getFavouriteMovie().observe(this, new Observer<FavouriteMovie>() {
            @Override
            public void onChanged(@Nullable FavouriteMovie favouriteMovie) {
                if (favouriteMovie != null) {
                    String posterUrl = favouriteMovie.getPosterPath();
                    Picasso.with(mContext)
                            .load(buildCompleteBackdropUrl(posterUrl))
                            .error(mContext.getResources().getDrawable(R.drawable.no_image))
                            .placeholder(mContext.getResources().getDrawable(R.drawable
                                    .im_poster_placeholder))
                            .into(mDetailsFragBinding.clDetailsFragTop.ivBackdropImage);

                    mTitle = favouriteMovie.getTitle();
                    mDetailsFragBinding.clDetailsFragTop.tvTitle.setText(mTitle);

                    LayerDrawable layerDrawable = (LayerDrawable) mDetailsFragBinding
                            .clDetailsFragTop.rating.getProgressDrawable();
                    DrawableCompat.setTint(DrawableCompat.wrap(layerDrawable.getDrawable(0)),
                            Color.WHITE);
                    DrawableCompat.setTint(DrawableCompat.wrap(layerDrawable.getDrawable(1)),
                            Color.YELLOW);
                    DrawableCompat.setTint(DrawableCompat.wrap(layerDrawable.getDrawable(2)),
                            Color.YELLOW);

                    Double voteAverage = favouriteMovie.getVoteAverage();
                    float d = (float) (voteAverage / 2);

                    mDetailsFragBinding.clDetailsFragTop.rating.setRating(d);

                    String popularity = String.valueOf(favouriteMovie.getPopularity());
                    mDetailsFragBinding.clDetailsFragTop.tvPopularity.setText(
                            String.format("(%s)", popularity));

                    String releaseDate = favouriteMovie.getReleaseDate();
                    String releaseYear = releaseDate.substring(0, 4);
                    mDetailsFragBinding.clDetailsFragTop.tvReleaseYear.setText(String
                            .format("Released: %s", releaseYear));

                    String synopsis = favouriteMovie.getOverview();
                    mDetailsFragBinding.clDetailsFragTop.tvPlotSynopsis.setText(synopsis);
                    mDetailsFragBinding.clDetailsFragTop.cbFavourite.setChecked(true);
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

    private void observeReviews() {
        mViewModel.getReviews().observe(this, new Observer<List<Review>>() {
            @Override
            public void onChanged(@Nullable List<Review> reviews) {
                if (reviews != null && reviews.size() != 0) {
                    mReviewAdapter.setReviewData(reviews);
                }
            }
        });
    }

    @Override
    public void onVideoClick(Video clickedVideo) {
        String key = clickedVideo.getKey();
        // https://www.youtube.com/watch?v=SUXWAEX2jlg
        String youtubeVideoUrl = YOUTUBE_VIDEO_URL + key;

        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + key));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(youtubeVideoUrl));
        try {
            startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            startActivity(webIntent);
        }
    }

    @Override
    public void onClick(View v) {
        boolean checked = ((CheckBox) v).isChecked();
        switch (v.getId()) {
            case R.id.cb_favourite:
                if (checked) {
                    saveFavourite();
                } else {
                    deleteFavourite();
                }
        }
    }

    private void saveFavourite() {
        mViewModel.saveFavouriteMovie(new FavouriteMovie(mMovie.getListType(),
                mMovie.getTitle(),
                mMovie.getId(),
                mMovie.getVoteCount(),
                mMovie.getVideo(),
                mMovie.getVoteAverage(),
                mMovie.getPopularity(),
                mMovie.getPosterPath(),
                mMovie.getOriginalTitle(),
                mMovie.getBackdropPath(),
                mMovie.getOverview(),
                mMovie.getReleaseDate()));
    }

    private void deleteFavourite() {
        mViewModel.deleteFavouriteMovie();
    }
}
