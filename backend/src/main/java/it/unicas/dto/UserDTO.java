package it.unicas.dto;

import java.sql.Date;
import java.sql.Timestamp; // Import for new Timestamp fields

public class UserDTO {
    private int userId;
    private String weeklyIncome;
    private String weeklyIncomeOther;
    private String weeklyHomeCost;
    private String notes;
    private String gender;
    private Date birthday;
    private String address;
    private Timestamp createdAt; // New field
    private Timestamp updatedAt; // New field

    // Constructor
    public UserDTO() {
    }

    public UserDTO(int userId, String weeklyIncome, String weeklyIncomeOther, String weeklyHomeCost,
                   String notes, String gender, Date birthday, String address, Timestamp createdAt, Timestamp updatedAt) { // Updated constructor
        this.userId = userId;
        this.weeklyIncome = weeklyIncome;
        this.weeklyIncomeOther = weeklyIncomeOther;
        this.weeklyHomeCost = weeklyHomeCost;
        this.notes = notes;
        this.gender = gender;
        this.birthday = birthday;
        this.address = address;
        this.createdAt = createdAt; // Set new field
        this.updatedAt = updatedAt; // Set new field
    }

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getWeeklyIncome() {
        return weeklyIncome;
    }

    public void setWeeklyIncome(String weeklyIncome) {
        this.weeklyIncome = weeklyIncome;
    }

    public String getWeeklyIncomeOther() {
        return weeklyIncomeOther;
    }

    public void setWeeklyIncomeOther(String weeklyIncomeOther) {
        this.weeklyIncomeOther = weeklyIncomeOther;
    }

    public String getWeeklyHomeCost() {
        return weeklyHomeCost;
    }

    public void setWeeklyHomeCost(String weeklyHomeCost) {
        this.weeklyHomeCost = weeklyHomeCost;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Timestamp getCreatedAt() { // New getter
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) { // New setter
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() { // New getter
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) { // New setter
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", weeklyIncome='" + weeklyIncome + '\'' +
                ", weeklyIncomeOther='" + weeklyIncomeOther + '\'' +
                ", weeklyHomeCost='" + weeklyHomeCost + '\'' +
                ", notes='" + notes + '\'' +
                ", gender='" + gender + '\'' +
                ", birthday=" + birthday +
                ", address='" + address + '\'' +
                ", createdAt=" + createdAt + // New field
                ", updatedAt=" + updatedAt + // New field
                '}';
    }
}
