package com.zizohanto.popularmovies.data.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface MovieDao {
    // Returns a list of all movies in the database
    @Query("SELECT * FROM movies")
    List<Movie> getAll();

    // Inserts multiple movies
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void bulkInsert(Movie... movies);

    // Deletes all movies from the database
    @Delete
    void deleteAll(Movie... movies);
}
