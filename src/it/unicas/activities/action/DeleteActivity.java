package it.unicas.products.action;

import com.opensymphony.xwork2.ActionSupport;

import it.unicas.products.dao.ActivityManagementDAO;

public class DeleteActivity extends ActionSupport {

    private Integer activityId;

    public String execute() {
        String statusCode = "";
        int recordDeleted = ActivityManagementDAO.deleteActivity(activityId);
        if (recordDeleted == 1) {
            statusCode = "success";
        } else {
            statusCode = "error";
        }
        return statusCode;
    }

    public Integer getActivityId() {
        return activityId;
    }

    public void setActivityId(Integer activityId) {
        this.activityId = activityId;
    }
}