package com.invariant.android.tasks.database;

import android.app.Application;

import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.invariant.android.tasks.AppData;
import com.invariant.android.tasks.Task;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Handles all interaction with the local room DB.
 * All DB interaction muse be handled in the separate thread.
 * It interacts with the room DB Dao.
 */
public class DatabaseHandler {

    /**
     * Object with global application data
     */
    private AppData appData;

    /**
     * DB object. Used to interact with DB.
     */
    private AppDatabase db;

    /**
     * Constructor. Sets everything up.
     * @param context Application context
     */
    public DatabaseHandler(Application context) {
        appData = (AppData) context;
        db = Room.databaseBuilder(context.getApplicationContext(),
                AppDatabase.class, "db-tasks")
                .addCallback(dbMockupCallback)
                .build();
    }

    /**
     * Custom callback to mock db when it is created for the first time (onCreate)
     *
     * TODO: remove when removing mocking
     */
    RoomDatabase.Callback dbMockupCallback = new RoomDatabase.Callback() {
        public void onCreate (SupportSQLiteDatabase db) {
            Random random = new Random();
            int hourInMillis = 1000 * 60 * 60;
            int cnt = 24;
            for(int i = 0; i < cnt; i++) {
                Task task = new Task(i, "Naziv" + i, System.currentTimeMillis(), System.currentTimeMillis()+hourInMillis);
                task.setPosition(i);
                task.setTag("tag " + random.nextInt(cnt/2));
                appData.addTask(task);
            }
        }
        public void onOpen (SupportSQLiteDatabase db) {

        }
    };

    /**
     * Loads all tasks and saves them into global task list variable.
     */
    public void loadAllTasks() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Task> tasks = db.taskDao().getAll();

                // Sort tasks in ascending order
                Collections.sort(tasks, new Comparator<Task>() {
                    @Override
                    public int compare(Task o1, Task o2) {
                        return o1.getPosition() - o2.getPosition();
                    }
                });

                for(Task task : tasks) {
                    appData.getTasks().add(task);
                }
            }
        }).start();
    }

    /**
     * Adds task to the DB.
     * @param task Task to add to the DB.
     */
    public void addTask(final Task task) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                db.taskDao().insertAll(task);
            }
        }).start();
    }

    /**
     * Updates task in the DB (based on primary key)
     * @param task Task to update.
     */
    public void updateTask(final Task task) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                db.taskDao().updateTask(task);
            }
        }).start();
    }

    /**
     * Updates all tasks in the DB (based on primary key).
     */
    public void updateAll() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                db.taskDao().updateAll(appData.getTasks());
            }
        }).start();
    }

    /**
     * Removes, deletes given task from the DB
     * @param task Task to delete.
     */
    public void removeTask(final Task task) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                db.taskDao().delete(task);
            }
        }).start();
    }

}
