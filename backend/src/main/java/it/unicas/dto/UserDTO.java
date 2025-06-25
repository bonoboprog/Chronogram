package it.unicas.dto;

import java.sql.Date;
import java.sql.Timestamp;

public class UserDTO {
    private int userId;
    private String name;          // New field
    private String surname;       // New field
    private String phone;         // New field
    private String weeklyIncome;
    private String weeklyIncomeOther;
    private String weeklyHomeCost;
    private String notes;
    private String gender;
    private Date birthday;
    private String address;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Constructor
    public UserDTO() {
    }

    // Updated constructor with new fields
    public UserDTO(int userId, String name, String surname, String phone,
                   String weeklyIncome, String weeklyIncomeOther, String weeklyHomeCost,
                   String notes, String gender, Date birthday, String address,
                   Timestamp createdAt, Timestamp updatedAt) {
        this.userId = userId;
        this.name = name;
        this.surname = surname;
        this.phone = phone;
        this.weeklyIncome = weeklyIncome;
        this.weeklyIncomeOther = weeklyIncomeOther;
        this.weeklyHomeCost = weeklyHomeCost;
        this.notes = notes;
        this.gender = gender;
        this.birthday = birthday;
        this.address = address;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "userId=" + userId +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", phone='" + phone + '\'' +
                ", weeklyIncome='" + weeklyIncome + '\'' +
                ", weeklyIncomeOther='" + weeklyIncomeOther + '\'' +
                ", weeklyHomeCost='" + weeklyHomeCost + '\'' +
                ", notes='" + notes + '\'' +
                ", gender='" + gender + '\'' +
                ", birthday=" + birthday +
                ", address='" + address + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}