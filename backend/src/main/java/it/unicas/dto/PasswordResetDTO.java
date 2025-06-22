package it.unicas.dto;

import java.sql.Timestamp;

public class PasswordResetDTO {
    private int userId;
    private int token_id;
    private String token_hash;
    private Timestamp expirationTime;
    private Timestamp created_at;

    public PasswordResetDTO() {
    }

    public PasswordResetDTO(int userId, int token_id, String token_hash, Timestamp esxpirationTime, Timestamp created_at) {
        this.userId = userId;
        this.token_id = token_id;
        this.token_hash = token_hash;
        this.expirationTime = esxpirationTime;
        this.created_at = created_at;
    }

    public int getUserId() {return userId;}
    public int getToken_id() {return token_id;}
    public String getToken_hash() {return token_hash;}
    public Timestamp getExpirationTime() {return expirationTime;}
    public Timestamp getCreated_at() {return created_at;}

    public void setUserId(int userId) {this.userId = userId;}
    public void setToken_id(int token_id) {this.token_id = token_id;}
    public void setToken_hash(String token_hash) {this.token_hash = token_hash;}
    public void setExpirationTime(Timestamp expirationTime) {this.expirationTime = expirationTime;}
    public void setCreated_at(Timestamp created_at) {this.created_at = created_at;}

    @Override
    public String toString() {
        return "PasswordResetDTO{" +
                "userId=" + userId +
                ", token_id=" + token_id +
                ", token_hash=" + token_hash +
                ", expirationTime=" + expirationTime +
                ", created_at=" + created_at +
                "}";
    }




}

