package com.zizohanto.popularmovies.data.database.review;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface ReviewDao {
    /**
     * @param movieId Movie id you want reviews for
     * @return {@link LiveData} of reviews with movie id specified
     */
    @Query("SELECT * FROM review WHERE movieId = :movieId")
    LiveData<List<Review>> getReviewsOfMovie(int movieId);

    // Inserts multiple reviews
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void bulkInsert(List<Review> reviews);

    // Deletes all reviews of movie specified from the database
    @Query("DELETE FROM review WHERE movieId == :movieId")
    void deleteReviewsOfMovie(int movieId);
}
