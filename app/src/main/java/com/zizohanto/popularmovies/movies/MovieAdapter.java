package com.zizohanto.popularmovies.movies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.zizohanto.popularmovies.R;
import com.zizohanto.popularmovies.data.Movie;

import java.util.ArrayList;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private int mNumberItems;
    private ArrayList<Movie> mMovies;
    private Context mContext;

    private MovieItemClickListener mOnClickListener;

    public MovieAdapter(Context context, int numberOfItems, MovieItemClickListener listener) {
        mContext = context;
        mNumberItems = numberOfItems;
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
        return mNumberItems;
    }

    public void setMovieData(ArrayList<Movie> movies) {
        mMovies = movies;
        notifyDataSetChanged();
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
            //Toast.makeText(mContext, "Picasso baiby: " + posterUrl, Toast.LENGTH_SHORT).show();
            Picasso.with(mContext)
                    .load(posterUrl)
                    .into(mMoviePoster);
        }
    }
}
