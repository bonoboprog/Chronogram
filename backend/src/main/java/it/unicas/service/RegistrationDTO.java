package it.unicas.dto; // <-- 1. PACKAGE MODIFICATO

import java.util.Objects;

/**
 * Un Data Transfer Object (DTO) immutabile per contenere i dati di registrazione.
 * Trasporta i dati dal form (Action) al Service Layer.
 */
public final class RegistrationDTO { // <-- 2. NOME DELLA CLASSE MODIFICATO

    private final String name;
    private final String surname;
    private final String phone;
    private final String email;
    private final String password;
    private final String birthday;
    private final String gender;

    public RegistrationDTO(String name, String surname, String phone, String email, String password, String birthday, String gender) { // <-- 3. NOME DEL COSTRUTTORE MODIFICATO
        this.name = name;
        this.surname = surname;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.birthday = birthday;
        this.gender = gender;
    }

    // Getters
    public String getName() { return name; }
    public String getSurname() { return surname; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getBirthday() { return birthday; }
    public String getGender() { return gender; }

    // Metodi standard: equals, hashCode, toString 
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegistrationDTO that = (RegistrationDTO) o; // <-- 4. TIPO INTERNO MODIFICATO
        return Objects.equals(name, that.name) &&
               Objects.equals(surname, that.surname) &&
               Objects.equals(phone, that.phone) &&
               Objects.equals(email, that.email) &&
               Objects.equals(password, that.password) &&
               Objects.equals(birthday, that.birthday) &&
               Objects.equals(gender, that.gender);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, surname, phone, email, password, birthday, gender);
    }

    @Override
    public String toString() {
        return "RegistrationDTO[" + // <-- 5. NOME NEL toString() MODIFICATO
               "name='" + name + '\'' +
               ", surname='" + surname + '\'' +
               ", phone='" + phone + '\'' +
               ", email='" + email + '\'' +
               ", password='[REDACTED]'" +
               ", birthday='" + birthday + '\'' +
               ", gender='" + gender + '\'' +
               ']';
    }
}
