package it.unicas.products.pojo;

// Importing classes from JavaFX for property bindings.
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Class representing an ActivityType entity.
 * Provides properties and methods to manage activity type data.
 */
public class ActivityType {

    /**
     * Unique identifier for the activity type.
     */
    private IntegerProperty activityTypeId;

    /**
     * Name of the activity type.
     */
    private StringProperty name;

    /**
     * Indicates if the activity type is instrumental (1 for true, 0 for false).
     */
    private IntegerProperty isInstrumental;

    /**
     * Indicates if the activity type is routinary (1 for true, 0 for false).
     */
    private IntegerProperty isRoutinary;

    /**
     * Default constructor initializing properties with default values.
     * Useful for creating an empty ActivityType object.
     */
    public ActivityType() {
        this(null, null);
    }

    /**
     * Constructor to initialize name with a given value.
     * Other properties will remain with default values.
     *
     * @param name Name of the activity type.
     */
    public ActivityType(String name) {
        // Calls the full constructor to initialize all properties.
        this(null, name, null, null);
    }

    /**
     * Full constructor to initialize all properties with given values.
     *
     * @param activityTypeId Unique identifier for the activity type.
     * @param name Name of the activity type.
     * @param isInstrumental Indicates if the activity type is instrumental (1 for true, 0 for false).
     * @param isRoutinary Indicates if the activity type is routinary (1 for true, 0 for false).
     */
    public ActivityType(Integer activityTypeId, String name, Integer isInstrumental, Integer isRoutinary) {
        this.activityTypeId = new SimpleIntegerProperty(activityTypeId != null ? activityTypeId : 0); // Sets activityTypeId with a default of 0.
        this.name = new SimpleStringProperty(name != null ? name : ""); // Sets name with a default empty string.
        this.isInstrumental = new SimpleIntegerProperty(isInstrumental != null ? isInstrumental : 0); // Sets isInstrumental with a default of 0.
        this.isRoutinary = new SimpleIntegerProperty(isRoutinary != null ? isRoutinary : 0); // Sets isRoutinary with a default of 0.
    }

    /**
     * Gets the activity type's unique identifier.
     *
     * @return The activity type ID.
     */
    public Integer getActivityTypeId() {
        return activityTypeId.get();
    }

    /**
     * Sets the activity type's unique identifier.
     *
     * @param activityTypeId The new activity type ID.
     */
    public void setActivityTypeId(Integer activityTypeId) {
        this.activityTypeId.set(activityTypeId);
    }

    /**
     * Returns the property object for activityTypeId (useful for bindings).
     *
     dwarfs @return The activityTypeId property.
     */
    public IntegerProperty activityTypeIdProperty() {
        return activityTypeId;
    }

    /**
     * Gets the name of the activity type.
     *
     * @return The name of the activity type.
     */
    public String getName() {
        return name.get();
    }

    /**
     * Sets the name of the activity type.
     *
     * @param name The new name.
     */
    public void setName(String name) {
        this.name.set(name);
    }

    /**
     * Returns the property object for name (useful for bindings).
     *
     * @return The name property.
     */
    public StringProperty nameProperty() {
        return name;
    }

    /**
     * Gets whether the activity type is instrumental.
     *
     * @return 1 if instrumental, 0 if not.
     */
    public Integer getIsInstrumental() {
        return isInstrumental.get();
    }

    /**
     * Sets whether the activity type is instrumental.
     *
     * @param isInstrumental The new value (1 for true, 0 for false).
     */
    public void setIsInstrumental(Integer isInstrumental) {
        this.isInstrumental.set(isInstrumental);
    }

    /**
     * Returns the property object for isInstrumental (useful for bindings).
     *
     * @return The isInstrumental property.
     */
    public IntegerProperty isInstrumentalProperty() {
        return isInstrumental;
    }

    /**
     * Gets whether the activity type is routinary.
     *
     * @return 1 if routinary, 0 if not.
     */
    public Integer getIsRoutinary() {
        return isRoutinary.get();
    }

    /**
     * Sets whether the activity type is routinary.
     *
     * @param isRoutinary The new value (1 for true, 0 for false).
     */
    public void setIsRoutinary(Integer isRoutinary) {
        this.isRoutinary.set(isRoutinary);
    }

    /**
     * Returns the property object for isRoutinary (useful for bindings).
     *
     * @return The isRoutinary property.
     */
    public IntegerProperty isRoutinaryProperty() {
        return isRoutinary;
    }

    /**
     * Overrides the toString method to return a string representation of the ActivityType object.
     * Includes all properties for easier debugging and logging.
     *
     * @return A string representation of the ActivityType object.
     */
    @Override
    public String toString() {
        return "ActivityType{" +
                "activityTypeId=" + activityTypeId.get() +
                ", name=" + name.get() +
                ", isInstrumental=" + isInstrumental.get() +
                ", isRoutinary=" + isRoutinary.get() +
                '}';
    }
}
