package it.polimi.wt.parenti.beans;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

public class ExamSession implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private int id;
    private LocalDate date;
    private Course course;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }
}
