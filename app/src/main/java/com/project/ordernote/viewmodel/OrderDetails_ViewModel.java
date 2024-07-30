package com.project.ordernote.viewmodel;

import android.app.Application;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.project.ordernote.data.model.ItemDetails_Model;
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

    private MutableLiveData<List<ItemDetails_Model>> itemDetailsArrayListLiveData;
    private Observer<ApiResponseState_Enum<List<OrderDetails_Model>>> ordersObserver;



    public OrderDetails_ViewModel(@NonNull Application application) {
        super(application);
        repository = new OrderDetails_Repository();
        orderDetailsLiveData = new MutableLiveData<>();
        itemDetailsArrayListLiveData = new MutableLiveData<>();
        initObserver();


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
            orderDetailsLiveData = repository.getOrdersByStatus(status);
            Log.d("orderdetails getOrdersByStatus  :  ", String.valueOf(Objects.requireNonNull(orderDetailsLiveData.getValue()).data));
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


    @Override
    protected void onCleared() {
        super.onCleared();
        // Remove the observer to avoid memory leaks
        orderDetailsLiveData.removeObserver(ordersObserver);

     }

}