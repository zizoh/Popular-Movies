package com.zizohanto.popularmovies.data.database.video;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VideoResponse {

    @SerializedName("id")
    private Integer id;

    @SerializedName("results")
    private List<Video> results = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Video> getVideos() {
        return results;
    }

    public void setVideos(List<Video> results) {
        this.results = results;
    }

}