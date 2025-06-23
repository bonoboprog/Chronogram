package it.unicas.service;

import it.unicas.dao.UserAuthDAO;
import it.unicas.dbutil.DBUtil;
import it.unicas.dto.UserAuthDTO;
import it.unicas.service.exception.AuthenticationException;
import it.unicas.util.JwtUtil;
import it.unicas.util.PasswordUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class LoginService {
    private static final Logger logger = LogManager.getLogger(LoginService.class);
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int LOCKOUT_DURATION_MINUTES = 15;

    private final UserAuthDAO userAuthDAO;

    public LoginService() {
        this.userAuthDAO = new UserAuthDAO();
    }
    
    // Un record per restituire più valori in modo pulito
    public record LoginResult(String username, String jwtToken) {}

    public LoginResult loginUser(String email, String password) throws AuthenticationException, SQLException {
        if (email == null || email.trim().isEmpty() || password == null || password.isEmpty()) {
            throw new AuthenticationException("Email and password are required.");
        }

        try (Connection conn = DBUtil.getConnection()) {
            UserAuthDTO user = userAuthDAO.getUserAuthByEmail(email, conn);

            if (user == null || user.getIsActive() == 0) {
                logger.warn("Login attempt for non-existent or inactive user: {}", email);
                throw new AuthenticationException("Invalid credentials.");
            }

            // Controlla se l'account è bloccato
            Timestamp lockedUntil = user.getLockedUntil();
            if (lockedUntil != null && lockedUntil.after(Timestamp.valueOf(LocalDateTime.now()))) {
                logger.warn("Login attempt on locked account: {}", email);
                throw new AuthenticationException("Account is locked. Please try again later.");
            }

            // Verifica la password
            if (PasswordUtil.getInstance().matches(password, user.getPasswordHash())) {
                // Successo!
                logger.info("Login successful for {}", email);
                userAuthDAO.resetLoginAttempts(user.getUserId(), conn); // Azzera i tentativi falliti
                
                // Genera il JWT
                String jwtToken = JwtUtil.generateToken(email);
                
                return new LoginResult(user.getUsername(), jwtToken);
            } else {
                // Fallimento!
                logger.warn("Wrong password for {}", email);
                int newAttempts = user.getFailedLoginAttempts() + 1;
                Timestamp newLockout = null;
                String message = "Invalid credentials.";
                
                if (newAttempts >= MAX_FAILED_ATTEMPTS) {
                    newLockout = Timestamp.valueOf(LocalDateTime.now().plusMinutes(LOCKOUT_DURATION_MINUTES));
                    message = "Account locked due to too many failed attempts.";
                    logger.warn("Account for {} locked.", email);
                }
                
                userAuthDAO.updateFailedLoginAttempt(user.getUserId(), newAttempts, newLockout, conn);
                throw new AuthenticationException(message);
            }
        }
    }
}
