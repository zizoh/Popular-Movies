package com.zizohanto.popularmovies.data.network;

import com.zizohanto.popularmovies.data.database.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

final public class JsonUtils {

    /* Movie information. Each movie information is an element of the "result" array */
    private static final String PM_RESULTS = "results";

    private static final String PM_MOVIE_TITLE = "title";

    private static final String PM_MOVIE_VOTE_AVERAGE = "vote_average";

    private static final String PM_MOVIE_POPULARITY = "popularity";

    private static final String PM_MOVIE_RELEASE_DATE = "release_date";

    private static final String PM_MOVIE_POSTER_URL = "poster_path";

    private static final String PM_MOVIE_PLOT_SYNOPSIS = "overview";


    public static MovieResponse parseMovieJson(String json) throws JSONException {

        JSONObject movieJSON = new JSONObject(json);

        /* Get the array of JSONObjects representing the movies  */
        JSONArray jsonMoviesArray = movieJSON.getJSONArray(PM_RESULTS);

        Movie[] movies = new Movie[jsonMoviesArray.length()];

        for (int i = 0; i < jsonMoviesArray.length(); i++) {
            // Get the JSON object representing the movie
            JSONObject movieJson = jsonMoviesArray.getJSONObject(i);

            // Create the movie object
            Movie movie = fromJson(movieJson);

            movies[i] = movie;
        }
        return new MovieResponse(movies);
    }

    private static Movie fromJson(JSONObject movieJson) throws JSONException {

        /* Get the String representing movie title */
        String title = getStringFromJSONObject(movieJson, PM_MOVIE_TITLE);

        /* Get the long representing the movie's vote average */
        long voteAverage = getLongFromJSONObject(movieJson, PM_MOVIE_VOTE_AVERAGE);

        /* Get the long representing the movie's popularity */
        long popularity = getLongFromJSONObject(movieJson, PM_MOVIE_POPULARITY);

        /* Get the string representing the movie's release date */
        String releaseDate = getStringFromJSONObject(movieJson, PM_MOVIE_RELEASE_DATE);

        /* Get the string representing the movie's poster file path */
        String posterFilePath = getStringFromJSONObject(movieJson, PM_MOVIE_POSTER_URL);

        /* Get the string representing the movie's plot synopsis */
        String plotSynopsis = getStringFromJSONObject(movieJson, PM_MOVIE_PLOT_SYNOPSIS);

        return new Movie(title, voteAverage, popularity, releaseDate,
                buildCompletePosterUrl(posterFilePath), plotSynopsis);
    }

    private static String getStringFromJSONObject(JSONObject jsonObject, String key) throws JSONException {
        return jsonObject.getString(key);
    }

    private static long getLongFromJSONObject(JSONObject jsonObject, String key) throws JSONException {
        return jsonObject.getLong(key);
    }

    private static String buildCompletePosterUrl(String filePath) {

        final String moviePosterBaseUrl = "http://image.tmdb.org/t/p/";

        final String moviePosterSize = "w185/";

        return new String(moviePosterBaseUrl + moviePosterSize + filePath);
    }


}
