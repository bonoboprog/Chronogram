package it.unicas.dto;

import java.sql.Date; // Use java.sql.Date for database DATE types

public class UserDTO {
    private int userId;
    private String username;
    private String email;
    private String weeklyIncome; // Assuming VARCHAR(45) as per DB
    private String weeklyIncomeOther; // Assuming VARCHAR(45) as per DB
    private String weeklyHomeCost; // Assuming VARCHAR(45) as per DB
    private String notes; // Assuming VARCHAR(45) as per DB
    private String gender; // Assuming VARCHAR(45) as per DB
    private Date birthday; // Use java.sql.Date for DATE
    private String address; // Assuming VARCHAR(100) as per DB

    // Constructor
    public UserDTO() {
    }

    public UserDTO(int userId, String weeklyIncome, String weeklyIncomeOther, String weeklyHomeCost,
                   String notes, String gender, Date birthday, String address, String username,
                   String email) {
        this.userId = userId;
        this.weeklyIncome = weeklyIncome;
        this.weeklyIncomeOther = weeklyIncomeOther;
        this.weeklyHomeCost = weeklyHomeCost;
        this.notes = notes;
        this.gender = gender;
        this.birthday = birthday;
        this.address = address;
        this.username = username;
        this.email = email;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", weeklyIncome='" + weeklyIncome + '\'' +
                ", weeklyIncomeOther='" + weeklyIncomeOther + '\'' +
                ", weeklyHomeCost='" + weeklyHomeCost + '\'' +
                ", notes='" + notes + '\'' +
                ", gender='" + gender + '\'' +
                ", birthday=" + birthday +
                ", address='" + address + '\'' +
                '}';
    }
}