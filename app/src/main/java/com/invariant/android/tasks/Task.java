package com.invariant.android.tasks;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * Class for storing all the data of the Task. Task is the element shown in the main ListView.
 *
 * It is also the entity for a room db.
 */
@Entity
public class Task {

    /**
     * Id of the task for the DB.
     */
    @PrimaryKey
    private int id;
    /**
     * Position of the task in the tasks list.
     */
    @ColumnInfo(name = "list_position")
    private int position;

    /**
     * Data of the {@link Task}.
     */
    @ColumnInfo(name = "task_title")
    private String title;
    @ColumnInfo(name = "task_tag")
    private String tag;

    /**
     * Start and end time of the task (event,...) expressed milliseconds
     */
    @ColumnInfo(name = "task_start_time")
    private long start;
    @ColumnInfo(name = "task_end_time")
    private long end;

    /**
     * Constructor. Sets all required elements.
     *
     * @param title Title of the {@link Task}.
     * @param start Start time of the {@link Task} in milliseconds.
     * @param end End time of the {@link Task} in milliseconds.
     */
    public Task(int id, String title, long start, long end) {
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
    @Ignore
    Task(Task copyFrom) {
        shallowCopy(copyFrom);
    }

    /**
     * Constructor for making empty {@link Task} object.
     * Initialises all variable.
     */
    @Ignore
    public Task(int id) {
        this.id = id;
        this.title = "";
        this.start = this.end = System.currentTimeMillis();
        this.tag = "";
    }

    /**.
     * It shallow copies an existing {@link Task} object into this one.
     */
    private void shallowCopy(Task copyFrom) {
        this.id = copyFrom.getId();
        this.position = copyFrom.getPosition();
        this.title = copyFrom.getTitle();
        this.start = copyFrom.getStart();
        this.end = copyFrom.getEnd();
        this.tag = copyFrom.getTag();
    }

    /**
     * @return total duration of the {@link Task} in milliseconds.
     *         Returns 0 if {@link Task#end} is before {@link Task#start}.
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
    public int getId() {
        return this.id;
    }
    public void setPosition(int position) {
        this.position = position;
    }
    public int getPosition() {
        return this.position;
    }
    void setTitle(@NonNull String title) {
        this.title = title;
    }
    public String getTitle() {
        return title;
    }
    void setStart(long start) {
        this.start = start;
    }
    public long getStart() {
        return start;
    }
    void setEnd(long end) {
        this.end = end;
    }
    public long getEnd() {
        return end;
    }
    public void setTag(String tag) {
        this.tag = tag;
    }
    public String getTag() {
        return tag;
    }

}
