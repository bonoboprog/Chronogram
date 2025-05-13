package it.unicas.products.pojo;

// Importing classes from JavaFX for property bindings.
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Class representing an Activity entity.
 * Provides properties and methods to manage activity data.
 */
public class Activity {

    /**
     * Unique identifier for the activity.
     */
    private IntegerProperty activityId;

    /**
     * Identifier of the user associated with the activity.
     */
    private IntegerProperty userId;

    /**
     * Identifier of the activity type.
     */
    private IntegerProperty activityTypeId;

    /**
     * Date of the activity.
     */
    private StringProperty activityDate;

    /**
     * Start time of the activity.
     */
    private StringProperty startTime;

    /**
     * Duration of the activity in minutes.
     */
    private IntegerProperty durationMinutes;

    /**
     * Pleasantness rating of the activity.
     */
    private IntegerProperty pleasantness;

    /**
     * Location where the activity took place.
     */
    private StringProperty location;

    /**
     * Cost of the activity in euros.
     */
    private FloatProperty costEur;

    /**
     * Default constructor initializing properties with default values.
     * Useful for creating an empty Activity object.
     */
    public Activity() {
        this(null, null);
    }

    /**
     * Constructor to initialize userId and activityTypeId with given values.
     * Other properties will remain with default values.
     *
     * @param userId Identifier of the user associated with the activity.
     * @param activityTypeId Identifier of the activity type.
     */
    public Activity(Integer userId, Integer activityTypeId) {
        // Calls the full constructor to initialize all properties.
        this(null, userId, activityTypeId, null, null, null, null, null, null);
    }

    /**
     * Full constructor to initialize all properties with given values.
     *
     * @param activityId Unique identifier for the activity.
     * @param userId Identifier of the user associated with the activity.
     * @param activityTypeId Identifier of the activity type.
     * @param activityDate Date of the activity.
     * @param startTime Start time of the activity.
     * @param durationMinutes Duration of the activity in minutes.
     * @param pleasantness Pleasantness rating of the activity.
     * @param location Location where the activity took place.
     * @param costEur Cost of the activity in euros.
     */
    public Activity(Integer activityId, Integer userId, Integer activityTypeId, String activityDate, String startTime,
                    Integer durationMinutes, Integer pleasantness, String location, Float costEur) {
        this.activityId = new SimpleIntegerProperty(activityId != null ? activityId : 0); // Sets activityId with a default of 0.
        this.userId = new SimpleIntegerProperty(userId != null ? userId : 0); // Sets userId with a default of 0.
        this.activityTypeId = new SimpleIntegerProperty(activityTypeId != null ? activityTypeId : 0); // Sets activityTypeId with a default of 0.
        this.activityDate = new SimpleStringProperty(activityDate != null ? activityDate : ""); // Sets activityDate with a default empty string.
        this.startTime = new SimpleStringProperty(startTime != null ? startTime : ""); // Sets startTime with a default empty string.
        this.durationMinutes = new SimpleIntegerProperty(durationMinutes != null ? durationMinutes : 0); // Sets durationMinutes with a default of 0.
        this.pleasantness = new SimpleIntegerProperty(pleasantness != null ? pleasantness : 0); // Sets pleasantness with a default of 0.
        this.location = new SimpleStringProperty(location != null ? location : ""); // Sets location with a default empty string.
        this.costEur = new SimpleFloatProperty(costEur != null ? costEur : 0.0f); // Sets costEur with a default of 0.0.
    }

    /**
     * Gets the activity's unique identifier.
     *
     * @return The activity's ID.
     */
    public Integer getActivityId() {
        return activityId.get();
    }

    /**
     * Sets the activity's unique identifier.
     *
     * @param activityId The new activity ID.
     */
    public void setActivityId(Integer activityId) {
        this.activityId.set(activityId);
    }

    /**
     * Returns the property object for activityId (useful for bindings).
     *
     * @return The activityId property.
     */
    public IntegerProperty activityIdProperty() {
        return activityId;
    }

    /**
     * Gets the user ID associated with the activity.
     *
     * @return The user ID.
     */
    public Integer getUserId() {
        return userId.get();
    }

    /**
     * Sets the user ID associated with the activity.
     *
     * @param userId The new user ID.
     */
    public void setUserId(Integer userId) {
        this.userId.set(userId);
    }

    /**
     * Returns the property object for userId (useful for bindings).
     *
     * @return The userId property.
     */
    public IntegerProperty userIdProperty() {
        return userId;
    }

    /**
     * Gets the activity type ID.
     *
     * @return The activity type ID.
     */
    public Integer getActivityTypeId() {
        return activityTypeId.get();
    }

    /**
     * Sets the activity type ID.
     *
     * @param activityTypeId The new activity type ID.
     */
    public void setActivityTypeId(Integer activityTypeId) {
        this.activityTypeId.set(activityTypeId);
    }

    /**
     * Returns the property object for activityTypeId (useful for bindings).
     *
     * @return The activityTypeId property.
     */
    public IntegerProperty activityTypeIdProperty() {
        return activityTypeId;
    }

    /**
     * Gets the date of the activity.
     *
     * @return The activity date.
     */
    public String getActivityDate() {
        return activityDate.get();
    }

    /**
     * Sets the date of the activity.
     *
     * @param activityDate The new activity date.
     */
    public void setActivityDate(String activityDate) {
        this.activityDate.set(activityDate);
    }

    /**
     * Returns the property object for activityDate (useful for bindings).
     *
     * @return The activityDate property.
     */
    public StringProperty activityDateProperty() {
        return activityDate;
    }

    /**
     * Gets the start time of the activity.
     *
     * @return The start time.
     */
    public String getStartTime() {
        return startTime.get();
    }

    /**
     * Sets the start time of the activity.
     *
     * @param startTime The new start time.
     */
    public void setStartTime(String startTime) {
        this.startTime.set(startTime);
    }

    /**
     * Returns the property object for startTime (useful for bindings).
     *
     * @return The startTime property.
     */
    public StringProperty startTimeProperty() {
        return startTime;
    }

    /**
     * Gets the duration of the activity in minutes.
     *
     * @return The duration in minutes.
     */
    public Integer getDurationMinutes() {
        return durationMinutes.get();
    }

    /**
     * Sets the duration of the activity in minutes.
     *
     * @param durationMinutes The new duration in minutes.
     */
    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes.set(durationMinutes);
    }

    /**
     * Returns the property object for durationMinutes (useful for bindings).
     *
     * @return The durationMinutes property.
     */
    public IntegerProperty durationMinutesProperty() {
        return durationMinutes;
    }

    /**
     * Gets the pleasantness rating of the activity.
     *
     * @return The pleasantness rating.
     */
    public Integer getPleasantness() {
        return pleasantness.get();
    }

    /**
     * Sets the pleasantness rating of the activity.
     *
     * @param pleasantness The new pleasantness rating.
     */
    public void setPleasantness(Integer pleasantness) {
        this.pleasantness.set(pleasantness);
    }

    /**
     * Returns the property object for pleasantness (useful for bindings).
     *
     * @return The pleasantness property.
     */
    public IntegerProperty pleasantnessProperty() {
        return pleasantness;
    }

    /**
     * Gets the location of the activity.
     *
     * @return The location.
     */
    public String getLocation() {
        return location.get();
    }

    /**
     * Sets the location of the activity.
     *
     * @param location The new location.
     */
    public void setLocation(String location) {
        this.location.set(location);
    }

    /**
     * Returns the property object for location (useful for bindings).
     *
     * @return The location property.
     */
    public StringProperty locationProperty() {
        return location;
    }

    /**
     * Gets the cost of the activity in euros.
     *
     * @return The cost in euros.
     */
    public Float getCostEur() {
        return costEur.get();
    }

    /**
     * Sets the cost of the activity in euros.
     *
     * @param costEur The new cost in euros.
     */
    public void setCostEur(Float costEur) {
        this.costEur.set(costEur);
    }

    /**
     * Returns the property object for costEur (useful for bindings).
     *
     * @return The costEur property.
     */
    public FloatProperty costEurProperty() {
        return costEur;
    }

    /**
     * Overrides the toString method to return a string representation of the Activity object.
     * Includes all properties for easier debugging and logging.
     *
     * @return A string representation of the Activity object.
     */
    @Override
    public String toString() {
        return "Activity{" +
                "activityId=" + activityId.get() +
                ", userId=" + userId.get() +
                ", activityTypeId=" + activityTypeId.get() +
                ", activityDate=" + activityDate.get() +
                ", startTime=" + startTime.get() +
                ", durationMinutes=" + durationMinutes.get() +
                ", pleasantness=" + pleasantness.get() +
                ", location=" + location.get() +
                ", costEur=" + costEur.get() +
                '}';
    }
}