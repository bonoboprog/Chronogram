package it.unicas.products.pojo;

// Importing classes from JavaFX for property bindings.
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Class representing a User entity.
 * Provides properties and methods to manage user data.
 */
public class User {

    /**
     * Unique identifier for the user.
     */
    private IntegerProperty userId;

    /**
     * User's weekly income in euros.
     */
    private FloatProperty weeklyIncome;

    /**
     * User's other weekly income in euros.
     */
    private FloatProperty weeklyIncomeOther;

    /**
     * User's weekly budget in euros.
     */
    private FloatProperty weeklyBudget;

    /**
     * User's weekly home cost in euros.
     */
    private FloatProperty weeklyHomeCost;

    /**
     * User's weekly vehicle cost in euros.
     */
    private FloatProperty vehicleWeeklyCost;

    /**
     * User's weekly expenditure on durables in euros.
     */
    private FloatProperty durablesWeeklyExpenditure;

    /**
     * User's weekly expenditure on services in euros.
     */
    private FloatProperty servicesWeeklyExpenditure;

    /**
     * Additional notes about the user.
     */
    private StringProperty notes;

    /**
     * Default constructor initializing properties with default values.
     * Useful for creating an empty User object.
     */
    public User() {
        this(null);
    }

    /**
     * Constructor to initialize userId with a given value.
     * Other properties will remain with default values.
     *
     * @param userId Unique identifier for the user.
     */
    public User(Integer userId) {
        // Calls the full constructor to initialize all properties.
        this(userId, null, null, null, null, null, null, null, null);
    }

    /**
     * Full constructor to initialize all properties with given values.
     *
     * @param userId Unique identifier for the user.
     * @param weeklyIncome User's weekly income in euros.
     * @param weeklyIncomeOther User's other weekly income in euros.
     * @param weeklyBudget User's weekly budget in euros.
     * @param weeklyHomeCost User's weekly home cost in euros.
     * @param vehicleWeeklyCost User's weekly vehicle cost in euros.
     * @param durablesWeeklyExpenditure User's weekly expenditure on durables in euros.
     * @param servicesWeeklyExpenditure User's weekly expenditure on services in euros.
     * @param notes Additional notes about the user.
     */
    public User(Integer userId, Float weeklyIncome, Float weeklyIncomeOther, Float weeklyBudget, Float weeklyHomeCost,
                Float vehicleWeeklyCost, Float durablesWeeklyExpenditure, Float servicesWeeklyExpenditure, String notes) {
        this.userId = new SimpleIntegerProperty(userId != null ? userId : 0); // Sets userId with a default of 0.
        this.weeklyIncome = new SimpleFloatProperty(weeklyIncome != null ? weeklyIncome : 0.0f); // Sets weeklyIncome with a default of 0.0.
        this.weeklyIncomeOther = new SimpleFloatProperty(weeklyIncomeOther != null ? weeklyIncomeOther : 0.0f); // Sets weeklyIncomeOther with a default of 0.0.
        this.weeklyBudget = new SimpleFloatProperty(weeklyBudget != null ? weeklyBudget : 0.0f); // Sets weeklyBudget with a default of 0.0.
        this.weeklyHomeCost = new SimpleFloatProperty(weeklyHomeCost != null ? weeklyHomeCost : 0.0f); // Sets weeklyHomeCost with a default of 0.0.
        this.vehicleWeeklyCost = new SimpleFloatProperty(vehicleWeeklyCost != null ? vehicleWeeklyCost : 0.0f); // Sets vehicleWeeklyCost with a default of 0.0.
        this.durablesWeeklyExpenditure = new SimpleFloatProperty(durablesWeeklyExpenditure != null ? durablesWeeklyExpenditure : 0.0f); // Sets durablesWeeklyExpenditure with a default of 0.0.
        this.servicesWeeklyExpenditure = new SimpleFloatProperty(servicesWeeklyExpenditure != null ? servicesWeeklyExpenditure : 0.0f); // Sets servicesWeeklyExpenditure with a default of 0.0.
        this.notes = new SimpleStringProperty(notes != null ? notes : ""); // Sets notes with a default empty string.
    }

    /**
     * Gets the user's unique identifier.
     *
     * @return The user ID.
     */
    public Integer getUserId() {
        return userId.get();
    }

    /**
     * Sets the user's unique identifier.
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
     * Gets the user's weekly income.
     *
     * @return The weekly income in euros.
     */
    public Float getWeeklyIncome() {
        return weeklyIncome.get();
    }

    /**
     * Sets the user's weekly income.
     *
     * @param weeklyIncome The new weekly income in euros.
     */
    public void setWeeklyIncome(Float weeklyIncome) {
        this.weeklyIncome.set(weeklyIncome);
    }

    /**
     * Returns the property object for weeklyIncome (useful for bindings).
     *
     * @return The weeklyIncome property.
     */
    public FloatProperty weeklyIncomeProperty() {
        return weeklyIncome;
    }

    /**
     * Gets the user's other weekly income.
     *
     * @return The other weekly income in euros.
     */
    public Float getWeeklyIncomeOther() {
        return weeklyIncomeOther.get();
    }

    /**
     * Sets the user's other weekly income.
     *
     * @param weeklyIncomeOther The new other weekly income in euros.
     */
    public void setWeeklyIncomeOther(Float weeklyIncomeOther) {
        this.weeklyIncomeOther.set(weeklyIncomeOther);
    }

    /**
     * Returns the property object for weeklyIncomeOther (useful for bindings).
     *
     * @return The weeklyIncomeOther property.
     */
    public FloatProperty weeklyIncomeOtherProperty() {
        return weeklyIncomeOther;
    }

    /**
     * Gets the user's weekly budget.
     *
     * @return The weekly budget in euros.
     */
    public Float getWeeklyBudget() {
        return weeklyBudget.get();
    }

    /**
     * Sets the user's weekly budget.
     *
     * @param weeklyBudget The new weekly budget in euros.
     */
    public void setWeeklyBudget(Float weeklyBudget) {
        this.weeklyBudget.set(weeklyBudget);
    }

    /**
     * Returns the property object for weeklyBudget (useful for bindings).
     *
     * @return The weeklyBudget property.
     */
    public FloatProperty weeklyBudgetProperty() {
        return weeklyBudget;
    }

    /**
     * Gets the user's weekly home cost.
     *
     * @return The weekly home cost in euros.
     */
    public Float getWeeklyHomeCost() {
        return weeklyHomeCost.get();
    }

    /**
     * Sets the user's weekly home cost.
     *
     * @param weeklyHomeCost The new weekly home cost in euros.
     */
    public void setWeeklyHomeCost(Float weeklyHomeCost) {
        this.weeklyHomeCost.set(weeklyHomeCost);
    }

    /**
     * Returns the property object for weeklyHomeCost (useful for bindings).
     *
     * @return The weeklyHomeCost property.
     */
    public FloatProperty weeklyHomeCostProperty() {
        return weeklyHomeCost;
    }

    /**
     * Gets the user's weekly vehicle cost.
     *
     * @return The weekly vehicle cost in euros.
     */
    public Float getVehicleWeeklyCost() {
        return vehicleWeeklyCost.get();
    }

    /**
     * Sets the user's weekly vehicle cost.
     *
     * @param vehicleWeeklyCost The new weekly vehicle cost in euros.
     */
    public void setVehicleWeeklyCost(Float vehicleWeeklyCost) {
        this.vehicleWeeklyCost.set(vehicleWeeklyCost);
    }

    /**
     * Returns the property object for vehicleWeeklyCost (useful for bindings).
     *
     * @return The vehicleWeeklyCost property.
     */
    public FloatProperty vehicleWeeklyCostProperty() {
        return vehicleWeeklyCost;
    }

    /**
     * Gets the user's weekly expenditure on durables.
     *
     * @return The weekly expenditure on durables in euros.
     */
    public Float getDurablesWeeklyExpenditure() {
        return durablesWeeklyExpenditure.get();
    }

    /**
     * Sets the user's weekly expenditure on durables.
     *
     * @param durablesWeeklyExpenditure The new weekly expenditure on durables in euros.
     */
    public void setDurablesWeeklyExpenditure(Float durablesWeeklyExpenditure) {
        this.durablesWeeklyExpenditure.set(durablesWeeklyExpenditure);
    }

    /**
     * Returns the property object for durablesWeeklyExpenditure (useful for bindings).
     *
     * @return The durablesWeeklyExpenditure property.
     */
    public FloatProperty durablesWeeklyExpenditureProperty() {
        return durablesWeeklyExpenditure;
    }

    /**
     * Gets the user's weekly expenditure on services.
     *
     * @return The weekly expenditure on services in euros.
     */
    public Float getServicesWeeklyExpenditure() {
        return servicesWeeklyExpenditure.get();
    }

    /**
     * Sets the user's weekly expenditure on services.
     *
     * @param servicesWeeklyExpenditure The new weekly expenditure on services in euros.
     */
    public void setServicesWeeklyExpenditure(Float servicesWeeklyExpenditure) {
        this.servicesWeeklyExpenditure.set(servicesWeeklyExpenditure);
    }

    /**
     * Returns the property object for servicesWeeklyExpenditure (useful for bindings).
     *
     * @return The servicesWeeklyExpenditure property.
     */
    public FloatProperty servicesWeeklyExpenditureProperty() {
        return servicesWeeklyExpenditure;
    }

    /**
     * Gets the additional notes about the user.
     *
     * @return The notes.
     */
    public String getNotes() {
        return notes.get();
    }

    /**
     * Sets the additional notes about the user.
     *
     * @param notes The new notes.
     */
    public void setNotes(String notes) {
        this.notes.set(notes);
    }

    /**
     * Returns the property object for notes (useful for bindings).
     *
     * @return The notes property.
     */
    public StringProperty notesProperty() {
        return notes;
    }

    /**
     * Overrides the toString method to return a string representation of the User object.
     * Includes all properties for easier debugging and logging.
     *
     * @return A string representation of the User object.
     */
    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId.get() +
                ", weeklyIncome=" + weeklyIncome.get() +
                ", weeklyIncomeOther=" + weeklyIncomeOther.get() +
                ", weeklyBudget=" + weeklyBudget.get() +
                ", weeklyHomeCost=" + weeklyHomeCost.get() +
                ", vehicleWeeklyCost=" + vehicleWeeklyCost.get() +
                ", durablesWeeklyExpenditure=" + durablesWeeklyExpenditure.get() +
                ", servicesWeeklyExpenditure=" + servicesWeeklyExpenditure.get() +
                ", notes=" + notes.get() +
                '}';
    }
}