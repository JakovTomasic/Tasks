package com.invariant.android.tasks;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;


class EditTaskData {

    private static final int ID_TYPE_START = 0;
    private static final int ID_TYPE_END = 1;

    private Activity context;
    private Task task;

    private View dialogView;

    EditTaskData(Activity context, Task task) {
        this.context = context;
        this.task = new Task(task);
    }

    void openDialog() {
        dialogView = View.inflate(context, R.layout.dialog_edit_task, null);

        final EditText txtTitle = dialogView.findViewById(R.id.edit_text_task_title);
        txtTitle.setText(task.getTitle());
        txtTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                task.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        final EditText txtTag = dialogView.findViewById(R.id.edit_text_task_tag);
        txtTag.setText(task.getTag());
        txtTag.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                task.setTag(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        refreshDatesTimes();

        dialogView.findViewById(R.id.btn_edit_task_start)
                .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker(ID_TYPE_START);
            }
        });

        dialogView.findViewById(R.id.btn_edit_task_end)
                .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker(ID_TYPE_END);
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogCustom);
        builder.setTitle("Title");
        builder.setPositiveButton("kk", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                task.setTitle(task.getTitle().trim());
                task.setTag(task.getTag().trim());

                if (dialog != null) {
                    dialog.dismiss();
                }

                onDataSavedListener.onSuccessfulSave(task);
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setView(dialogView);
        alertDialog.show();
    }

    void openDatePicker(final int typeId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final DatePicker datePicker = new DatePicker(context);
        datePicker.setCalendarViewShown(false);

        builder.setTitle("Create Year");
        builder.setView(datePicker);
        builder.setNegativeButton("Cancel", null);
        builder.setPositiveButton("Next", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                openTimePicker(typeId, datePicker);
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        builder.show();
    }

    void openTimePicker(final int typeId, final DatePicker datePicker) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final TimePicker timePicker = new TimePicker(context);
        timePicker.setIs24HourView(true);

        builder.setTitle("Create Year");
        builder.setView(timePicker);
        builder.setNegativeButton("Cancel", null);
        builder.setPositiveButton("Set", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(),
                        timePicker.getCurrentHour(), timePicker.getCurrentMinute(), 0);
                long timeMillis = calendar.getTimeInMillis();

                switch (typeId) {
                    case ID_TYPE_START:
                        task.setStart(timeMillis);
                        break;
                    case ID_TYPE_END:
                        task.setEnd(timeMillis);
                        break;
                }
                refreshDatesTimes();

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        builder.show();
    }

    void refreshDatesTimes() {
        // TODO: this dateFormat string should not be hardcoded
        ((TextView) dialogView.findViewById(R.id.txt_start_value)).setText(DateTimeConverter
                .getDateTime(task.getStart(), "dd/MM/yyyy\nHH:mm"));
        ((TextView) dialogView.findViewById(R.id.txt_end_value)).setText(DateTimeConverter
                .getDateTime(task.getEnd(), "dd/MM/yyyy\nHH:mm"));
    }

    interface onDataSavedListener {
        void onSuccessfulSave(Task task);
    }

    private onDataSavedListener onDataSavedListener;

    void setOnDataSavedListener(onDataSavedListener onDataSavedListener) {
        this.onDataSavedListener = onDataSavedListener;
    }

}
