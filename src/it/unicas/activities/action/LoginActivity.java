package it.unicas.activities.action;


import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;

import it.unicas.activities.dao.UserAuthDAO;
import it.unicas.activities.pojo.UserAuth;

public class LoginActivity extends ActionSupport {
    private String email;
    private String password;

    public String execute() {
        String statusCode = "";
        boolean isUserValid = UserAuthDAO.isUserValid(new UserAuth(email, password));
        if (isUserValid) {
            ServletActionContext.getRequest().getSession().setAttribute("loggedinUser", email);
            statusCode = SUCCESS;
        } else {
            statusCode = INPUT;
        }
        return statusCode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}