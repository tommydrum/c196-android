package me.t8d.c196.ui.term;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
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
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import me.t8d.c196.R;
import me.t8d.c196.models.Course;
import me.t8d.c196.models.CourseList;
import me.t8d.c196.models.Term;
import me.t8d.c196.models.TermList;
import me.t8d.c196.repository.DataManager;

public class TermAddEditFragment extends DialogFragment {
    Term currentTerm;
    CourseList courseList;
    public interface OnTermUpdatedListener {
        void onTermUpdated(int position, Term updatedTerm);
        void onTermDeleted(int position, Term term);
    }
    private OnTermUpdatedListener updateListener;
    public void setOnTermUpdatedListener(OnTermUpdatedListener callback) {
        this.updateListener = callback;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Inflate and set the layout for the dialog
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_add_edit_term, null);
        // Linking buttons/etc to functions
        Button cancelButton = view.findViewById(R.id.cancelTermButton);
        cancelButton.setOnClickListener(v -> dismiss());
        Button assessmentButton = view.findViewById(R.id.coursesButton);
        assessmentButton.setOnClickListener(v -> showCourseSelectionDialog());
        EditText editTextStartDate = view.findViewById(R.id.editTermStartDate);
        editTextStartDate.setOnClickListener(v -> showStartDatePickerDialog());
        EditText editTextEndDate = view.findViewById(R.id.editTermEndDate);
        editTextEndDate.setOnClickListener(v -> showEndDatePickerDialog());
        Button saveButton = view.findViewById(R.id.saveTermButton);
        saveButton.setOnClickListener(v -> save());
        Button deleteButton = view.findViewById(R.id.deleteTermButton);
        deleteButton.setOnClickListener(v -> delete());

        builder.setView(view);
        return builder.create();
    }
    private void save() {
        Dialog dialog = getDialog();
        Bundle args = getArguments();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        boolean isEditMode = args != null && args.getBoolean("isEditMode", false);
        boolean isValid = true;
        int itemId = args != null ? args.getInt("itemId", 0) : 0;
        EditText editTextTitle = dialog.findViewById(R.id.editTextTermTitle);
        String title = editTextTitle != null ? editTextTitle.getText().toString() : "";
        if (title.isEmpty()) {
            editTextTitle.setError("Please enter a title");
            isValid = false;
        }
        EditText editTextStartDate = dialog.findViewById(R.id.editTermStartDate);
        String startDate = editTextStartDate != null ? editTextStartDate.getText().toString() : "";
        Date startDateObj = null;
        try {
            startDateObj = dateFormat.parse(startDate);
        } catch (Exception e) {
            editTextStartDate.setError("Please enter a valid date");
            isValid = false;
        }
        EditText editTextEndDate = dialog.findViewById(R.id.editTermEndDate);
        String endDate = editTextEndDate != null ? editTextEndDate.getText().toString() : "";
        Date endDateObj = null;
        try {
            endDateObj = dateFormat.parse(endDate);
        } catch (Exception e) {
            editTextEndDate.setError("Please enter a valid date");
            isValid = false;
        }
        // Check if the end date is before the start date
        if (startDateObj != null && endDateObj != null && endDateObj.before(startDateObj)) {
            editTextEndDate.setError("End date must be after start date");
            isValid = false;
        }
        // Check to see if the term dates overlap with any other terms
        DataManager dataManager = new DataManager();
        TermList termList = dataManager.GetTermList();
        for (Term term : termList.GetTermList()) {
            if (currentTerm != null && term.equals(currentTerm)) {
                continue; // Skip the current term if we're editing it
            }
            if (startDateObj != null && endDateObj != null) {
                if (startDateObj.after(term.GetStartDate()) && startDateObj.before(term.GetEndDate())) {
                    editTextStartDate.setError("Term dates cannot overlap with other terms");
                    isValid = false;
                }
                if (endDateObj.after(term.GetStartDate()) && endDateObj.before(term.GetEndDate())) {
                    editTextEndDate.setError("Term dates cannot overlap with other terms");
                    isValid = false;
                }
            }
        }
        if (!isValid) return;
        Term newTerm = new Term(startDateObj, endDateObj, title, courseList);
        if (updateListener != null) {
            if (isEditMode)
                updateListener.onTermUpdated(itemId, newTerm);
            else
                updateListener.onTermUpdated(-1, newTerm);
        }
        dismiss();
    }
    private void delete() {
        Dialog dialog = getDialog();
        Bundle args = getArguments();
        boolean isEditMode = args != null && args.getBoolean("isEditMode", false);
        int itemId = args != null ? args.getInt("itemId", 0) : 0;
        // Prevent the user from deleting a term with courses
        if (currentTerm != null && currentTerm.GetCourseList().GetCourseList().size() > 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Error")
                    .setMessage("You cannot delete a term with courses")
                    .setPositiveButton("OK", (dialog1, which) -> {
                        // Do nothing
                    });
            AlertDialog dialog1 = builder.create();
            dialog1.show();
            return;
        }
        if (isEditMode) {
            if (updateListener != null) {
                updateListener.onTermDeleted(itemId, currentTerm);
            }
        }
        dismiss();
    }

    private void showCourseSelectionDialog() {
        DataManager dataManager = new DataManager();
        CourseList allCourses = dataManager.GetCourseList();
        String[] courses = allCourses.GetCourseList().stream().map(Course::GetTitle).toArray(String[]::new);
        boolean[] checkedItems = new boolean[allCourses.GetCourseList().size()];
        CourseList selectedCourses = new CourseList(new ArrayList<>());

        if (currentTerm != null) {
            if (currentTerm.GetCourseList() != null)
                selectedCourses = new CourseList((ArrayList<Course>) currentTerm.GetCourseList().GetCourseList().clone());
        }

        // Initialize the checked states based on the current term's course list
        for (int i = 0; i < allCourses.GetCourseList().size(); i++) {
            Course course = allCourses.GetCourseList().get(i);
            if (selectedCourses.GetCourseList().contains(course)) {
                checkedItems[i] = true;
            } else {
                checkedItems[i] = false;
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        CourseList finalSelectedCourses = selectedCourses;
        builder.setTitle("Select Courses")
                .setMultiChoiceItems(courses, checkedItems, (dialog, which, isChecked) -> {
                    Course selectedCourse = allCourses.GetCourseList().get(which);
                    if (isChecked) {
                        finalSelectedCourses.GetCourseList().add(selectedCourse);
                    } else {
                        finalSelectedCourses.GetCourseList().remove(selectedCourse);
                    }
                })
                .setPositiveButton("OK", (dialog, which) -> {
                    courseList = finalSelectedCourses; // Update the term's course list
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    // Cancel action, no changes saved
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
                        // Format the date and set it to the EditText
                        String selectedDate = (month + 1) + "/" + dayOfMonth + "/" + year;
                        EditText editTextEndDate = getDialog().findViewById(R.id.editTermStartDate);
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
                        EditText editTextEndDate = getDialog().findViewById(R.id.editTermEndDate);
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
                TextView headerTitle = dialog.findViewById(R.id.headerTermTitle);
                Button deleteButton = dialog.findViewById(R.id.deleteTermButton);
                LinearLayout buttonRow = dialog.findViewById(R.id.termButtonRow);
                if (headerTitle != null) {
                    headerTitle.setText(isEditMode ? "Edit Term" : "Add Term");
                }
                if (deleteButton != null && buttonRow != null) {
                    if (!isEditMode) {
                        buttonRow.removeViewInLayout(deleteButton);
                    }
                }
            }
            if (isEditMode) {
                int itemId = args.getInt("itemId", 0);
                DataManager dataManager = new DataManager();
                TermList termList = dataManager.GetTermList();
                currentTerm = termList.GetTermList().get(itemId);
                courseList = currentTerm.GetCourseList();
                if (courseList == null)
                    courseList = new CourseList(new ArrayList<>());
                EditText editTextTitle = dialog.findViewById(R.id.editTextTermTitle);
                EditText editTextStartDate = dialog.findViewById(R.id.editTermStartDate);
                EditText editTextEndDate = dialog.findViewById(R.id.editTermEndDate);
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                if (editTextTitle != null) {
                    editTextTitle.setText(currentTerm.GetTermName());
                }
                if (editTextStartDate != null) {
                    editTextStartDate.setText(dateFormat.format(currentTerm.GetStartDate()));
                }
                if (editTextEndDate != null) {
                    editTextEndDate.setText(dateFormat.format(currentTerm.GetEndDate()));
                }
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
                TextView headerTitle = dialog.findViewById(R.id.headerTermTitle);
                Button deleteButton = dialog.findViewById(R.id.deleteTermButton);
                LinearLayout buttonRow = dialog.findViewById(R.id.termButtonRow);
                if (headerTitle != null) {
                    headerTitle.setText(isEditMode ? "Edit Term" : "Add Term");
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
