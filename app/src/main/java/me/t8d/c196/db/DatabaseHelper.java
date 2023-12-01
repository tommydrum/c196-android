package me.t8d.c196.db;

import me.t8d.c196.models.AssessmentList;
import me.t8d.c196.models.CourseList;
import me.t8d.c196.models.TermList;
import me.t8d.c196.repository.DataManager;
import java.io.*;
import java.util.List;

public class DatabaseHelper {
    public void saveListToFile(AssessmentList list, String filename) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(list);
        }
    }

    public AssessmentList readAssessmentListFromFile(String filename) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            return (AssessmentList) ois.readObject();
        }
    }
    public void saveListToFile(CourseList list, String filename) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(list);
        }
    }

    public CourseList readCourseListFromFile(String filename) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            return (CourseList) ois.readObject();
        }
    }
    public void saveListToFile(TermList list, String filename) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(list);
        }
    }
    public TermList readTermListFromFile(String filename) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            return (TermList) ois.readObject();
        }
    }
}
