package com.project.ordernote.data.model;

public class OrderDetails_Model {
    private String OrderId;
    private String OrderStatus;

    public OrderDetails_Model() {
    }

    public OrderDetails_Model(String orderStatus, String orderId) {
        OrderStatus = orderStatus;
        OrderId = orderId;
    }

    public String getOrderId() {
        return OrderId;
    }

    public void setOrderId(String orderId) {
        OrderId = orderId;
    }

    public String getOrderStatus() {
        return OrderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        OrderStatus = orderStatus;
    }
}

