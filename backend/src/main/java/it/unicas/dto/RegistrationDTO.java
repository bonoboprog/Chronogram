package it.unicas.dto;

import java.util.Objects;
public final class RegistrationDTO {

    private final String username; // <-- NEW FIELD
    private final String name;
    private final String surname;
    private final String phone;
    private final String email;
    private final String password;
    private final String birthday;
    private final String gender;
    private final String address;


    public RegistrationDTO(String username, String name, String surname, String phone, String email,
                           String password, String birthday, String gender, String address) { // <-- UPDATED
        this.username = username; // <-- NEW FIELD
        this.name = name;
        this.surname = surname;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.birthday = birthday;
        this.gender = gender;
        this.address = address;
    }

    // Getters
    public String getUsername() { return username; } // <-- NEW GETTER
    public String getName() { return name;
    }
    public String getSurname() { return surname; }
    public String getPhone() { return phone;
    }
    public String getEmail() { return email; }
    public String getPassword() { return password;
    }
    public String getBirthday() { return birthday; }
    public String getGender() { return gender;
    }
    public String getAddress() { return address;
    }

    // equals
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RegistrationDTO)) return false;
        RegistrationDTO that = (RegistrationDTO) o;
        return Objects.equals(username, that.username) && // <-- UPDATED
                Objects.equals(name, that.name) &&
                Objects.equals(surname, that.surname) &&
                Objects.equals(phone, that.phone) &&
                Objects.equals(email, that.email) &&
                Objects.equals(password, that.password) &&
                Objects.equals(birthday, that.birthday) &&

                Objects.equals(gender, that.gender) &&
                Objects.equals(address, that.address);

    }

    @Override
    public int hashCode() {
        return Objects.hash(username, name, surname, phone, email, password, birthday, gender, address); // <-- UPDATED

    }

    @Override
    public String toString() {
        return "RegistrationDTO[" +
                "username='" + username + '\'' + // <-- UPDATED
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", phone='" + phone + '\'' +

                ", email='" + email + '\'' +
                ", password='[REDACTED]'" +
                ", birthday='" + birthday + '\'' +
                ", gender='" + gender + '\'' +
                ", address='" + address + '\'' +

                ']';
    }
}