package it.unicas.dto;

import java.util.Objects;

/**
 * Un DTO immutabile per restituire il risultato di un login andato a buon fine,
 * contenente le informazioni necessarie per il client.
 */
public final class LoginResultDTO {

    private final String email;
    private final String jwtToken;

    public LoginResultDTO(String email, String jwtToken) {
        this.email = email;
        this.jwtToken = jwtToken;
    }

    public String getEmail() {
        return email;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoginResultDTO that = (LoginResultDTO) o;
        return Objects.equals(email, that.email) && Objects.equals(jwtToken, that.jwtToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, jwtToken);
    }

    @Override
    public String toString() {
        return "LoginResultDTO[" +
               "email='" + email + '\'' +
               ", jwtToken='[REDACTED]'" +
               ']';
    }
}
