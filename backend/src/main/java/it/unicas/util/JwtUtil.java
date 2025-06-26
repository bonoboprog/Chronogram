package it.unicas.util;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

import java.util.Date;

public final class JwtUtil {

    private static final String SECRET_KEY = System.getenv("JWT_SECRET_KEY");
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

    /**
     * Valida un token JWT ricevuto e restituisce l'oggetto DecodedJWT.
     * @param token Il token JWT da validare.
     * @return Un oggetto DecodedJWT se la validazione ha successo.
     * @throws JWTVerificationException Se il token non è valido (es. firma errata, scaduto, ecc.).
     */
    public static DecodedJWT validateToken(String token) throws JWTVerificationException {
        if (SECRET_KEY == null) {
            throw new IllegalStateException("JWT Secret Key is not configured in environment variables.");
        }

        JWTVerifier verifier = JWT.require(algorithm)
                .build(); // Reusable verifier instance
        return verifier.verify(token);
    }
}