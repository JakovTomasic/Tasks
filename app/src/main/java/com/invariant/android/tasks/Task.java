package com.invariant.android.tasks;

import androidx.annotation.NonNull;

/**
 * Class for storing all the data of the Task. Task is the element shown in the main ListView.
 */
public class Task {

    private int id, position;
    /**
     * Data of the {@link Task}.
     */
    private String title, tag;
    /**
     * Start and end time of the task (event,...) expressed milliseconds
     */
    private long start, end;

    /**
     * Constructor. Sets all required elements.
     *
     * @param title Title of the {@link Task}.
     * @param start Start time of the {@link Task} in milliseconds.
     * @param end End time of the {@link Task} in milliseconds.
     */
    Task(int id, String title, long start, long end) {
        this.id = id;
        this.title = title;
        this.start = start;
        this.end = end;
        this.tag = "";
    }

    /**
     * Constructor for making new {@link Task} object out of existing one.
     * It shallow copies an {@link Task} object.
     */
    Task(Task copyFrom) {
        shallowCopy(copyFrom);
    }

    /**
     * Constructor for making empty {@link Task} object.
     * Initialises all variable.
     */
    Task(int id) {
        this.id = id;
        this.title = "";
        this.start = this.end = System.currentTimeMillis();
        this.tag = "";
    }

    /**.
     * It shallow copies an existing {@link Task} object into this one.
     */
    void shallowCopy(Task copyFrom) {
        this.id = copyFrom.getId();
        this.position = copyFrom.getPosition();
        this.title = copyFrom.getTitle();
        this.start = copyFrom.getStart();
        this.end = copyFrom.getEnd();
        this.tag = copyFrom.getTag();
    }

    /**
     * @return total duration of the {@link Task} in milliseconds.
     *         Retruns 0 if {@link Task#end} is before {@link Task#start}.
     */
    long getDuration() {
        return Math.max(getEnd()-getStart(), 0);
    }

    /**
     * Checks if given task is valid. Used for task changing input validation.
     * @return true if task is valid, false otherwise.
     */
    boolean isValid() {
        if(start > end) return false;
        return !title.isEmpty();
    }

    /**
     * Setter and getter methods
     */
    int getId() {
        return this.id;
    }
    public void setPosition(int position) {
        this.position = position;
    }
    int getPosition() {
        return this.position;
    }
    void setTitle(@NonNull String title) {
        this.title = title;
    }
    String getTitle() {
        return title;
    }
    void setStart(long start) {
        this.start = start;
    }
    long getStart() {
        return start;
    }
    void setEnd(long end) {
        this.end = end;
    }
    long getEnd() {
        return end;
    }
    void setTag(String tag) {
        this.tag = tag;
    }
    public String getTag() {
        return tag;
    }

}
