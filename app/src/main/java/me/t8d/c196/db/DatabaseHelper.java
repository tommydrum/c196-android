package me.t8d.c196.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import me.t8d.c196.models.AssessmentList;
import me.t8d.c196.models.CourseList;
import me.t8d.c196.models.TermList;

public class DatabaseHelper {
    public void saveListToFile(AssessmentList list, String filename, File pathname) throws IOException {
        File file = new File(pathname, filename);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(list);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public AssessmentList readAssessmentListFromFile(String filename, File pathname) throws IOException, ClassNotFoundException {
        File file = new File(pathname, filename);
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (AssessmentList) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    public void saveListToFile(CourseList list, String filename, File pathname) throws IOException {
        File file = new File(pathname, filename);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(list);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public CourseList readCourseListFromFile(String filename, File pathname) throws IOException, ClassNotFoundException {
        File file = new File(pathname, filename);
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (CourseList) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    public void saveListToFile(TermList list, String filename, File pathname) throws IOException {
        File file = new File(pathname, filename);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(list);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    public TermList readTermListFromFile(String filename, File pathname) throws IOException, ClassNotFoundException {
        File file = new File(pathname, filename);
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (TermList) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
