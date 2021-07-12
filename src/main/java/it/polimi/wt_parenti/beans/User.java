package it.polimi.wt_parenti.beans;

import it.polimi.wt_parenti.utils.enumerations.UserRole;

public class User {
    private int id;
    private String username;
    private UserRole role;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole userRole) {
        this.role = userRole;
    }
}
