package me.t8d.c196.models;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class Term implements Serializable {
    private static final long serialVersionUID = 1L;
    private CourseList courseList;
    private Date startDate;
    private Date endDate;
    private String termName;
    public Term(Date start, Date end, String termName, CourseList list) {
        this.courseList = list;
        this.startDate = start;
        this.endDate = end;
        this.termName = termName;
    }
    public CourseList GetCourseList() {
        return courseList;
    }
    public void AddCourse(Course course) {
        courseList.AddCourse(course);
    }
    public void RemoveCourse(Course course) {
        courseList.RemoveCourse(course);
    }
    public Date GetStartDate() {
        return this.startDate;
    }
    public void SetStartDate(Date startDate) {
        this.startDate = startDate;
    }
    public Date GetEndDate() {
        return this.endDate;
    }
    public void SetEndDate(Date endDate) {
        this.endDate = endDate;
    }
    public String GetTermName() {
        return this.termName;
    }
    public void SetTermName(String termName) {
        this.termName = termName;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Term term = (Term) o;
        return Objects.equals(courseList, term.courseList) &&
                Objects.equals(startDate, term.startDate) &&
                Objects.equals(endDate, term.endDate) &&
                Objects.equals(termName, term.termName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseList, startDate, endDate, termName);
    }
}
