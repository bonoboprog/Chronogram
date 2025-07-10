package it.unicas.dto;

import java.sql.Timestamp;

public class UserAuthDTO {
    private int userId;
    private String email;
    private String passwordHash; // Renamed from passwd_hash
    private Timestamp createdAt; // Changed from String to Timestamp
    private Timestamp updatedAt; // New field
    private Timestamp lastLogin;
    private int isActive; // New field (TINYINT(1))
    private int failedLoginAttempts; // New field
    private Timestamp lockedUntil; // New field

    // Constructors
    public UserAuthDTO() {
    }

    public UserAuthDTO(int userId, String email, String passwordHash, Timestamp createdAt,
                       Timestamp updatedAt, Timestamp lastLogin,
                       int isActive, int failedLoginAttempts, Timestamp lockedUntil) {
        this.userId = userId;
        this.email = email;
        this.passwordHash = passwordHash;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.lastLogin = lastLogin;
        this.isActive = isActive;
        this.failedLoginAttempts = failedLoginAttempts;
        this.lockedUntil = lockedUntil;
    }

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() { // Renamed getter
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) { // Renamed setter
        this.passwordHash = passwordHash;
    }

    public Timestamp getCreatedAt() { // Changed return type
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) { // Changed parameter type
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() { // New getter
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) { // New setter
        this.updatedAt = updatedAt;
    }

    public Timestamp getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Timestamp lastLogin) {
        this.lastLogin = lastLogin;
    }

    public int getIsActive() { // New getter
        return isActive;
    }

    public void setIsActive(int isActive) { // New setter
        this.isActive = isActive;
    }

    public int getFailedLoginAttempts() { // New getter
        return failedLoginAttempts;
    }

    public void setFailedLoginAttempts(int failedLoginAttempts) { // New setter
        this.failedLoginAttempts = failedLoginAttempts;
    }

    public Timestamp getLockedUntil() { // New getter
        return lockedUntil;
    }

    public void setLockedUntil(Timestamp lockedUntil) { // New setter
        this.lockedUntil = lockedUntil;
    }

    @Override
    public String toString() {
        return "UserAuthDTO{" +
                "userId=" + userId +
                ", email='" + email + '\'' +
                ", passwordHash=''" + // Updated field name
                ", createdAt=" + createdAt + // Updated field name
                ", updatedAt=" + updatedAt + // New field
                ", lastLogin=" + lastLogin +
                ", isActive=" + isActive + // New field
                ", failedLoginAttempts=" + failedLoginAttempts + // New field
                ", lockedUntil=" + lockedUntil + // New field
                '}';
    }
}
