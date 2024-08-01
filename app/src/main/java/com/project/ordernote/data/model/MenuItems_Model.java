package com.project.ordernote.data.model;

import com.project.ordernote.utils.WeightConverter;

public class MenuItems_Model {

    int grossweight = 0 , quantity = 0;
    double priceperkg = 0 , unitprice = 0   ;
    boolean showforbilling = true;
    String vendorkey = "" , vendorname = "" , itemtype = "" , itemname = "" , portionsize = "" ,
            itemkey = "" , netweight = "";

    public double getQuantity() {
        return (double) quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = (int) quantity;
    }

    public String getNetweight() {
        return netweight;
    }

    public void setNetweight(String netweight) {
        this.netweight = netweight;
    }

    public double getGrossweight() {
        double grossweightinKg = 0;
             try{
                 grossweightinKg = Double.parseDouble(WeightConverter.ConvertGramsToKilograms(String.valueOf(grossweight)));
                 return  grossweightinKg;

             }
            catch (Exception e){

                e.printStackTrace();
                return  grossweight;

            }

    }

    public void setGrossweightAndConvertItToGrams(double grossweightinKg) {
        int grossweightingrams = 0;
        try{
            grossweightingrams = (int) (Double.parseDouble(WeightConverter.ConvertKilogramstoGrams(String.valueOf(grossweightinKg))));
            this.grossweight = grossweightingrams;

        }
        catch (Exception e){

            e.printStackTrace();
            this.grossweight =(int) grossweightinKg;

        }
        //  this.grossweight =(int) grossweight;
    }


    public void setGrossweight(double grossweight) {

        this.grossweight =(int) grossweight;
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

    public String getItemkey() {
        return itemkey;
    }

    public void setItemkey(String itemkey) {
        this.itemkey = itemkey;
    }
}
