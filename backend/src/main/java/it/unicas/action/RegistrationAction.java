package it.unicas.action;

import com.opensymphony.xwork2.ActionSupport;
import it.unicas.dao.UserDAO;
import it.unicas.dao.UserAuthDAO;
import it.unicas.dto.UserDTO;
import it.unicas.dto.UserAuthDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mindrot.jbcrypt.BCrypt; // For password hashing

import java.sql.Date; // For birthday
import java.text.SimpleDateFormat;
import java.sql.Timestamp; // For Timestamp
import java.time.LocalDateTime;

public class RegistrationAction extends ActionSupport {

    private static final Logger logger = LogManager.getLogger(RegistrationAction.class);

    // Properties to bind form data from frontend
    private String name;
    private String surname;
    private String phone;
    private String email;
    private String password;
    private String birthday; // Frontend sends as String, will convert to java.sql.Date
    private String gender;
    // Note: weekly_income fields are excluded as per user request

    // Response properties for frontend
    private boolean success;
    private String message;

    // Getters and Setters for form properties (required by Struts2)
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getBirthday() { return birthday; }
    public void setBirthday(String birthday) { this.birthday = birthday; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    // Getters for response properties
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }

    @Override
    public String execute() {
        logger.info("Registration attempt for email: {}", email);

        // 1. Server-Side Validation (Basic)
        if (email == null || email.trim().isEmpty() ||
                password == null || password.trim().isEmpty() ||
                name == null || name.trim().isEmpty() ||
                surname == null || surname.trim().isEmpty()) {
            this.success = false;
            this.message = "All required fields must be filled.";
            logger.warn("Validation failed: Missing required fields for registration.");
            return SUCCESS; // Struts2 convention for JSON result
        }

        // 2. Secure Password Hashing with BCrypt
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        logger.debug("Password hashed successfully.");

        // Get current timestamp for created_at and updated_at
        LocalDateTime now = LocalDateTime.now();
        Timestamp currentTimestamp = Timestamp.valueOf(now);

        // 3. Prepare UserDTO and UserAuthDTO
        UserDTO userDTO = new UserDTO();
        userDTO.setGender(gender);
        userDTO.setAddress(phone); // Using phone for address field, or create new field in DB
        userDTO.setNotes(""); // Default empty notes
        userDTO.setCreatedAt(currentTimestamp); // Set new field
        userDTO.setUpdatedAt(currentTimestamp); // Set new field

        // Convert frontend birthday string (dd-MM-yyyy) to java.sql.Date
        Date sqlBirthday = null;
        if (birthday!= null &&!birthday.trim().isEmpty()) {
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                formatter.setLenient(false); // Strict parsing
                java.util.Date utilDate = formatter.parse(birthday);
                sqlBirthday = new Date(utilDate.getTime());
                logger.debug("Birthday converted to SQL Date: {}", sqlBirthday);
            } catch (Exception e) {
                logger.error("Invalid birthday format received: {}", birthday, e);
                this.success = false;
                this.message = "Invalid birthday format. Please use dd-MM-yyyy.";
                return SUCCESS;
            }
        }
        userDTO.setBirthday(sqlBirthday);

        UserAuthDTO userAuthDTO = new UserAuthDTO();
        userAuthDTO.setEmail(email);
        userAuthDTO.setPasswordHash(hashedPassword); // Corrected method call
        userAuthDTO.setUsername(name + " " + surname); // Mirror username from UserDTO

        // Set created_at, updated_at, last_login, is_active, failed_login_attempts, locked_until
        userAuthDTO.setCreatedAt(currentTimestamp);
        userAuthDTO.setUpdatedAt(currentTimestamp); // Set updated_at to current time on creation
        userAuthDTO.setLastLogin(null); // Set to null on registration, updated on first login
        userAuthDTO.setIsActive(1); // Default to active
        userAuthDTO.setFailedLoginAttempts(0); // Default to zero failed attempts
        userAuthDTO.setLockedUntil(null); // Default to not locked

        // 4. Database Interaction via DAOs
        UserDAO userDAO = new UserDAO();
        UserAuthDAO userAuthDAO = new UserAuthDAO();

        // Check for duplicate email/username before insertion
        if (userAuthDAO.getUserAuthByEmail(email) != null) {
            this.success = false;
            this.message = "Email already registered.";
            logger.warn("Registration failed: Email {} already exists.", email);
            return SUCCESS;
        }

        // Step 1: Insert into user_auth and retrieve generated user_id
        int userId = userAuthDAO.insertUserAuth(userAuthDTO);
        if (userId != -1) {
            userDTO.setUserId(userId); // Set FK
            int insertedUserId = userDAO.insertUser(userDTO);

            if (insertedUserId != -1) {
                this.success = true;
                this.message = "Registration successful!";
                logger.info("User registered successfully with ID: {}", insertedUserId);
            } else {
                this.success = false;
                this.message = "Registration failed: Could not create user profile.";
                logger.error("UserAuth created but failed to insert UserDTO for user_id: {}", userId);
                // Optional: rollback auth insert if you implement transaction management
            }

        } else {
            this.success = false;
            this.message = "Registration failed: Could not create authentication record.";
            logger.error("Failed to create UserAuth record for email: {}", email);
        }

        return SUCCESS;

    }
}
