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
 * Handles user registration in a single DB transaction.
 * Exposes a minimal JSON response (success + message) through RegistrationResponse.
 */
public class RegistrationAction extends ActionSupport {

    private static final Logger logger = LogManager.getLogger(RegistrationAction.class);

    /* --------- form-bound fields (set by Struts JSON interceptor) --------- */
    private String name;
    private String surname;
    private String phone;
    private String email;
    private String password;
    private String birthday;   // dd-MM-yyyy
    private String gender;

    /* --------- response wrapper exposed to the frontend --------- */
    private RegistrationResponse registrationResponse;
    public RegistrationResponse getRegistrationResponse() {
        return registrationResponse;
    }

    /* --------- execute --------- */
    @Override
    public String execute() {
        logger.info("Registration attempt for {}", email);

        /* 1. basic validation */
        if (isBlank(email) || isBlank(password) || isBlank(name) || isBlank(surname)) {
            setFailure("All required fields must be filled.");
            return SUCCESS;
        }

        UserAuthDAO authDAO = new UserAuthDAO();
        UserDAO      userDAO = new UserDAO();

        /* 2. DTO preparation */
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());

        UserAuthDTO authDTO = new UserAuthDTO();
        authDTO.setEmail(email);
        authDTO.setPasswordHash(BCrypt.hashpw(password, BCrypt.gensalt()));
        authDTO.setUsername(name + " " + surname);
        authDTO.setCreatedAt(now);
        authDTO.setUpdatedAt(now);
        authDTO.setIsActive(1);

        UserDTO userDTO = new UserDTO();
        userDTO.setGender(gender);
        userDTO.setAddress(phone);          // (placeholder)
        userDTO.setBirthday(parseBirthday(birthday));
        userDTO.setCreatedAt(now);
        userDTO.setUpdatedAt(now);

        /* 3. transactional block */
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);

            // uniqueness check inside the Tx
            if (authDAO.getUserAuthByEmail(email, conn) != null) {
                setFailure("Email already registered.");
                return SUCCESS;
            }

            int userId = authDAO.insertUserAuth(authDTO, conn);
            if (userId == -1) {
                conn.rollback();
                setFailure("Could not create authentication record.");
                return SUCCESS;
            }

            userDTO.setUserId(userId);
            if (userDAO.insertUser(userDTO, conn) == -1) {
                conn.rollback();
                setFailure("Could not create user profile.");
                return SUCCESS;
            }

            conn.commit();
            setSuccess("Registration successful!");
            logger.info("User {} registered (user_id={})", email, userId);

        } catch (SQLException ex) {
            logger.error("DB error during registration", ex);
            setFailure("Registration failed due to a system error.");
        }

        return SUCCESS;
    }

    /* --------- helper methods --------- */
    private void setSuccess(String msg) {
        registrationResponse = new RegistrationResponse(true, msg);
    }
    private void setFailure(String msg) {
        registrationResponse = new RegistrationResponse(false, msg);
        logger.warn(msg);
    }
    private boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }

    private Date parseBirthday(String str) {
        if (isBlank(str)) return null;
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yyyy");
            fmt.setLenient(false);
            return new Date(fmt.parse(str).getTime());
        } catch (Exception e) {
            logger.warn("Invalid birthday '{}'", str);
            return null;
        }
    }

    /* --------- inner DTO exposed as JSON --------- */
    public static class RegistrationResponse {
        private final boolean success;
        private final String  message;

        public RegistrationResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
    }

    /* --------- setters required by Struts2  --------- */
    public void setName(String name)         { this.name = name; }
    public void setSurname(String surname)   { this.surname = surname; }
    public void setPhone(String phone)       { this.phone = phone; }
    public void setEmail(String email)       { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setBirthday(String birthday) { this.birthday = birthday; }
    public void setGender(String gender)     { this.gender = gender; }
}
