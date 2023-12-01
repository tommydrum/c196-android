package me.t8d.c196.models;

import java.util.ArrayList;
import java.util.Date;
import java.io.Serializable;
import java.util.Objects;

public class Assessment implements Serializable {
    private static final long serialVersionUID = 1L;
    public enum Type {
        Performance,
        Objective
    }
    private Type type;
    private String title;
    private Date endDate;
    public Assessment(Type type, String title, Date endDate) {
        this.type = type;
        this.title = title;
        this.endDate = endDate;
    }
    public Type GetTypeEnum() {
        return this.type;
    }
    public String GetTitle() {
        return this.title;
    }
    public Date GetEndDate() {
        return this.endDate;
    }
    public void SetTypeEnum(Type type) {
        this.type = type;
    }
    public void SetTitle(String title) {
        this.title = title;
    }
    public void SetEndDate(Date date) {
        this.endDate = date;
    }
    @Override
    public int hashCode() {
        return Objects.hash(title, endDate, type);
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Assessment that = (Assessment) o;
        return Objects.equals(title, that.title) &&
                Objects.equals(endDate, that.endDate) &&
                type == that.type;
    }
}
