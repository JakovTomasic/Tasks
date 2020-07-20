package com.invariant.android.tasks;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;


/**
 * Handles dialog for editing existing task and adding a new one.
 */
class EditTaskData {

    /**
     * Constant for date time formatting.
     */
    private static final String DATE_TIME_FORMAT = "dd/MM/yyyy\nHH:mm";

    /**
     * Constants for the DateTime dialog. Tells if the
     * dialog is changing task start or task end time.
     */
    private static final int ID_TYPE_START = 0;
    private static final int ID_TYPE_END = 1;

    /**
     * Current activity for context
     */
    private Activity context;
    /**
     * Task that is being edited.
     * New task if adding and existing one (shallow copied) if editing.
     *
     */
    private Task task;

    /**
     * Main dialog view for context.
     */
    private View dialogView;

    /**
     * True if this is dialog for the new task,
     * false if task is already in the tasks list
     */
    private boolean addingNewTask;

    /**
     * Interface for the custom listener dialog is closed.
     */
    interface OnFinishListener {
        /**
         * Interface for the custom listener when task is saved successfully.
         * @param task Task that is saved in.
         */
        void onSuccessfulSave(Task task);

        /**
         * Interface for the custom listener when task is deleted.
         * @param task Task that will be deleted.
         */
        void onDelete(Task task);
    }
    /**
     * Listener
     */
    private OnFinishListener onFinishListener = null;


    /**
     * Constructor. It copies given task in case editing is terminated.
     * @param context See {@link #context}
     * @param task Task to edit. It is copied into the {@link #task}.
     * @param addingNewTask See {@link #addingNewTask}
     */
    EditTaskData(Activity context, Task task, boolean addingNewTask) {
        this.context = context;
        this.task = new Task(task);
        this.addingNewTask = addingNewTask;
    }

    /**
     * Creates and opens alert dialog for editing task.
     */
    void openDialog() {
        dialogView = View.inflate(context, R.layout.dialog_edit_task, null);

        // On title text change, save the title
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

        // On tag text change, save the tag
        final EditText txtTag = dialogView.findViewById(R.id.edit_text_task_tag);
        txtTag.setText(task.getTag());
        txtTag.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                task.setTag(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Set start and edn dateTime textViews
        refreshDatesTimes();

        // Edit start time edit button
        dialogView.findViewById(R.id.btn_edit_task_start)
                .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker(ID_TYPE_START);
            }
        });

        // Edit end time edit button
        dialogView.findViewById(R.id.btn_edit_task_end)
                .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker(ID_TYPE_END);
            }
        });


        // Build dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogCustom);

        builder.setTitle((addingNewTask ? context.getResources().getString(R.string.add) :
                context.getResources().getString(R.string.edit)));
        // Listener is added later
        builder.setPositiveButton(context.getResources().getString(R.string.save), null);

        // Create AlertDialog
        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setView(dialogView);

        // When defined like this, clicking positive button won't dismiss dialog automatically
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button btnPositive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                btnPositive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Trim title and tag strings
                        task.setTitle(task.getTitle().trim());
                        task.setTag(task.getTag().trim());

                        if(!task.isValid()) {
                            // Show error toast
                            Toast.makeText(context,
                                    context.getResources().getString(R.string.error_task_not_valid),
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Close the dialog
                        alertDialog.dismiss();
                        // Call listener
                        if(onFinishListener != null) onFinishListener.onSuccessfulSave(task);
                    }
                });

            }
        });

        // Setup delete button
        View btnDelete = dialogView.findViewById(R.id.btn_delete);
        if(addingNewTask) btnDelete.setVisibility(View.GONE);
        else btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close the dialog
                alertDialog.dismiss();
                // Call listener
                if(onFinishListener != null) onFinishListener.onDelete(task);
            }
        });

        alertDialog.show();
    }

    /**
     * Opens date picker dialog. On the positive button (next) time picker is opened.
     *
     * @param typeId Tells if this is for task start or end time.
     *               See {@link #ID_TYPE_START} and {@link #ID_TYPE_END}
     */
    private void openDatePicker(final int typeId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final DatePicker datePicker = new DatePicker(context);
        datePicker.setCalendarViewShown(false);

        builder.setTitle(context.getResources().getString(R.string.choose_date));
        builder.setView(datePicker);
        builder.setNegativeButton(context.getResources().getString(R.string.cancel), null);
        builder.setPositiveButton(context.getResources().getString(R.string.next),
                new DialogInterface.OnClickListener() {
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

    /**
     * Opens time picker dialog. On the positive button date and time are saved.
     *
     * @param typeId Tells if this is for task start or end time.
     *               See {@link #ID_TYPE_START} and {@link #ID_TYPE_END}.
     * @param datePicker Date picker from the previous {@link #openDatePicker(int)} dialog.
     *                   Used to get selected date from it.
     */
    private void openTimePicker(final int typeId, final DatePicker datePicker) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final TimePicker timePicker = new TimePicker(context);
        timePicker.setIs24HourView(true);

        builder.setTitle(context.getResources().getString(R.string.choose_time));
        builder.setView(timePicker);
        builder.setNegativeButton(context.getResources().getString(R.string.cancel), null);
        builder.setPositiveButton(context.getResources().getString(R.string.set),
                new DialogInterface.OnClickListener() {
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
                // Refresh date time TextViews
                refreshDatesTimes();

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        builder.show();
    }

    /**
     * Refreshes (sets) all dateTime TestViews in the dialog
     */
    private void refreshDatesTimes() {
        ((TextView) dialogView.findViewById(R.id.txt_start_value)).setText(DateTimeConverter
                .getDateTime(task.getStart(), DATE_TIME_FORMAT));
        ((TextView) dialogView.findViewById(R.id.txt_end_value)).setText(DateTimeConverter
                .getDateTime(task.getEnd(), DATE_TIME_FORMAT));
    }

    /**
     * Setter method for the listener.
     * @param onFinishListener Custom listener. See {@link #onFinishListener}.
     */
    void setOnFinishListener(OnFinishListener onFinishListener) {
        this.onFinishListener = onFinishListener;
    }

}
