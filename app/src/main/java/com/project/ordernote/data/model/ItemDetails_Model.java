package com.project.ordernote.data.model;

import com.project.ordernote.utils.WeightConverter;

public class ItemDetails_Model {
     String itemname = "" ;
    String menuitemkey = "" ;
    String menutype = "" ;
    String netweight = "" ;
    String portionsize = "" ;


    double menuitemprice = 0 ;
    double totalprice = 0 ;
    double price = 0 ;
    double grossweight = 0 ;
    int quantity = 0;
 

    public ItemDetails_Model() {}


    public double getMenuitemprice() {
        return menuitemprice;
    }

    public void setMenuitemprice(double menuitemprice) {
        this.menuitemprice = menuitemprice;
    }

    public String getPortionsize() {
        return portionsize;
    }

    public void setPortionsize(String portionsize) {
        this.portionsize = portionsize;
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



    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
