package it.polimi.wt_parenti.beans;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ExamReport implements Serializable {
    private int id;
    private LocalDateTime creation;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getCreation() {
        return creation;
    }

    public void setCreation(LocalDateTime creation) {
        this.creation = creation;
    }
}
