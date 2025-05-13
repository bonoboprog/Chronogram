package it.unicas.products.action;

import com.opensymphony.xwork2.ActionSupport;

import it.unicas.products.dao.ActivityManagementDAO;
import it.unicas.products.pojo.Activity;

public class UpdateActivityData extends ActionSupport {

    private Integer activityId;
    private Integer userId;
    private Integer activityTypeId;
    private String activityDate;
    private String startTime;
    private Integer durationMinutes;
    private Integer pleasantness;
    private String location;
    private Float costEur;

    public String execute() {
        Activity activity = ActivityManagementDAO.getActivityById(activityId);
        activityId = activity.getActivityId();
        userId = activity.getUserId();
        activityTypeId = activity.getActivityTypeId();
        activityDate = activity.getActivityDate();
        startTime = activity.getStartTime();
        durationMinutes = activity.getDurationMinutes();
        pleasantness = activity.getPleasantness();
        location = activity.getLocation();
        costEur = activity.getCostEur();
        return "success";
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

    public String getActivityDate() {
        return activityDate;
    }

    public void setActivityDate(String activityDate) {
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