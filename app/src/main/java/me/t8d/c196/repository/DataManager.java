package me.t8d.c196.repository;

import android.content.Context;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import me.t8d.c196.db.DatabaseHelper;
import me.t8d.c196.models.AssessmentList;
import me.t8d.c196.models.CourseList;
import me.t8d.c196.models.TermList;

public class DataManager {
    private DatabaseHelper dbHelper;
    private static AssessmentList assessmentList;
    private static CourseList courseList;
    private static TermList termList;
    private static File path;
    public DataManager(Context context) {
        dbHelper = new DatabaseHelper();
        if (assessmentList == null || courseList == null || termList == null) {
            path = context.getFilesDir();
            loadAllData();
        }
    }
    public DataManager() {
        dbHelper = new DatabaseHelper();
        if (assessmentList == null || courseList == null || termList == null) {
            throw new IllegalStateException("DataManager must be initialized with a Context before use.");
        }
    }
    private void loadAllData() {
        try {
            assessmentList = (AssessmentList) dbHelper.readAssessmentListFromFile("assessments.dat", path);
        } catch (IOException | ClassNotFoundException e) {
            assessmentList = new AssessmentList(new ArrayList<>());
        }
        try {
            courseList = (CourseList) dbHelper.readCourseListFromFile("courses.dat", path);
        } catch (IOException | ClassNotFoundException e) {
            courseList = new CourseList(new ArrayList<>());
        }
        try {
            termList = (TermList) dbHelper.readTermListFromFile("terms.dat", path);
        } catch (IOException | ClassNotFoundException e) {
            termList = new TermList(new ArrayList<>());
        }
    }

    public void saveAllData() {
        try {
            dbHelper.saveListToFile(assessmentList, "assessments.dat", path);
            dbHelper.saveListToFile(courseList, "courses.dat", path);
            dbHelper.saveListToFile(termList, "terms.dat", path);
        } catch (IOException e) {
            // Handle IO exceptions (write errors, etc.)
        }
    }
    public AssessmentList GetAssessmentList() {
        return assessmentList;
    }
    public CourseList GetCourseList() {
        return courseList;
    }
    public TermList GetTermList() {
        return termList;
    }
}
