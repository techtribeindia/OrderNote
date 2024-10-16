package com.project.ordernote.data.remote;

import android.util.Log;

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
import com.project.ordernote.data.model.VendorDetails_Model;
import com.project.ordernote.utils.BaseActivity;
import com.project.ordernote.utils.Constants;
import com.project.ordernote.utils.DatabaseReference;
import com.project.ordernote.utils.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FirestoreService {

    private FirebaseFirestore db;
    private SessionManager sessionManager;
    public String vendorkey;

    public FirestoreService() {
            db = FirebaseFirestore.getInstance();


    }
    public  void setUserDetails(String vendorkey)
    {
        this.vendorkey = vendorkey;
    }

    public void fetchBuyersListUsingVendorkey(String vendorKey, FirestoreCallback<List<Buyers_Model>> callback) {

        if(BaseActivity.baseActivity.isOnline()) {

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
        else{
            callback.onFailure(new Exception(" No Internet Connection "));
        }

    }

  //changes made by arun directlt

    public void fetchAppData(FirestoreCallback<AppData_Model> callback) {

        if(BaseActivity.baseActivity.isOnline()) {

            db.collection("AppData")
                 .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            AppData_Model appData_model = document.toObject(AppData_Model.class);
                            Log.d("fetchAppData from arun firestore", String.valueOf(appData_model.getPaymentmode().toString()));
                            callback.onSuccess(appData_model);

                        }
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
        }
        else{
            callback.onFailure(new Exception(" No Internet Connection "));
        }

    }


    public void getUserDetails(String mobileNumber, LoginCallback callback) {
        if(BaseActivity.baseActivity.isOnline()) {

            db.collection(DatabaseReference.UserDetails_TableName)
                .whereEqualTo(DatabaseReference.mobileno_UserDetails, mobileNumber)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (!querySnapshot.isEmpty()) {
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                callback.onLoginResult(true,"Logged in SuccessFully", document);

                            }
                        } else {
                            callback.onLoginResult(false,"The entered mobile number is not in the database", null);
                        }
                    } else {
                        callback.onLoginResult(false,"Something went wrong, Please check the internet connection and try again", null);
                    }
                });
        }
        else{
            callback.onLoginResult(false,"Something went wrong, Please check the internet connection and try again", null);
        }

    }

    public void userDetailsFetchAndCheckUserStatus(String mobileNumber, String password, LoginCallback callback) {
        if(BaseActivity.baseActivity.isOnline()) {

            db.collection(DatabaseReference.UserDetails_TableName)
                .whereEqualTo(DatabaseReference.mobileno_UserDetails, mobileNumber)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (!querySnapshot.isEmpty()) {
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                String dbPassword = document.getString(DatabaseReference.password_UserDetails);
                                String dbRole = document.getString(DatabaseReference.role_UserDetails);
                                Boolean isBlocked = false;
                                if(Objects.equals(dbRole, ""))
                                {
                                    isBlocked  = true;
                                }
                                else if(Objects.requireNonNull(dbRole).toLowerCase().equals(Constants.blocked_role))
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
                                    callback.onLoginResult(false,"Please enter the correct password", null);
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
        else{
            callback.onLoginResult(false,"Something went wrong, Please check the internet connection and try again", null);
        }
    }
    public void fetchOrdersByDate(String startDate, String endDate, FirestoreCallback<List<OrderDetails_Model>> callback) {

        if(BaseActivity.baseActivity.isOnline()) {

            db.collection("orders")
                .whereGreaterThanOrEqualTo("orderDate", startDate)
                .whereLessThanOrEqualTo("orderDate", endDate)
                .whereEqualTo("vendorkey", vendorkey)
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
        else{
            callback.onFailure(new Exception(" No Internet Connection "));
        }

    }

    public void acceptOrder(String orderId, String status, FirestoreCallback<String> callback) {

        if(BaseActivity.baseActivity.isOnline()) {

            FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference orderRef = db.collection(DatabaseReference.OrderDetails_TableName).document(orderId);

        Map<String, Object> updates = new HashMap<>();

        if (status != null && !status.isEmpty()) {
            updates.put(DatabaseReference.status_OrderDetails, status);
        }
        updates.put(DatabaseReference.orderacceptedddate_OrderDetails, Timestamp.now());
        // Check if there are any updates to make
        if (!updates.isEmpty()) {
            orderRef.update(updates)
                    .addOnSuccessListener(aVoid -> {
                        callback.onSuccess("The order successfully accepted");
                    })
                    .addOnFailureListener(e -> {
                        callback.onFailure(e);
                    });
        } else {
            // No updates to make, you may want to handle this case
            callback.onFailure(new Exception("No orders found with orderid: " + orderId));
        }

        }
        else{
            callback.onFailure(new Exception(" No Internet Connection "));
        }

    }

    public void updateBatchDetails(String orderid,String transporName, String driverMobieno, String truckNo, FirestoreCallback<String> callback) {
        if(BaseActivity.baseActivity.isOnline()) {

            FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference orderRef = db.collection(DatabaseReference.OrderDetails_TableName).document(orderid);

        Map<String, Object> updates = new HashMap<>();

        // Check if each parameter is not empty and add to the map if so
        if (transporName != null && !transporName.isEmpty()) {
            updates.put(DatabaseReference.transportname_OrderDetails, transporName);
        }
        if (driverMobieno != null && !driverMobieno.isEmpty()) {
            updates.put(DatabaseReference.drivermobileno_OrderDetails, driverMobieno);
        }
        if (truckNo != null && !truckNo.isEmpty()) {
            updates.put(DatabaseReference.truckno_OrderDetails, truckNo);
        }
        updates.put(DatabaseReference.dispatchstatus_OrderDetails, Constants.dispatched_dispatchstatus);

        // Check if there are any updates to make
        if (!updates.isEmpty()) {
            orderRef.update(updates)
                    .addOnSuccessListener(aVoid -> {
                        callback.onSuccess("Dispatch Successfully Updated");
                    })
                    .addOnFailureListener(e -> {
                        callback.onFailure(e);
                    });
        } else {
            // No updates to make, you may want to handle this case
            callback.onFailure(new Exception("No orders found with orderid: " + orderid));
        }

    }
        else{
        callback.onFailure(new Exception(" No Internet Connection "));
    }

}


    public void rejectOrder( String orderid,String status, FirestoreCallback<String> callback)
    {

        if(BaseActivity.baseActivity.isOnline()) {
            Map<String, Object> updates = new HashMap<>();
            DocumentReference orderRef = db.collection(DatabaseReference.OrderDetails_TableName).document(orderid);
            updates.put(DatabaseReference.status_OrderDetails, status);
            updates.put(DatabaseReference.orderrejectedddate_OrderDetails, Timestamp.now());
            orderRef.update(updates)
                .addOnSuccessListener(aVoid -> {
                    callback.onSuccess("The order status changed to rejected");
                    // Handle success, e.g., notify user, update UI
                })
                .addOnFailureListener(e -> {
                    callback.onFailure(e);
                    // Handle failure, e.g., notify user, retry
                });
        }
        else{
            callback.onFailure(new Exception(" No Internet Connection "));
        }

    }

    public void cancelOrder( String orderid,String status, FirestoreCallback<String> callback)
    {
        if(BaseActivity.baseActivity.isOnline()) {

            DocumentReference orderRef = db.collection(DatabaseReference.OrderDetails_TableName).document(orderid);
            Map<String, Object> updates = new HashMap<>();
            updates.put(DatabaseReference.status_OrderDetails, status);
            updates.put(DatabaseReference.orderrecancelledddate_OrderDetails, Timestamp.now());
            orderRef.update(updates)
                .addOnSuccessListener(aVoid -> {
                    callback.onSuccess("Order Cancelled");
                    // Handle success, e.g., notify user, update UI
                })
                .addOnFailureListener(e -> {
                    callback.onFailure(e);
                    // Handle failure, e.g., notify user, retry
                });
    }
        else{
        callback.onFailure(new Exception(" No Internet Connection "));
    }

}

    public void placeOrder( String orderid,String status, FirestoreCallback<String> callback)
    {
        if(BaseActivity.baseActivity.isOnline()) {

            DocumentReference orderRef = db.collection(DatabaseReference.OrderDetails_TableName).document(orderid);
            Map<String, Object> updates = new HashMap<>();
            updates.put(DatabaseReference.status_OrderDetails, status);
            updates.put(DatabaseReference.orderplaceddate_OrderDetails, Timestamp.now());
            orderRef.update(updates)
                .addOnSuccessListener(aVoid -> {
                    callback.onSuccess("Order Placed");
                    // Handle success, e.g., notify user, update UI
                })
                .addOnFailureListener(e -> {
                    callback.onFailure(e);
                    // Handle failure, e.g., notify user, retry
                });
        }
        else{
            callback.onFailure(new Exception(" No Internet Connection "));
        }

    }

    public void EditRequest( String orderid, String DispatchStatus, FirestoreCallback<String> callback)
    {
        if(BaseActivity.baseActivity.isOnline()) {

            DocumentReference orderRef = db.collection(DatabaseReference.OrderDetails_TableName).document(orderid);

        orderRef.update(DatabaseReference.dispatchstatus_OrderDetails, DispatchStatus)
                .addOnSuccessListener(aVoid -> {
                    callback.onSuccess("Requested permission to edit the Dispatch Details");
                    // Handle success, e.g., notify user, update UI
                })
                .addOnFailureListener(e -> {
                    callback.onFailure(e);
                    // Handle failure, e.g., notify user, retry
                });
    }
        else{
        callback.onFailure(new Exception(" No Internet Connection "));
    }

}

    public void fetchOrdersByStatus(String status, FirestoreCallback<List<OrderDetails_Model>> callback)
    {
        if(BaseActivity.baseActivity.isOnline()) {

        db.collection(DatabaseReference.OrderDetails_TableName)
                .whereEqualTo(DatabaseReference.status_OrderDetails, status)
                .whereEqualTo(DatabaseReference.vendorkey, vendorkey)
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
                            callback.onSuccess(new ArrayList<>());
                           // callback.onFailure(new Exception("No orders found with status: " + status));
                        }
                    } else {

                        callback.onFailure(task.getException());
                    }
                });
        }
        else{
            callback.onFailure(new Exception(" No Internet Connection "));
        }

    }


    public void getOrdersByDateAndVendorKey(Timestamp startdatee, Timestamp endDatee, String vendorkey, FirestoreCallback<List<OrderDetails_Model>> callback) {
        if(BaseActivity.baseActivity.isOnline()) {

            db.collection(DatabaseReference.OrderDetails_TableName)
                     .whereEqualTo(DatabaseReference.vendorkey, vendorkey)
                    .whereGreaterThanOrEqualTo(DatabaseReference.orderplaceddate_OrderDetails, startdatee)
                    .whereLessThanOrEqualTo(DatabaseReference.orderplaceddate_OrderDetails, endDatee)
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
                                callback.onSuccess(new ArrayList<>());
                                //   callback.onFailure(new Exception("No orders found with status: " + status + " and date range."));
                            }
                        } else {
                            callback.onFailure(task.getException());
                        }
                    });
    }
        else{
        callback.onFailure(new Exception(" No Internet Connection "));
    }



}

    public void getOrdersByStatus_DateAndVendorKey(String status, Timestamp startTimestamp, Timestamp endTimestamp, String vendorkey, FirestoreCallback<List<OrderDetails_Model>> callback)
    {
        if(BaseActivity.baseActivity.isOnline()) {


            db.collection(DatabaseReference.OrderDetails_TableName)
                .whereEqualTo(DatabaseReference.status_OrderDetails, status)
                .whereEqualTo(DatabaseReference.vendorkey, vendorkey)
                .whereGreaterThanOrEqualTo(DatabaseReference.orderplaceddate_OrderDetails, startTimestamp)
                .whereLessThanOrEqualTo(DatabaseReference.orderplaceddate_OrderDetails, endTimestamp)
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
                            callback.onSuccess(new ArrayList<>());
                         //   callback.onFailure(new Exception("No orders found with status: " + status + " and date range."));
                        }
                    } else {
                        callback.onFailure(task.getException());
                    }
                });

        }
        else{
            callback.onFailure(new Exception(" No Internet Connection "));
        }

    }
    public void getOrdersByBuyerKey_Status_DateAndVendorKey(String buyerkey,String status, Timestamp startTimestamp, Timestamp endTimestamp, String vendorkey, FirestoreCallback<List<OrderDetails_Model>> callback)
    {
        if(BaseActivity.baseActivity.isOnline()) {

            db.collection(DatabaseReference.OrderDetails_TableName)
                .whereEqualTo(DatabaseReference.status_OrderDetails, status)
                .whereEqualTo(DatabaseReference.buyerkey_OrderDetails, buyerkey)
                .whereEqualTo(DatabaseReference.vendorkey, vendorkey)
                .whereGreaterThanOrEqualTo(DatabaseReference.orderplaceddate_OrderDetails, startTimestamp)
                .whereLessThanOrEqualTo(DatabaseReference.orderplaceddate_OrderDetails, endTimestamp)
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
                            callback.onSuccess(new ArrayList<>());
                            //callback.onFailure(new Exception("No orders found with status: " + status + " and date range."));
                        }
                    } else {
                        callback.onFailure(task.getException());
                    }
                });

    }
        else{
        callback.onFailure(new Exception(" No Internet Connection "));
    }

}

    public void updateInsertUpdateMenu(MenuItems_Model menuItemsModel, String process,   FirestoreService.FirestoreCallback <String>callback) {

        if(BaseActivity.baseActivity.isOnline()) {

            Map<String, Object> updates = new HashMap<>();
        if(Objects.equals(process, "add"))
        {
            db.collection(DatabaseReference.MenuItems_TableName)
                    .document(menuItemsModel.getItemkey())
                    .set(menuItemsModel)
                    .addOnSuccessListener(documentReference -> callback.onSuccess("Menu Item added Successfully"))
                    .addOnFailureListener(callback::onFailure);
        }
        else {
            updates.put(DatabaseReference.itemname_MenuItems,menuItemsModel.getItemname());
            updates.put(DatabaseReference.itemtype_MenuItems,menuItemsModel.getItemtype());
            updates.put(DatabaseReference.grossweight_MenuItems,menuItemsModel.getGrossweight());
            updates.put(DatabaseReference.portionsize_MenuItems,menuItemsModel.getPortionsize());
            updates.put(DatabaseReference.unitprice_MenuItems,menuItemsModel.getUnitprice());
            updates.put(DatabaseReference.priceperkg_MenuItems,menuItemsModel.getPriceperkg());

            DocumentReference orderRef = db.collection(DatabaseReference.MenuItems_TableName).document(menuItemsModel.getItemkey());
            orderRef.update(updates)
                    .addOnSuccessListener(aVoid -> {
                        callback.onSuccess("Menu Item updated Successfully");
                        // Handle success, e.g., notify user, update UI
                    })
                    .addOnFailureListener(e -> {
                        callback.onFailure(e);
                        // Handle failure, e.g., notify user, retry
                    });

        }
        }
        else{
            callback.onFailure(new Exception(" No Internet Connection "));
        }


    }
    public void createOrder(OrderDetails_Model orderDetailsModel,   FirestoreService.FirestoreCallback <Void>callback) {
        if(BaseActivity.baseActivity.isOnline()) {


            db.collection(DatabaseReference.OrderDetails_TableName)
                .document(orderDetailsModel.getOrderid())
                .set(orderDetailsModel)
                .addOnSuccessListener(documentReference -> callback.onSuccess(null))
                .addOnFailureListener(callback::onFailure);
    }
        else{
        callback.onFailure(new Exception(" No Internet Connection "));
    }

    }

    public void fetchOrdersByStatus1(String status, fetchOrdersWithStatusCallback callback) {

            db.collection(DatabaseReference.OrderDetails_TableName)
                .whereEqualTo(DatabaseReference.status_OrderDetails, status)
                .whereEqualTo(DatabaseReference.vendorkey, vendorkey)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (!querySnapshot.isEmpty()) {
                            //callback.onOrdersWithStatusResult(querySnapshot);
                            //don't use this method

                        }
                    }
                });


    }






    public void fetchMenuItemsUsingVendorkey(String vendorkey, FirestoreCallback<List<MenuItems_Model>> callback) {

        if(BaseActivity.baseActivity.isOnline()) {

            db.collection(DatabaseReference.MenuItems_TableName)
                .whereEqualTo(DatabaseReference.vendorkey_MenuItems, vendorkey)
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
        else{
            callback.onFailure(new Exception(" No Internet Connection "));
        }

    }



    public void incrementOrderCounter(String vendorkey, FirestoreCallback callback) {
        if(BaseActivity.baseActivity.isOnline()) {

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
        else{
        callback.onFailure(new Exception(" No Internet Connection "));
    }

}

    public void getOrderCounterAndIncrementLocally(String vendorkey, FirestoreCallback<Long> callback) {

        if(BaseActivity.baseActivity.isOnline()) {

            db.collection(DatabaseReference.Counter_TableName).document(vendorkey)
                 .get()
                .addOnCompleteListener(task -> {
                    Log.i("orderno taskcomplete : ", String.valueOf(Objects.requireNonNull(task)));

                    if (task.isSuccessful()) {
                        Log.i("orderno task.isSuccessful() : ", String.valueOf(Objects.requireNonNull(task.isSuccessful())));




                            Counter_Model appData_model = task.getResult().toObject(Counter_Model.class);
                            Objects.requireNonNull(appData_model).setOrderno(appData_model.getOrderno()+1);
                            Log.d("orderno fetchordernoData", String.valueOf(Objects.requireNonNull(appData_model).getOrderno()));
                            callback.onSuccess(appData_model.getOrderno());


                    } else {
                        callback.onFailure(task.getException());
                        Log.i("orderno taskFailure: ", String.valueOf(Objects.requireNonNull(task.getException())));
                    }
                });
        }
        else{
            callback.onFailure(new Exception(" No Internet Connection "));
        }
    }

    public void addOrderItemDetails(OrderItemDetails_Model orderItemDetailsModel) {

        db.collection(DatabaseReference.OrderItemDetails_TableName)
                .document(orderItemDetailsModel.getUnqiuekey())
                .set(orderItemDetailsModel);




    }

    public void addBuyerInDB(Buyers_Model buyersModel, FirestoreCallback<Void> callback) {

        if(BaseActivity.baseActivity.isOnline()) {

        db.collection(DatabaseReference.BuyerDetails_TableName)
                .document(buyersModel.getUniquekey())
                .set(buyersModel)
                .addOnSuccessListener(documentReference -> callback.onSuccess(null))
                .addOnFailureListener(callback::onFailure);
        }
        else{
            callback.onFailure(new Exception(" No Internet Connection "));
        }
    }

    public void deleteBuyerDetails(String buyerkey, FirestoreCallback<Void> callback) {
        if(BaseActivity.baseActivity.isOnline()) {

        db.collection(DatabaseReference.BuyerDetails_TableName)
                .document(buyerkey)
                .delete()
                .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                .addOnFailureListener(callback::onFailure);
    }
        else{
        callback.onFailure(new Exception(" No Internet Connection "));
    }
    }

    public void updateBuyerInDB(Buyers_Model buyersModel, FirestoreCallback<Void> callback) {

        if(BaseActivity.baseActivity.isOnline()) {

            db.collection(DatabaseReference.BuyerDetails_TableName)
                .document(buyersModel.getUniquekey())
                .set(buyersModel, SetOptions.merge())
                .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                .addOnFailureListener(callback::onFailure);
        }
        else{
            callback.onFailure(new Exception(" No Internet Connection "));
        }
    }
    public void getOrderItemsByDateAndVendorKey(Timestamp startdatee, Timestamp endDatee, String vendorkey, FirestoreCallback<List<OrderItemDetails_Model>> callback) {
        if(BaseActivity.baseActivity.isOnline()) {

        db.collection(DatabaseReference.OrderItemDetails_TableName)
                 .whereGreaterThanOrEqualTo(DatabaseReference.orderplacedDate_OrderItemDetails, startdatee)
                .whereLessThanOrEqualTo(DatabaseReference.orderplacedDate_OrderItemDetails, endDatee)
                .whereEqualTo(DatabaseReference.vendorkey_OrderItemDetails, vendorkey)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (!querySnapshot.isEmpty()) {
                            List<OrderItemDetails_Model> orders = new ArrayList<>();
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                OrderItemDetails_Model orderItemDetailsModel = document.toObject(OrderItemDetails_Model.class);
                                orders.add(orderItemDetailsModel);
                            }
                            callback.onSuccess(orders);
                        } else {
                            callback.onSuccess(new ArrayList<>());
                            // callback.onFailure(new Exception("No orders found with status: " + status + " and date range."));
                        }
                    } else {
                          callback.onFailure(task.getException());
                    }
                });
    }
        else{
        callback.onFailure(new Exception(" No Internet Connection "));
    }

    }

    public void fetchVendorDetails(String vendorkeyy, FirestoreCallback<VendorDetails_Model> firestoreCallback) {

        if(BaseActivity.baseActivity.isOnline()) {

            db.collection(DatabaseReference.VendorDetails_TableName)
                    .whereEqualTo(DatabaseReference.vendorkey, vendorkeyy)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                          //  List<VendorDetails_Model> vendorDetailslist = new ArrayList<>();
                            VendorDetails_Model vendorDetailsModel = null;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                 vendorDetailsModel = document.toObject(VendorDetails_Model.class);
                                Log.d("fetchvendorUsingvendorkey", vendorDetailsModel.getVendorname());
                               // vendorDetailslist.add(vendorDetailsModel);
                            }
                            firestoreCallback.onSuccess(vendorDetailsModel);
                        } else {
                            firestoreCallback.onFailure(task.getException());
                        }
                    });
        }
        else{
            firestoreCallback.onFailure(new Exception(" No Internet Connection "));
        }



    }


   /* public void getOrderItemsByVendorAndDate(Timestamp startTimestamp, Timestamp endTimestamp, String vendorkey, FirestoreCallback<List<OrderItemDetails_Model>> callback) {

        db.collection(DatabaseReference.OrderItemDetails_TableName)
                .whereEqualTo(DatabaseReference.vendorkey_OrderItemDetails, vendorkey)
                 .whereGreaterThanOrEqualTo(DatabaseReference.orderplacedDate_OrderItemDetails, startTimestamp)
                .whereLessThanOrEqualTo(DatabaseReference.orderplacedDate_OrderItemDetails, endTimestamp)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (!querySnapshot.isEmpty()) {
                            List<OrderItemDetails_Model> orders = new ArrayList<>();
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                OrderItemDetails_Model orderItemDetailsModel = document.toObject(OrderItemDetails_Model.class);
                                orders.add(orderItemDetailsModel);
                            }
                            callback.onSuccess(orders);
                        } else {
                            callback.onSuccess(new ArrayList<>());
                           // callback.onFailure(new Exception("No orders found with status: " + status + " and date range."));
                        }
                    } else {
                        callback.onFailure(task.getException());
                    }
                });


    }

    public void getOrderItemsByDateAndVendorKey( Timestamp startTimestamp, Timestamp endTimestamp, String vendorkey, FirestoreCallback<List<OrderItemDetails_Model>> callback) {


        db.collection(DatabaseReference.OrderItemDetails_TableName)
                 .whereGreaterThanOrEqualTo(DatabaseReference.orderplacedDate_OrderItemDetails, startTimestamp)
                .whereLessThanOrEqualTo(DatabaseReference.orderplacedDate_OrderItemDetails, endTimestamp)
                .whereEqualTo(DatabaseReference.vendorkey_MenuItems, vendorkey)
                 .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (!querySnapshot.isEmpty()) {
                            List<OrderItemDetails_Model> orders = new ArrayList<>();
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                OrderItemDetails_Model orderItemDetailsModel = document.toObject(OrderItemDetails_Model.class);
                                orders.add(orderItemDetailsModel);
                            }
                            callback.onSuccess(orders);
                        } else {
                            callback.onSuccess(new ArrayList<>());
                          //  callback.onFailure(new Exception("No orders found with status: " + status + " and date range."));
                        }
                    } else {
                        callback.onFailure(task.getException());
                    }
                });




    }

    */




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
