package me.t8d.c196.ui.assessment;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import me.t8d.c196.AlarmReceiver;
import me.t8d.c196.R;
import me.t8d.c196.models.Assessment;
import me.t8d.c196.repository.DataManager;

public class AssessmentAddEditFragment extends DialogFragment {
    Assessment currentAssessment;
    public interface OnAssessmentUpdatedListener {
        void onAssessmentUpdated(int position, Assessment newAssessment);
        void onAssessmentDeleted(int position, Assessment assessment);
    }
    private OnAssessmentUpdatedListener updateListener;
    public void setOnAssessmentUpdatedListener(OnAssessmentUpdatedListener listener) {
        this.updateListener = listener;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Inflate and set the layout for the dialog
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_add_edit_assessment, null);

        // Linking buttons/etc to functions

        EditText editTextStartDate = view.findViewById(R.id.editAssessmentStartDate);
        editTextStartDate.setOnClickListener(v -> showStartDatePickerDialog());
        EditText editTextEndDate = view.findViewById(R.id.editAssessmentEndDate);
        editTextEndDate.setOnClickListener(v -> showEndDatePickerDialog());

        Button cancelButton = view.findViewById(R.id.cancelAssessmentButton);
        cancelButton.setOnClickListener(v -> dismiss());
        Button saveButton = view.findViewById(R.id.saveAssessmentButton);
        saveButton.setOnClickListener(v -> save());
        Button deleteButton = view.findViewById(R.id.deleteAssessmentButton);
        deleteButton.setOnClickListener(v -> delete());

        builder.setView(view);
        return builder.create();
    }
    private void save() {
        Dialog dialog = getDialog();
        Bundle args = getArguments();
        boolean isValid = true;
        boolean isEditMode = args != null && args.getBoolean("isEditMode", false);
        DataManager dataManager = new DataManager();
        int itemId = args != null ? args.getInt("itemId", 0) : 0;
        EditText title = dialog.findViewById(R.id.editTextAssessmentTitle);
        RadioButton perfRadioButton = dialog.findViewById(R.id.radioButtonPerformance);
        Assessment.Type type;

        if (perfRadioButton.isChecked())
            type = Assessment.Type.Performance;
        else
            type = Assessment.Type.Objective;
        boolean isPerformance = perfRadioButton.isChecked();
        EditText endDate = dialog.findViewById(R.id.editAssessmentEndDate);
        EditText startDate = dialog.findViewById(R.id.editAssessmentStartDate);
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        Date endDateObj = null;
        Date startDateObj = null;
        try {
            endDateObj = formatter.parse(endDate.getText().toString());
            endDate.setError(null);
        } catch (ParseException e) {
            //Validation Error
            endDate.setError("Required");
            isValid = false;
        }
        try {
            startDateObj = formatter.parse(startDate.getText().toString());
            endDate.setError(null);
        } catch (ParseException e) {
            //Validation Error
            startDate.setError("Required");
            isValid = false;
        }
        if (title.getText().length() == 0) {
            //Validation Error
            title.setError("Required");
            isValid = false;
        } else title.setError(null);
        // Check if end date is before start date
        if (endDateObj != null && startDateObj != null && endDateObj.before(startDateObj)) {
            endDate.setError("End date must be after start date");
            isValid = false;
        }
        if (!isValid) return;
        Assessment newAssessment = new Assessment(type, title.getText().toString(), endDateObj, startDateObj);
        if (updateListener != null)
        {
            if (isEditMode) {
                // set the ID to the original assessment's ID
                newAssessment.SetNotificationId(currentAssessment.GetNotificationId());
                // cancel the old notification
                cancelNotification(currentAssessment.GetNotificationId());
                cancelNotification(currentAssessment.GetNotificationId() + 1);
                updateListener.onAssessmentUpdated(itemId, newAssessment);
            }
            else {
                // find next available ID
                int id = 0;
                for (Assessment assessment : dataManager.GetAssessmentList().GetAssessmentList()) {
                    if (assessment.GetNotificationId() > id) {
                        id = assessment.GetNotificationId();
                    }
                }
                newAssessment.SetNotificationId(id + 2);
                updateListener.onAssessmentUpdated(-1, newAssessment);
            }
            // schedule the new notification if date is in the future
            if (startDateObj.after(Calendar.getInstance().getTime()))
                scheduleInexactAlarm(startDateObj, "Assessment " + newAssessment.GetTitle() + " is starting today!", newAssessment.GetNotificationId());
            if (endDateObj.after(Calendar.getInstance().getTime()))
                scheduleInexactAlarm(endDateObj, "Assessment " + newAssessment.GetTitle() + " is ending today!", newAssessment.GetNotificationId() + 1);
        }
        dismiss();
    }
    private void delete() {
        if (updateListener != null) {
            Bundle args = getArguments();
            int itemId = args != null ? args.getInt("itemId", 0) : 0;
            updateListener.onAssessmentDeleted(itemId, currentAssessment);
        }
        dismiss();
    }

    private void scheduleInexactAlarm(Date date, String message, int notificationId) {
        Context context = getContext();
        if (context != null) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, AlarmReceiver.class);
            intent.putExtra("message", message);
            intent.putExtra("notificationId", notificationId);

            // Specify FLAG_IMMUTABLE for PendingIntent
            int flags = PendingIntent.FLAG_UPDATE_CURRENT;
            flags |= PendingIntent.FLAG_IMMUTABLE;

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context, notificationId, intent, flags);

            long triggerAtMillis = date.getTime();
            long intervalMillis = AlarmManager.INTERVAL_DAY; // Repeat every day

            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, triggerAtMillis, intervalMillis, pendingIntent);
        }
    }

    private void cancelNotification(int notificationId) {
        Context context = getContext();
        if (context != null) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(notificationId);
        }
    }

    private void showStartDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // Format the date and set it to the EditText
                        String selectedDate = (month + 1) + "/" + dayOfMonth + "/" + year;
                        EditText editTextEndDate = getDialog().findViewById(R.id.editAssessmentStartDate);
                        editTextEndDate.setText(selectedDate);
                    }
                }, year, month, day);
        datePickerDialog.show();
    }
    private void showEndDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // Format the date and set it to the EditText
                        String selectedDate = (month + 1) + "/" + dayOfMonth + "/" + year;
                        EditText editTextEndDate = getDialog().findViewById(R.id.editAssessmentEndDate);
                        editTextEndDate.setText(selectedDate);
                    }
                }, year, month, day);
        datePickerDialog.show();
    }


    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            Bundle args = getArguments();
            boolean isEditMode = args != null && args.getBoolean("isEditMode", false);
            Window window = dialog.getWindow();
            if (window != null) {
                // Set the width and height for the dialog
                int width = ViewGroup.LayoutParams.MATCH_PARENT;
                int height = ViewGroup.LayoutParams.WRAP_CONTENT;
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.copyFrom(dialog.getWindow().getAttributes());
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                window.setLayout(width, height);
                window.setAttributes(layoutParams);
                TextView headerTitle = dialog.findViewById(R.id.headerAssessmentTitle);
                Button deleteButton = dialog.findViewById(R.id.deleteAssessmentButton);
                LinearLayout buttonRow = dialog.findViewById(R.id.assessmentButtonRow);
                if (headerTitle != null) {
                    headerTitle.setText(isEditMode ? "Edit Assessment" : "Add Assessment");
                }
                if (deleteButton != null && buttonRow != null) {
                    if (!isEditMode) {
                        buttonRow.removeViewInLayout(deleteButton);
                    }
                }
            }
            if (isEditMode) {
                // Retrieve Assessment Object by ID (really the index).
                DataManager dataManager = new DataManager();
                int id = args.getInt("itemId");
                currentAssessment = dataManager.GetAssessmentList().GetAssessmentList().get(id);
                // Prefill form with current Assessment data
                EditText title = dialog.findViewById(R.id.editTextAssessmentTitle);
                title.setText(currentAssessment.GetTitle());
                RadioButton perfRadioButton = dialog.findViewById(R.id.radioButtonPerformance);
                RadioButton objRadioButton = dialog.findViewById(R.id.radioButtonObjective);
                switch(currentAssessment.GetTypeEnum()) {
                    case Performance:
                        perfRadioButton.setChecked(true);
                        objRadioButton.setChecked(false);
                        break;
                    case Objective:
                        perfRadioButton.setChecked(false);
                        objRadioButton.setChecked(true);
                        break;
                }
                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
                EditText startDate = dialog.findViewById(R.id.editAssessmentStartDate);
                startDate.setText(formatter.format(currentAssessment.GetStartDate()));
                EditText endDate = dialog.findViewById(R.id.editAssessmentEndDate);
                endDate.setText(formatter.format(currentAssessment.GetEndDate()));
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Dialog dialog = getDialog();
        if (dialog != null) {
            Bundle args = getArguments();
            boolean isEditMode = args != null && args.getBoolean("isEditMode", false);
            Window window = dialog.getWindow();
            if (window != null) {
                int width = ViewGroup.LayoutParams.MATCH_PARENT;
                int height = ViewGroup.LayoutParams.WRAP_CONTENT;
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.copyFrom(dialog.getWindow().getAttributes());
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                window.setLayout(width, height);
                window.setAttributes(layoutParams);
                TextView headerTitle = dialog.findViewById(R.id.headerAssessmentTitle);
                Button deleteButton = dialog.findViewById(R.id.deleteAssessmentButton);
                LinearLayout buttonRow = dialog.findViewById(R.id.assessmentButtonRow);
                if (headerTitle != null) {
                    headerTitle.setText(isEditMode ? "Edit Assessment" : "Add Assessment");
                }
                if (deleteButton != null && buttonRow != null) {
                    if (!isEditMode) {
                        buttonRow.removeViewInLayout(deleteButton);
                    }
                }
            }
        }
    }

}
