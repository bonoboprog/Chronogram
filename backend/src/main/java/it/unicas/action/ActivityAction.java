package it.unicas.action;

import com.opensymphony.xwork2.ActionSupport;
import it.unicas.dto.ActivityDTO;
import it.unicas.dto.ActivityTypeDTO;
import it.unicas.service.ActivityService;
import it.unicas.service.exception.ServiceException;
import it.unicas.service.exception.ValidationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Action per la gestione delle attività.
 * Gestisce tutte le operazioni CRUD per le attività usando approccio dinamico.
 */
public class ActivityAction extends ActionSupport {
    private static final Logger logger = LogManager.getLogger(ActivityAction.class);

    private final ActivityService activityService = new ActivityService();

    // --- INPUT PARAMETERS ---
    private Integer activityId;
    private String activityDate;
    private String startDate;
    private String endDate;
    private Integer durationMins;
    private Integer pleasantness;
    private String location;
    private String costEuro;
    private Integer userId;
    private Integer activityTypeId;

    // --- OUTPUT WRAPPER ---
    private ActivityResponse activityResponse;

    /**
     * Crea una nuova attività.
     */
    public String create() {
        logger.info("Creating new activity for user_id: {}", userId);

        try {
            ActivityDTO activityData = buildActivityDTOFromInput();
            ActivityDTO createdActivity = activityService.createActivity(activityData);

            setSuccess("Activity created successfully", createdActivity);
            logger.info("Activity created successfully with ID: {}", createdActivity.getActivityId());

        } catch (ValidationException e) {
            logger.warn("Validation error while creating activity: {}", e.getMessage());
            setFailure(e.getMessage());
        } catch (ServiceException e) {
            logger.error("Service error while creating activity", e);
            setFailure("Failed to create activity due to system error");
        } catch (Exception e) {
            logger.error("Unexpected error while creating activity", e);
            setFailure("An unexpected error occurred");
        }

        return SUCCESS;
    }

    /**
     * Aggiorna un'attività esistente.
     */
    public String update() {
        logger.info("Updating activity with ID: {} for user_id: {}", activityId, userId);

        try {
            if (activityId == null) {
                throw new ValidationException("Activity ID is required for update");
            }

            ActivityDTO activityData = buildActivityDTOFromInput();
            ActivityDTO updatedActivity = activityService.updateActivity(activityId, activityData);

            setSuccess("Activity updated successfully", updatedActivity);
            logger.info("Activity updated successfully with ID: {}", activityId);

        } catch (ValidationException e) {
            logger.warn("Validation error while updating activity: {}", e.getMessage());
            setFailure(e.getMessage());
        } catch (ServiceException e) {
            logger.error("Service error while updating activity", e);
            setFailure("Failed to update activity due to system error");
        } catch (Exception e) {
            logger.error("Unexpected error while updating activity", e);
            setFailure("An unexpected error occurred");
        }

        return SUCCESS;
    }

    /**
     * Elimina un'attività.
     */
    public String delete() {
        logger.info("Deleting activity with ID: {} for user_id: {}", activityId, userId);

        try {
            if (activityId == null || userId == null) {
                throw new ValidationException("Activity ID and User ID are required");
            }

            activityService.deleteActivity(activityId, userId);

            setSuccess("Activity deleted successfully", null);
            logger.info("Activity deleted successfully with ID: {}", activityId);

        } catch (ValidationException e) {
            logger.warn("Validation error while deleting activity: {}", e.getMessage());
            setFailure(e.getMessage());
        } catch (ServiceException e) {
            logger.error("Service error while deleting activity", e);
            setFailure("Failed to delete activity due to system error");
        } catch (Exception e) {
            logger.error("Unexpected error while deleting activity", e);
            setFailure("An unexpected error occurred");
        }

        return SUCCESS;
    }

    /**
     * Recupera le attività per data o range di date.
     */
    public String list() {
        logger.info("Retrieving activities for user_id: {}", userId);

        try {
            if (userId == null) {
                throw new ValidationException("User ID is required");
            }

            List<ActivityDTO> activities;

            if (startDate != null && endDate != null) {
                // Range di date
                Date start = parseDate(startDate);
                Date end = parseDate(endDate);
                activities = activityService.getActivitiesByDateRange(start, end, userId);
                logger.info("Retrieved {} activities for date range: {} to {}", activities.size(), startDate, endDate);
            } else if (activityDate != null) {
                // Data singola
                Date date = parseDate(activityDate);
                activities = activityService.getActivitiesByDate(date, userId);
                logger.info("Retrieved {} activities for date: {}", activities.size(), activityDate);
            } else {
                // Data odierna se non specificata
                Date today = new Date(System.currentTimeMillis());
                activities = activityService.getActivitiesByDate(today, userId);
                logger.info("Retrieved {} activities for today", activities.size());
            }

            setSuccessList("Activities retrieved successfully", activities);

        } catch (ValidationException e) {
            logger.warn("Validation error while retrieving activities: {}", e.getMessage());
            setFailure(e.getMessage());
        } catch (ServiceException e) {
            logger.error("Service error while retrieving activities", e);
            setFailure("Failed to retrieve activities due to system error");
        } catch (Exception e) {
            logger.error("Unexpected error while retrieving activities", e);
            setFailure("An unexpected error occurred");
        }

        return SUCCESS;
    }

    /**
     * Recupera una singola attività per ID.
     */
    public String get() {
        logger.info("Retrieving activity with ID: {} for user_id: {}", activityId, userId);

        try {
            if (activityId == null || userId == null) {
                throw new ValidationException("Activity ID and User ID are required");
            }

            ActivityDTO activity = activityService.getActivityById(activityId, userId);

            setSuccess("Activity retrieved successfully", activity);
            logger.info("Activity retrieved successfully with ID: {}", activityId);

        } catch (ValidationException e) {
            logger.warn("Validation error while retrieving activity: {}", e.getMessage());
            setFailure(e.getMessage());
        } catch (ServiceException e) {
            logger.error("Service error while retrieving activity", e);
            setFailure("Failed to retrieve activity due to system error");
        } catch (Exception e) {
            logger.error("Unexpected error while retrieving activity", e);
            setFailure("An unexpected error occurred");
        }

        return SUCCESS;
    }

    /**
     * Recupera tutti i tipi di attività disponibili.
     */
    public String types() {
        logger.info("Retrieving all activity types");

        try {
            List<ActivityTypeDTO> activityTypes = activityService.getAllActivityTypes();

            setSuccessActivityTypes("Activity types retrieved successfully", activityTypes);
            logger.info("Retrieved {} activity types", activityTypes.size());

        } catch (ServiceException e) {
            logger.error("Service error while retrieving activity types", e);
            setFailure("Failed to retrieve activity types due to system error");
        } catch (Exception e) {
            logger.error("Unexpected error while retrieving activity types", e);
            setFailure("An unexpected error occurred");
        }

        return SUCCESS;
    }

    // --- HELPER METHODS ---

    /**
     * Costruisce un ActivityDTO dai parametri di input.
     */
    private ActivityDTO buildActivityDTOFromInput() throws ValidationException {
        ActivityDTO activity = new ActivityDTO();
        activity.setUserId(userId);
        activity.setActivityTypeId(activityTypeId);
        activity.setDurationMins(durationMins);
        activity.setPleasantness(pleasantness);
        activity.setLocation(location);
        activity.setCostEuro(costEuro);

        if (activityDate != null) {
            activity.setActivityDate(parseDate(activityDate));
        }

        return activity;
    }

    /**
     * Converte una stringa in Date.
     */
    private Date parseDate(String dateString) throws ValidationException {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            formatter.setLenient(false);
            return new Date(formatter.parse(dateString).getTime());
        } catch (ParseException e) {
            throw new ValidationException("Invalid date format. Use yyyy-MM-dd");
        }
    }

    /**
     * Imposta una risposta di successo con singola attività.
     */
    private void setSuccess(String message, ActivityDTO activity) {
        this.activityResponse = new ActivityResponse(true, message, activity, null, null);
    }

    /**
     * Imposta una risposta di successo con lista di attività.
     */
    private void setSuccessList(String message, List<ActivityDTO> activities) {
        this.activityResponse = new ActivityResponse(true, message, null, activities, null);
    }

    /**
     * Imposta una risposta di successo con lista di tipi di attività.
     */
    private void setSuccessActivityTypes(String message, List<ActivityTypeDTO> activityTypes) {
        this.activityResponse = new ActivityResponse(true, message, null, null, activityTypes);
    }

    /**
     * Imposta una risposta di errore.
     */
    private void setFailure(String message) {
        this.activityResponse = new ActivityResponse(false, message, null, null, null);
    }

    // --- DTO INTERNO PER LA RISPOSTA JSON ---
    public static class ActivityResponse {
        private final boolean success;
        private final String message;
        private final ActivityDTO activity;
        private final List<ActivityDTO> activities;
        private final List<ActivityTypeDTO> activityTypes;

        public ActivityResponse(boolean success, String message, ActivityDTO activity,
                                List<ActivityDTO> activities, List<ActivityTypeDTO> activityTypes) {
            this.success = success;
            this.message = message;
            this.activity = activity;
            this.activities = activities;
            this.activityTypes = activityTypes;
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public ActivityDTO getActivity() { return activity; }
        public List<ActivityDTO> getActivities() { return activities; }
        public List<ActivityTypeDTO> getActivityTypes() { return activityTypes; }
    }

    // --- GETTERS E SETTERS PER STRUTS ---
    public ActivityResponse getActivityResponse() { return activityResponse; }

    public void setActivityId(Integer activityId) { this.activityId = activityId; }
    public void setActivityDate(String activityDate) { this.activityDate = activityDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
    public void setDurationMins(Integer durationMins) { this.durationMins = durationMins; }
    public void setPleasantness(Integer pleasantness) { this.pleasantness = pleasantness; }
    public void setLocation(String location) { this.location = location; }
    public void setCostEuro(String costEuro) { this.costEuro = costEuro; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public void setActivityTypeId(Integer activityTypeId) { this.activityTypeId = activityTypeId; }
}