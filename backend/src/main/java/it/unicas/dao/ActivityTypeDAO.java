package it.unicas.dao;

import it.unicas.dto.ActivityTypeDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO per la gestione dei tipi di attività nel database.
 */
public class ActivityTypeDAO {
    private static final Logger logger = LogManager.getLogger(ActivityTypeDAO.class);

    /**
     * Verifica se un tipo di attività esiste.
     */
    public boolean activityTypeExists(Integer activityTypeId, Connection conn) throws SQLException {
        final String SQL = "SELECT 1 FROM activity_type WHERE activity_type_id =?";
        logger.debug("Checking if activity type exists with ID: {}", activityTypeId);

        try (PreparedStatement pstmt = conn.prepareStatement(SQL)) {
            pstmt.setInt(1, activityTypeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                boolean exists = rs.next();
                logger.debug("Activity type exists check result: {} for ID: {}", exists, activityTypeId);
                return exists;
            }
        } catch (SQLException e) {
            logger.error("Error checking if activity type exists with ID: {}", activityTypeId, e);
            throw e;
        }
    }

    /**
     * Recupera tutti i tipi di attività dal database.
     * @param conn La connessione al database.
     * @return Una lista di ActivityTypeDTO.
     * @throws SQLException in caso di errore DB.
     */
    public List<ActivityTypeDTO> getAllActivityTypes(Connection conn) throws SQLException {
        final String SQL = "SELECT activity_type_id, name, description, is_instrumental, is_routinary, created_at, updated_at FROM activity_type ORDER BY name ASC";
        logger.debug("Retrieving all activity types.");

        List<ActivityTypeDTO> activityTypes = new ArrayList<>();
        try (PreparedStatement pstmt = conn.prepareStatement(SQL)) {
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    activityTypes.add(mapResultSetToActivityTypeDTO(rs));
                }
            }
            logger.info("Retrieved {} activity types.", activityTypes.size());
            return activityTypes;
        } catch (SQLException e) {
            logger.error("Error retrieving all activity types.", e);
            throw e;
        }
    }

    /**
     * Mappa un ResultSet a un ActivityTypeDTO.
     */
    private ActivityTypeDTO mapResultSetToActivityTypeDTO(ResultSet rs) throws SQLException {
        ActivityTypeDTO activityType = new ActivityTypeDTO();
        activityType.setActivityTypeId(rs.getInt("activity_type_id"));
        activityType.setName(rs.getString("name"));
        activityType.setDescription(rs.getString("description"));
        activityType.setIsInstrumental(rs.getBoolean("is_instrumental"));
        activityType.setIsRoutinary(rs.getBoolean("is_routinary"));
        activityType.setCreatedAt(rs.getTimestamp("created_at"));
        activityType.setUpdatedAt(rs.getTimestamp("updated_at"));
        return activityType;
    }
}