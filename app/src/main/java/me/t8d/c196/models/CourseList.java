package me.t8d.c196.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class CourseList implements Serializable {
    private static final long serialVersionUID = 1L;
    private ArrayList<Course> courseList;
    public CourseList(ArrayList<Course> list) {
        courseList = list;
    }
    public void AddCourse(Course course) {
        courseList.add(course);
    }
    public void RemoveCourse(Course course) {
        courseList.remove(course);
    }
    public ArrayList<Course> GetCourseList() {
        return courseList;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CourseList that = (CourseList) o;
        return Objects.equals(courseList, that.courseList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseList);
    }
}
