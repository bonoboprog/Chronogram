package it.unicas.dto;

public class llmDTO {
    private String name;
    private Integer durationMins;
    private String details;
    private Integer pleasantness;
    private Integer activityTypeId;
    private String recurrence;
    private String costEuro;
    private String location;

    // Getter e Setter
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getDurationMins() { return durationMins; }
    public void setDurationMins(Integer durationMins) { this.durationMins = durationMins; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public Integer getPleasantness() { return pleasantness; }
    public void setPleasantness(Integer pleasantness) { this.pleasantness = pleasantness; }

    public Integer getActivityTypeId() { return activityTypeId; }
    public void setActivityTypeId(Integer activityTypeId) { this.activityTypeId = activityTypeId; }

    public String getRecurrence() { return recurrence; }
    public void setRecurrence(String recurrence) { this.recurrence = recurrence; }

    public String getCostEuro() { return costEuro; }
    public void setCostEuro(String costEuro) { this.costEuro = costEuro; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
}
