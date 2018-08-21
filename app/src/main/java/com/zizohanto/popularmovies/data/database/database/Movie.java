package com.zizohanto.popularmovies.data.database.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "movies")
public class Movie {
    // TODO: Delete public to make class private

    @PrimaryKey(autoGenerate = true)
    public int mId;
    private String mTitle;
    private long mVoteAverage;
    private long mPopularity;
    private String mReleaseDate;
    private String mPosterUrl;
    private String mPlotSynopsis;

    public Movie(int id, String title, long voteAverage, long popularity, String releaseDate,
                 String posterUrl, String plotSynopsis) {
        mId = id;
        mTitle = title;
        mVoteAverage = voteAverage;
        mPopularity = popularity;
        mReleaseDate = releaseDate;
        mPosterUrl = posterUrl;
        mPlotSynopsis = plotSynopsis;
    }

    @Ignore
    public Movie(String title, long voteAverage, long popularity, String releaseDate,
                 String posterUrl, String plotSynopsis) {
        mTitle = title;
        mVoteAverage = voteAverage;
        mPopularity = popularity;
        mReleaseDate = releaseDate;
        mPosterUrl = posterUrl;
        mPlotSynopsis = plotSynopsis;
    }

    public String getTitle() {
        return mTitle;
    }

    public Number getVoteAverage() {
        return mVoteAverage;
    }

    public long getPopularity() {
        return mPopularity;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public String getPosterUrl() {
        return mPosterUrl;
    }

    public String getPlotSynopsis() {
        return mPlotSynopsis;
    }
}

