package com.invariant.android.tasks.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.invariant.android.tasks.Task;

/**
 * Room DB Database component.
 * Change the version number when something changes in the DB.
 */
@Database(entities = {Task.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract TaskDao taskDao();
}
