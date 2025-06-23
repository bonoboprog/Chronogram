package it.unicas.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import java.util.Date;

public final class JwtUtil {

    // Legge la chiave segreta dall'ambiente.
    private static final String SECRET_KEY = System.getenv("JWT_SECRET_KEY");
    // Il token scade dopo 24 ore (in millisecondi)
    private static final long EXPIRATION_TIME = 86_400_000; // 24 * 60 * 60 * 1000

    private static final Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);

    private JwtUtil() {}

    /**
     * Genera un nuovo JWT per un dato utente.
     * @param email L'email dell'utente, usata come "subject" del token.
     * @return una stringa che rappresenta il JWT.
     */
    public static String generateToken(String email) {
        if (SECRET_KEY == null) {
            throw new IllegalStateException("JWT Secret Key is not configured in environment variables.");
        }
        
        return JWT.create()
                .withSubject(email) // Identifica a chi appartiene il token
                .withIssuedAt(new Date()) // Data di emissione
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // Data di scadenza
                .sign(algorithm); // Firma il token con la nostra chiave segreta
    }
    
    // In futuro, aggiungerai qui un metodo per validare un token ricevuto
    // public static String validateTokenAndGetSubject(String token) { ... }
}
