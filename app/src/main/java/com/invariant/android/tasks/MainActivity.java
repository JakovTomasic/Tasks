package com.invariant.android.tasks;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.invariant.android.tasks.dragAndDrop.DragAndDropHandler;
import com.invariant.android.tasks.tagLines.NonScrollableScrollView;
import com.invariant.android.tasks.tagLines.TagLinesView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    /**
     * Main application object with all global application data.
     */
    private AppData appData;

    /**
     * ListView of all tasks.
     */
    private ListView lvTasks;
    /**
     * Adapter for {@link #lvTasks}
     */
    private TasksAdapter tasksAdapter;

    /**
     * Custom view for drawing lines between tasks with the same tag.
     */
    private TagLinesView tagLinesView;

    /**
     * Custom ScrollView for containing {@link #tagLinesView}.
     * Disables all click and touch callbacks and passes
     * them to the {@link #lvTasks} that is below it.
     */
    private NonScrollableScrollView tagLinesRootView;


    /**
     * First function called on activity creation.
     * Sets all important views, prepares the activity and thw whole application.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize important views
        lvTasks = findViewById(R.id.tasks_list_view);
        tagLinesView = findViewById(R.id.tag_lines_view);
        appData = (AppData) getApplication();

        tagLinesRootView = findViewById(R.id.tag_lines_root_view);

        // This list of tasks is now bound to the global application tasks list.
        // If anything in that list changes, it changes everywhere in the app.
        ArrayList<Task> tasks = ((AppData) getApplication()).getTasks();

        // Save screen dimensions for future calculations
        appData.setScreenDimensions(this);

        // Setup tasks ListView
        tasksAdapter = new TasksAdapter(this, tasks,
                Math.max(appData.getScreenWidth(), appData.getScreenHeight()) / 8);
        lvTasks.setAdapter(tasksAdapter);

        DragAndDropHandler dragAndDropHandler = new DragAndDropHandler(
                this, lvTasks, tasksAdapter, tagLinesView);
        lvTasks.setOnItemLongClickListener(dragAndDropHandler);
        lvTasks.setOnDragListener(dragAndDropHandler);
        // On click open task edit dialog
        lvTasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                EditTaskData editTaskData = new EditTaskData(MainActivity.this,
                        tasksAdapter.getItem(position), false);
                editTaskData.setOnFinishListener(new EditTaskData.OnFinishListener() {
                    @Override
                    public void onSuccessfulSave(Task task) {
                        ((AppData) getApplication()).updateTask(position, task);
                        tasksAdapter.notifyDataSetChanged();

                        tagLinesView.refresh(tasksAdapter);
                    }
                    @Override
                    public void onDelete(Task task) {
                        ((AppData) getApplication()).removeTask(position);
                        tasksAdapter.notifyDataSetChanged();

                        tagLinesView.refresh(tasksAdapter);
                    }
                });
                editTaskData.openDialog();
            }
        });

        // Scroll lines along with tasks ListView
        lvTasks.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                try {
                    View c = lvTasks.getChildAt(0);
                    int scrollY = -c.getTop() + lvTasks.getFirstVisiblePosition() * c.getHeight();

                    tagLinesRootView.scrollTo(0, scrollY);
                } catch (Exception ignored) {
                    // Exception throw on start when lvTasks is still null
                }
            }
        });

        // Setup custom tag lines view
        tagLinesView.refresh(tasksAdapter);
    }

    /**
     * Sets actionBar menu layout.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Handles on actionBar menu item click.
     *
     * R.id.add_task Opens {@link EditTaskData} to add a new task
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //noinspection SwitchStatementWithTooFewBranches
        switch (item.getItemId()) {
            case R.id.add_task:
                EditTaskData editTaskData = new EditTaskData(this,
                        new Task(appData.getNextId()), true);
                editTaskData.setOnFinishListener(new EditTaskData.OnFinishListener() {
                    @Override
                    public void onSuccessfulSave(Task task) {
                        ((AppData) getApplication()).addTask(task);
                        tasksAdapter.notifyDataSetChanged();

                        tagLinesView.refresh(tasksAdapter);
                    }

                    @Override
                    public void onDelete(Task task) {

                    }
                });
                editTaskData.openDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
