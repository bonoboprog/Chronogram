package it.unicas.action.activity;

import it.unicas.action.BaseAction;
import it.unicas.dto.ActivityDTO;
import it.unicas.service.activity.ListActivitiesService;
import it.unicas.service.exception.ServiceException;
import it.unicas.service.exception.ValidationException;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletRequest;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Struts2 Action for listing activities.
 * Retrieves authenticated user ID from the request and lists activities.
 */
public class ListActivitiesAction extends BaseAction {

    private String activityDate; // Comes from frontend (optional)
    private final ListActivitiesService listActivitiesService = new ListActivitiesService();

    @Override
    public String execute() {
        try {
            // Recupera l'userId autenticato messo nell’interceptor
            HttpServletRequest request = ServletActionContext.getRequest();
            Integer userId = (Integer) request.getAttribute("authenticatedUserId");

            logger.info("Attempting to retrieve activities for user: {} on date: {}", userId, activityDate);

            if (userId == null) {
                throw new ValidationException("Utente non autenticato.");
            }

            // Gestisce la data: se manca, usa oggi
            Date dateToFetch;
            if (activityDate != null && !activityDate.trim().isEmpty()) {
                try {
                    dateToFetch = Date.valueOf(LocalDate.parse(activityDate));
                } catch (DateTimeParseException e) {
                    throw new ValidationException("Formato data non valido. Formato atteso: YYYY-MM-DD.");
                }
            } else {
                dateToFetch = Date.valueOf(LocalDate.now());
            }

            // Chiama il servizio con userId recuperato e data
            List<ActivityDTO> activities = listActivitiesService.getActivitiesByDateAndUser(dateToFetch, userId);
            setSuccess("Attività recuperate con successo", activities);
            logger.info("Recuperate {} attività per utente {} in data {}", activities.size(), userId, dateToFetch);

        } catch (ValidationException e) {
            logger.warn("Errore di validazione: {}", e.getMessage());
            setFailure(e.getMessage());
        } catch (ServiceException e) {
            logger.error("Errore di servizio", e);
            setFailure("Errore durante il recupero delle attività.");
        } catch (Exception e) {
            logger.error("Errore imprevisto", e);
            setFailure("Si è verificato un errore inatteso.");
        }

        return SUCCESS;
    }

    // Setter solo per activityDate
    public void setActivityDate(String activityDate) {
        this.activityDate = activityDate;
    }
}
