package com.zizohanto.popularmovies.data.database.video;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface VideoDao {
    /**
     * @param movieId Movie id you want videos for
     * @return {@link LiveData} of videos with movie id specified
     */
    @Query("SELECT * FROM video WHERE movieId == :movieId")
    LiveData<List<Video>> getVideosOfMovie(int movieId);

    // Inserts multiple videos
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void bulkInsert(List<Video> videos);

    // Deletes all videos of movie specified from the database
    @Query("DELETE FROM video WHERE movieId == :movieId")
    void deleteVideosOfMovie(int movieId);
}
