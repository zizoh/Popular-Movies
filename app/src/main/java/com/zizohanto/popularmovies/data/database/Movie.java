package com.zizohanto.popularmovies.data.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "movie", indices = {@Index(value = {"title"}, unique = true)})
public class Movie {

    @PrimaryKey(autoGenerate = true)
    public int id;
    private String title;
    private long voteAverage;
    private long popularity;
    private String releaseDate;
    private String posterUrl;
    private String plotSynopsis;

    public Movie(int id, String title, long voteAverage, long popularity, String releaseDate,
                 String posterUrl, String plotSynopsis) {
        this.id = id;
        this.title = title;
        this.voteAverage = voteAverage;
        this.popularity = popularity;
        this.releaseDate = releaseDate;
        this.posterUrl = posterUrl;
        this.plotSynopsis = plotSynopsis;
    }

    @Ignore
    public Movie(String title, long voteAverage, long popularity, String releaseDate,
                 String posterUrl, String plotSynopsis) {
        this.title = title;
        this.voteAverage = voteAverage;
        this.popularity = popularity;
        this.releaseDate = releaseDate;
        this.posterUrl = posterUrl;
        this.plotSynopsis = plotSynopsis;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public long getVoteAverage() {
        return voteAverage;
    }

    public long getPopularity() {
        return popularity;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public String getPlotSynopsis() {
        return plotSynopsis;
    }
}

