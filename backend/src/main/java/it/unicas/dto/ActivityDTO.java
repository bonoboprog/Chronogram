package it.unicas.dto;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * DTO per la gestione delle attività basato sullo schema del database.
 * Riflette esattamente la struttura della tabella 'activity'.
 */
public class ActivityDTO {
    private Integer activityId;           // activity_id INT (PK)
    private Date activityDate;            // activity_date DATE
    private Integer durationMins;         // duration_mins INT
    private Integer pleasantness;         // pleasantness INT (-3 to +3)
    private String location;              // location VARCHAR(100)
    private String costEuro;              // cost_euro DECIMAL(5,2) -> String per gestione decimali
    private Integer userId;               // user_id INT (FK)
    private Integer activityTypeId;       // activity_type_id INT (FK)
    private Timestamp createdAt;          // created_at TIMESTAMP
    private Timestamp updatedAt;          // updated_at TIMESTAMP

    // Campi aggiuntivi per le JOIN con activity_type
    private String activityTypeName;      // name dalla tabella activity_type
    private String activityTypeDescription; // description dalla tabella activity_type
    private Boolean isInstrumental;       // is_instrumental dalla tabella activity_type
    private Boolean isRoutinary;          // is_routinary dalla tabella activity_type

    // Costruttori
    public ActivityDTO() {}

    public ActivityDTO(Integer activityId, Date activityDate, Integer durationMins,
                       Integer pleasantness, String location, String costEuro,
                       Integer userId, Integer activityTypeId) {
        this.activityId = activityId;
        this.activityDate = activityDate;
        this.durationMins = durationMins;
        this.pleasantness = pleasantness;
        this.location = location;
        this.costEuro = costEuro;
        this.userId = userId;
        this.activityTypeId = activityTypeId;
    }

    // Getters e Setters
    public Integer getActivityId() { return activityId; }
    public void setActivityId(Integer activityId) { this.activityId = activityId; }

    public Date getActivityDate() { return activityDate; }
    public void setActivityDate(Date activityDate) { this.activityDate = activityDate; }

    public Integer getDurationMins() { return durationMins; }
    public void setDurationMins(Integer durationMins) { this.durationMins = durationMins; }

    public Integer getPleasantness() { return pleasantness; }
    public void setPleasantness(Integer pleasantness) { this.pleasantness = pleasantness; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getCostEuro() { return costEuro; }
    public void setCostEuro(String costEuro) { this.costEuro = costEuro; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public Integer getActivityTypeId() { return activityTypeId; }
    public void setActivityTypeId(Integer activityTypeId) { this.activityTypeId = activityTypeId; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    // Getters e Setters per i campi JOIN
    public String getActivityTypeName() { return activityTypeName; }
    public void setActivityTypeName(String activityTypeName) { this.activityTypeName = activityTypeName; }

    public String getActivityTypeDescription() { return activityTypeDescription; }
    public void setActivityTypeDescription(String activityTypeDescription) { this.activityTypeDescription = activityTypeDescription; }

    public Boolean getIsInstrumental() { return isInstrumental; }
    public void setIsInstrumental(Boolean isInstrumental) { this.isInstrumental = isInstrumental; }

    public Boolean getIsRoutinary() { return isRoutinary; }
    public void setIsRoutinary(Boolean isRoutinary) { this.isRoutinary = isRoutinary; }

    @Override
    public String toString() {
        return "ActivityDTO{" +
                "activityId=" + activityId +
                ", activityDate=" + activityDate +
                ", durationMins=" + durationMins +
                ", pleasantness=" + pleasantness +
                ", location='" + location + '\'' +
                ", costEuro='" + costEuro + '\'' +
                ", userId=" + userId +
                ", activityTypeId=" + activityTypeId +
                ", activityTypeName='" + activityTypeName + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}