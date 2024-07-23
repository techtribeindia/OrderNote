package com.project.ordernote.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.DocumentSnapshot;
import com.project.ordernote.data.model.OrderDetails_Model;
import com.project.ordernote.data.remote.FirestoreService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    public LiveData<List<Map<String, Object>>> getOrdersByStatus(String status) {
        MutableLiveData<List<Map<String, Object>>> ordersWithStatusResult = new MutableLiveData<>();
        firestoreService.fetchOrdersByStatus(status, orderWithStatusDocument -> {
            List<Map<String, Object>> ordersData = new ArrayList<>();
            for (DocumentSnapshot document : orderWithStatusDocument.getDocuments()) {
                ordersData.add(document.getData());
            }
            ordersWithStatusResult.setValue(ordersData);
        });
        return ordersWithStatusResult;
    }

    public void addOrder(OrderDetails_Model order, FirestoreService.FirestoreCallback<Void> callback) {
        firestoreService.addOrder(order, callback);
    }



}