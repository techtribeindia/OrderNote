package com.project.ordernote.viewmodel;

import android.app.Application;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

 import com.project.ordernote.data.model.ItemDetails_Model;
 import com.google.gson.Gson;
 
import com.project.ordernote.data.model.MenuItems_Model;

import com.project.ordernote.data.model.Buyers_Model;

import com.project.ordernote.data.model.OrderDetails_Model;
import com.project.ordernote.data.model.OrderItemDetails_Model;
import com.project.ordernote.data.remote.FirestoreService;
import com.project.ordernote.data.repository.OrderDetails_Repository;
import com.project.ordernote.utils.ApiResponseState_Enum;
import com.project.ordernote.utils.calculations.OrderValueCalculator;

import java.util.ArrayList;
import java.util.List;

import java.util.Map;
import java.util.Objects;


public class OrderDetails_ViewModel extends AndroidViewModel {
    private final OrderDetails_Repository repository;

    private MutableLiveData<ApiResponseState_Enum<List<OrderDetails_Model>>> orderDetailsLiveData;
    private MutableLiveData<String> selectedOrderJson;

    private Observer<ApiResponseState_Enum<List<OrderDetails_Model>>> ordersObserver;

    private MutableLiveData<List<ItemDetails_Model>> itemDetailsArrayListLiveData;
    private Observer<ApiResponseState_Enum<List<OrderDetails_Model>>> ordersObserver;



    public OrderDetails_ViewModel(@NonNull Application application) {
        super(application);
        repository = new OrderDetails_Repository();
        orderDetailsLiveData = new MutableLiveData<>();
 
        itemDetailsArrayListLiveData = new MutableLiveData<>();
 
        selectedOrderJson = new MutableLiveData<>();
        initObserver();
    }

    private void initObserver() {
        ordersObserver = state -> orderDetailsLiveData.setValue(state);
     }



    private void initObserver() {
        ordersObserver = state -> orderDetailsLiveData.setValue(state);
    }

    public LiveData<ApiResponseState_Enum<List<OrderDetails_Model>>> getOrdersByStatusFromViewModel() {
        return orderDetailsLiveData;
    }
    public LiveData<ApiResponseState_Enum<List<OrderDetails_Model>>> getOrdersListFromViewModel() {
        if(orderDetailsLiveData == null){
            orderDetailsLiveData = new MutableLiveData<>();
        }
        return orderDetailsLiveData;
    }
    public LiveData<List<ItemDetails_Model>> getItemDetailsArraylistViewModel() {
        if(itemDetailsArrayListLiveData == null){
            itemDetailsArrayListLiveData = new MutableLiveData<>();
        }
        return itemDetailsArrayListLiveData;
    }

/*
    public void getOrdersByStatus(String status) {
        try {
       //     orderDetailsLiveData = repository.getOrdersByStatus(status);
         //   Log.d("orderdetails getOrdersByStatus  :  ", String.valueOf(Objects.requireNonNull(orderDetailsLiveData.getValue()).data));

        LiveData<ApiResponseState_Enum<List<OrderDetails_Model>>> source = repository.getOrdersByStatus(status);
        source.observeForever(ordersObserver);


        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

 */




    public void getOrdersByStatus(String status) {

      //  orderDetailsLiveData = repository.getOrdersByStatus(status);
        LiveData<ApiResponseState_Enum<List<OrderDetails_Model>>> source = repository.getOrdersByStatus(status);
        source.observeForever(ordersObserver);
    }





    public LiveData<List<Map<String, Object>>> getOrdersByStatus1(String status) {
        return repository.getOrdersByStatus1(status);
    }
    public LiveData<List<OrderDetails_Model>> getOrdersByDate(String startDate, String endDate) {
        return repository.getOrdersByDate(startDate, endDate);
    }



    public void addOrder(OrderDetails_Model order, List<OrderItemDetails_Model> cartItems, double discountPercentage, FirestoreService.FirestoreCallback<Void> callback) {
          double payablePrice = OrderValueCalculator.calculateTotalPrice(cartItems);



        repository.addOrder(order, callback);
    }
    public void setSelectedOrder(OrderDetails_Model order) {
        Gson gson = new Gson();
        String orderJson = gson.toJson(order);
        selectedOrderJson.setValue(orderJson);
    }

    public MutableLiveData<ApiResponseState_Enum<String>> acceptOrder(String transporName, String driverMobieno, String truckNo, String status, String orderId) {
        MutableLiveData<ApiResponseState_Enum<String>> resultLiveData = repository.acceptOrder(transporName, driverMobieno, truckNo, status, orderId);
        resultLiveData.observeForever(result -> {
            if (result != null && result.status == ApiResponseState_Enum.Status.SUCCESS) {
                removeOrderFromLiveData(orderId);
            }
        });
        return resultLiveData;
    }

    public MutableLiveData<ApiResponseState_Enum<String>> rejectOrder(String status, String orderId) {
        MutableLiveData<ApiResponseState_Enum<String>> resultLiveData = repository.rejectOrder(status, orderId);
        resultLiveData.observeForever(result -> {
            if (result != null && result.status == ApiResponseState_Enum.Status.SUCCESS) {
                removeOrderFromLiveData(orderId);
            }
        });
        return resultLiveData;
    }

    public void removeOrderFromLiveData(String orderId) {

  
 


        ApiResponseState_Enum<List<OrderDetails_Model>> currentData = orderDetailsLiveData.getValue();
        if (currentData != null && currentData.data != null) {
            List<OrderDetails_Model> updatedOrders = new ArrayList<>(currentData.data);
            for (OrderDetails_Model order : updatedOrders) {
                if (order.getOrderid().equals(orderId)) {
                    updatedOrders.remove(order);
                    break;
                }
            }

            orderDetailsLiveData.setValue(ApiResponseState_Enum.success(updatedOrders));
          //  orderDetailsLiveData.observeForever(ordersObserver);

            //orderDetailsLiveData.setValue(new ApiResponseState_Enum.Status.SUCCESS, updatedOrders, null));
        }
    }

    public LiveData<String> getSelectedOrderJson() {
        return selectedOrderJson;
    }

    @Override
    protected void onCleared() {
        super.onCleared();

        orderDetailsLiveData.removeObserver(ordersObserver);
    }
 
}