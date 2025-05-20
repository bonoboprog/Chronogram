package it.unicas.activities.pojo;


// Importing classes from JavaFX for property bindings.
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Class representing a UserAuth entity.
 * Provides properties and methods to manage user authentication data.
 */
public class UserAuth {

    /**
     * Identifier of the user associated with the authentication record.
     */
    private IntegerProperty userId;

    /**
     * Email address of the user.
     */
    private StringProperty email;

    /**
     * Hashed password of the user.
     */
    private StringProperty passwordHash;

    /**
     * Timestamp when the user account was created.
     */
    private StringProperty createdAt;

    /**
     * Timestamp of the user's last login.
     */
    private StringProperty lastLogin;

    /**
     * Default constructor initializing properties with default values.
     * Useful for creating an empty UserAuth object.
     */
    public UserAuth() {
        this(null, null);
    }

    /**
     * Constructor to initialize email and passwordHash with given values.
     * Other properties will remain with default values.
     *
     * @param email Email address of the user.
     * @param passwordHash Hashed password of the user.
     */
    public UserAuth(String email, String passwordHash) {
        // Calls the full constructor to initialize all properties.
        this(null, email, passwordHash, null, null);
    }

    /**
     * Full constructor to initialize all properties with given values.
     *
     * @param userId Identifier of the user associated with the authentication record.
     * @param email Email address of the user.
     * @param passwordHash Hashed password of the user.
     * @param createdAt Timestamp when the user account was created.
     * @param lastLogin Timestamp of the user's last login.
     */
    public UserAuth(Integer userId, String email, String passwordHash, String createdAt, String lastLogin) {
        this.userId = new SimpleIntegerProperty(userId != null ? userId : 0); // Sets userId with a default of 0.
        this.email = new SimpleStringProperty(email != null ? email : ""); // Sets email with a default empty string.
        this.passwordHash = new SimpleStringProperty(passwordHash != null ? passwordHash : ""); // Sets passwordHash with a default empty string.
        this.createdAt = new SimpleStringProperty(createdAt != null ? createdAt : ""); // Sets createdAt with a default empty string.
        this.lastLogin = new SimpleStringProperty(lastLogin != null ? lastLogin : ""); // Sets lastLogin with a default empty string.
    }

    /**
     * Gets the user ID associated with the authentication record.
     *
     * @return The user ID.
     */
    public Integer getUserId() {
        return userId.get();
    }

    /**
     * Sets the user ID associated with the authentication record.
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
     * Gets the email address of the user.
     *
     * @return The email address.
     */
    public String getEmail() {
        return email.get();
    }

    /**
     * Sets the email address of the user.
     *
     * @param email The new email address.
     */
    public void setEmail(String email) {
        this.email.set(email);
    }

    /**
     * Returns the property object for email (useful for bindings).
     *
     * @return The email property.
     */
    public StringProperty emailProperty() {
        return email;
    }

    /**
     * Gets the hashed password of the user.
     *
     * @return The hashed password.
     */
    public String getPasswordHash() {
        return passwordHash.get();
    }

    /**
     * Sets the hashed password of the user.
     *
     * @param passwordHash The new hashed password.
     */
    public void setPasswordHash(String passwordHash) {
        this.passwordHash.set(passwordHash);
    }

    /**
     * Returns the property object for passwordHash (useful for bindings).
     *
     * @return The passwordHash property.
     */
    public StringProperty passwordHashProperty() {
        return passwordHash;
    }

    /**
     * Gets the timestamp when the user account was created.
     *
     * @return The creation timestamp.
     */
    public String getCreatedAt() {
        return createdAt.get();
    }

    /**
     * Sets the timestamp when the user account was created.
     *
     * @param createdAt The new creation timestamp.
     */
    public void setCreatedAt(String createdAt) {
        this.createdAt.set(createdAt);
    }

    /**
     * Returns the property object for createdAt (useful for bindings).
     *
     * @return The createdAt property.
     */
    public StringProperty createdAtProperty() {
        return createdAt;
    }

    /**
     * Gets the timestamp of the user's last login.
     *
     * @return The last login timestamp.
     */
    public String getLastLogin() {
        return lastLogin.get();
    }

    /**
     * Sets the timestamp of the user's last login.
     *
     * @param lastLogin The new last login timestamp.
     */
    public void setLastLogin(String lastLogin) {
        this.lastLogin.set(lastLogin);
    }

    /**
     * Returns the property object for lastLogin (useful for bindings).
     *
     * @return The lastLogin property.
     */
    public StringProperty lastLoginProperty() {
        return lastLogin;
    }

    /**
     * Overrides the toString method to return a string representation of the UserAuth object.
     * Includes all properties for easier debugging and logging.
     *
     * @return A string representation of the UserAuth object.
     */
    @Override
    public String toString() {
        return "UserAuth{" +
                "userId=" + userId.get() +
                ", email=" + email.get() +
                ", passwordHash=" + passwordHash.get() +
                ", createdAt=" + createdAt.get() +
                ", lastLogin=" + lastLogin.get() +
                '}';
    }
}