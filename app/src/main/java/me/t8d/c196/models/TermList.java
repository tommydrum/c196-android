package me.t8d.c196.models;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class TermList implements Serializable {
    private static final long serialVersionUID = 1L;
    private ArrayList<Term> termList;
    public TermList (ArrayList<Term> list) {
        termList = list;
    }
    public void AddTerm(Term term) {
        termList.add(term);
    }
    public void RemoveTerm(Term term) {
        termList.remove(term);
    }
    public ArrayList<Term> GetTermList() {
        return termList;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TermList that = (TermList) o;
        return Objects.equals(termList, that.termList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(termList);
    }

    public Term GetCurrentTerm() {
        for (Term term : termList
             ) {
            if (term.GetStartDate().compareTo(new Date()) <= 0 && term.GetEndDate().compareTo(new Date()) >= 0)
                return term;
        }
        return null;
    }
}
