package com.invariant.android.tasks;

import android.app.Activity;
import android.app.Application;
import android.util.DisplayMetrics;

import java.util.ArrayList;

/**
 * Class for storing all global application data.
 * Example of accessing data: {@code ((AppData) getApplication()).getTasks()}.
 */
public class AppData extends Application {

    /**
     * Constant for undefined dimensions. Used to know when they are undefined
     */
    private static final int DIMENSION_UNDEFINED = -1;
    /**
     * Dimensions of the physical device in pixels
     */
    private int screenWidth, screenHeight;

    /**
     * List of all stored tasks
     */
    private ArrayList<Task> tasks = null;

    /**
     * Method automatically called on application start. Sets up all important data.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        screenWidth = screenHeight = DIMENSION_UNDEFINED;
    }

    /**
     * Getter and setter methods for {@link this#screenHeight} and {@link this#screenWidth}
     */
    void setScreenWidth(int width) {
        this.screenWidth = width;
    }
    int getScreenWidth() {
        return this.screenWidth;
    }
    void setScreenHeight(int height) {
        this.screenHeight = height;
    }
    int getScreenHeight() {
        return this.screenHeight;
    }

    /**
     * // TODO: support for split screen and foldable devices
     * Saves all screen dimensions in variables {@link this#screenWidth}
     * and {@link this#screenHeight} if they are not saved already.
     *
     * @param activity Current opened activity to get dimensions from.
     */
    void setScreenDimensions(Activity activity) {
        if(screenWidth != DIMENSION_UNDEFINED && screenHeight != DIMENSION_UNDEFINED) return;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        setScreenWidth(displayMetrics.widthPixels);
        setScreenHeight(displayMetrics.heightPixels);
    }

    /**
     * @return A valid {@link Task} ID for construction new {@link Task}
     */
    public int getNextId() {
        int maxId = 0;
        for(Task t : tasks) {
            maxId = Math.max(maxId, t.getId());
        }
        return maxId+1;
    }

    /**
     * Moves the element from previous {@param fromPosition} to the new position {@param toPosition}.
     *
     * @param fromPosition Previous position
     * @param toPosition To position
     */
    public void moveItem(int fromPosition, int toPosition) {
        Task moveItem = tasks.get(fromPosition);
        tasks.remove(moveItem);
        tasks.add(toPosition, moveItem);
        moveItem.setPosition(toPosition);
    }

    /**
     * Getter and setter methods for {@link this#tasks}.
     */
    public ArrayList<Task> getTasks() {
        return tasks;
    }
    void setTasks(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    /**
     * Replaces task at the given position with the new task.
     * @param position Position of the old task.
     * @param tasks New task that will replace old one.
     */
    void replaceTask(int position, Task tasks) {
        this.tasks.set(position, tasks);
    }

    /**
     * Adds the {@param task} to the {@link AppData#tasks} list.
     */
    void addTask(Task task) {
        this.tasks.add(task);
        task.setPosition(getTasks().size()-1);
    }

    /**
     * Removes task from the list.
     * @param position Index of the task that need to be removed.
     */
    void removeTask(int position) {
        this.tasks.remove(position);
    }

}
