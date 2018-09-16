package com.zizohanto.popularmovies.data.database.review;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface ReviewDao {
    // Returns a list of all reviews of the movie from the database
    @Query("SELECT * FROM review")
    LiveData<List<Review>> getAllReviews();

    /**
     * @param id The id you want reviews for
     * @return {@link LiveData} of reviews with movie id specified
     */
    @Query("SELECT * FROM review WHERE id = :id")
    LiveData<List<Review>> getReviewsOfMovieId(Integer id);

    // Inserts multiple reviews
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void bulkInsert(List<Review> reviews);

    // Deletes all reviews from the database
    @Query("DELETE FROM review")
    void deleteAllReviews();
}
