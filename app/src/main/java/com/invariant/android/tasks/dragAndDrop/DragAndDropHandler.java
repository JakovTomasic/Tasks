package com.invariant.android.tasks.dragAndDrop;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Point;
import android.view.DragEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.invariant.android.tasks.tagLines.TagLinesView;
import com.invariant.android.tasks.TasksAdapter;


/**
 * Handles drag and drop functionality in the given list.
 */
public class DragAndDropHandler implements ListView.OnItemLongClickListener, ListView.OnDragListener {

    /**
     * See {@link AutoScrollHandler}
     */
    private AutoScrollHandler autoScrollHandler;

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
     * Position (index) of the item that is being dragged in the list before
     * drag started.
     */
    private Integer dragStartPosition;
    /**
     * Current position (index) of the item that is being dragged in the list.
     */
    private Integer currentItemPosition;
    /**
     * true if item is successfully dropped, false if drag and drop action
     * has been terminated. In that case item needs to be moved back to
     * starting position {@link #dragStartPosition}.
     * If drag was successful, item just stays at current position {@link #currentItemPosition}
     */
    private boolean isItemDropped;

    /**
     * Constructor. Sets all up.
     * @param context See Current activity for context
     * @param lvTasks See {@link this#lvTasks}
     * @param tasksAdapter See {@link this#tasksAdapter}
     */
    public DragAndDropHandler(Activity context, ListView lvTasks,
                              TasksAdapter tasksAdapter, TagLinesView tagLinesView) {
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
        currentItemPosition = dragStartPosition = position;
        isItemDropped = false;
        if(tasksAdapter.getItem(position) != null)
            //noinspection ConstantConditions
            tasksAdapter.setDraggingItem(tasksAdapter.getItem(position).getId());
        tasksAdapter.notifyDataSetChanged();
    }

    /**
     * Handle everything that needs to be handled on drag stop.
     */
    private void stopDrag() {
        autoScrollHandler.stopScrolling();
        if(!isItemDropped) {
            // If dragging is canceled, move item back to starting position
            tasksAdapter.moveItem(currentItemPosition, dragStartPosition);
            tagLinesView.refresh(tasksAdapter);
        }

        currentItemPosition = dragStartPosition = null;
        isItemDropped = false;
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
                // When drag item has entered back into ListView stop scrolling
                autoScrollHandler.stopScrolling();
                break;
            case DragEvent.ACTION_DRAG_LOCATION:
                // When drag item is moving update last location
                lastHoverY = event.getY();

                // If item is hovering over the new position, move it there to
                // show empty place in the tasks ListView
                if(currentItemPosition == position) break;
                tasksAdapter.moveItem(currentItemPosition, position);
                tasksAdapter.notifyDataSetChanged();
                tagLinesView.refresh(tasksAdapter);

                currentItemPosition = position;
                break;
            case DragEvent.ACTION_DRAG_EXITED:
                // When drag item exit ListView start scrolling
                if((int)lastHoverY < lvTasks.getHeight()/2) autoScrollHandler.startScrollingUp();
                else autoScrollHandler.startScrollingDown();
                break;
            case DragEvent.ACTION_DROP:
                // When drag item is dropped move item
                tasksAdapter.moveItem(currentItemPosition, position);
                tagLinesView.refresh(tasksAdapter);
                isItemDropped = true;
                break;
            case DragEvent.ACTION_DRAG_ENDED:
                // When drag ends set item visibility back to visible and finish everything
                View view = (View) event.getLocalState();
                view.setVisibility(View.VISIBLE);
                stopDrag();
                break;
        }
        return true;
    }
}
