package it.unicas.action;

import com.opensymphony.xwork2.ActionSupport;
import it.unicas.dao.UserDAO;
import it.unicas.dao.UserAuthDAO;
import it.unicas.dbutil.DBUtil;
import it.unicas.dto.UserDTO;
import it.unicas.dto.UserAuthDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

/**
 * RegistrationAction now performs user_auth and user inserts in a single transaction.
 * If any step fails, the whole operation is rolled back, ensuring database consistency.
 */
public class RegistrationAction extends ActionSupport {

    private static final Logger logger = LogManager.getLogger(RegistrationAction.class);

    // Form-bound properties
    private String name;
    private String surname;
    private String phone;
    private String email;
    private String password;
    private String birthday; // Expected format: dd-MM-yyyy
    private String gender;

    // Response to frontend
    private boolean success;
    private String message;

    // === Getters & Setters ===
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
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }

    @Override
    public String execute() {
        logger.info("Registration attempt for email: {}", email);

        // 1. Basic validation
        if (isBlank(email) || isBlank(password) || isBlank(name) || isBlank(surname)) {
            fail("All required fields must be filled.");
            logger.warn("Validation failed: missing required fields");
            return SUCCESS;
        }

        UserAuthDAO authDAO = new UserAuthDAO();
        UserDAO userDAO = new UserDAO();

        // 2. Prepare DTOs
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());

        UserAuthDTO authDTO = new UserAuthDTO();
        authDTO.setEmail(email);
        authDTO.setPasswordHash(hashedPassword);
        authDTO.setUsername(name + " " + surname);
        authDTO.setCreatedAt(now);
        authDTO.setUpdatedAt(now);
        authDTO.setLastLogin(null);
        authDTO.setIsActive(1);
        authDTO.setFailedLoginAttempts(0);
        authDTO.setLockedUntil(null);

        UserDTO userDTO = new UserDTO();
        userDTO.setGender(gender);
        userDTO.setAddress(phone); // Reuse phone as address
        userDTO.setNotes("");
        userDTO.setBirthday(parseBirthday(birthday));
        userDTO.setCreatedAt(now);
        userDTO.setUpdatedAt(now);

        // 3. Transactional execution
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);

            // 🔎 Email uniqueness check inside the transaction
            if (authDAO.getUserAuthByEmail(email, conn) != null) {
                fail("Email already registered.");
                logger.warn("Registration failed: email {} already exists", email);
                return SUCCESS;
            }

            int userId = authDAO.insertUserAuth(authDTO, conn);
            if (userId == -1) {
                conn.rollback();
                fail("Registration failed: could not create authentication record.");
                return SUCCESS;
            }

            userDTO.setUserId(userId);
            int uid = userDAO.insertUser(userDTO, conn);
            if (uid == -1) {
                conn.rollback();
                fail("Registration failed: could not create user profile.");
                logger.error("UserAuth inserted but UserDTO failed (user_id={})", userId);
                return SUCCESS;
            }

            conn.commit();
            this.success = true;
            this.message = "Registration successful!";
            logger.info("User registered successfully (user_id={})", uid);

        } catch (SQLException e) {
            this.success = false;
            this.message = "Registration failed due to a system error.";
            logger.error("Database error during registration", e);
        }

        return SUCCESS;
    }

    // === Helpers ===
    private void fail(String msg) {
        this.success = false;
        this.message = msg;
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private Date parseBirthday(String birthdayStr) {
        if (isBlank(birthdayStr)) return null;
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yyyy");
            fmt.setLenient(false);
            java.util.Date utilDate = fmt.parse(birthdayStr);
            logger.debug("Birthday parsed: {}", utilDate);
            return new Date(utilDate.getTime());
        } catch (Exception ex) {
            logger.warn("Invalid birthday format: {}", birthdayStr);
            return null;
        }
    }
}
