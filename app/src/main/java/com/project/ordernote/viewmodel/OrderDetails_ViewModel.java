package com.project.ordernote.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.project.ordernote.data.model.OrderDetails_Model;
import com.project.ordernote.data.model.OrderItemDetails_Model;
import com.project.ordernote.data.remote.FirestoreService;
import com.project.ordernote.data.repository.OrderDetails_Repository;

import java.util.List;


public class OrderDetails_ViewModel extends AndroidViewModel {
    private final OrderDetails_Repository repository;

    public OrderDetails_ViewModel(@NonNull Application application) {
        super(application);
        repository = new OrderDetails_Repository();
    }

    public LiveData<List<OrderDetails_Model>> getOrdersByDate(String startDate, String endDate) {
        return repository.getOrdersByDate(startDate, endDate);
    }

    public LiveData<List<OrderDetails_Model>> getOrdersByStatus(String status) {
        return repository.getOrdersByStatus(status);
    }

    public void addOrder(OrderDetails_Model order, List<OrderItemDetails_Model> cartItems, double discountPercentage, FirestoreService.FirestoreCallback<Void> callback) {
        double totalPrice = calculateTotalPrice(cartItems);
        double discountAmount = totalPrice * (discountPercentage / 100);
        double payablePrice = totalPrice - discountAmount;

       // order.setTotalPrice(totalPrice);
       // order.setDiscountAmount(discountAmount);
      //  order.setPayablePrice(payablePrice);

        repository.addOrder(order, callback);
    }

    private double calculateTotalPrice(List<OrderItemDetails_Model> cartItems) {
        double total = 0;
        for (OrderItemDetails_Model item : cartItems) {
         //   total += item.getPrice() * item.getQuantity();
        }
        return total;
    }
}