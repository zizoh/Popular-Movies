package com.zizohanto.popularmovies.data.database.movie;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface MovieDao {
    /**
     * @param id The id you want movie for
     * @return {@link LiveData} of movie with id specified
     */
    @Query("SELECT * FROM movie WHERE id = :id")
    LiveData<Movie> getMovieById(int id);

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
    @Query("SELECT * FROM movie WHERE id IN(:ids) AND listType > :listType")
    LiveData<List<Movie>> getMoviesByIds(int[] ids, int listType);


    // Inserts multiple movies
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void bulkInsert(List<Movie> movies);

    //

    /**
     * Deletes all movies of type specified from the database
     *
     * @param listType the type of movies to be deleted
     */
    @Query("DELETE FROM movie WHERE listType == :listType")
    void deleteMoviesByListType(int listType);
}
