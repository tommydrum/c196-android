package me.t8d.c196.ui.assessment;

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

import java.util.Calendar;

import me.t8d.c196.R;

public class AssessmentAddEditFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Inflate and set the layout for the dialog
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_add_edit_assessment, null);

        // Linking buttons/etc to functions
        Button cancelButton = view.findViewById(R.id.cancelAssessmentButton);
        cancelButton.setOnClickListener(v -> dismiss());
        EditText editTextEndDate = view.findViewById(R.id.editTextEndDate);
        editTextEndDate.setOnClickListener(v -> showDatePickerDialog());
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
        boolean isEditMode = args != null && args.getBoolean("isEditMode", false);
        int itemId = args != null ? args.getInt("itemId", 0) : 0;
    }
    private void delete() {
        Dialog dialog = getDialog();
        Bundle args = getArguments();
        boolean isEditMode = args != null && args.getBoolean("isEditMode", false);
        int itemId = args != null ? args.getInt("itemId", 0) : 0;
    }

    private void showDatePickerDialog() {
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
                        EditText editTextEndDate = getDialog().findViewById(R.id.editTextEndDate);
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
