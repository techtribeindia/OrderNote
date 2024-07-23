package com.project.ordernote.data.remote;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import com.google.firebase.firestore.QuerySnapshot;

import com.project.ordernote.data.model.Buyers_Model;
import com.project.ordernote.data.model.MenuItems_Model;

import com.project.ordernote.data.model.OrderDetails_Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FirestoreService {

    private final FirebaseFirestore db;

    public FirestoreService() {
        db = FirebaseFirestore.getInstance();
    }

    public void fetchBuyersListUsingVendorkey(String vendorKey, FirestoreCallback<List<Buyers_Model>> callback) {

        db.collection("BuyerDetails")
                .whereEqualTo("vendorkey", vendorKey)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Buyers_Model> menuList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Buyers_Model buyer = document.toObject(Buyers_Model.class);
                            Log.d("fetchBuyerlistUsingvendorkey", buyer.getName()
                            );
                            menuList.add(buyer);
                        }
                        callback.onSuccess(menuList);
                    } else {
                        callback.onFailure(task.getException());
                    }
                });


    }

    public interface LoginCallback {
        void onLoginResult(boolean isSuccess,String result, QueryDocumentSnapshot userDocument);
    }

    public interface fetchOrdersWithStatusCallback{
        void onOrdersWithStatusResult(QuerySnapshot orderWithStatusDocument);
    }

    public void fetchOrdersWithStatus(fetchOrdersWithStatusCallback callback)
    {
        db.collection("OrderDetails")
                .whereEqualTo("status", "ORDERCONFIRMED")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (!querySnapshot.isEmpty()) {
                            callback.onOrdersWithStatusResult(querySnapshot);
                        }
                    }
                });
    }
    public void userDetailsFetch(String mobileNumber, String password, LoginCallback callback) {
        db.collection("UserDetails")
                .whereEqualTo("mobileno", mobileNumber)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (!querySnapshot.isEmpty()) {
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                String dbPassword = document.getString("password");
                                String dbRole = document.getString("role");
                                Boolean isBlocked = false;
                                if(Objects.equals(dbRole, ""))
                                {
                                    isBlocked  = true;
                                }
                                else if(Objects.requireNonNull(dbRole).toLowerCase().equals("BLOCKED"))
                                {
                                    isBlocked  = true;
                                }
                                if (password.equals(dbPassword)) {
                                    if(!isBlocked)
                                    {
                                        callback.onLoginResult(true,"Logged in SuccessFully", document);
                                    }
                                    else {
                                        callback.onLoginResult(true,"You do not have access to Login, Please Contact Admin", document);
                                    }
                                }
                                else {
                                    callback.onLoginResult(false,"Please enter the currect password", null);
                                }
                            }
                        } else {
                            callback.onLoginResult(false,"The entered mobile number is not in the database", null);
                        }
                    } else {
                        callback.onLoginResult(false,"Something went wrong, Please check the internet connection and try again", null);
                    }
                });
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

    public void fetchOrdersByStatus(String status, fetchOrdersWithStatusCallback callback) {
        db.collection("OrderDetails")
                .whereEqualTo("status", status)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (!querySnapshot.isEmpty()) {
                            callback.onOrdersWithStatusResult(querySnapshot);
                        }
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
                            Log.d("fetchmenuUsingvendorkey", menu.getItemname());
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