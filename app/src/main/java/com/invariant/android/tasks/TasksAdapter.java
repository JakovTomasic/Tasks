package com.invariant.android.tasks;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

/**
 * Handles item/row drawing and handles {@link View.OnTouchListener} {@code onTouch(...)}
 * method for each item
 */
public class TasksAdapter extends ArrayAdapter<Task> implements View.OnTouchListener {

    /**
     * Constant for the invalid item position (index)
     */
    private static final int INVALID_POSITION = -1;

    /**
     * Height of one item/row in the ListView in pixels
     */
    private int rowHeight;
    /**
     * Position (index) of the item that is currently being dragged.
     * Equals to {@link TasksAdapter#INVALID_POSITION} if no item is being dragged
     */
    private int draggingItemPosition;

    /**
     * Stores root view and coordinates of that view that have been touched most recently.
     * Stored as a root view Tag. Use {@code view.getTag()} to get tag and
     * {@code view.setTag(viewHolder)} to set tag.
     *
     * It is used to get coordinates from which item is dragged (as an additional data of the view).
     */
    public static class ViewHolder {
        RelativeLayout view;
        public float lastTouchedX;
        public float lastTouchedY;

        ViewHolder(View v) {
            view = (RelativeLayout) v;
        }
    }

    /**
     * Constructor. Sets all values to default.
     *
     * @param context Activity in which listView is used
     * @param items List of items to show in the ListView. Stores all data.
     * @param rowHeight See {@link this#rowHeight}
     */
    TasksAdapter(Activity context, ArrayList<Task> items, int rowHeight) {
        super(context, 0, items);
        this.rowHeight = rowHeight;
        draggingItemPosition = INVALID_POSITION;
    }

    /**
     * Used on successful drag and drop. Moves the dropped element from previous
     * {@param fromPosition} to the new position {@param toPosition}.
     *
     * @param fromPosition Previous position
     * @param toPosition To position
     * TODO: fix size changes so toPosition is not valid anymore
     */
    public void moveItem(int fromPosition, int toPosition) {
        if(!isValidPosition(fromPosition) || !isValidPosition(toPosition)) return;
        if(fromPosition == toPosition) return;
        ((AppData)((Activity) getContext()).getApplication()).moveItem(fromPosition, toPosition);
    }

    /**
     * Checks if given {@param position} is valid for the current ListView
     *
     * @param position
     * @return true if position is valid, otherwise false
     */
    private boolean isValidPosition(int position) {
        if(position == INVALID_POSITION) return false;
        return position >= 0 && position < getCount();
    }

    /**
     * @return See {@link this#rowHeight}
     */
    public int getRowHeight() {
        return this.rowHeight;
    }
    /**
     * Sets {@link this#draggingItemPosition} on the start of drag
     *
     * @param position index of the element that is being dragged in the list
     */
    public void setDraggingItem(int position) {
        this.draggingItemPosition = position;
    }
    /**
     * Resets all drag-helper variables as before drag
     */
    public void draggingStopped() {
        this.draggingItemPosition = INVALID_POSITION;
    }

    /**
     * Sets the look of the given item/row of the ListView.
     *
     * @param position Index of the current element in the list. Its data is being displayed.
     * @param convertView Previous view displaying other element. Used for reusability of the views,
     *                    performance optimization.
     * @param container
     * @return View that will be drawn.
     */
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup container) {
        View view;
        ViewHolder vh;

        if (convertView == null) {
            view = LayoutInflater.from(container.getContext()).inflate(
                    R.layout.list_item_task, container, false);
            view.setOnTouchListener(this);

            RelativeLayout root = view.findViewById(R.id.root);
            ViewGroup.LayoutParams params = root.getLayoutParams();
            if (params == null) {
                params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, rowHeight);
            } else {
                params.height = rowHeight;
            }
            root.setLayoutParams(params);

            vh = new ViewHolder(view);
            view.setTag(vh);
        } else {
            view = convertView;
            vh = (ViewHolder) view.getTag();
        }

        // TODO: this dateFormat string should not be hardcoded
        ((TextView) vh.view.findViewById(R.id.tv_task_title)).setText(getItem(position).getTitle());
        ((TextView) vh.view.findViewById(R.id.tv_start_time)).setText(DateTimeConverter
                .getDateTime(getItem(position).getStart(), "dd/MM/yyyy"));
        ((TextView) vh.view.findViewById(R.id.tv_end_time)).setText(DateTimeConverter
                .getDateTime(getItem(position).getEnd(), "dd/MM/yyyy"));

        if(position != draggingItemPosition) view.setVisibility(View.VISIBLE);
        else view.setVisibility(View.INVISIBLE);

        return view;
    }

    /**
     * Handles onTouch event for each ListView item.
     *
     * @param v View that has been touched.
     * @param event ID of the type of action (event) that has been performed on {@param view} touch
     * @return
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        ViewHolder vh = (ViewHolder) v.getTag();

        vh.lastTouchedX = event.getX();
        vh.lastTouchedY = event.getY();

        return false;
    }

}
