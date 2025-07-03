package it.unicas.dto;

public class llmDTO {
    private String name;
    private Integer duration;
    private String details;
    private Integer enjoyment;
    private String category;
    private String type;
    private String recurrence;
    private Double cost;
    private String location;

    // Getter e Setter
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public Integer getEnjoyment() { return enjoyment; }
    public void setEnjoyment(Integer enjoyment) { this.enjoyment = enjoyment; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getRecurrence() { return recurrence; }
    public void setRecurrence(String recurrence) { this.recurrence = recurrence; }

    public Double getCost() { return cost; }
    public void setCost(Double cost) { this.cost = cost; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
}