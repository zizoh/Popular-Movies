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
     * @param id The id you want movie for
     * @return {@link LiveData} of movie with id specified
     */
    @Query("SELECT * FROM movie WHERE id = :id")
    LiveData<Movie> getMovieById(Integer id);

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
}
