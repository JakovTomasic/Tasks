package com.invariant.android.tasks;

import android.app.Application;

import java.util.ArrayList;

/**
 * Class for storing all global application data.
 * Example of accessing data: {@code ((AppData) getApplication()).getTasks()}.
 */
public class AppData extends Application {

    /**
     * List of all stored tasks
     */
    private ArrayList<Task> tasks = null;

    /**
     * Getter and setter methods
     */
    ArrayList<Task> getTasks() {
        return tasks;
    }
    void setTasks(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }
    void replaceTask(int position, Task tasks) {
        this.tasks.set(position, tasks);
    }
    void addTask(Task tasks) {
        this.tasks.add(tasks);
    }

}
