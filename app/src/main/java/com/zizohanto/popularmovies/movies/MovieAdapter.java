package com.zizohanto.popularmovies.movies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.zizohanto.popularmovies.R;
import com.zizohanto.popularmovies.data.Movie;
import com.zizohanto.popularmovies.utils.JsonUtils;

import org.json.JSONException;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private int mNumberItems;
    private List<Movie> mMovies;
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

        String[] movies = mContext.getResources().getStringArray(R.array.popular_movies_details);
        String json = movies[position];
        Movie movie = null;
        try {
            movie = JsonUtils.parseMovieJson(json);
        } catch (JSONException e) {
            // TODO: Check there is no error parsing JSON without escaping apostrophe (')
            e.printStackTrace();
        }
        if (movie == null) {
            // TODO: Handle error
            //closeOnError();
            Toast.makeText(mContext, "E didn't work at position: " + String.valueOf(position), Toast.LENGTH_LONG).show();
            return;
        }

        String moviePosterUrl = movie.getPosterUrl();

        Picasso.with(mContext)
                .load(moviePosterUrl)
                .into(holder.mMoviePoster);

    }

    @Override
    public int getItemCount() {
        return mNumberItems;
    }

    public void setMovieData(List<Movie> movies) {
        mMovies = movies;
        notifyDataSetChanged();
    }

    public interface MovieItemClickListener {
        void onMovieClick(int clickedMovieIndex);
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
            mOnClickListener.onMovieClick(clickedPosition);
        }

        private void bind(String posterUrl) {
            //Toast.makeText(mContext, "Picasso baiby: " + posterUrl, Toast.LENGTH_SHORT).show();
            Picasso.with(mContext)
                    .load(posterUrl)
                    .into(mMoviePoster);
        }
    }
}
