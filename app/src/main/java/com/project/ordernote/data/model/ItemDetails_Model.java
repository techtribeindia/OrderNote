package com.project.ordernote.data.model;

public class ItemDetails_Model {
    String itemname = "" ;
    String menuitemkey = "" ;
    String menutype = "" ;
    String netweight = "" ;
    double totalprice = 0 ;
    double price = 0 ;
    double grossweight = 0 ;
    int quantity = 0;

    public ItemDetails_Model() {}


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
        this.grossweight = grossweight;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
