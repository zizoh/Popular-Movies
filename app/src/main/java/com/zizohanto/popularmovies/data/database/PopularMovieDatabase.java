package com.zizohanto.popularmovies.data.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import timber.log.Timber;

@Database(entities = {Movie.class}, version = 1)
@TypeConverters(ListTypeConverter.class)
public abstract class PopularMovieDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "movie";

    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static PopularMovieDatabase sInstance;

    public static PopularMovieDatabase getInstance(Context context) {
        Timber.d("Getting the database");
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        PopularMovieDatabase.class, PopularMovieDatabase.DATABASE_NAME).build();
                Timber.d("Made new database");
            }
        }
        return sInstance;
    }

    // The associated DAOs for the database
    public abstract MovieDao movieDao();
}
