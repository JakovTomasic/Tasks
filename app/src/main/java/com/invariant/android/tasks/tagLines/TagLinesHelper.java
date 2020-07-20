package com.invariant.android.tasks.tagLines;

import android.app.Activity;
import android.content.Context;

import com.invariant.android.tasks.AppData;
import com.invariant.android.tasks.Task;
import com.invariant.android.tasks.TasksAdapter;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Helper class for calculating all parameters for drawing the tag lines in
 * the {@link TagLinesView} class.
 */
class TagLinesHelper {

    /**
     * List of all tasks.
     */
    private ArrayList<Task> tasks;
    /**
     * Adapter for tasks ListView.
     * Copied from the {@link com.invariant.android.tasks.MainActivity}
     */
    private TasksAdapter tasksAdapter;

    /**
     * List of all lines organized by the task tag.
     * HashMap<(tag of the task), ({@link Line} object for that tag)>
     */
    private HashMap<String, Line> lines;

    /**
     * Calculated width and height of the {@link TagLinesView} object
     * based on the current number of lines and theirs arrangement.
     */
    private int viewWidth, viewHeight;

    /**
     * Attributes of the {@link TagLinesView} object that are important for the
     * calculating {@link TagLinesView} and it's lines dimensions and positions.
     *
     * maxWidthAttr -  Maximum width of {@link TagLinesView} object
     * lineWidthAttr - Preferred line width.
     */
    private int maxWidthAttr, lineWidthAttr;
    /**
     * Horizontal size scale for when total size is wider than the {@link this#maxWidthAttr}.
     * It is always in the range (0 < value <= 1) as it is calculated as
     * {@link this#maxWidthAttr} / {@link this#viewWidth}
     *
     * To scale object width with this variable use:
     * {@code scaledWidth = width * horizontalScale}
     */
    private float horizontalScale;

    /**
     * Constructor. Sets all element and calculates everything ({@link this#calculate()}).
     *
     * @param context Context of the {@link TagLinesView} that is creating this object
     * @param tasksAdapter See {@link this#tasksAdapter}
     * @param maxWidthAttr See {@link this#maxWidthAttr}
     * @param lineWidthAttr See {@link this#lineWidthAttr}
     */
    TagLinesHelper(Context context, TasksAdapter tasksAdapter,
                   int maxWidthAttr, int lineWidthAttr) {
        AppData appData = (AppData) ((Activity) context).getApplication();
        this.tasks = appData.getTasks();
        this.tasksAdapter = tasksAdapter;
        lines = new HashMap<>();

        this.maxWidthAttr = maxWidthAttr;
        this.lineWidthAttr = lineWidthAttr;

        calculate();
    }

    /**
     * Calculates everything.
     */
    private void calculate() {
        /*
         * Calculating height
         */
        viewHeight = tasksAdapter.getRowHeight() * tasksAdapter.getCount();

        /*
         * Calculating width
         */
        viewWidth = 0;

        // Creates all lines and puts them in the lines HashMap
        int currentRow = -1;
        for(Task task : tasks) {
            currentRow++;
            if(task.getTag().isEmpty()) continue;
            if(!lines.containsKey(task.getTag())) {
                lines.put(task.getTag(), new Line());
            }
            lines.get(task.getTag()).addRow(currentRow);
        }

        // Calculates view width (by maximum number of lines present at one place)
        // and sets column of every line
        // TODO: shorted lines are the most left, bigger ones more right (maybe for each column - not available until and the row..).
        currentRow = -1;
        int currentTags = 0;
        Line currentLine;
        for(Task task : tasks) {
            currentRow++;
            if(task.getTag().isEmpty()) continue;

            currentLine = lines.get(task.getTag());

            if(currentRow == currentLine.getFirstRow()) currentTags++;
            viewWidth = Math.max(viewWidth, currentTags);
            // TODO: this is buggy (can be same for two tags)
            if(!currentLine.isLineColumnSet()) currentLine.setLineColumn(currentTags-1);
            if(currentRow == currentLine.getLastRow()) currentTags--;
        }

        // Width of one line between every adjacent lines
        viewWidth *= lineWidthAttr*2;
        // and the same space before the first and after the last line
        viewWidth += lineWidthAttr*2;

        // Calculating horizontal scale if needed
        horizontalScale = 1;
        if(viewWidth > maxWidthAttr) {
            horizontalScale = (float) maxWidthAttr / viewWidth;
        }

        // Scaling width
        viewWidth *= horizontalScale;
    }

    /**
     * Getter methods.
     * @return Calculated dimension.
     */
    int getViewWidth() {
        return this.viewWidth;
    }
    int getViewHeight() {
        return this.viewHeight;
    }

    /**
     * @param column Column (0-indexed from left to right) of the line
     * @return scaled X coordinate of that column (for that line)
     *         from start of the canvas {@link TagLinesView}
     */
    float getColumnX(int column) {
        float x = lineWidthAttr*2 * (column+1);
        return x * horizontalScale;
    }

    /**
     * @param row Row (0-indexed from top to bottom) from the tasks ListView
     * @return Y coordinate of the center of that row
     */
    float getRowY(int row) {
        return row * tasksAdapter.getRowHeight() + (float)(tasksAdapter.getRowHeight()/2);
    }

    /**
     * @return Scaled calculated dimension
     */
    float getLineWidth() {
        return lineWidthAttr * horizontalScale;
    }
    float getCircleRadius() {
        return lineWidthAttr * horizontalScale;
    }

    /**
     * @return See {@link this#lines}
     */
    HashMap<String, Line> getLines() {
        return this.lines;
    }

}
