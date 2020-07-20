package com.invariant.android.tasks.dragAndDrop;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Point;
import android.view.DragEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.invariant.android.tasks.tagLines.TagLinesView;
import com.invariant.android.tasks.Task;
import com.invariant.android.tasks.TasksAdapter;

import java.util.ArrayList;

/**
 * Handles drag and drop functionality in the given list.
 */
public class DragAndDropHandler implements ListView.OnItemLongClickListener, ListView.OnDragListener {

    /**
     * Current activity for context
     */
    private Activity context;
    /**
     * See {@link AutoScrollHandler}
     */
    private AutoScrollHandler autoScrollHandler;

    /**
     * List of all tasks. Points to the global application variable.
     */
    private ArrayList<Task> tasks;
    /**
     * ListView in which drag and drop functionality is being used.
     */
    private ListView lvTasks;
    /**
     * Adapter of that list {@link this#lvTasks}.
     */
    private TasksAdapter tasksAdapter;

    /**
     * Custom view currently displaying tag lines. Used for invalidation on {@link this#lvTasks}
     * data change.
     */
    private TagLinesView tagLinesView;

    /**
     * Vertical coordinate of the last point over which item has been dragged.
     * Used to determine to automatically scroll {@link AutoScrollHandler} up or down.
     */
    private float lastHoverY;
    /**
     * Position (index) of the item that is being dragged in the list ({@link this#tasks}) before
     * drag started.
     */
    private Integer dragStartPosition;

    /**
     * Constructor. Sets all up.
     * @param context See {@link this#context}
     * @param tasks See {@link this#tasks}
     * @param lvTasks See {@link this#lvTasks}
     * @param tasksAdapter See {@link this#tasksAdapter}
     */
    public DragAndDropHandler(Activity context, ArrayList<Task> tasks, ListView lvTasks,
                              TasksAdapter tasksAdapter, TagLinesView tagLinesView) {
        this.context = context;
        this.tasks = tasks;
        this.lvTasks = lvTasks;
        this.tasksAdapter = tasksAdapter;
        this.tagLinesView = tagLinesView;
        autoScrollHandler = new AutoScrollHandler(context, lvTasks);
    }

    /**
     * Handle everything that needs to be handled on drag start.
     * @param position See {@link this#dragStartPosition}
     */
    private void startDrag(int position) {
        dragStartPosition = position;
        tasksAdapter.setDraggingItem(position);
        tasksAdapter.notifyDataSetChanged();
    }

    /**
     * Handle everything that needs to be handled on drag stop.
     */
    private void stopDrag() {
        dragStartPosition = null;
        tasksAdapter.draggingStopped();
        tasksAdapter.notifyDataSetChanged();
    }

    /**
     * Overrides ListView.OnItemLongClickListener onItemLongClick(...) method.
     * See official documentation.
     * On long click drag is started.
     */
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        TasksAdapter.ViewHolder vh = (TasksAdapter.ViewHolder) view.getTag();

        final int touchedX = (int) (vh.lastTouchedX + 0.5f);
        final int touchedY = (int) (vh.lastTouchedY + 0.5f);
        lastHoverY = touchedY;

        startDrag(position);

        view.startDrag(null, new View.DragShadowBuilder(view) {
            @Override
            public void onProvideShadowMetrics(Point shadowSize, Point shadowTouchPoint) {
                super.onProvideShadowMetrics(shadowSize, shadowTouchPoint);
                shadowTouchPoint.x = touchedX;
                shadowTouchPoint.y = touchedY;
            }

            @Override
            public void onDrawShadow(Canvas canvas) {
                super.onDrawShadow(canvas);
            }
        }, view, 0);

        view.setVisibility(View.INVISIBLE);

        return true;
    }

    // TODO: set invisible element over the dragged one
    /**
     * Overrides ListView.OnDragListener onDrag(...) method.
     * See official documentation.
     */
    @Override
    public boolean onDrag(View v, DragEvent event) {
        View c = lvTasks.getChildAt(0);
        int scrollY = -c.getTop() + lvTasks.getFirstVisiblePosition() * c.getHeight();
        int position = ((int) event.getY() + scrollY) / tasksAdapter.getRowHeight();

        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                break;
            case DragEvent.ACTION_DRAG_ENTERED:
                autoScrollHandler.stopScrolling();
                break;
            case DragEvent.ACTION_DRAG_LOCATION:
                lastHoverY = event.getY();
                break;
            case DragEvent.ACTION_DRAG_EXITED:
                if((int)lastHoverY < lvTasks.getHeight()/2) autoScrollHandler.startScrollingUp();
                else autoScrollHandler.startScrollingDown();
                break;
            case DragEvent.ACTION_DROP:
                tasksAdapter.moveItem(dragStartPosition, position);
                tagLinesView.refresh(tasksAdapter);
                break;
            case DragEvent.ACTION_DRAG_ENDED:
                View view = (View) event.getLocalState();
                view.setVisibility(View.VISIBLE);
                autoScrollHandler.stopScrolling();
                stopDrag();
                break;
        }
        return true;
    }
}
