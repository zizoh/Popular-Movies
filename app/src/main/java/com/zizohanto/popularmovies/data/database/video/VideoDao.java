package com.zizohanto.popularmovies.data.database.video;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface VideoDao {
    // Returns a list of all videos of the movie from the database
    @Query("SELECT * FROM video")
    LiveData<List<Video>> getAllVideos();

    /**
     * @param id The id you want videos for
     * @return {@link LiveData} of videos with movie id specified
     */
    @Query("SELECT * FROM video WHERE id = :id")
    LiveData<List<Video>> getVideosOfMovieId(Integer id);

    // Inserts multiple videos
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void bulkInsert(List<Video> videos);

    // Deletes all videos from the database
    @Query("DELETE FROM video")
    void deleteAllVideos();
}
