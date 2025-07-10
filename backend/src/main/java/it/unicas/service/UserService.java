package it.unicas.service;

import it.unicas.dao.UserAuthDAO;

import java.sql.SQLException;

public class UserService {
    private static final UserService instance = new UserService();
    private final UserAuthDAO UserAuthDAO = new UserAuthDAO();

    private UserService() {}

    public static UserService getInstance() {
        return instance;
    }

    public Integer getUserIdByEmail(String email) throws SQLException {
        return UserAuthDAO.getUserIdByEmail(email);
    }
}
