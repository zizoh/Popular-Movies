package com.zizohanto.popularmovies.data.database.favouritemovie;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface FavouriteMovieDao {
    // Returns a list of favourite movies in the database
    @Query("SELECT * FROM favouritemovie")
    LiveData<List<FavouriteMovie>> getAllFavouriteMovies();

    /**
     * @param title The title you want movie for
     * @return {@link LiveData} of movie with title specified
     */
    @Query("SELECT * FROM favouritemovie WHERE title = :title")
    LiveData<FavouriteMovie> getFavouriteMovieWithTitle(String title);

    // Inserts favourite movie
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFavouriteMovie(FavouriteMovie favouriteMovie);

    // Deletes favorite movie with title from the database
    @Query("DELETE FROM favouritemovie WHERE title = :title")
    void deleteFavouriteMovie(String title);

    // Deletes all favorite movies from the database
    @Query("DELETE FROM favouritemovie")
    void deleteAllFavouriteMovies();

}
