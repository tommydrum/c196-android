package me.t8d.c196.ui.course;

import static androidx.core.content.ContextCompat.getSystemService;

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
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import me.t8d.c196.AlarmReceiver;
import me.t8d.c196.R;
import me.t8d.c196.models.Assessment;
import me.t8d.c196.models.AssessmentList;
import me.t8d.c196.models.Course;
import me.t8d.c196.repository.DataManager;

public class CourseAddEditFragment extends DialogFragment {
    Course currentCourse;
    AssessmentList assessmentList = new AssessmentList(new ArrayList<Assessment>());
    public interface OnCourseUpdatedListener {
        void onCourseUpdated(int position, Course newCourse);
        void onCourseDeleted(int position, Course course);
    }
    private OnCourseUpdatedListener updateListener;
    public void setOnCourseUpdatedListener(OnCourseUpdatedListener listener) {
        this.updateListener = listener;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Inflate and set the layout for the dialog
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_add_edit_course, null);
        // Make dropdown for status spinner
        Spinner spinnerCourseStatus = view.findViewById(R.id.spinnerCourseStatus);
        Course.Status[] statuses = Course.Status.values();
        ArrayAdapter<Course.Status> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, statuses);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCourseStatus.setAdapter(adapter);
        // Linking buttons/etc to functions
        Button cancelButton = view.findViewById(R.id.cancelCourseButton);
        cancelButton.setOnClickListener(v -> dismiss());
        Button assessmentButton = view.findViewById(R.id.assessmentsButton);
        assessmentButton.setOnClickListener(v -> showAssessmentSelectionDialog());
        EditText editTextStartDate = view.findViewById(R.id.editCourseDate);
        editTextStartDate.setOnClickListener(v -> showStartDatePickerDialog());
        EditText editTextEndDate = view.findViewById(R.id.editCourseEndDate);
        editTextEndDate.setOnClickListener(v -> showEndDatePickerDialog());
        Button saveButton = view.findViewById(R.id.saveCourseButton);
        saveButton.setOnClickListener(v -> save());
        Button deleteButton = view.findViewById(R.id.deleteCourseButton);
        deleteButton.setOnClickListener(v -> delete());
        Button shareButton = view.findViewById(R.id.shareNotesButton);
        shareButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, "Course Notes");
            EditText editTextCourseNote = view.findViewById(R.id.editTextCourseNote);
            intent.putExtra(Intent.EXTRA_TEXT, editTextCourseNote.getText());
            startActivity(Intent.createChooser(intent, "Share Notes"));
        });
        Button scheduleStartNotificationButton = view.findViewById(R.id.startCourseNotificationButton);
        scheduleStartNotificationButton.setOnClickListener(v -> scheduleStartNotification());
        Button scheduleEndNotificationButton = view.findViewById(R.id.endCourseNotificationButton);
        scheduleEndNotificationButton.setOnClickListener(v -> scheduleEndNotification());

        builder.setView(view);
        return builder.create();
    }
    private void save() {
        Dialog dialog = getDialog();
        Bundle args = getArguments();
        boolean isEditMode = args != null && args.getBoolean("isEditMode", false);
        boolean isValid = true;
        int itemId = args != null ? args.getInt("itemId", 0) : 0;
        Spinner spinnerCourseStatus = dialog.findViewById(R.id.spinnerCourseStatus);
        Course.Status selectedStatus = (Course.Status) spinnerCourseStatus.getSelectedItem();
        EditText editTextCourseTitle = dialog.findViewById(R.id.editTextCourseTitle);
        String courseTitle = editTextCourseTitle.getText().toString();
        EditText editTextCourseNote = dialog.findViewById(R.id.editTextCourseNote);
        String courseNote = editTextCourseNote.getText().toString();
        EditText editTextInstructorFirstName = dialog.findViewById(R.id.editInstructorFirstName);
        String instructorFirstName = editTextInstructorFirstName.getText().toString();
        EditText editTextInstructorLastName = dialog.findViewById(R.id.editInstructorLastName);
        String instructorLastName = editTextInstructorLastName.getText().toString();
        EditText editTextInstructorPhone = dialog.findViewById(R.id.editTextInstructorPhone);
        String instructorPhone = editTextInstructorPhone.getText().toString();
        EditText editTextInstructorEmail = dialog.findViewById(R.id.editTextInstructorEmail);
        String instructorEmail = editTextInstructorEmail.getText().toString();
        EditText editTextStartDate = dialog.findViewById(R.id.editCourseDate);
        String startDate = editTextStartDate.getText().toString();
        EditText editTextEndDate = dialog.findViewById(R.id.editCourseEndDate);
        String endDate = editTextEndDate.getText().toString();
        if (courseTitle.isEmpty()) {
            editTextCourseTitle.setError("Course title is required");
            isValid = false;
        }
        if (instructorFirstName.isEmpty()) {
            editTextInstructorFirstName.setError("Instructor first name is required");
            isValid = false;
        }
        if (instructorLastName.isEmpty()) {
            editTextInstructorLastName.setError("Instructor last name is required");
            isValid = false;
        }
        if (instructorPhone.isEmpty()) {
            editTextInstructorPhone.setError("Instructor phone is required");
            isValid = false;
        }
        // regex check phone number
        if (!instructorPhone.matches("\\d{3}-\\d{3}-\\d{4}")) {
            editTextInstructorPhone.setError("Bad format, use ###-###-####");
            isValid = false;
        }
        if (instructorEmail.isEmpty()) {
            editTextInstructorEmail.setError("Instructor email is required");
            isValid = false;
        }
        // regex check email
        if (!instructorEmail.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            editTextInstructorEmail.setError("Invalid Email");
            isValid = false;
        }
        if (startDate.isEmpty()) {
            editTextStartDate.setError("Start date is required");
            isValid = false;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        Date startDateObj = null;
        try {
            startDateObj = dateFormat.parse(startDate);
        } catch (Exception e) {
            editTextStartDate.setError("Invalid date format");
            isValid = false;
        }
        Date endDateObj = null;
        try {
            endDateObj = dateFormat.parse(endDate);
        } catch (Exception e) {
            editTextEndDate.setError("Invalid date format");
            isValid = false;
        }
        if (endDate.isEmpty()) {
            editTextEndDate.setError("End date is required");
            isValid = false;
        }
        if (endDateObj != null && startDateObj != null && endDateObj.before(startDateObj)) {
            editTextEndDate.setError("End date must be after start date");
            isValid = false;
        }
        if (!isValid) return;
        if (updateListener != null) {
            Course newCourse = new Course(courseTitle, startDateObj, endDateObj, selectedStatus, courseNote, assessmentList, instructorFirstName, instructorLastName, instructorPhone, instructorEmail);
            // Set notificationId
            if (isEditMode) {
                newCourse.SetNotificationId(currentCourse.GetNotificationId());
            } else {
                // Find the next available notification ID
                int nextNotificationId = 0;
                for (Course course : new DataManager().GetCourseList().GetCourseList()) {
                    if (course.GetNotificationId() > nextNotificationId) {
                        nextNotificationId = course.GetNotificationId();
                    }
                }
                newCourse.SetNotificationId(nextNotificationId + 2);
            }
            // Save the new course
            if (isEditMode) {
                //Reset notification
                cancelNotification(currentCourse.GetNotificationId());
                cancelNotification(currentCourse.GetNotificationId() + 1);
                //Only set if the date is after today
                if (currentCourse.GetStartNotification()) {
                    scheduleInexactAlarm(newCourse.GetStartDate(), "Course " + newCourse.GetTitle() + " starts today!", newCourse.GetNotificationId());
                    newCourse.SetStartNotification(true);
                }
                //end date notification
                if (currentCourse.GetEndNotification()) {
                    scheduleInexactAlarm(newCourse.GetEndDate(), "Course " + newCourse.GetTitle() + " ends today!", newCourse.GetNotificationId() + 1);
                    newCourse.SetEndNotification(true);
                }
                updateListener.onCourseUpdated(itemId, newCourse);
            } else {
                updateListener.onCourseUpdated(-1, newCourse);
            }
        }
        dismiss();
    }
    private void delete() {
        Dialog dialog = getDialog();
        Bundle args = getArguments();
        boolean isEditMode = args != null && args.getBoolean("isEditMode", false);
        int itemId = args != null ? args.getInt("itemId", 0) : 0;
        if (isEditMode) {
            if (updateListener != null) {
                updateListener.onCourseDeleted(itemId, currentCourse);
                cancelNotification(currentCourse.GetNotificationId());
                cancelNotification(currentCourse.GetNotificationId() + 1);
            }
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

            int flags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, intent, flags);

            // Set the alarm to go off at approximately 8 AM
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.HOUR_OF_DAY, 8);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            long triggerAtMillis = calendar.getTimeInMillis();
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
        }
    }
    private void cancelNotification(int notificationId) {
        Context context = getContext();
        if (context != null) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, AlarmReceiver.class);

            // Use the same flags as when you created the PendingIntent
            int flags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, intent, flags);

            if (alarmManager != null) {
                alarmManager.cancel(pendingIntent);
            }
        }
    }
    public void scheduleStartNotification() {
        Dialog dialog = getDialog();
        EditText startDate = dialog.findViewById(R.id.editCourseDate);
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        Date startDateObj = null;
        try {
            startDateObj = formatter.parse(startDate.getText().toString());
            startDate.setError(null);
        } catch (ParseException e) {
            //Validation Error
            startDate.setError("Required");
            return;
        }
        // if isEditMode and start date is before today, and start notification is set, cancel the notification
        if (currentCourse.GetStartNotification() ) {
            cancelNotification(currentCourse.GetNotificationId());
            // Set button text to "Schedule Start Notification"
            Button scheduleStartNotificationButton = dialog.findViewById(R.id.startCourseNotificationButton);
            scheduleStartNotificationButton.setText("Schedule Start Notification");
            currentCourse.SetStartNotification(false);
        }
        else if (startDateObj != null) {
            scheduleInexactAlarm(startDateObj, "Course " + currentCourse.GetTitle() + " is starting today!", currentCourse.GetNotificationId());
            int itemId = getArguments() != null ? getArguments().getInt("itemId", 0) : 0;
            // Set button text to "Cancel Start Notification"
            Button scheduleStartNotificationButton = dialog.findViewById(R.id.startCourseNotificationButton);
            scheduleStartNotificationButton.setText("Cancel Start Notification");
            currentCourse.SetStartNotification(true);
            currentCourse.SetEndDate(startDateObj);
            updateListener.onCourseUpdated(itemId, currentCourse);
        }
    }

    public void scheduleEndNotification() {
        Dialog dialog = getDialog();
        EditText endDate = dialog.findViewById(R.id.editCourseEndDate);
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        Date endDateObj = null;
        try {
            endDateObj = formatter.parse(endDate.getText().toString());
            endDate.setError(null);
        } catch (ParseException e) {
            //Validation Error
            endDate.setError("Required");
            return;
        }
        // if isEditMode and end date is before today, and end notification is set, cancel the notification
        if (currentCourse.GetEndNotification() ) {
            cancelNotification(currentCourse.GetNotificationId() + 1);
            // Set button text to "Schedule End Notification"
            Button scheduleEndNotificationButton = dialog.findViewById(R.id.endCourseNotificationButton);
            scheduleEndNotificationButton.setText("Schedule End Notification");
            currentCourse.SetEndNotification(false);
        }
        else if (endDateObj != null) {
            scheduleInexactAlarm(endDateObj, "Course " + currentCourse.GetTitle() + " is ending today!", currentCourse.GetNotificationId() + 1);
            int itemId = getArguments() != null ? getArguments().getInt("itemId", 0) : 0;
            // Set button text to "Cancel End Notification"
            Button scheduleEndNotificationButton = dialog.findViewById(R.id.endCourseNotificationButton);
            scheduleEndNotificationButton.setText("Cancel End Notification");
            currentCourse.SetEndNotification(true);
            currentCourse.SetEndDate(endDateObj);
            updateListener.onCourseUpdated(itemId, currentCourse);
        }
    }

    private void showAssessmentSelectionDialog() {
        DataManager dataManager = new DataManager();
        AssessmentList allAssessments = dataManager.GetAssessmentList();
        String[] assessments = allAssessments.GetAssessmentList().stream().map(Assessment::GetTitle).toArray(String[]::new);
        boolean[] checkedItems = new boolean[allAssessments.GetAssessmentList().size()];
        AssessmentList selectedAssessments = new AssessmentList(new ArrayList<>());
        if (currentCourse != null) {
            if (currentCourse.GetAssessmentList() != null)
                selectedAssessments = new AssessmentList((ArrayList<Assessment>) currentCourse.GetAssessmentList().GetAssessmentList().clone());
        }

        selectedAssessments.GetAssessmentList().clear();
        for (int i = 0; i < allAssessments.GetAssessmentList().size(); i++) {
            Assessment assessment = allAssessments.GetAssessmentList().get(i);
            if (assessmentList.GetAssessmentList().contains(assessment)) {
                selectedAssessments.GetAssessmentList().add(assessment);
                checkedItems[i] = true;
            } else {
                checkedItems[i] = false;
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        AssessmentList finalSelectedAssessments = selectedAssessments;
        builder.setTitle("Select Assessments")
                .setMultiChoiceItems(assessments, checkedItems, (dialog, which, isChecked) -> {
                    Assessment selectedAssessment = allAssessments.GetAssessmentList().get(which);
                    if (isChecked) {
                        finalSelectedAssessments.GetAssessmentList().add(selectedAssessment);
                    } else {
                        finalSelectedAssessments.GetAssessmentList().remove(selectedAssessment);
                    }
                })
                .setPositiveButton("OK", (dialog, which) -> {
                    assessmentList = finalSelectedAssessments;
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    // Does nothing, the selected assessments will not be saved
                });

        AlertDialog dialog = builder.create();
        dialog.show();
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
                        String selectedDate = (month + 1) + "/" + dayOfMonth + "/" + year;
                        EditText editTextEndDate = getDialog().findViewById(R.id.editCourseDate);
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
                        EditText editTextEndDate = getDialog().findViewById(R.id.editCourseEndDate);
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
                TextView headerTitle = dialog.findViewById(R.id.headerCourseTitle);
                Button deleteButton = dialog.findViewById(R.id.deleteCourseButton);
                LinearLayout buttonRow = dialog.findViewById(R.id.courseButtonRow);
                if (headerTitle != null) {
                    headerTitle.setText(isEditMode ? "Edit Course" : "Add Course");
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
                currentCourse = dataManager.GetCourseList().GetCourseList().get(id);
                assessmentList = currentCourse.GetAssessmentList();
                // Prefill form with current Course data
                EditText title = dialog.findViewById(R.id.editTextCourseTitle);
                title.setText(currentCourse.GetTitle());
                Spinner spinnerCourseStatus = getDialog().findViewById(R.id.spinnerCourseStatus);
                spinnerCourseStatus.setSelection(currentCourse.GetStatus().ordinal());
                EditText courseNotes = dialog.findViewById(R.id.editTextCourseNote);
                courseNotes.setText(currentCourse.GetNotes());
                EditText instructorFirstName = dialog.findViewById(R.id.editInstructorFirstName);
                instructorFirstName.setText(currentCourse.GetInstructor().GetFirstName());
                EditText instructorLastName = dialog.findViewById(R.id.editInstructorLastName);
                instructorLastName.setText(currentCourse.GetInstructor().GetLastName());
                EditText instructorPhone = dialog.findViewById(R.id.editTextInstructorPhone);
                instructorPhone.setText(currentCourse.GetInstructor().GetPhone());
                EditText instructorEmail = dialog.findViewById(R.id.editTextInstructorEmail);
                instructorEmail.setText(currentCourse.GetInstructor().GetEmail());

                EditText startDate = dialog.findViewById(R.id.editCourseDate);
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                startDate.setText(dateFormat.format(currentCourse.GetStartDate().getTime()));
                EditText endDate = dialog.findViewById(R.id.editCourseEndDate);
                endDate.setText(dateFormat.format(currentCourse.GetEndDate().getTime()));
                // set notification buttons to correct text
                Button scheduleStartNotificationButton = dialog.findViewById(R.id.startCourseNotificationButton);
                if (currentCourse.GetStartNotification()) {
                    scheduleStartNotificationButton.setText("Cancel Start Notification");
                } else {
                    scheduleStartNotificationButton.setText("Schedule Start Notification");
                }
                Button scheduleEndNotificationButton = dialog.findViewById(R.id.endCourseNotificationButton);
                if (currentCourse.GetEndNotification()) {
                    scheduleEndNotificationButton.setText("Cancel End Notification");
                } else {
                    scheduleEndNotificationButton.setText("Schedule End Notification");
                }

            } else {
                // Hide the share button
                Button shareButton = dialog.findViewById(R.id.shareNotesButton);
                shareButton.setVisibility(View.GONE);
                // Hide the notification buttons
                Button scheduleStartNotificationButton = dialog.findViewById(R.id.startCourseNotificationButton);
                scheduleStartNotificationButton.setVisibility(View.GONE);
                Button scheduleEndNotificationButton = dialog.findViewById(R.id.endCourseNotificationButton);
                scheduleEndNotificationButton.setVisibility(View.GONE);
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
                TextView headerTitle = dialog.findViewById(R.id.headerCourseTitle);
                Button deleteButton = dialog.findViewById(R.id.deleteCourseButton);
                LinearLayout buttonRow = dialog.findViewById(R.id.courseButtonRow);
                if (headerTitle != null) {
                    headerTitle.setText(isEditMode ? "Edit Course" : "Add Course");
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
