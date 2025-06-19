package it.unicas.action;

import com.opensymphony.xwork2.ActionSupport;
import it.unicas.dao.UserAuthDAO;

public class UsernameExistsAction extends ActionSupport {

    private String username;   // ⇦ param da querystring
    private boolean exists;    // ⇦ verrà serializzato in JSON

    public void setUsername(String username) { this.username = username; }
    public boolean isExists() { return exists; }

    @Override
    public String execute() {
        exists = new UserAuthDAO().usernameExists(username);
        return SUCCESS;         // restituito come JSON
    }
}
