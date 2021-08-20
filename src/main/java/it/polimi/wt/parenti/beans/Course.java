package it.polimi.wt.parenti.beans;

import java.io.Serial;
import java.io.Serializable;

public class Course implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private int id;
    private String code;
    private String name;
    private Professor professor;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Professor getProfessor() {
        return professor;
    }

    public void setProfessor(Professor professor) {
        this.professor = professor;
    }
}
