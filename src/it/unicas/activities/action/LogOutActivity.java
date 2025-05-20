package it.unicas.activities.action;


import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;

public class LogoutActivity extends ActionSupport {

    public String execute() {
        ServletActionContext.getRequest().getSession().invalidate();
        return INPUT;
    }
}
