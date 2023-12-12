package me.t8d.c196.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class AssessmentList implements Serializable {
    private ArrayList<Assessment> assessmentList;
    public AssessmentList(ArrayList<Assessment> list) {
        assessmentList = list;
    }
    public void addAssessment(Assessment assessment) {
        assessmentList.add(assessment);
    }
    public void removeAssessment(Assessment assessment) {
        assessmentList.remove(assessment);
    }
    public ArrayList<Assessment> GetAssessmentList() {
        return assessmentList;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AssessmentList that = (AssessmentList) o;
        return Objects.equals(assessmentList, that.assessmentList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(assessmentList);
    }

    public AssessmentList GetUpcomingAssessments() {
        ArrayList<Assessment> upcomingAssessments = new ArrayList<>();
        for (Assessment assessment : assessmentList) {
            // if assessment is within 7 days, add to list
            if (assessment.GetEndDate().getTime() - System.currentTimeMillis() <= 604800000) {
                upcomingAssessments.add(assessment);
            }
        }
        return new AssessmentList(upcomingAssessments);
    }
}
