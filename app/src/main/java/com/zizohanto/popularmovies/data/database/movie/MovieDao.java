package com.zizohanto.popularmovies.data.database.movie;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface MovieDao {
    // Returns a list of all movies in the database
    @Query("SELECT * FROM movie")
    LiveData<List<Movie>> getAllMovies();

    /**
     * @param title The title you want movie for
     * @return {@link LiveData} of movie with title specified
     */
    @Query("SELECT * FROM movie WHERE title = :title")
    LiveData<Movie> getMovieByTitle(String title);

    /**
     * @param listType The listType you want movies for
     * @return {@link LiveData} of movies with listType specified
     */
    @Query("SELECT * FROM movie WHERE listType == :listType")
    LiveData<List<Movie>> getMoviesByType(int listType);

    /**
     * @param ids The ids you want movies for
     * @return {@link LiveData} of movies with ids specified
     */
    @Query("SELECT * FROM movie WHERE id IN(:ids)")
    LiveData<List<Movie>> getMoviesByIds(int[] ids);


    // Inserts multiple movies
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void bulkInsert(List<Movie> movies);

    // Deletes all movies from the database
    @Query("DELETE FROM movie")
    void deleteAllMovies();

    @Query("DELETE FROM movie WHERE listType == :listType")
    void deleteMoviesByListType(int listType);
}
