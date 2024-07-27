package com.project.ordernote.utils.calculations;

import com.project.ordernote.data.model.OrderItemDetails_Model;

import java.util.List;

public class OrderValueCalculator {



    public static double calculateTotalPrice(List<OrderItemDetails_Model> items) {
        double totalPrice = 0.0;
        for (OrderItemDetails_Model item : items) {
           // totalPrice += item.getPrice() * item.getQuantity();
        }
        return totalPrice;
    }
}
