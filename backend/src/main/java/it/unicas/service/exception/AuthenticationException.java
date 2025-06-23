package it.unicas.service.exception;

/**
 * Un'eccezione custom per rappresentare errori specifici del processo di autenticazione,
 * come credenziali non valide, account bloccati o inattivi.
 * Estende Exception, rendendola una "checked exception" che forza il chiamante a gestirla.
 */
public class AuthenticationException extends Exception {

    /**
     * Costruttore che accetta un messaggio di errore.
     * @param message Il messaggio che descrive la causa dell'errore di autenticazione.
     */
    public AuthenticationException(String message) {
        super(message);
    }

    /**
     * Costruttore che accetta un messaggio e una causa (per il wrapping di altre eccezioni).
     * @param message Il messaggio che descrive la causa dell'errore.
     * @param cause L'eccezione originale che ha causato questo errore.
     */
    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
