package com.project.ordernote.data.model;

public class ReportsItemwiseCalculationDetail_Model {
    String menuitemkey = "" , menuitemname = "" , menuitemtype = "";
    double totalweight = 0 , totalprice = 0;
    int totalquantity =  0;


    public String getMenuitemtype() {
        return menuitemtype;
    }

    public void setMenuitemtype(String menuitemtype) {
        this.menuitemtype = menuitemtype;
    }

    public int getTotalquantity() {
        return totalquantity;
    }

    public void setTotalquantity(int totalquantity) {
        this.totalquantity = totalquantity;
    }


    public double getTotalprice() {
        return totalprice;
    }

    public void setTotalprice(double totalprice) {
        this.totalprice = totalprice;
    }

    public double getTotalweight() {
        return totalweight;
    }

    public void setTotalweight(double totalweight) {
        this.totalweight = totalweight;
    }

    public String getMenuitemname() {
        return menuitemname;
    }

    public void setMenuitemname(String menuitemname) {
        this.menuitemname = menuitemname;
    }

    public String getMenuitemkey() {
        return menuitemkey;
    }

    public void setMenuitemkey(String menuitemkey) {
        this.menuitemkey = menuitemkey;
    }
}
