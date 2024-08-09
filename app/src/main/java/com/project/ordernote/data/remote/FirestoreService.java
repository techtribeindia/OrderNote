package com.project.ordernote.data.remote;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.Firebase;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.QueryDocumentSnapshot;

import com.google.firebase.firestore.QuerySnapshot;

import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Transaction;
import com.project.ordernote.data.model.AppData_Model;
import com.project.ordernote.data.model.Buyers_Model;
import com.project.ordernote.data.model.Counter_Model;
import com.project.ordernote.data.model.MenuItems_Model;

import com.project.ordernote.data.model.OrderDetails_Model;
import com.project.ordernote.data.model.OrderItemDetails_Model;
import com.project.ordernote.utils.Constants;
import com.project.ordernote.utils.DatabaseReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public void fetchAppData(FirestoreCallback<AppData_Model> callback) {

        db.collection("AppData")
                 .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            AppData_Model appData_model = document.toObject(AppData_Model.class);
                            Log.d("fetchAppData", String.valueOf(appData_model.getPaymentmode().toString()));
                            callback.onSuccess(appData_model);

                        }
                    } else {
                        callback.onFailure(task.getException());
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

    public void acceptOrder(String transportName, String driverMobileNo, String truckNo, String orderId, String status, FirestoreCallback<String> callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference orderRef = db.collection("OrderDetails").document(orderId);

        Map<String, Object> updates = new HashMap<>();

        // Check if each parameter is not empty and add to the map if so
        if (transportName != null && !transportName.isEmpty()) {
            updates.put("transportname", transportName);
        }
        if (driverMobileNo != null && !driverMobileNo.isEmpty()) {
            updates.put("drivermobileno", driverMobileNo);
        }
        if (truckNo != null && !truckNo.isEmpty()) {
            updates.put("truckno", truckNo);
        }
        if (status != null && !status.isEmpty()) {
            updates.put("status", status);
        }
        updates.put("orderplaceddate", Timestamp.now());
        // Check if there are any updates to make
        if (!updates.isEmpty()) {
            orderRef.update(updates)
                    .addOnSuccessListener(aVoid -> {
                        callback.onSuccess("The order succfully accepted");
                    })
                    .addOnFailureListener(e -> {
                        callback.onFailure(e);
                    });
        } else {
            // No updates to make, you may want to handle this case
            callback.onFailure(new Exception("No orders found with orderid: " + orderId));
        }
    }


    public void rejectOrder( String orderid,String status, FirestoreCallback<String> callback)
    {
        DocumentReference orderRef = db.collection("OrderDetails").document(orderid);

        orderRef.update("status", status)
                .addOnSuccessListener(aVoid -> {
                    callback.onSuccess("The order status changed to rejected");
                    // Handle success, e.g., notify user, update UI
                })
                .addOnFailureListener(e -> {
                    callback.onFailure(e);
                    // Handle failure, e.g., notify user, retry
                });
    }

    public void cancelOrder( String orderid,String status, FirestoreCallback<String> callback)
    {
        DocumentReference orderRef = db.collection("OrderDetails").document(orderid);

        orderRef.update("status", status)
                .addOnSuccessListener(aVoid -> {
                    callback.onSuccess("Order Cancelled");
                    // Handle success, e.g., notify user, update UI
                })
                .addOnFailureListener(e -> {
                    callback.onFailure(e);
                    // Handle failure, e.g., notify user, retry
                });
    }

    public void placeOrder( String orderid,String status, FirestoreCallback<String> callback)
    {
        DocumentReference orderRef = db.collection("OrderDetails").document(orderid);

        orderRef.update("status", status)
                .addOnSuccessListener(aVoid -> {
                    callback.onSuccess("Order Placed");
                    // Handle success, e.g., notify user, update UI
                })
                .addOnFailureListener(e -> {
                    callback.onFailure(e);
                    // Handle failure, e.g., notify user, retry
                });
    }

    public void EditRequest( String orderid, FirestoreCallback<String> callback)
    {
        DocumentReference orderRef = db.collection("OrderDetails").document(orderid);

        orderRef.update("editrequest", true)
                .addOnSuccessListener(aVoid -> {
                    callback.onSuccess("Requested permission to edit the Dispatch Details");
                    // Handle success, e.g., notify user, update UI
                })
                .addOnFailureListener(e -> {
                    callback.onFailure(e);
                    // Handle failure, e.g., notify user, retry
                });
    }

    public void fetchOrdersByStatus(String status, FirestoreCallback<List<OrderDetails_Model>> callback)
    {

        db.collection("OrderDetails")
                .whereEqualTo("status", status)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (!querySnapshot.isEmpty()) {
                            List<OrderDetails_Model> orders = new ArrayList<>();
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                OrderDetails_Model order = document.toObject(OrderDetails_Model.class);
                                orders.add(order);

                            }
                            Log.d("fetchOrdersByStatus", orders.toString());
                            callback.onSuccess(orders);
                        } else {
                            // Handle empty result
                            callback.onFailure(new Exception("No orders found with status: " + status));
                        }
                    } else {
              
                        callback.onFailure(task.getException());
                    }
                });
    }

    public void getOrdersByStatusAndDate(String status, Timestamp startTimestamp, Timestamp endTimestamp, FirestoreCallback<List<OrderDetails_Model>> callback)
    {
        db.collection("OrderDetails")
                .whereEqualTo("status", status)
                .whereGreaterThanOrEqualTo("orderplaceddate", startTimestamp)
                .whereLessThanOrEqualTo("orderplaceddate", endTimestamp)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (!querySnapshot.isEmpty()) {
                            List<OrderDetails_Model> orders = new ArrayList<>();
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                OrderDetails_Model order = document.toObject(OrderDetails_Model.class);
                                orders.add(order);
                            }
                            callback.onSuccess(orders);
                        } else {
                            callback.onFailure(new Exception("No orders found with status: " + status + " and date range."));
                        }
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }


    public void createOrder(OrderDetails_Model orderDetailsModel,   FirestoreService.FirestoreCallback <Void>callback) {
        db.collection(DatabaseReference.OrderDetails_TableName)
                .document(orderDetailsModel.getOrderid())
                .set(orderDetailsModel)
                .addOnSuccessListener(documentReference -> callback.onSuccess(null))
                .addOnFailureListener(callback::onFailure);

    }

    public void fetchOrdersByStatus1(String status, fetchOrdersWithStatusCallback callback) {
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



    public void incrementOrderCounter(String vendorkey, FirestoreCallback callback) {
        DocumentReference counterRef = db.collection(DatabaseReference.Counter_TableName).document(vendorkey);

        db.runTransaction((Transaction.Function<Void>) transaction -> {
            DocumentSnapshot snapshot = transaction.get(counterRef);
            long newOrderNumber = snapshot.getLong(DatabaseReference.Orderno_CounterTable) + 1;
            transaction.update(counterRef, DatabaseReference.Orderno_CounterTable, newOrderNumber);

            // Return the new order number through the callback
            callback.onSuccess(newOrderNumber);

            return null;
        }).addOnFailureListener(e -> {
            // Handle failure through the callback
            callback.onFailure(e);
        });
    }

    public void getOrderCounterAndIncrementLocally(String vendorkey, FirestoreCallback<Long> callback) {
        db.collection(DatabaseReference.Counter_TableName).document(vendorkey)
                 .get()
                .addOnCompleteListener(task -> {
                    Log.i("orderno taskcomplete : ", String.valueOf(Objects.requireNonNull(task)));

                    if (task.isSuccessful()) {
                        Log.i("orderno task.isSuccessful() : ", String.valueOf(Objects.requireNonNull(task.isSuccessful())));




                            Counter_Model appData_model = task.getResult().toObject(Counter_Model.class);
                            Log.d("orderno fetchordernoData", String.valueOf(Objects.requireNonNull(appData_model).getOrderno()));
                            callback.onSuccess(appData_model.getOrderno());


                    } else {
                        callback.onFailure(task.getException());
                        Log.i("orderno taskFailure: ", String.valueOf(Objects.requireNonNull(task.getException())));
                    }
                });

    }

    public void addOrderItemDetails(OrderItemDetails_Model orderItemDetailsModel) {

        db.collection(DatabaseReference.OrderItemDetails_TableName)
                .document(orderItemDetailsModel.getUnqiuekey())
                .set(orderItemDetailsModel);




    }

    public void addBuyerInDB(Buyers_Model buyersModel, FirestoreCallback<Void> callback) {


        db.collection(DatabaseReference.BuyerDetails_TableName)
                .document(buyersModel.getUniquekey())
                .set(buyersModel)
                .addOnSuccessListener(documentReference -> callback.onSuccess(null))
                .addOnFailureListener(callback::onFailure);

    }

    public void deleteBuyerDetails(String buyerkey, FirestoreCallback<Void> callback) {

        db.collection(DatabaseReference.BuyerDetails_TableName)
                .document(buyerkey)
                .delete()
                .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                .addOnFailureListener(callback::onFailure);

    }

    public void updateBuyerInDB(Buyers_Model buyersModel, FirestoreCallback<Void> callback) {
        db.collection(DatabaseReference.BuyerDetails_TableName)
                .document(buyersModel.getUniquekey())
                .set(buyersModel, SetOptions.merge())
                .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                .addOnFailureListener(callback::onFailure);
    }


    public interface FirestoreCallback<T> {
        void onSuccess(T result);
        void onFailure(Exception e);
    }


    public interface LoginCallback {
        void onLoginResult(boolean isSuccess,String result, QueryDocumentSnapshot userDocument);
    }

    public interface fetchOrdersWithStatusCallback{
        void onOrdersWithStatusResult(QuerySnapshot orderWithStatusDocument);
    }


}