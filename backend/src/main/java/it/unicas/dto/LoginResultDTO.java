package it.unicas.dto;

import java.util.Objects;

/**
 * Un DTO immutabile per restituire il risultato di un login andato a buon fine,
 * contenente le informazioni necessarie per il client.
 */
public final class LoginResultDTO {

    private final String username;
    private final String jwtToken;

    public LoginResultDTO(String username, String jwtToken) {
        this.username = username;
        this.jwtToken = jwtToken;
    }

    public String getUsername() {
        return username;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoginResultDTO that = (LoginResultDTO) o;
        return Objects.equals(username, that.username) && Objects.equals(jwtToken, that.jwtToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, jwtToken);
    }

    @Override
    public String toString() {
        return "LoginResultDTO[" +
               "username='" + username + '\'' +
               ", jwtToken='[REDACTED]'" +
               ']';
    }
}
