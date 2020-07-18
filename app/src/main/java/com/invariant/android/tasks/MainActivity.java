package com.invariant.android.tasks;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.invariant.android.tasks.DragAndDrop.DragAndDropHandler;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView lvTasks;
    TasksAdapter tasksAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lvTasks = findViewById(R.id.tasks_list_view);

        ArrayList<Task> tasks;
        if (((AppData) getApplication()).getTasks() == null) {
            tasks = new ArrayList<>();
            for (int i = 0; i < 21; i++) {
                tasks.add(new Task("test " + i, 0, i));
            }
            ((AppData) getApplication()).setTasks(tasks);
        } else {
            tasks = ((AppData) getApplication()).getTasks();
        }

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        tasksAdapter = new TasksAdapter(this, tasks,
                Math.max(displayMetrics.heightPixels, displayMetrics.widthPixels) / 8);
        lvTasks.setAdapter(tasksAdapter);

        DragAndDropHandler dragAndDropHandler = new DragAndDropHandler(
                this, tasks, lvTasks, tasksAdapter);
        lvTasks.setOnItemLongClickListener(dragAndDropHandler);
        lvTasks.setOnDragListener(dragAndDropHandler);
        lvTasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                EditTaskData editTaskData = new EditTaskData(MainActivity.this, tasksAdapter.getItem(position));
                editTaskData.setOnDataSavedListener(new EditTaskData.onDataSavedListener() {
                    @Override
                    public void onSuccessfulSave(Task task) {
                        ((AppData) getApplication()).replaceTask(position, task);
                        tasksAdapter.notifyDataSetChanged();
                    }
                });
                editTaskData.openDialog();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_task:
                EditTaskData editTaskData = new EditTaskData(this, new Task());
                editTaskData.setOnDataSavedListener(new EditTaskData.onDataSavedListener() {
                    @Override
                    public void onSuccessfulSave(Task task) {
                        ((AppData) getApplication()).addTask(task);
                        tasksAdapter.notifyDataSetChanged();
                    }
                });
                editTaskData.openDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
