package com.zizohanto.popularmovies.data.network;

import com.zizohanto.popularmovies.data.database.movie.MovieResponse;
import com.zizohanto.popularmovies.data.database.review.ReviewResponse;
import com.zizohanto.popularmovies.data.database.video.VideoResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {

    //api.themoviedb.org/3/movie/popular?api_key=1234&page=1
    @GET("movie/{moviesSortType}")
    Call<MovieResponse> getMovies(@Path("moviesSortType") String moviesSortType,
                                  @Query("api_key") String apiKey,
                                  @Query("page") Integer pageNumber);

    //api.themoviedb.org/3/movie/157336/videos?api_key=1234#
    @GET("movie/{id}/videos")
    Call<VideoResponse> getVideos(@Path("id") long id, @Query("api_key") String apiKey);

    //api.themoviedb.org/3/movie/157336/reviews?api_key=1234#
    @GET("movie/{id}/reviews")
    Call<ReviewResponse> getReviews(@Path("id") long id, @Query("api_key") String apiKey);

}
