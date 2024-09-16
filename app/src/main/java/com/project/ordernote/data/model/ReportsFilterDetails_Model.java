package com.project.ordernote.data.model;

import com.google.firebase.Timestamp;

public class ReportsFilterDetails_Model {


    private String selectedBuyerKey =""   ,selectedBuyerName = "" ;
    private String vendorkey =""  ;
    private String selectedFilterType =""  ;
    private String selectedFileType =""  ;
    Timestamp startDate;
    Timestamp endDate;


    public String getSelectedBuyerName() {
        return selectedBuyerName;
    }

    public void setSelectedBuyerName(String selectedBuyerName) {
        this.selectedBuyerName = selectedBuyerName;
    }

    public String getSelectedBuyerKey() {
        return selectedBuyerKey;
    }

    public void setSelectedBuyerKey(String selectedBuyerKey) {
        this.selectedBuyerKey = selectedBuyerKey;
    }

    public String getVendorkey() {
        return vendorkey;
    }

    public void setVendorkey(String vendorkey) {
        this.vendorkey = vendorkey;
    }

    public String getSelectedFilterType() {
        return selectedFilterType;
    }

    public void setSelectedFilterType(String selectedFilterType) {
        this.selectedFilterType = selectedFilterType;
    }

    public String getSelectedFileType() {
        return selectedFileType;
    }

    public void setSelectedFileType(String selectedFileType) {
        this.selectedFileType = selectedFileType;
    }

    public Timestamp getStartDate() {
        return startDate;
    }

    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

    public Timestamp getEndDate() {
        return endDate;
    }

    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }
}
