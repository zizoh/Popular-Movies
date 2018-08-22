package com.zizohanto.popularmovies.data.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.util.Log;

@Database(entities = {Movie.class}, version = 1)
public abstract class PopularMovieDatabase extends RoomDatabase {

    private static final String LOG_TAG = PopularMovieDatabase.class.getSimpleName();
    private static final String DATABASE_NAME = "movie";

    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static PopularMovieDatabase sInstance;

    public static PopularMovieDatabase getInstance(Context context) {
        Log.d(LOG_TAG, "Getting the database");
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        PopularMovieDatabase.class, PopularMovieDatabase.DATABASE_NAME).build();
                Log.d(LOG_TAG, "Made new database");
            }
        }
        return sInstance;
    }

    // The associated DAOs for the database
    public abstract MovieDao movieDao();
}
