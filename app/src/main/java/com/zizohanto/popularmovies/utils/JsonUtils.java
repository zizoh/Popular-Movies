package com.zizohanto.popularmovies.utils;

import com.zizohanto.popularmovies.data.database.Movie;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtils {

    public static Movie parseMovieJson(String json) throws JSONException {

        /* Movie information. Each movie information is an element of the passed json string */
        final String MOVIE_TITLE = "title";

        final String MOVIE_VOTE_AVERAGE = "vote_average";

        final String MOVIE_POPULARITY = "popularity";

        final String MOVIE_RELEASE_DATE = "release_date";

        final String MOVIE_POSTER_URL = "poster_path";

        final String MOVIE_PLOT_SYNOPSIS = "overview";

        JSONObject movieJSON = new JSONObject(json);

        /* Get the String representing movie title */
        String title = getStringFromJSONObject(movieJSON, MOVIE_TITLE);

        /* Get the long representing the movie's vote average */
        long voteAverage = getLongFromJSONObject(movieJSON, MOVIE_VOTE_AVERAGE);

        /* Get the long representing the movie's popularity */
        long popularity = getLongFromJSONObject(movieJSON, MOVIE_POPULARITY);

        /* Get the string representing the movie's release date */
        String releaseDate = getStringFromJSONObject(movieJSON, MOVIE_RELEASE_DATE);

        /* Get the string representing the movie's poster url */
        String posterUrl = getStringFromJSONObject(movieJSON, MOVIE_POSTER_URL);

        /* Get the string representing the movie's plot synopsis */
        String plotSynopsis = getStringFromJSONObject(movieJSON, MOVIE_PLOT_SYNOPSIS);

        return new Movie(title, voteAverage, popularity, releaseDate,
                buildCompletePosterUrl(posterUrl), plotSynopsis);
    }

    private static String getStringFromJSONObject(JSONObject jsonObject, String key) throws JSONException {
        return jsonObject.getString(key);
    }

    private static long getLongFromJSONObject(JSONObject jsonObject, String key) throws JSONException {
        return jsonObject.getLong(key);
    }

    private static String buildCompletePosterUrl(String relativePath) {

        final String moviePosterBaseUrl = "http://image.tmdb.org/t/p/";

        final String moviePosterSize = "w185";

        return new String(moviePosterBaseUrl + moviePosterSize + "/" + relativePath);
    }
}
