package it.unicas.products.pojo;

// Importing classes from JavaFX for property bindings.
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Class representing a ParallelActivity entity.
 * Provides properties and methods to manage parallel activity data.
 */
public class ParallelActivity {

    /**
     * Unique identifier for the parallel activity.
     */
    private IntegerProperty parallelId;

    /**
     * Identifier of the associated activity.
     */
    private IntegerProperty activityId;

    /**
     * Description of the parallel activity.
     */
    private StringProperty description;

    /**
     * Default constructor initializing properties with default values.
     * Useful for creating an empty ParallelActivity object.
     */
    public ParallelActivity() {
        this(null, null);
    }

    /**
     * Constructor to initialize activityId with a given value.
     * Other properties will remain with default values.
     *
     * @param activityId Identifier of the associated activity.
     */
    public ParallelActivity(Integer activityId) {
        // Calls the full constructor to initialize all properties.
        this(null, activityId, null);
    }

    /**
     * Full constructor to initialize all properties with given values.
     *
     * @param parallelId Unique identifier for the parallel activity.
     * @param activityId Identifier of the associated activity.
     * @param description Description of the parallel activity.
     */
    public ParallelActivity(Integer parallelId, Integer activityId, String description) {
        this.parallelId = new SimpleIntegerProperty(parallelId != null ? parallelId : 0); // Sets parallelId with a default of 0.
        this.activityId = new SimpleIntegerProperty(activityId != null ? activityId : 0); // Sets activityId with a default of 0.
        this.description = new SimpleStringProperty(description != null ? description : ""); // Sets description with a default empty string.
    }

    /**
     * Gets the parallel activity's unique identifier.
     *
     * @return The parallel activity ID.
     */
    public Integer getParallelId() {
        return parallelId.get();
    }

    /**
     * Sets the parallel activity's unique identifier.
     *
     * @param parallelId The new parallel activity ID.
     */
    public void setParallelId(Integer parallelId) {
        this.parallelId.set(parallelId);
    }

    /**
     * Returns the property object for parallelId (useful for bindings).
     *
     * @return The parallelId property.
     */
    public IntegerProperty parallelIdProperty() {
        return parallelId;
    }

    /**
     * Gets the associated activity's identifier.
     *
     * @return The activity ID.
     */
    public Integer getActivityId() {
        return activityId.get();
    }

    /**
     * Sets the associated activity's identifier.
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
     * Gets the description of the parallel activity.
     *
     * @return The description.
     */
    public String getDescription() {
        return description.get();
    }

    /**
     * Sets the description of the parallel activity.
     *
     * @param description The new description.
     */
    public void setDescription(String description) {
        this.description.set(description);
    }

    /**
     * Returns the property object for description (useful for bindings).
     *
     * @return The description property.
     */
    public StringProperty descriptionProperty() {
        return description;
    }

    /**
     * Overrides the toString method to return a string representation of the ParallelActivity object.
     * Includes all properties for easier debugging and logging.
     *
     * @return A string representation of the ParallelActivity object.
     */
    @Override
    public String toString() {
        return "ParallelActivity{" +
                "parallelId=" + parallelId.get() +
                ", activityId=" + activityId.get() +
                ", description=" + description.get() +
                '}';
    }
}