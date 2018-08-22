package com.zizohanto.popularmovies.data.database;

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
    LiveData<List<Movie>> getAll();

    /**
     * @param title The title you want movie for
     * @return {@link LiveData} with movie with title specified
     */
    @Query("SELECT * FROM movie WHERE title = :title")
    LiveData<Movie> getMovieByTitle(String title);

    // Inserts multiple movies
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void bulkInsert(Movie... movies);

    // Deletes all movies from the database
    @Query("DELETE FROM movie")
    void deleteAll();
}
