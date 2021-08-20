package it.polimi.wt.parenti.beans;

import java.io.Serial;
import java.io.Serializable;

public class Student implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private int id;
    private int matriculation;
    private String name;
    private String surname;
    private String email;
    private String bachelorCourse;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMatriculation() {
        return matriculation;
    }

    public void setMatriculation(int matriculation) {
        this.matriculation = matriculation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBachelorCourse() {
        return bachelorCourse;
    }

    public void setBachelorCourse(String bachelorCourse) {
        this.bachelorCourse = bachelorCourse;
    }
}
