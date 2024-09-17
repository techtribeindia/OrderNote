package com.project.ordernote.data.model;

import com.google.firebase.Timestamp;


public class OrderItemDetails_Model {
     String itemname = "" ;
    String unqiuekey = "";
    String menuitemkey = "" ;
    String menutype = "" ;
    String netweight = "" ;
    String portionsize = "" ;
    String vendorkey ="";
    String vendorname = "";

  /*  String buyername ="";
    String buyeraddress ="";
     String buyerkey = "";
    String buyermobileno = "";
    String status = "";

   */


    String orderid = "";
    Timestamp orderplaceddate = null;

    double menuitemprice = 0 ;
    double totalprice = 0 ;
    double price = 0 ;
     double grossweight = 0 ;
    int quantity = 0;


    /*
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBuyername() {
        return buyername;
    }

    public void setBuyername(String buyername) {
        this.buyername = buyername;
    }

    public String getBuyeraddress() {
        return buyeraddress;
    }

    public void setBuyeraddress(String buyeraddress) {
        this.buyeraddress = buyeraddress;
    }

    public String getBuyermobileno() {
        return buyermobileno;
    }

    public void setBuyermobileno(String buyermobileno) {
        this.buyermobileno = buyermobileno;
    }


    public String getBuyerkey() {
        return buyerkey;
    }

    public void setBuyerkey(String buyerkey) {
        this.buyerkey = buyerkey;
    }


     */
    public double getMenuitemprice() {
        return menuitemprice;
    }

    public void setMenuitemprice(double menuitemprice) {
        this.menuitemprice = menuitemprice;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getUnqiuekey() {
        return unqiuekey;
    }

    public void setUnqiuekey(String unqiuekey) {
        this.unqiuekey = unqiuekey;
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

    public String getPortionsize() {
        return portionsize;
    }

    public void setPortionsize(String portionsize) {
        this.portionsize = portionsize;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getItemname() {
        return itemname;
    }

    public void setItemname(String itemname) {
        this.itemname = itemname;
    }

    public String getMenuitemkey() {
        return menuitemkey;
    }

    public void setMenuitemkey(String menuitemkey) {
        this.menuitemkey = menuitemkey;
    }

    public String getMenutype() {
        return menutype;
    }

    public void setMenutype(String menutype) {
        this.menutype = menutype;
    }

    public String getNetweight() {
        return netweight;
    }

    public void setNetweight(String netweight) {
        this.netweight = netweight;
    }

    public double getTotalprice() {
        return totalprice;
    }

    public void setTotalprice(double totalprice) {
        this.totalprice = totalprice;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getGrossweight() {
        return grossweight;
    }

    public void setGrossweight(double grossweight) {
        try{
            this.grossweight = grossweight;

        }
        catch (Exception e){
            e.printStackTrace();

        }
    }

    public Timestamp getOrderplaceddate() {
        return orderplaceddate;
    }

    public void setOrderplaceddate(Timestamp orderplaceddate) {
        this.orderplaceddate = orderplaceddate;
    }
}
