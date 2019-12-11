package com.example.coworkandroid.model;

public class User {
    String uuidUser;
    String name;
    String surname;
    String mail;

    public User(String uuidUser, String name, String surname, String mail) {
        this.uuidUser = uuidUser;
        this.name = name;
        this.surname = surname;
        this.mail = mail;
    }

    public String getUuidUser() {
        return uuidUser;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getMail() {
        return mail;
    }
}
