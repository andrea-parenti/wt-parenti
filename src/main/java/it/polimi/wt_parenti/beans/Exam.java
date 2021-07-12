package it.polimi.wt_parenti.beans;

import it.polimi.wt_parenti.utils.enumerations.ExamResult;
import it.polimi.wt_parenti.utils.enumerations.ExamStatus;

import java.io.Serial;
import java.io.Serializable;

public class Exam implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private int id;
    private ExamSession examSession;
    private Student student;
    private ExamStatus status;
    private ExamResult result;
    private int grade;
    private boolean laude;
    private ExamReport report;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ExamSession getExamSession() {
        return examSession;
    }

    public void setExamSession(ExamSession examSession) {
        this.examSession = examSession;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public ExamStatus getStatus() {
        return status;
    }

    public void setStatus(ExamStatus status) {
        this.status = status;
    }

    public ExamResult getResult() {
        return result;
    }

    public void setResult(ExamResult result) {
        this.result = result;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public boolean isLaude() {
        return laude;
    }

    public void setLaude(boolean laude) {
        this.laude = laude;
    }

    public ExamReport getReport() {
        return report;
    }

    public void setReport(ExamReport report) {
        this.report = report;
    }
}
