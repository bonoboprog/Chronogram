package it.unicas.service;

import it.unicas.dao.ActivityDAO;
import it.unicas.dao.ActivityTypeDAO;
import it.unicas.dbutil.DBUtil;
import it.unicas.dto.ActivityDTO;
import it.unicas.dto.ActivityTypeDTO;
import it.unicas.service.exception.ServiceException;
import it.unicas.service.exception.ValidationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service per la gestione delle attività.
 * Implementa la logica di business e coordina le operazioni sui DAO.
 */
public class ActivityService {
    private static final Logger logger = LogManager.getLogger(ActivityService.class);

    private final ActivityDAO activityDAO;
    private final ActivityTypeDAO activityTypeDAO;

    public ActivityService() {
        this.activityDAO = new ActivityDAO();
        this.activityTypeDAO = new ActivityTypeDAO();
    }

    /**
     * Crea una nuova attività.
     */
    public ActivityDTO createActivity(ActivityDTO activityData) throws ValidationException, ServiceException {
        logger.info("Creating new activity for user_id: {}", activityData.getUserId());

        validateActivityData(activityData, false);

        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            // Verifica che il tipo di attività esista
            if (!activityTypeDAO.activityTypeExists(activityData.getActivityTypeId(), conn)) {
                throw new ValidationException("Invalid activity type ID: " + activityData.getActivityTypeId());
            }

            // Imposta i timestamp
            Timestamp now = Timestamp.valueOf(LocalDateTime.now());
            activityData.setCreatedAt(now);
            activityData.setUpdatedAt(now);

            // Inserisce l'attività
            int activityId = activityDAO.insertActivity(activityData, conn);
            if (activityId == -1) {
                throw new ServiceException("Failed to create activity");
            }

            // Recupera l'attività completa con le informazioni del tipo
            ActivityDTO createdActivity = activityDAO.getActivityByIdAndUser(activityId, activityData.getUserId(), conn);

            conn.commit();
            logger.info("Activity created successfully with ID: {}", activityId);
            return createdActivity;

        } catch (SQLException e) {
            rollback(conn);
            logger.error("Database error while creating activity for user_id: {}", activityData.getUserId(), e);
            throw new ServiceException("Failed to create activity due to database error", e);
        } catch (Exception e) {
            rollback(conn);
            throw e;
        } finally {
            closeConnection(conn);
        }
    }

    /**
     * Aggiorna un'attività esistente.
     */
    public ActivityDTO updateActivity(Integer activityId, ActivityDTO activityData) throws ValidationException, ServiceException {
        logger.info("Updating activity with ID: {} for user_id: {}", activityId, activityData.getUserId());

        activityData.setActivityId(activityId);
        validateActivityData(activityData, true);

        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            // Verifica che l'attività esista e appartenga all'utente
            ActivityDTO existingActivity = activityDAO.getActivityByIdAndUser(activityId, activityData.getUserId(), conn);
            if (existingActivity == null) {
                throw new ValidationException("Activity not found or access denied");
            }

            // Verifica che il tipo di attività esista (se specificato)
            if (activityData.getActivityTypeId() != null &&
                    !activityTypeDAO.activityTypeExists(activityData.getActivityTypeId(), conn)) {
                throw new ValidationException("Invalid activity type ID: " + activityData.getActivityTypeId());
            }

            // Imposta il timestamp di aggiornamento
            activityData.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

            // Aggiorna l'attività
            boolean updated = activityDAO.updateActivity(activityData, conn);
            if (!updated) {
                throw new ServiceException("Failed to update activity");
            }

            // Recupera l'attività aggiornata
            ActivityDTO updatedActivity = activityDAO.getActivityByIdAndUser(activityId, activityData.getUserId(), conn);

            conn.commit();
            logger.info("Activity updated successfully with ID: {}", activityId);
            return updatedActivity;

        } catch (SQLException e) {
            rollback(conn);
            logger.error("Database error while updating activity with ID: {}", activityId, e);
            throw new ServiceException("Failed to update activity due to database error", e);
        } catch (Exception e) {
            rollback(conn);
            throw e;
        } finally {
            closeConnection(conn);
        }
    }

    /**
     * Elimina un'attività.
     */
    public void deleteActivity(Integer activityId, Integer userId) throws ValidationException, ServiceException {
        logger.info("Deleting activity with ID: {} for user_id: {}", activityId, userId);

        if (activityId == null || userId == null) {
            throw new ValidationException("Activity ID and User ID are required");
        }

        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            // Verifica che l'attività esista e appartenga all'utente
            ActivityDTO existingActivity = activityDAO.getActivityByIdAndUser(activityId, userId, conn);
            if (existingActivity == null) {
                throw new ValidationException("Activity not found or access denied");
            }

            // Elimina l'attività
            boolean deleted = activityDAO.deleteActivity(activityId, userId, conn);
            if (!deleted) {
                throw new ServiceException("Failed to delete activity");
            }

            conn.commit();
            logger.info("Activity deleted successfully with ID: {}", activityId);

        } catch (SQLException e) {
            rollback(conn);
            logger.error("Database error while deleting activity with ID: {}", activityId, e);
            throw new ServiceException("Failed to delete activity due to database error", e);
        } catch (Exception e) {
            rollback(conn);
            throw e;
        } finally {
            closeConnection(conn);
        }
    }



    // ======================================================================================
    // METODO DI RECUPERO LISTA: PRENDE LE ATTIVITÀ ODIERNE PER L'UTENTE
    // ======================================================================================

    /**
     * Recupera tutte le attività di un utente per la data odierna.
     * Questo è l'unico metodo necessario per popolare la vista principale del front-end.
     *
     * @param userId L'ID dell'utente per cui recuperare le attività.
     * @return Una lista di ActivityDTO per la data corrente.
     * @throws ValidationException Se l'ID utente è nullo.
     * @throws ServiceException Se si verifica un errore di accesso al database.
     */
    public List<ActivityDTO> getTodaysActivities(Integer userId) throws ValidationException, ServiceException {
        logger.info("Retrieving today's activities for user_id: {}", userId);

        if (userId == null) {
            throw new ValidationException("User ID is required");
        }

        // Calcola la data odierna
        Date today = Date.valueOf(LocalDate.now());

        try (Connection conn = DBUtil.getConnection()) {
            // Chiama direttamente il metodo del DAO con la data odierna
            List<ActivityDTO> activities = activityDAO.getActivitiesByDateAndUser(today, userId, conn);
            logger.info("Retrieved {} activities for today for user_id: {}", activities.size(), userId);
            return activities;
        } catch (SQLException e) {
            logger.error("Database error while retrieving today's activities for user_id: {}", userId, e);
            throw new ServiceException("Failed to retrieve activities due to database error", e);
        }
    }

    /**
     * Valida i dati dell'attività.
     */
    private void validateActivityData(ActivityDTO activity, boolean isUpdate) throws ValidationException {
        if (activity.getUserId() == null) {
            throw new ValidationException("User ID is required");
        }

        if (!isUpdate || activity.getActivityDate() != null) {
            if (activity.getActivityDate() == null) {
                throw new ValidationException("Activity date is required");
            }
        }

        if (!isUpdate || activity.getActivityTypeId() != null) {
            if (activity.getActivityTypeId() == null) {
                throw new ValidationException("Activity type ID is required");
            }
        }

        if (activity.getDurationMins() != null && activity.getDurationMins() < 0) {
            throw new ValidationException("Duration must be non-negative");
        }

        if (activity.getPleasantness() != null && (activity.getPleasantness() < -3 || activity.getPleasantness() > 3)) {
            throw new ValidationException("Pleasantness must be between -3 and 3");
        }

        if (activity.getLocation() != null && activity.getLocation().length() > 100) {
            throw new ValidationException("Location must not exceed 100 characters");
        }

        if (activity.getCostEuro() != null) {
            try {
                double cost = Double.parseDouble(activity.getCostEuro());
                if (cost < 0) {
                    throw new ValidationException("Cost must be non-negative");
                }
            } catch (NumberFormatException e) {
                throw new ValidationException("Invalid cost format");
            }
        }
    }

    /**
     * Esegue il rollback della transazione.
     */
    private void rollback(Connection conn) {
        if (conn != null) {
            try {
                logger.warn("Rolling back transaction");
                conn.rollback();
            } catch (SQLException ex) {
                logger.error("Failed to rollback transaction", ex);
            }
        }
    }

    /**
     * Chiude la connessione al database.
     */
    private void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException ex) {
                logger.error("Failed to close connection", ex);
            }
        }
    }
}