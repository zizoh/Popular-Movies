package com.zizohanto.popularmovies.ui.movies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.zizohanto.popularmovies.R;
import com.zizohanto.popularmovies.data.database.movie.Movie;

import java.util.List;

import timber.log.Timber;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private List<Movie> mMovies;
    private Context mContext;
    private MovieItemClickListener mOnClickListener;

    MovieAdapter(Context context, MovieItemClickListener listener) {
        mContext = context;
        mOnClickListener = listener;
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

    private static String buildCompletePosterUrl(String filePath) {
        final String baseUrl = "http://image.tmdb.org/t/p/";
        final String posterSize = "w185/";

        return String.format("%s%s%s", baseUrl, posterSize, filePath);
    }

    @Override
    public int getItemCount() {
        if (null == mMovies) {
            return 0;
        } else {
            return mMovies.size();
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MovieAdapter.MovieAdapterViewHolder holder, int position) {
        holder.bind(mMovies.get(position));
    }

    public void setMovieData(List<Movie> newMovies) {
        Timber.e("Movies don show!");
        // If there was no movie data, then recreate all of the list
        if (mMovies == null) {
            mMovies = newMovies;
            notifyDataSetChanged();
        } else {
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
                    boolean areItemsTheSame = true;
                    try {
                        areItemsTheSame = mMovies.get(oldItemPosition).getId() ==
                                newMovies.get(newItemPosition).getId();
                    } catch (ArrayIndexOutOfBoundsException e) {
                        Timber.e(e.toString());
                    }
                    return areItemsTheSame;
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    boolean contentsTheSame = true;
                    try {
                        Movie newMovie = newMovies.get(newItemPosition);
                        Movie oldMovie = mMovies.get(oldItemPosition);

                        contentsTheSame = newMovie.getId() == oldMovie.getId()
                                && newMovie.getTitle().equals(oldMovie.getTitle());
                    } catch (ArrayIndexOutOfBoundsException e) {
                        Timber.e(e.toString());
                    }
                    return contentsTheSame;
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
        private TextView mMovieTitle;

        private MovieAdapterViewHolder(View itemView) {
            super(itemView);

            mMoviePoster = itemView.findViewById(R.id.iv_backdrop_image);
            mMovieTitle = itemView.findViewById(R.id.tv_movie_title);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onMovieClick(mMovies.get(clickedPosition));
        }

        void bind(Movie movie) {
            String posterUrl = movie.getPosterPath();

            Picasso.with(mContext)
                    .load(buildCompletePosterUrl(posterUrl))
                    .error(mContext.getResources().getDrawable(R.drawable.no_image))
                    .placeholder(mContext.getResources().getDrawable(R.drawable.im_poster_placeholder))
                    .into(mMoviePoster);

            mMovieTitle.setText(movie.getTitle());
        }
    }
}
