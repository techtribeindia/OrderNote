package com.project.ordernote.data.model;

public class Users_Model {
    private String mobileno;
    private String password;
    private Boolean loginResult;
    private String loginMessage;

    public Users_Model(String mobileno, String password, Boolean loginResult, String loginMessage) {
        this.mobileno = mobileno;
        this.password = password;
        this.loginResult = loginResult;
        this.loginMessage = loginMessage;
    }

    public Users_Model(Boolean loginResult, String loginMessage) {
        this.loginResult = loginResult;
        this.loginMessage = loginMessage;
    }

    public String getMobileno() {
        return mobileno;
    }

    public void setMobileno(String mobileno) {
        this.mobileno = mobileno;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getLoginResult() {
        return loginResult;
    }

    public void setLoginResult(Boolean loginResult) {
        this.loginResult = loginResult;
    }

    public String getLoginMessage() {
        return loginMessage;
    }

    public void setLoginMessage(String loginMessage) {
        this.loginMessage = loginMessage;
    }
}
