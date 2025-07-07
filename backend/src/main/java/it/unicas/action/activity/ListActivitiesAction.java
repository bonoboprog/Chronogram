package it.unicas.action.activity;

import it.unicas.action.BaseAction;
import it.unicas.dto.ActivityDTO;
import it.unicas.service.activity.ListActivitiesService;
import it.unicas.service.exception.ServiceException;
import it.unicas.service.exception.ValidationException;

import java.util.List;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeParseException; // Import for parsing exception

/**
 * Struts2 Action for listing activities.
 * Handles input parameters and delegates to ListActivitiesService.
 */
public class ListActivitiesAction extends BaseAction {

    // --- INPUT PARAMETERS ---
    private Integer userId;
    private String activityDate; // Assuming it comes as a string from frontend

    // Service dependency
    private final ListActivitiesService listActivitiesService = new ListActivitiesService();

    @Override
    public String execute() {
        logger.info("Attempting to retrieve activities for user: {} on date: {}", userId, activityDate);
        try {
            if (userId == null) {
                throw new ValidationException("User ID is required to list activities.");
            }

            // Parse the activityDate string to java.sql.Date
            Date dateToFetch;
            if (activityDate!= null &&!activityDate.trim().isEmpty()) {
                try {
                    dateToFetch = Date.valueOf(LocalDate.parse(activityDate));
                } catch (DateTimeParseException e) { // Use specific DateTimeParseException
                    throw new ValidationException("Invalid activity date format. Expected YYYY-MM-DD.");
                }
            } else {
                dateToFetch = Date.valueOf(LocalDate.now()); // Default to today if not provided
            }

            // CORRECTED LINE: Call the new method with both date and userId
            List<ActivityDTO> activities = listActivitiesService.getActivitiesByDateAndUser(dateToFetch, userId);
            setSuccess("Activities retrieved successfully", activities);
            logger.info("Retrieved {} activities for user: {} on date: {}", activities.size(), userId, dateToFetch);
        } catch (ValidationException e) {
            logger.warn("Validation error during activity listing: {}", e.getMessage());
            setFailure(e.getMessage());
        } catch (ServiceException e) {
            logger.error("Service error during activity listing", e);
            setFailure("Failed to retrieve activities due to a system error.");
        } catch (Exception e) {
            logger.error("Unexpected error during activity listing", e);
            setFailure("An unexpected error occurred.");
        }
        return SUCCESS;
    }

    // --- Getters and setters for Struts2 input binding ---
    public void setUserId(Integer userId) { this.userId = userId; }
    public void setActivityDate(String activityDate) { this.activityDate = activityDate; }
}