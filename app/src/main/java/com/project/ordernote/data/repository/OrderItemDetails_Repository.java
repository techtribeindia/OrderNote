package com.project.ordernote.data.repository;

import com.project.ordernote.data.model.OrderItemDetails_Model;
import com.project.ordernote.data.remote.FirestoreService;

public class OrderItemDetails_Repository {

    private final FirestoreService firestoreService;

    public OrderItemDetails_Repository() {
        firestoreService = new FirestoreService();
    }

    public void addOrderItemDetailsInDB(OrderItemDetails_Model orderItemDetailsModel) {

        firestoreService.addOrderItemDetails(orderItemDetailsModel);

    }
}
