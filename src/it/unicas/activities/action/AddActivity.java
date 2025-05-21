package it.unicas.activities.action;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.opensymphony.xwork2.ActionSupport;

import it.unicas.activities.dao.ActivityManagementDAO;
import it.unicas.activities.pojo.Activity;

public class AddActivity extends ActionSupport {

    private Integer activityId;
    private Integer userId;
    private Integer activityTypeId;
    private Date activityDate;
    private String startTime;
    private Integer durationMinutes;
    private Integer pleasantness;
    private String location;
    private Float costEur;

    public String execute() {
        String statusCode = "";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String activityDateStr = formatter.format(activityDate);
        Activity activity = new Activity(activityId, userId, activityTypeId, activityDateStr, startTime, durationMinutes, pleasantness, location, costEur);
        int recordAdded = ActivityManagementDAO.addActivity(activity);
        if (recordAdded == 1) {
            statusCode = SUCCESS;
        } else {
            statusCode = ERROR;
        }
        return statusCode;
    }

    // Getters and Setters
    public Integer getActivityId() {
        return activityId;
    }

    public void setActivityId(Integer activityId) {
        this.activityId = activityId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getActivityTypeId() {
        return activityTypeId;
    }

    public void setActivityTypeId(Integer activityTypeId) {
        this.activityTypeId = activityTypeId;
    }

    public Date getActivityDate() {
        return activityDate;
    }

    public void setActivityDate(Date activityDate) {
        this.activityDate = activityDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Integer getPleasantness() {
        return pleasantness;
    }

    public void setPleasantness(Integer pleasantness) {
        this.pleasantness = pleasantness;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Float getCostEur() {
        return costEur;
    }

    public void setCostEur(Float costEur) {
        this.costEur = costEur;
    }
}