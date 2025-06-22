package it.unicas.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public final class PasswordUtil {
    private static final BCryptPasswordEncoder INSTANCE = new BCryptPasswordEncoder();

    private PasswordUtil() {}

    public static BCryptPasswordEncoder getInstance() {
        return INSTANCE;
    }
}
