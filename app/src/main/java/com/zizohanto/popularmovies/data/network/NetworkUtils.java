package com.zizohanto.popularmovies.data.network;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * These utilities will be used to communicate with the themoviedb.org servers.
 */
final class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String MOVIE_BASE_URL = "http://api.themoviedb.org/3";

    private static final String POPULAR_MOVIES_ENDPOINT = "/movie/popular";

    private static final String TOP_RATED_MOVIES_ENDPOINT = "/movie/top_rated";

    //private static final String MOVIES_ENDPOINT = POPULAR_MOVIES_ENDPOINT;

    /* The query parameter allows us to provide an API KEY string to the API */
    private static final String QUERY_PARAM = "api_key";

    private static final String YOUR_API_KEY = "***REMOVED***";


    /**
     * Retrieves the proper URL to query for the movie data.
     *
     * @return URL to query movie service
     */
    static URL getUrl(String moviesSortType) {
        return buildUrlWithEndpoint(moviesSortType);
    }

    /**
     * Builds the URL used to talk to the movie server using a specified endpoint.
     * <p>
     * http://api.themoviedb.org/3/movie/popular?api_key=[YOUR_API_KEY]
     *
     * @param uriEndpoint The endpoint that will be queried for.
     * @return The URL to use to query the movie server.
     */
    private static URL buildUrlWithEndpoint(String uriEndpoint) {
        Uri movieQueryUri = Uri.parse(MOVIE_BASE_URL + uriEndpoint).buildUpon()
                .appendQueryParameter(QUERY_PARAM, YOUR_API_KEY)
                .build();

        try {
            URL movieQueryUrl = new URL(movieQueryUri.toString());
            Log.v(TAG, "URL: " + movieQueryUrl);
            return movieQueryUrl;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response, null if no response
     * @throws IOException Related to network and stream reading
     */
    static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            String response = null;
            if (hasInput) {
                response = scanner.next();
            }
            scanner.close();
            return response;
        } finally {
            urlConnection.disconnect();
        }
    }
}
