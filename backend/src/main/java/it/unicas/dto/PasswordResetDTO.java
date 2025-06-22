package it.unicas.dto;

import java.sql.Timestamp;

/**
 * Data Transfer Object che rappresenta un record della tabella `password_resets`.
 * Aggiornato per supportare il pattern di sicurezza Selector/Verifier.
 */
public class PasswordResetDTO {

    // Campi che mappano le colonne della tabella `password_resets`
    private int tokenId; // Corrisponde a 'token_id' o 'id' (PK)
    private int userId;
    private String selector;      // Nuovo campo: parte pubblica del token, indicizzata per la ricerca
    private String verifierHash;  // Nuovo campo: hash della parte privata del token
    private Timestamp expirationTime;
    private Timestamp createdAt;

    /**
     * Costruttore di default.
     */
    public PasswordResetDTO() {
    }

    /**
     * Costruttore con tutti i campi.
     */
    public PasswordResetDTO(int tokenId, int userId, String selector, String verifierHash, Timestamp expirationTime, Timestamp createdAt) {
        this.tokenId = tokenId;
        this.userId = userId;
        this.selector = selector;
        this.verifierHash = verifierHash;
        this.expirationTime = expirationTime;
        this.createdAt = createdAt;
    }

    // --- Getters e Setters ---

    public int getTokenId() {
        return tokenId;
    }

    public void setTokenId(int tokenId) {
        this.tokenId = tokenId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getSelector() {
        return selector;
    }

    public void setSelector(String selector) {
        this.selector = selector;
    }

    public String getVerifierHash() {
        return verifierHash;
    }

    public void setVerifierHash(String verifierHash) {
        this.verifierHash = verifierHash;
    }

    public Timestamp getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(Timestamp expirationTime) {
        this.expirationTime = expirationTime;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "PasswordResetDTO{" +
                "tokenId=" + tokenId +
                ", userId=" + userId +
                ", selector='" + selector + '\'' +
                ", verifierHash='[REDACTED]'" + // Non loggare mai l'hash completo
                ", expirationTime=" + expirationTime +
                ", createdAt=" + createdAt +
                '}';
    }
}
