package com.project.ordernote.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.project.ordernote.data.model.OrderDetails_Model;
import com.project.ordernote.data.remote.FirestoreService;

import java.util.List;

public class OrderDetails_Repository {

    private final FirestoreService firestoreService;

    public OrderDetails_Repository() {
        firestoreService = new FirestoreService();
    }

    public LiveData<List<OrderDetails_Model>> getOrdersByDate(String startDate, String endDate) {
        MutableLiveData<List<OrderDetails_Model>> ordersLiveData = new MutableLiveData<>();
        firestoreService.fetchOrdersByDate(startDate, endDate, new FirestoreService.FirestoreCallback<List<OrderDetails_Model>>() {
            @Override
            public void onSuccess(List<OrderDetails_Model> result) {
                ordersLiveData.postValue(result);
            }

            @Override
            public void onFailure(Exception e) {
                // Handle failure
            }
        });
        return ordersLiveData;
    }

    public LiveData<List<OrderDetails_Model>> getOrdersByStatus(String status) {
        MutableLiveData<List<OrderDetails_Model>> ordersLiveData = new MutableLiveData<>();
        firestoreService.fetchOrdersByStatus(status, new FirestoreService.FirestoreCallback<List<OrderDetails_Model>>() {
            @Override
            public void onSuccess(List<OrderDetails_Model> result) {
                ordersLiveData.postValue(result);
            }

            @Override
            public void onFailure(Exception e) {
                // Handle failure
            }
        });
        return ordersLiveData;
    }

    public void addOrder(OrderDetails_Model order, FirestoreService.FirestoreCallback<Void> callback) {
        firestoreService.addOrder(order, callback);
    }
}