package it.unicas.dto;

import java.sql.Timestamp;

/**
 * DTO per la gestione dei tipi di attività.
 * Riflette la struttura della tabella 'activity_type'.
 */
public class ActivityTypeDTO {
    private Integer activityTypeId;       // activity_type_id INT (PK)
    private String name;                  // name VARCHAR(45)
    private String description;           // description VARCHAR(255)
    private Boolean isInstrumental;       // is_instrumental TINYINT(1)
    private Boolean isRoutinary;          // is_routinary TINYINT(1)
    private Timestamp createdAt;          // created_at TIMESTAMP
    private Timestamp updatedAt;          // updated_at TIMESTAMP

    // Costruttori
    public ActivityTypeDTO() {}

    public ActivityTypeDTO(Integer activityTypeId, String name, String description,
                           Boolean isInstrumental, Boolean isRoutinary) {
        this.activityTypeId = activityTypeId;
        this.name = name;
        this.description = description;
        this.isInstrumental = isInstrumental;
        this.isRoutinary = isRoutinary;
    }

    // Getters e Setters
    public Integer getActivityTypeId() { return activityTypeId; }
    public void setActivityTypeId(Integer activityTypeId) { this.activityTypeId = activityTypeId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Boolean getIsInstrumental() { return isInstrumental; }
    public void setIsInstrumental(Boolean isInstrumental) { this.isInstrumental = isInstrumental; }

    public Boolean getIsRoutinary() { return isRoutinary; }
    public void setIsRoutinary(Boolean isRoutinary) { this.isRoutinary = isRoutinary; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "ActivityTypeDTO{" +
                "activityTypeId=" + activityTypeId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", isInstrumental=" + isInstrumental +
                ", isRoutinary=" + isRoutinary +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}