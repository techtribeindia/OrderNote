package com.project.ordernote.viewmodel;

import android.app.Application;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.project.ordernote.data.model.MenuItems_Model;

import com.project.ordernote.data.model.Buyers_Model;

import com.project.ordernote.data.model.OrderDetails_Model;
import com.project.ordernote.data.model.OrderItemDetails_Model;
import com.project.ordernote.data.remote.FirestoreService;
import com.project.ordernote.data.repository.OrderDetails_Repository;
import com.project.ordernote.utils.ApiResponseState_Enum;
import com.project.ordernote.utils.calculations.OrderValueCalculator;

import java.util.List;

import java.util.Map;
import java.util.Objects;


public class OrderDetails_ViewModel extends AndroidViewModel {
    private final OrderDetails_Repository repository;

    private MutableLiveData<ApiResponseState_Enum<List<OrderDetails_Model>>> orderDetailsLiveData;

    public OrderDetails_ViewModel(@NonNull Application application) {
        super(application);
        repository = new OrderDetails_Repository();
        orderDetailsLiveData = new MutableLiveData<>();
    }

    public LiveData<List<OrderDetails_Model>> getOrdersByDate(String startDate, String endDate) {
        return repository.getOrdersByDate(startDate, endDate);
    }

    public LiveData<ApiResponseState_Enum<List<OrderDetails_Model>>> getOrdersByStatusFromViewModel() {
        return orderDetailsLiveData;
    }

    public void getOrdersByStatus(String status) {
        try {
            orderDetailsLiveData = repository.getOrdersByStatus(status);
            Log.d("orderdetails getOrdersByStatus  :  ", String.valueOf(Objects.requireNonNull(orderDetailsLiveData.getValue()).data));
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public LiveData<ApiResponseState_Enum<List<OrderDetails_Model>>> getOrdersListFromViewModel() {
        if(orderDetailsLiveData == null){
            orderDetailsLiveData = new MutableLiveData<>();
        }
        return orderDetailsLiveData;
    }
    public LiveData<List<Map<String, Object>>> getOrdersByStatus1(String status) {
        return repository.getOrdersByStatus1(status);
    }
    public void addOrder(OrderDetails_Model order, List<OrderItemDetails_Model> cartItems, double discountPercentage, FirestoreService.FirestoreCallback<Void> callback) {
     //   double totalPrice = calculateTotalPrice(cartItems);
     //   double discountAmount = totalPrice * (discountPercentage / 100);
        double payablePrice = OrderValueCalculator.calculateTotalPrice(cartItems);

       // order.setTotalPrice(totalPrice);
       // order.setDiscountAmount(discountAmount);
      //  order.setPayablePrice(payablePrice);

        repository.addOrder(order, callback);
    }


}