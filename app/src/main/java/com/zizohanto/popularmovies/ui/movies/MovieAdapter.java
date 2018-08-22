package com.zizohanto.popularmovies.ui.movies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.zizohanto.popularmovies.R;
import com.zizohanto.popularmovies.data.database.Movie;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private List<Movie> mMovies;
    private Context mContext;
    private MoviesFragment mMoviesFragment;
    private MovieItemClickListener mOnClickListener;

    public MovieAdapter(Context context, MoviesFragment fragment, MovieItemClickListener listener) {
        mContext = context;
        mOnClickListener = listener;
        mMoviesFragment = fragment;
    }


    @NonNull
    @Override
    public MovieAdapter.MovieAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                                  int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context)
                .inflate(R.layout.movies_list_item, parent, false);

        return new MovieAdapterViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MovieAdapter.MovieAdapterViewHolder holder, int position) {

        Movie movie = mMovies.get(position);

        String moviePosterUrl = movie.getPosterUrl();

        Picasso.with(mContext)
                .load(moviePosterUrl)
                .into(holder.mMoviePoster);

    }

    @Override
    public int getItemCount() {
        if (null == mMovies) {
            return 0;
        } else {
            return mMovies.size();
        }
    }

    public void setMovieData(List<Movie> newMovies) {
        // If there was no forecast data, then recreate all of the list
        if (mMovies == null) {
            mMovies = newMovies;
            notifyDataSetChanged();
        } else {
            /*
             * Otherwise we use DiffUtil to calculate the changes and update accordingly. This
             * shows the four methods you need to override to return a DiffUtil callback. The
             * old list is the current list stored in mForecast, where the new list is the new
             * values passed in from the observing the database.
             */

            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return mMovies.size();
                }

                @Override
                public int getNewListSize() {
                    return newMovies.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return mMovies.get(oldItemPosition).getId() ==
                            newMovies.get(newItemPosition).getId();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    Movie newWeather = newMovies.get(newItemPosition);
                    Movie oldWeather = newMovies.get(oldItemPosition);
                    return newWeather.getId() == oldWeather.getId()
                            && newWeather.getTitle().equals(oldWeather.getTitle());
                }
            });
            mMovies = newMovies;
            result.dispatchUpdatesTo(this);
        }
    }

    public interface MovieItemClickListener {
        void onMovieClick(Movie clickedMovie);
    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView mMoviePoster;

        private MovieAdapterViewHolder(View itemView) {
            super(itemView);

            mMoviePoster = (ImageView) itemView.findViewById(R.id.iv_movie_poster);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onMovieClick(mMovies.get(clickedPosition));
        }

        private void bind(String posterUrl) {
            Picasso.with(mContext)
                    .load(posterUrl)
                    .into(mMoviePoster);
        }
    }
}
