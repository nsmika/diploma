package ru.netology.model;


import jakarta.persistence.*;

@Entity
@Table(name = "clients", schema = "cloud")
public class User {

    @Id
    @Column(name = "login", unique = true, nullable = false)
    private String login;

    @Column(name = "password", nullable = false)
    private String password;

    public String getLogin() {
        return login;
    }

    public void setLogin(String email) {
        this.login = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
