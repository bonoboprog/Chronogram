package it.unicas.dto;

import java.sql.Timestamp;

public class UserAuthDTO {
    private int user_id;
    private String email;
    private String password_hash;
    private String created_at;
    private Timestamp last_login;
    private String username;

    // Constructors
    public UserAuthDTO() {
    }

    public UserAuthDTO(int user_id, String email, String password_hash, String created_at,
                       Timestamp last_login, String username) {
        this.user_id = user_id;
        this.email = email;
        this.password_hash = password_hash;
        this.created_at = created_at;
        this.last_login = last_login;
        this.username = username;
    }

    // Getters and Setters
    public int getUserId() {
        return user_id;
    }

    public void setUserId(int user_id) {
        this.user_id = user_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return password_hash;
    }

    public void setPasswordHash(String password_hash) {
        this.password_hash = password_hash;
    }

    public String getCreatedAt() {
        return created_at;
    }

    public void setCreatedAt(String created_at) {
        this.created_at = created_at;
    }

    public Timestamp getLastLogin() {
        return last_login;
    }

    public void setLastLogin(Timestamp last_login) {
        this.last_login = last_login;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "UserAuthDTO{" +
                "user_id=" + user_id +
                ", email='" + email + '\'' +
                ", password_hash='[PROTECTED]'" +
                ", created_at='" + created_at + '\'' +
                ", last_login=" + last_login +
                ", username='" + username + '\'' +
                '}';
    }
}