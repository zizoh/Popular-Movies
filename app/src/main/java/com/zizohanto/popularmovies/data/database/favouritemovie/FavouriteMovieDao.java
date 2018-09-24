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
     * @param id The id you want movie for
     * @return {@link LiveData} of movie with id specified
     */
    @Query("SELECT * FROM favouritemovie WHERE id = :id")
    LiveData<FavouriteMovie> getFavouriteMovieWithId(Integer id);

    // Inserts favourite movie
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFavouriteMovie(FavouriteMovie favouriteMovie);

    // Deletes favorite movie with id from the database
    @Query("DELETE FROM favouritemovie WHERE id = :id")
    void deleteFavouriteMovie(Integer id);

}
