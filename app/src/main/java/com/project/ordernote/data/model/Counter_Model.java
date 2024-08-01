package com.project.ordernote.data.model;

import android.util.Log;

import java.util.Objects;

public class Counter_Model {
    long orderno = 0 ;

    public long getOrderno() {
        return orderno;
    }

    public void setOrderno(long orderno) {
        Log.i("orderno setOrderno : ", String.valueOf(Objects.requireNonNull(orderno)));

        this.orderno = orderno;
    }
}
