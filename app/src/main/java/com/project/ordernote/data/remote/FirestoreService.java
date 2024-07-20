package com.project.ordernote.data.remote;

import android.util.Log;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.project.ordernote.data.model.MenuItems_Model;
import com.project.ordernote.data.model.OrderDetails_Model;

import java.util.ArrayList;
import java.util.List;

public class FirestoreService {

    private  FirebaseFirestore db;

    public FirestoreService() {
        try{
            db = FirebaseFirestore.getInstance();
        }
        catch (Exception e){

            e.printStackTrace();
        }

    }

    public void fetchOrdersByDate(String startDate, String endDate, FirestoreCallback<List<OrderDetails_Model>> callback) {
        db.collection("orders")
                .whereGreaterThanOrEqualTo("orderDate", startDate)
                .whereLessThanOrEqualTo("orderDate", endDate)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<OrderDetails_Model> orderList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            OrderDetails_Model order = document.toObject(OrderDetails_Model.class);
                            orderList.add(order);
                        }
                        callback.onSuccess(orderList);
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }

    public void fetchOrdersByStatus(String status, FirestoreCallback<List<OrderDetails_Model>> callback) {
        db.collection("orders")
                .whereEqualTo("status", status)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<OrderDetails_Model> orderList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            OrderDetails_Model order = document.toObject(OrderDetails_Model.class);
                            orderList.add(order);
                        }
                        callback.onSuccess(orderList);
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }

    public void addOrder(OrderDetails_Model order, FirestoreCallback<Void> callback) {
        db.collection("orders")
                .add(order)
                .addOnSuccessListener(documentReference -> callback.onSuccess(null))
                .addOnFailureListener(callback::onFailure);
    }

    public void fetchMenuItemsUsingVendorkey(String vendorkey, FirestoreCallback<List<MenuItems_Model>> callback) {


        db.collection("MenuItems")
                .whereEqualTo("vendorkey", vendorkey)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<MenuItems_Model> menuList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            MenuItems_Model menu = document.toObject(MenuItems_Model.class);
                            Log.d("fetchmenuUsingvendorkey", menu.getItemname(), new Throwable("Errorrr"));
                            menuList.add(menu);
                        }
                        callback.onSuccess(menuList);
                    } else {
                        callback.onFailure(task.getException());
                    }
                });

    }

    public interface FirestoreCallback<T> {
        void onSuccess(T result);
        void onFailure(Exception e);
    }
}