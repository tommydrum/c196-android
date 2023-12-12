package me.t8d.c196.models;

import android.util.Pair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class Course implements Serializable {
    private static final long serialVersionUID = 1L;
    private String title;
    private String notes;
    private Date startDate;
    private Date endDate;
    private AssessmentList assessmentList;

    private int notificationId = 0;
    public int GetNotificationId() {
        return this.notificationId;
    }
    public void SetNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public void SetNotes(String courseNote) {
        this.notes = courseNote;
    }

    public void SetStatus(Status selectedStatus) {
        this.status = selectedStatus;
    }

    public void SetTitle(String courseTitle) {
        this.title = courseTitle;
    }

    public void SetStartDate(Date parse) {
        this.startDate = parse;
    }

    public void SetEndDate(Date parse) {
        this.endDate = parse;
    }

    public void SetAssessmentList(AssessmentList assessmentList) {
        this.assessmentList = assessmentList;
    }

    public enum Status {
        IN_PROGRESS, COMPLETED, DROPPED, PLANNED
    };
    private Status status;
    public class Instructor implements Serializable {
        private static final long serialVersionUID = 1L;
        public Instructor(String firstName, String lastName, String phone, String email) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.phone = phone;
            this.email = email;
        }
        private String firstName;
        private String lastName;
        private String phone;
        private String email;
        public String GetFirstName() {
            return this.firstName;
        }
        public void SetFirstName(String fName) {
            this.firstName = fName;
        }
        public String GetLastName() {
            return this.lastName;
        }
        public void SetLastName(String lName) {
            this.lastName = lName;
        }
        public String GetPhone() {
            return this.phone;
        }
        public void SetPhone(String phone) {
            this.phone = phone;
        }
        public String GetEmail() {
            return this.email;
        }
        public void SetEmail(String email) {
            this.email = email;
        }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Instructor that = (Instructor) o;
            return Objects.equals(firstName, that.firstName) &&
                    Objects.equals(lastName, that.lastName) &&
                    Objects.equals(phone, that.phone) &&
                    Objects.equals(email, that.email);
        }

        @Override
        public int hashCode() {
            return Objects.hash(firstName, lastName, phone, email);
        }
    };
    private Instructor instructor;
    public Course(String title, Date startDate, Date endDate, Status status, String notes, AssessmentList list, String fName, String lName, String phone, String email) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.instructor = new Instructor(fName, lName, phone, email);
        this.notes = notes; //requires one note for creation, the true marks it as required (non-optional)
        this.assessmentList = list;
    }
    public void addAssessment(Assessment assessment) {
        assessmentList.addAssessment(assessment);
    }
    public void removeAssessment(Assessment assessment) {
        assessmentList.removeAssessment(assessment);
    }
    public AssessmentList GetAssessmentList() {
        return this.assessmentList;
    }
    public String GetTitle() {
        return this.title;
    }
    public Date GetStartDate() {
        return this.startDate;
    }
    public Date GetEndDate() {
        return this.endDate;
    }
    public Status GetStatus() {
        return this.status;
    }
    public Instructor GetInstructor() {
        return this.instructor;
    }
    public String GetNotes() {
        return notes;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return Objects.equals(title, course.title) &&
                Objects.equals(notes, course.notes) &&
                Objects.equals(startDate, course.startDate) &&
                Objects.equals(endDate, course.endDate) &&
                Objects.equals(assessmentList, course.assessmentList) &&
                status == course.status &&
                Objects.equals(instructor, course.instructor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, notes, startDate, endDate, assessmentList, status, instructor);
    }
}
