package me.t8d.c196.repository;

import java.io.IOException;
import java.util.ArrayList;

import me.t8d.c196.db.DatabaseHelper;
import me.t8d.c196.models.Assessment;
import me.t8d.c196.models.AssessmentList;
import me.t8d.c196.models.CourseList;
import me.t8d.c196.models.TermList;

public class DataManager {
    private DatabaseHelper dbHelper;
    private static AssessmentList assessmentList;
    private static CourseList courseList;
    private static TermList termList;
    public DataManager() {
        dbHelper = new DatabaseHelper();
        if (assessmentList == null || courseList == null || termList == null) {
            loadAllData();
        }
    }
    private void loadAllData() {
        try {
            assessmentList = (AssessmentList) dbHelper.readAssessmentListFromFile("assessments.dat");
        } catch (IOException | ClassNotFoundException e) {
            assessmentList = new AssessmentList(new ArrayList<>());
        }
        try {
            courseList = (CourseList) dbHelper.readCourseListFromFile("courses.dat");
        } catch (IOException | ClassNotFoundException e) {
            courseList = new CourseList(new ArrayList<>());
        }
        try {
            termList = (TermList) dbHelper.readTermListFromFile("terms.dat");
        } catch (IOException | ClassNotFoundException e) {
            termList = new TermList(new ArrayList<>());
        }
    }

    public void saveAllData() {
        try {
            dbHelper.saveListToFile(assessmentList, "assessments.dat");
            dbHelper.saveListToFile(courseList, "courses.dat");
            dbHelper.saveListToFile(termList, "terms.dat");
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
