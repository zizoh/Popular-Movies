package com.zizohanto.popularmovies.data.database.favouritemovie;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "favouritemovie")
public class FavouriteMovie {

    @PrimaryKey(autoGenerate = true)
    private int roomId;
    private String title;
    private Integer id;

    @Ignore
    public FavouriteMovie(String mTitle, Integer id) {
        this.title = mTitle;
        this.id = id;
    }

    public FavouriteMovie(int roomId, String title, Integer id) {
        this.roomId = roomId;
        this.title = title;
        this.id = id;
    }

    public int getRoomId() {
        return roomId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
