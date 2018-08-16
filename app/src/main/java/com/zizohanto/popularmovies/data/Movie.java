package com.zizohanto.popularmovies.data;

public class Movie {

    private String mTitle;
    private long mVoteAverage;
    private long mPopularity;
    private String mReleaseDate;
    private String mPosterUrl;
    private String mPlotSynopsis;

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

    public void setTitle(String title) {
        mTitle = title;
    }

    public Number getVoteAverage() {
        return mVoteAverage;
    }

    public void setVoteAverage(long voteAverage) {
        mVoteAverage = voteAverage;
    }

    public long getPopularity() {
        return mPopularity;
    }

    public void setPopularity(long popularity) {
        mPopularity = popularity;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        mReleaseDate = releaseDate;
    }

    public String getPosterUrl() {
        return mPosterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        mPosterUrl = posterUrl;
    }

    public String getPlotSynopsis() {
        return mPlotSynopsis;
    }

    public void setPlotSynopsis(String plotSynopsis) {
        mPlotSynopsis = plotSynopsis;
    }
}
