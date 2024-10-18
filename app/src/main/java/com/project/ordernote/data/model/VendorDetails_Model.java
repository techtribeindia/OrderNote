package com.project.ordernote.data.model;

import com.google.firebase.Timestamp;

public class VendorDetails_Model {
    private String vendorkey = "";
    private String vendorname = "";
    private String address1 = "";
    private String address2 = "";
    private String city = "";
    private String mobileno = "";
    private String pincode = "";
    private String state = "";
    private Timestamp orderdeletiontriggeredon;
    private int orderExpiryDays = 0;
    private int orderdeletionintervaldays = 0;


    public Timestamp getOrderdeletiontriggeredon() {
        return orderdeletiontriggeredon;
    }

    public void setOrderdeletiontriggeredon(Timestamp orderdeletiontriggeredon) {
        this.orderdeletiontriggeredon = orderdeletiontriggeredon;
    }

    public int getOrderExpiryDays() {
        return orderExpiryDays;
    }

    public void setOrderExpiryDays(int orderExpiryDays) {
        this.orderExpiryDays = orderExpiryDays;
    }

    public int getOrderdeletionintervaldays() {
        return orderdeletionintervaldays;
    }

    public void setOrderdeletionintervaldays(int orderdeletionintervaldays) {
        this.orderdeletionintervaldays = orderdeletionintervaldays;
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

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getMobileno() {
        return mobileno;
    }

    public void setMobileno(String mobileno) {
        this.mobileno = mobileno;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
