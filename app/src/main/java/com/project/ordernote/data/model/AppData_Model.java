package com.project.ordernote.data.model;

import java.util.List;
import java.util.Map;

public class AppData_Model {

    private List<String> paymentmode;

    private Map<String, Object> currentversiondetails;


    public List<String> getPaymentmode() {
        return paymentmode;
    }

    public void setPaymentmode(List<String> paymentmode) {
        this.paymentmode = paymentmode;
    }

    public Map<String, Object> getCurrentversiondetails() {
        return currentversiondetails;
    }

    public void setCurrentversiondetails(Map<String, Object> currentversiondetails) {
        this.currentversiondetails = currentversiondetails;
    }
}
