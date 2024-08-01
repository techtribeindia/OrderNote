package com.project.ordernote.utils.calculations;

import com.project.ordernote.data.model.ItemDetails_Model;
import com.project.ordernote.data.model.OrderDetails_Model;
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

    public static OrderDetails_Model CalculateOrderDetailsValue(List<ItemDetails_Model> value, double discountValue) {
        OrderDetails_Model orderDetailsModel = new OrderDetails_Model();

        try {
            double totalPrice = 0.0;
           // double discount = 0.0;
            double finalPrice = 0.0;

          for (ItemDetails_Model item : value) {
              double itemPrice = 0.0;

              itemPrice = item.getTotalprice();
              totalPrice = totalPrice + itemPrice;

          }
           // discount = ((discountPercen/100)*totalPrice);
            finalPrice = totalPrice - discountValue;
            orderDetailsModel.setPrice(totalPrice);
            orderDetailsModel.setDiscount(discountValue);
            orderDetailsModel.setTotalprice(finalPrice);


            return orderDetailsModel;
      }
      catch (Exception e){
          e.printStackTrace();
          return new OrderDetails_Model();

      }

    }
}
