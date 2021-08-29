package it.polimi.wt.parenti.beans;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

public class ExamReport implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private int id;
    private String creation;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCreation() {
        return creation;
    }

    public void setCreation(LocalDateTime creation) {
        this.creation = creation.toString();
    }
}
