package it.unicas.service;

import it.unicas.dao.UserAuthDAO;
import it.unicas.dao.UserDAO;
import it.unicas.dbutil.DBUtil;
import it.unicas.dto.RegistrationDTO;
import it.unicas.dto.UserAuthDTO;
import it.unicas.dto.UserDTO;
import it.unicas.service.exception.ServiceException;
import it.unicas.service.exception.ValidationException;
import it.unicas.util.PasswordUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

/**
 * Gestisce la logica di business per la registrazione di un nuovo utente.
 */
public class RegistrationService {

    private static final Logger logger = LogManager.getLogger(RegistrationService.class);

    private final UserAuthDAO authDAO;
    private final UserDAO userDAO;

    public RegistrationService() {
        this.authDAO = new UserAuthDAO();
        this.userDAO = new UserDAO();
    }

    public void registerNewUser(RegistrationDTO data) throws ValidationException, ServiceException {
        validateRegistrationData(data);

        Timestamp now = Timestamp.valueOf(LocalDateTime.now());

        UserAuthDTO authDTO = new UserAuthDTO();
        authDTO.setEmail(data.getEmail());
        authDTO.setPasswordHash(PasswordUtil.getInstance().encode(data.getPassword()));
        authDTO.setUsername(data.getName() + " " + data.getSurname());
        authDTO.setCreatedAt(now);
        authDTO.setUpdatedAt(now);
        authDTO.setIsActive(1);

        UserDTO userDTO = new UserDTO();
        userDTO.setGender(data.getGender());
        userDTO.setAddress(data.getAddress()); // <-- CAMPO CORRETTO
        userDTO.setPhone(data.getPhone());  // <-- CAMPO CORRETTO
        userDTO.setName(data.getName());    // <-- CAMPO CORRETTO
        userDTO.setSurname(data.getSurname());  // <-- CAMPO CORRETTO
        userDTO.setBirthday(parseBirthday(data.getBirthday()));
        userDTO.setCreatedAt(now);
        userDTO.setUpdatedAt(now);

        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try {
                if (authDAO.getUserAuthByEmail(data.getEmail(), conn) != null) {
                    throw new ServiceException("Email already registered.");
                }

                int userId = authDAO.insertUserAuth(authDTO, conn);
                if (userId == -1) {
                    throw new ServiceException("Could not create authentication record.");
                }

                userDTO.setUserId(userId);
                if (userDAO.insertUser(userDTO, conn) == -1) {
                    throw new ServiceException("Could not create user profile.");
                }

                conn.commit();
                logger.info("User {} registered successfully with user_id={}", data.getEmail(), userId);

            } catch (Exception e) {
                conn.rollback();
                logger.warn("Registration transaction rolled back for email {}", data.getEmail(), e);
                throw new ServiceException(e.getMessage(), e);
            }
        } catch (SQLException e) {
            logger.error("Failed to get DB connection for registration", e);
            throw new ServiceException("Registration failed due to a system error.", e);
        }
    }

    private void validateRegistrationData(RegistrationDTO data) throws ValidationException {
        if (data.getName() == null || data.getName().trim().isEmpty() ||
                data.getSurname() == null || data.getSurname().trim().isEmpty() ||
                data.getEmail() == null || data.getEmail().trim().isEmpty() ||
                data.getPassword() == null || data.getPassword().trim().isEmpty()) {
            throw new ValidationException("All required fields must be filled.");
        }

        if (!data.getEmail().matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,}$")) {
            throw new ValidationException("Invalid email format.");
        }
        if (data.getPassword().length() < 8) {
            throw new ValidationException("Password must be at least 8 characters long.");
        }
    }

    private Date parseBirthday(String str) {
        if (str == null || str.trim().isEmpty()) return null;
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yyyy");
            fmt.setLenient(false);
            return new Date(fmt.parse(str).getTime());
        } catch (ParseException e) {
            logger.warn("Invalid birthday format '{}'", str);
            return null;
        }
    }
}
