package com.invariant.android.tasks.TagLines;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * ScrollView that is not manually scrollable, but only programmatically.
 * It passes all the touches to the views below.
 */
public class NonScrollableScrollView extends ScrollView {

    /**
     * Constructor sets for all touches to go through.
     * Here are defined some default settings so they don't have to repeat in xml layout.
     */
    public NonScrollableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setClickable(false);
        setFocusable(false);
    }

    /**
     * @return By returning false, touch event is carried to the next view
     * and manual scrolling is avoided.
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return false;
    }

    /**
     * @return By returning false, touch event is carried to the next view
     * and manual scrolling is avoided.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

}
