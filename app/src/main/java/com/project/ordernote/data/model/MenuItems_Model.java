package com.project.ordernote.data.model;

public class MenuItems_Model {

    int grossweight = 0 ;
    double priceperkg = 0 , unitprice = 0 ;
    boolean showforbilling = true;
    String vendorkey = "" , vendorname = "" , itemtype = "" , itemname = "" , portionsize = "" , key = "";

    public int getGrossweight() {
        return grossweight;
    }

    public void setGrossweight(int grossweight) {
        this.grossweight = grossweight;
    }

    public double getPriceperkg() {
        return priceperkg;
    }

    public void setPriceperkg(double priceperkg) {
        this.priceperkg = priceperkg;
    }

    public double getUnitprice() {
        return unitprice;
    }

    public void setUnitprice(double unitprice) {
        this.unitprice = unitprice;
    }

    public boolean isShowforbilling() {
        return showforbilling;
    }

    public void setShowforbilling(boolean showforbilling) {
        this.showforbilling = showforbilling;
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

    public String getItemtype() {
        return itemtype;
    }

    public void setItemtype(String itemtype) {
        this.itemtype = itemtype;
    }

    public String getItemname() {
        return itemname;
    }

    public void setItemname(String itemname) {
        this.itemname = itemname;
    }

    public String getPortionsize() {
        return portionsize;
    }

    public void setPortionsize(String portionsize) {
        this.portionsize = portionsize;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
