package com.invariant.android.tasks.DragAndDrop;

import android.app.Activity;
import android.widget.ListView;

/**
 * Handles automatic scrolling when dragging item above or under the ListView. Thanks to this
 * every element can be immediately dragged and dropped on the desired position.
 */
class AutoScrollHandler {

    /**
     * Constant for scrolling speed. Higher is faster.
     */
    private final static int AUTO_SCROLL_SPEED = 2;

    /**
     * Activity in which ListView {@link this#lvTasks} is. Used for context.
     */
    private Activity context;
    /**
     * ListView that needs to be scrolled.
     */
    private ListView lvTasks;

    /**
     * Stores scroll speed {@link this#AUTO_SCROLL_SPEED} in correct direction. Positive for down,
     * and negative for up direction.
     */
    private int scrollDir;
    /**
     * Thread that is constantly scrolling in the given direction when automatic scrolling is needed.
     */
    private Thread autoScrollThread;

    /**
     * @param context See {@link this#context}
     * @param lvTasks See {@link this#lvTasks}
     */
    AutoScrollHandler(Activity context, ListView lvTasks) {
        this.context = context;
        this.lvTasks = lvTasks;
    }

    /**
     * Start to automatically scroll up.
     */
    void startScrollingUp() {
        scrollDir = -AUTO_SCROLL_SPEED;
        startScrolling();
    }

    /**
     * Start to automatically scroll down.
     */
    void startScrollingDown() {
        scrollDir = AUTO_SCROLL_SPEED;
        startScrolling();
    }

    /**
     * Stop automatically scrolling (any direction).
     */
    void stopScrolling() {
        if(autoScrollThread == null) return;
        autoScrollThread.interrupt();
        autoScrollThread = null;
    }

    /**
     * Initialises and runs a thread {@link this#autoScrollThread} to constantly
     * scroll in the given direction and with the given speed (See {@link this#scrollDir}).
     * Updates (scrolls a little bit) 60 times a second.
     */
    private void startScrolling() {
        autoScrollThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            lvTasks.smoothScrollByOffset(scrollDir);
                        }
                    });

                    try {
                        Thread.sleep(16);
                    } catch (Exception e) {
                        return;
                    }
                }
            }
        });
        autoScrollThread.start();
    }

}
