package com.project.ordernote.data.model;

public class Users_Model {
    private String mobileno;
    private String password;
    private String name;
    private String role;
    private String vendorkey;
    private String vendorname;
    private String key;


    private Boolean loginResult;
    private String loginMessage;

    public Users_Model(String mobileno, String password, Boolean loginResult, String loginMessage) {
        this.mobileno = mobileno;
        this.password = password;
        this.loginResult = loginResult;
        this.loginMessage = loginMessage;
    }

    public Users_Model() {
    }

    public Users_Model(Boolean loginResult, String loginMessage) {
        this.loginResult = loginResult;
        this.loginMessage = loginMessage;
    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getVendorkey() {
        return vendorkey;
    }

    public void setVendorkey(String vendorkey) {
        this.vendorkey = vendorkey;
    }

    public String getVendorname() {
        return vendorname;
    }

    public void setVendorname(String vendorname) {
        this.vendorname = vendorname;
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
