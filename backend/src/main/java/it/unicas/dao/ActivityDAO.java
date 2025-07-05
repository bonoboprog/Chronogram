package it.unicas.dao;

import it.unicas.dto.ActivityDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO per la gestione delle attività nel database.
 * Implementa tutte le operazioni CRUD con logging completo e gestione errori.
 */
public class ActivityDAO {
    private static final Logger logger = LogManager.getLogger(ActivityDAO.class);

    /**
     * Inserisce una nuova attività nel database.
     */
    public int insertActivity(ActivityDTO activity, Connection conn) throws SQLException {
        final String SQL = "INSERT INTO activity (activity_date, duration_mins, pleasantness, " +
                "location, cost_euro, user_id, activity_type_id, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        logger.debug("Inserting new activity for user_id: {}", activity.getUserId());

        try (PreparedStatement pstmt = conn.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setDate(1, activity.getActivityDate());
            pstmt.setInt(2, activity.getDurationMins());
            pstmt.setInt(3, activity.getPleasantness());
            pstmt.setString(4, activity.getLocation());
            pstmt.setString(5, activity.getCostEuro());
            pstmt.setInt(6, activity.getUserId());
            pstmt.setInt(7, activity.getActivityTypeId());
            pstmt.setTimestamp(8, activity.getCreatedAt());
            pstmt.setTimestamp(9, activity.getUpdatedAt());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int activityId = rs.getInt(1);
                        activity.setActivityId(activityId);
                        logger.info("Activity inserted successfully with ID: {}", activityId);
                        return activityId;
                    }
                }
            }
            logger.warn("No rows affected when inserting activity for user_id: {}", activity.getUserId());
            return -1;
        } catch (SQLException e) {
            logger.error("Error inserting activity for user_id: {}", activity.getUserId(), e);
            throw e;
        }
    }

    /**
     * Recupera le attività per data e utente con informazioni del tipo di attività.
     */
    public List<ActivityDTO> getActivitiesByDateAndUser(Date activityDate, Integer userId, Connection conn) throws SQLException {
        final String SQL = "SELECT a.activity_id, a.activity_date, a.duration_mins, a.pleasantness, " +
                "a.location, a.cost_euro, a.user_id, a.activity_type_id, " +
                "a.created_at, a.updated_at, " +
                "at.name as activity_type_name, at.description as activity_type_description, " +
                "at.is_instrumental, at.is_routinary " +
                "FROM activity a " +
                "JOIN activity_type at ON a.activity_type_id = at.activity_type_id " +
                "WHERE a.activity_date = ? AND a.user_id = ? " +
                "ORDER BY a.created_at ASC";

        logger.debug("Retrieving activities for date: {} and user_id: {}", activityDate, userId);

        List<ActivityDTO> activities = new ArrayList<>();
        try (PreparedStatement pstmt = conn.prepareStatement(SQL)) {
            pstmt.setDate(1, activityDate);
            pstmt.setInt(2, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ActivityDTO activity = mapResultSetToActivityDTO(rs);
                    activities.add(activity);
                }
            }
            logger.info("Retrieved {} activities for date: {} and user_id: {}", activities.size(), activityDate, userId);
            return activities;
        } catch (SQLException e) {
            logger.error("Error retrieving activities for date: {} and user_id: {}", activityDate, userId, e);
            throw e;
        }
    }

    /**
     * Recupera una singola attività per ID e utente.
     */
    public ActivityDTO getActivityByIdAndUser(Integer activityId, Integer userId, Connection conn) throws SQLException {
        final String SQL = "SELECT a.activity_id, a.activity_date, a.duration_mins, a.pleasantness, " +
                "a.location, a.cost_euro, a.user_id, a.activity_type_id, " +
                "a.created_at, a.updated_at, " +
                "at.name as activity_type_name, at.description as activity_type_description, " +
                "at.is_instrumental, at.is_routinary " +
                "FROM activity a " +
                "JOIN activity_type at ON a.activity_type_id = at.activity_type_id " +
                "WHERE a.activity_id = ? AND a.user_id = ?";

        logger.debug("Retrieving activity with ID: {} for user_id: {}", activityId, userId);

        try (PreparedStatement pstmt = conn.prepareStatement(SQL)) {
            pstmt.setInt(1, activityId);
            pstmt.setInt(2, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    ActivityDTO activity = mapResultSetToActivityDTO(rs);
                    logger.info("Activity found with ID: {} for user_id: {}", activityId, userId);
                    return activity;
                }
            }
            logger.warn("No activity found with ID: {} for user_id: {}", activityId, userId);
            return null;
        } catch (SQLException e) {
            logger.error("Error retrieving activity with ID: {} for user_id: {}", activityId, userId, e);
            throw e;
        }
    }

    /**
     * Aggiorna un'attività esistente.
     */
    public boolean updateActivity(ActivityDTO activity, Connection conn) throws SQLException {
        final String SQL = "UPDATE activity SET activity_date = ?, duration_mins = ?, pleasantness = ?, " +
                "location = ?, cost_euro = ?, activity_type_id = ?, updated_at = ? " +
                "WHERE activity_id = ? AND user_id = ?";

        logger.debug("Updating activity with ID: {} for user_id: {}", activity.getActivityId(), activity.getUserId());

        try (PreparedStatement pstmt = conn.prepareStatement(SQL)) {
            pstmt.setDate(1, activity.getActivityDate());
            pstmt.setInt(2, activity.getDurationMins());
            pstmt.setInt(3, activity.getPleasantness());
            pstmt.setString(4, activity.getLocation());
            pstmt.setString(5, activity.getCostEuro());
            pstmt.setInt(6, activity.getActivityTypeId());
            pstmt.setTimestamp(7, activity.getUpdatedAt());
            pstmt.setInt(8, activity.getActivityId());
            pstmt.setInt(9, activity.getUserId());

            int affectedRows = pstmt.executeUpdate();
            boolean success = affectedRows > 0;

            if (success) {
                logger.info("Activity updated successfully with ID: {}", activity.getActivityId());
            } else {
                logger.warn("No activity updated with ID: {} for user_id: {}",
                        activity.getActivityId(), activity.getUserId());
            }
            return success;
        } catch (SQLException e) {
            logger.error("Error updating activity with ID: {} for user_id: {}",
                    activity.getActivityId(), activity.getUserId(), e);
            throw e;
        }
    }

    /**
     * Elimina un'attività.
     */
    public boolean deleteActivity(Integer activityId, Integer userId, Connection conn) throws SQLException {
        final String SQL = "DELETE FROM activity WHERE activity_id = ? AND user_id = ?";

        logger.debug("Deleting activity with ID: {} for user_id: {}", activityId, userId);

        try (PreparedStatement pstmt = conn.prepareStatement(SQL)) {
            pstmt.setInt(1, activityId);
            pstmt.setInt(2, userId);

            int affectedRows = pstmt.executeUpdate();
            boolean success = affectedRows > 0;

            if (success) {
                logger.info("Activity deleted successfully with ID: {}", activityId);
            } else {
                logger.warn("No activity deleted with ID: {} for user_id: {}", activityId, userId);
            }
            return success;
        } catch (SQLException e) {
            logger.error("Error deleting activity with ID: {} for user_id: {}", activityId, userId, e);
            throw e;
        }
    }

    /**
     * Mappa un ResultSet a un ActivityDTO.
     */
    private ActivityDTO mapResultSetToActivityDTO(ResultSet rs) throws SQLException {
        ActivityDTO activity = new ActivityDTO();
        activity.setActivityId(rs.getInt("activity_id"));
        activity.setActivityDate(rs.getDate("activity_date"));
        activity.setDurationMins(rs.getInt("duration_mins"));
        activity.setPleasantness(rs.getInt("pleasantness"));
        activity.setLocation(rs.getString("location"));
        activity.setCostEuro(rs.getString("cost_euro"));
        activity.setUserId(rs.getInt("user_id"));
        activity.setActivityTypeId(rs.getInt("activity_type_id"));
        activity.setCreatedAt(rs.getTimestamp("created_at"));
        activity.setUpdatedAt(rs.getTimestamp("updated_at"));

        // Campi dalla JOIN con activity_type
        activity.setActivityTypeName(rs.getString("activity_type_name"));
        activity.setActivityTypeDescription(rs.getString("activity_type_description"));
        activity.setIsInstrumental(rs.getBoolean("is_instrumental"));
        activity.setIsRoutinary(rs.getBoolean("is_routinary"));

        return activity;
    }
}