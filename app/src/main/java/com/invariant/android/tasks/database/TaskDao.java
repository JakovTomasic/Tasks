package com.invariant.android.tasks.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.invariant.android.tasks.Task;

import java.util.List;

/**
 * Room DB DAO (data access objects) component.
 * Contains the methods used for accessing the database.
 */
@Dao
public interface TaskDao {

    /**
     * @return All entries (all rows) from the DB (all saved tasks).
     */
    @Query("SELECT * FROM Task")
    List<Task> getAll();

    /**
     * Updates task data in the DB based on it's id (primary key)
     */
    @Update
    void updateTask(Task task);

    /**
     * Updates list of tasks (based ont he id)
     * @param tasks List of tasks to update
     */
    @Update
    void updateAll(List<Task> tasks);

    /**
     * Inserts list of tasks to the DB.
     * @param tasks List of tasks to insert
     */
    @Insert
    void insertAll(Task... tasks);

    /**
     * Delete given task from the DB.
     * @param task Task to delete.
     */
    @Delete
    void delete(Task task);

}
