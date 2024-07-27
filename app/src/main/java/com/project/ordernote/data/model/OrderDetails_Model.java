package com.project.ordernote.data.model;


import com.google.firebase.Timestamp;

import org.json.JSONArray;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class OrderDetails_Model {

    String orderid = "";
    String buyername ="";

    String buyeraddress ="";
    String buyergstin = "";
    String buyerkey = "";
    double discount =0;

    JSONArray itemDetailsJsonArray = new JSONArray();
    String orderplaceddate = "";

    double price = 0 ;
    String status = "";
    String tokenno = "";
    double totalprice =0;
    String vendorkey = "";
    String vendorname ="";

    double totalqty = 0;
    public OrderDetails_Model()
    {

    }
    public OrderDetails_Model(String orderid, String buyername, String buyeraddress, String buyergstin, String buyerkey, double discount, List<Map<String, Object>> itemdetails, Timestamp orderplaceddate, double price, String status, String tokenno, double totalprice, String vendorkey, String vendorname, double totalqty) {
        this.orderid = orderid;
        this.buyername = buyername;
        this.buyeraddress = buyeraddress;
        this.buyergstin = buyergstin;
        this.buyerkey = buyerkey;
        this.discount = discount;
        this.itemdetails = itemdetails;
        this.orderplaceddate = orderplaceddate;
        this.price = price;
        this.status = status;
        this.tokenno = tokenno;
        this.totalprice = totalprice;
        this.vendorkey = vendorkey;
        this.vendorname = vendorname;
        this.totalqty = totalqty;
    }

    public String getBuyername() {
        return buyername;
    }

    public double getTotalqty() {
        return totalqty;
    }

    public void setTotalqty(double totalqty) {
        this.totalqty = totalqty;
    }

    public void setBuyername(String buyername) {
        this.buyername = buyername;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getBuyeraddress() {
        return buyeraddress;
    }

    public void setBuyeraddress(String buyeraddress) {
        this.buyeraddress = buyeraddress;
    }

    public String getBuyergstin() {
        return buyergstin;
    }

    public void setBuyergstin(String buyergstin) {
        this.buyergstin = buyergstin;
    }

    public String getBuyerkey() {
        return buyerkey;
    }

    public void setBuyerkey(String buyerkey) {
        this.buyerkey = buyerkey;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }


    public JSONArray getItemDetailsJsonArray() {
        return itemDetailsJsonArray;
    }

    public void setItemDetailsJsonArray(JSONArray itemDetailsJsonArray) {
        this.itemDetailsJsonArray = itemDetailsJsonArray;
    }

    public String getOrderplaceddate() {
        return orderplaceddate;
    }

    public void setOrderplaceddate(String orderplaceddate) {

        this.orderplaceddate = orderplaceddate;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTokenno() {
        return tokenno;
    }

    public void setTokenno(String tokenno) {
        this.tokenno = tokenno;
    }

    public double getTotalprice() {
        return totalprice;
    }

    public void setTotalprice(double totalprice) {
        this.totalprice = totalprice;
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
}

