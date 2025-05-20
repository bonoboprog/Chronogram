package it.unicas.activities.pojo;

// Importing classes from JavaFX for property bindings.
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Class representing a SpecialActivity entity.
 * Provides properties and methods to manage special activity data.
 */
public class SpecialActivity {

    /**
     * Unique identifier for the special activity.
     */
    private IntegerProperty specialId;

    /**
     * Identifier of the user associated with the special activity.
     */
    private IntegerProperty userId;

    /**
     * Description of the special activity.
     */
    private StringProperty description;

    /**
     * Indicates if the special activity is seasonal (1 for true, 0 for false).
     */
    private IntegerProperty isSeasonal;

    /**
     * Default constructor initializing properties with default values.
     * Useful for creating an empty SpecialActivity object.
     */
    public SpecialActivity() {
        this(null, null);
    }

    /**
     * Constructor to initialize userId with a given value.
     * Other properties will remain with default values.
     *
     * @param userId Identifier of the user associated with the special activity.
     */
    public SpecialActivity(Integer userId) {
        // Calls the full constructor to initialize all properties.
        this(null, userId, null, null);
    }

    /**
     * Full constructor to initialize all properties with given values.
     *
     * @param specialId Unique identifier for the special activity.
     * @param userId Identifier of the user associated with the special activity.
     * @param description Description of the special activity.
     * @param isSeasonal Indicates if the special activity is seasonal (1 for true, 0 for false).
     */
    public SpecialActivity(Integer specialId, Integer userId, String description, Integer isSeasonal) {
        this.specialId = new SimpleIntegerProperty(specialId != null ? specialId : 0); // Sets specialId with a default of 0.
        this.userId = new SimpleIntegerProperty(userId != null ? userId : 0); // Sets userId with a default of 0.
        this.description = new SimpleStringProperty(description != null ? description : ""); // Sets description with a default empty string.
        this.isSeasonal = new SimpleIntegerProperty(isSeasonal != null ? isSeasonal : 0); // Sets isSeasonal with a default of 0.
    }

    /**
     * Gets the special activity's unique identifier.
     *
     * @return The special activity ID.
     */
    public Integer getSpecialId() {
        return specialId.get();
    }

    /**
     * Sets the special activity's unique identifier.
     *
     * @param specialId The new special activity ID.
     */
    public void setSpecialId(Integer specialId) {
        this.specialId.set(specialId);
    }

    /**
     * Returns the property object for specialId (useful for bindings).
     *
     * @return The specialId property.
     */
    public IntegerProperty specialIdProperty() {
        return specialId;
    }

    /**
     * Gets the user ID associated with the special activity.
     *
     * @return The user ID.
     */
    public Integer getUserId() {
        return userId.get();
    }

    /**
     * Sets the user ID associated with the special activity.
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
     * Gets the description of the special activity.
     *
     * @return The description.
     */
    public String getDescription() {
        return description.get();
    }

    /**
     * Sets the description of the special activity.
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
     * Gets whether the special activity is seasonal.
     *
     * @return 1 if seasonal, 0 if not.
     */
    public Integer getIsSeasonal() {
        return isSeasonal.get();
    }

    /**
     * Sets whether the special activity is seasonal.
     *
     * @param isSeasonal The new value (1 for true, 0 for false).
     */
    public void setIsSeasonal(Integer isSeasonal) {
        this.isSeasonal.set(isSeasonal);
    }

    /**
     * Returns the property object for isSeasonal (useful for bindings).
     *
     * @return The isSeasonal property.
     */
    public IntegerProperty isSeasonalProperty() {
        return isSeasonal;
    }

    /**
     * Overrides the toString method to return a string representation of the SpecialActivity object.
     * Includes all properties for easier debugging and logging.
     *
     * @return A string representation of the SpecialActivity object.
     */
    @Override
    public String toString() {
        return "SpecialActivity{" +
                "specialId=" + specialId.get() +
                ", userId=" + userId.get() +
                ", description=" + description.get() +
                ", isSeasonal=" + isSeasonal.get() +
                '}';
    }
}