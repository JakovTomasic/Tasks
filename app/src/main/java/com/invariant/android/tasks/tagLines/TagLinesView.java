package com.invariant.android.tasks.tagLines;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.invariant.android.tasks.R;
import com.invariant.android.tasks.TasksAdapter;

import java.util.Map;

/**
 * Custom View for displaying a task tag lines.
 * A task tag line is line that connects all tasks with the same tag.
 */
public class TagLinesView extends View {

    /**
     * Helper class for calculating all parameters for drawing the tag lines.
     */
    private TagLinesHelper tagLinesHelper;

    /**
     * Background paint for drawing on the canvas.
     */
    private Paint backgroundPaint;
    /**
     * Line and dots paint for drawing on the canvas.
     */
    private Paint linePaint;

    /**
     * Custom attributes set in the xml layout file.
     */
    private int maxWidthAttr, lineWidthAttr;
    private int lineColorAttr;

    /**
     * View constructor. Saves attributes and sets up paints.
     *
     * @param context View context
     * @param attrs All xml attributes
     */
    public TagLinesView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setClickable(false);
        setFocusable(false);

        readAttributes(context, attrs);

        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.TRANSPARENT);

        linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setColor(Color.BLACK);
    }

    /**
     * Refreshes (invalidates) view layout. (Initialises everything again.)
     * During this {@link #onMeasure(int, int)} resizing is called.
     * Mostly used when data is changed (list reordered).
     *
     * @param tasksAdapter Adapter of the ListView of tasks in the activity.
     */
    public void refresh(TasksAdapter tasksAdapter) {
        tagLinesHelper = new TagLinesHelper(getContext(), tasksAdapter, maxWidthAttr, lineWidthAttr);
        invalidate();
    }


    /**
     * Reads and saves all custom attributes.
     *
     * @param context View context
     * @param attrs All attributes
     */
    void readAttributes(Context context, @Nullable AttributeSet attrs) {
        TypedArray attributes = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.TagLinesView,
                0, 0);

        try {
            maxWidthAttr = attributes.getDimensionPixelSize(
                    R.styleable.TagLinesView_maxWidth,  1000000);
            lineWidthAttr = attributes.getDimensionPixelSize(
                    R.styleable.TagLinesView_preferredLineWidth, 5);

            lineColorAttr = attributes.getColor(R.styleable.TagLinesView_lineColor, 0xf000);
        } catch (Exception ignored) {
        } finally {
            // Important!
            attributes.recycle();
        }
    }

    /**
     * Called when setting view dimensions.
     * Sets custom dimensions based on {@link #tagLinesHelper}.
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(tagLinesHelper.getViewWidth(), tagLinesHelper.getViewHeight());
    }

    /**
     * Called on view draw. Draws all lines and dots
     * based on {@link #tagLinesHelper} dimensions.
     *
     * @param canvas View canvas to draw on.
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Set background for whole canvas
        canvas.drawLine(0, 0, getWidth(), getHeight(), backgroundPaint);

        // Setup paint
        linePaint.setStrokeWidth(tagLinesHelper.getLineWidth());
        linePaint.setColor(lineColorAttr);

        // Loop through all lines and draw them (and their's dots)
        for(Map.Entry<String, Line> entry : tagLinesHelper.getLines().entrySet()) {
            Line line = entry.getValue();

            // TODO: draw one dot custom attr
            // Don't draw lines with just one dot
            if(line.getRows().size() <= 1) continue;

            float x = tagLinesHelper.getColumnX(line.getLineColumn());
            float startY = tagLinesHelper.getRowY(line.getFirstRow());
            float endY = tagLinesHelper.getRowY(line.getLastRow());

            canvas.drawLine(x, startY, x, endY, linePaint);

            // Draw all dots
            for(Integer row : line.getRows()) {
                float y = tagLinesHelper.getRowY(row);
                float radius = tagLinesHelper.getCircleRadius();
                canvas.drawCircle(x, y, radius, linePaint);
            }
        }
    }

}
