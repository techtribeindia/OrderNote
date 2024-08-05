package com.project.ordernote.data.repository;


import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.project.ordernote.data.model.OrderDetails_Model;
import com.project.ordernote.data.remote.FirestoreService;
import com.project.ordernote.utils.ApiResponseState_Enum;

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


    public MutableLiveData<ApiResponseState_Enum<List<OrderDetails_Model>>> getOrdersByStatus(String status) {
        MutableLiveData<ApiResponseState_Enum<List<OrderDetails_Model>>> ordersLiveData = new MutableLiveData<>();
        ordersLiveData.postValue(ApiResponseState_Enum.loading(null));

    firestoreService.fetchOrdersByStatus(status, new FirestoreService.FirestoreCallback<List<OrderDetails_Model>>() {
        @Override
        public void onSuccess(List<OrderDetails_Model> result) {
            Log.d("orderdetails response   :  ", result.toString());

            if (result.isEmpty()) {
                ordersLiveData.postValue(ApiResponseState_Enum.error("No data available", result));
            } else {
                ordersLiveData.postValue(ApiResponseState_Enum.success(result));

            }
        }



        @Override
        public void onFailure(Exception e) {
            ordersLiveData.postValue(ApiResponseState_Enum.error(e.getMessage(), null));
        }
    });
        Log.d("orderdetails ordersLiveData  :  ", ordersLiveData.toString());


        return ordersLiveData;

    }

    public MutableLiveData<ApiResponseState_Enum<List<OrderDetails_Model>>> getOrdersByStatusAndDate(String status, Timestamp startTimestamp, Timestamp endTimestamp) {
        MutableLiveData<ApiResponseState_Enum<List<OrderDetails_Model>>> ordersLiveData = new MutableLiveData<>();
        ordersLiveData.postValue(ApiResponseState_Enum.loading(null));

        firestoreService.getOrdersByStatusAndDate(status, startTimestamp, endTimestamp, new FirestoreService.FirestoreCallback<List<OrderDetails_Model>>() {
            @Override
            public void onSuccess(List<OrderDetails_Model> result) {
                if (result.isEmpty()) {
                    ordersLiveData.postValue(ApiResponseState_Enum.error("No data available", result));
                } else {
                    ordersLiveData.postValue(ApiResponseState_Enum.success(result));
                }
            }

            @Override
            public void onFailure(Exception e) {
                ordersLiveData.postValue(ApiResponseState_Enum.error(e.getMessage(), null));
            }
        });

        return ordersLiveData;
    }


    public LiveData<List<Map<String, Object>>> getOrdersByStatus1(String status) {
        MutableLiveData<List<Map<String, Object>>> ordersWithStatusResult = new MutableLiveData<>();
        firestoreService.fetchOrdersByStatus1(status, orderWithStatusDocument -> {
            List<Map<String, Object>> ordersData = new ArrayList<>();
            for (DocumentSnapshot document : orderWithStatusDocument.getDocuments()) {
                ordersData.add(document.getData());
            }
            ordersWithStatusResult.setValue(ordersData);
        });
        return ordersWithStatusResult;

    }



    public MutableLiveData<ApiResponseState_Enum<String>> acceptOrder(String transporName,String driverMobieno,String truckNo, String orderid, String status) {
        MutableLiveData<ApiResponseState_Enum<String>> ordersLiveData = new MutableLiveData<>();
        ordersLiveData.postValue(ApiResponseState_Enum.loading(null));

        firestoreService.acceptOrder(transporName,driverMobieno,truckNo,orderid,status, new FirestoreService.FirestoreCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d("orderdetails response   :  ", result.toString());

                if (result.isEmpty()) {
                    ordersLiveData.postValue(ApiResponseState_Enum.error("No data available", result));
                } else {
                    ordersLiveData.postValue(ApiResponseState_Enum.success(result));
                }
            }

            @Override
            public void onFailure(Exception e) {
                ordersLiveData.postValue(ApiResponseState_Enum.error(e.getMessage(), null));
            }
        });
        Log.d("orderdetails ordersLiveData  :  ", ordersLiveData.toString());


        return ordersLiveData;

    }

    public MutableLiveData<ApiResponseState_Enum<String>> rejectOrder( String orderid,String status) {
        MutableLiveData<ApiResponseState_Enum<String>> ordersLiveData = new MutableLiveData<>();
        ordersLiveData.postValue(ApiResponseState_Enum.loading(null));

        firestoreService.rejectOrder( orderid,status, new FirestoreService.FirestoreCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d("orderdetails response   :  ", result.toString());

                if (result.isEmpty()) {
                    ordersLiveData.postValue(ApiResponseState_Enum.error("No data available", result));
                } else {
                    ordersLiveData.postValue(ApiResponseState_Enum.success(result));
                }
            }

            @Override
            public void onFailure(Exception e) {
                ordersLiveData.postValue(ApiResponseState_Enum.error(e.getMessage(), null));
            }
        });
        Log.d("orderdetails ordersLiveData  :  ", ordersLiveData.toString());


        return ordersLiveData;

    }

    public MutableLiveData<ApiResponseState_Enum<String>> cancelOrder( String orderid,String status) {
        MutableLiveData<ApiResponseState_Enum<String>> ordersLiveData = new MutableLiveData<>();
        ordersLiveData.postValue(ApiResponseState_Enum.loading(null));

        firestoreService.cancelOrder( orderid,status, new FirestoreService.FirestoreCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d("orderdetails response   :  ", result.toString());

                if (result.isEmpty()) {
                    ordersLiveData.postValue(ApiResponseState_Enum.error("No data available", result));
                } else {
                    ordersLiveData.postValue(ApiResponseState_Enum.success(result));
                }
            }

            @Override
            public void onFailure(Exception e) {
                ordersLiveData.postValue(ApiResponseState_Enum.error(e.getMessage(), null));
            }
        });
        Log.d("orderdetails ordersLiveData  :  ", ordersLiveData.toString());


        return ordersLiveData;

    }

    public MutableLiveData<ApiResponseState_Enum<String>> placeOrder( String orderid,String status) {
        MutableLiveData<ApiResponseState_Enum<String>> ordersLiveData = new MutableLiveData<>();
        ordersLiveData.postValue(ApiResponseState_Enum.loading(null));

        firestoreService.placeOrder( orderid,status, new FirestoreService.FirestoreCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d("orderdetails response   :  ", result.toString());

                if (result.isEmpty()) {
                    ordersLiveData.postValue(ApiResponseState_Enum.error("No data available", result));
                } else {
                    ordersLiveData.postValue(ApiResponseState_Enum.success(result));
                }
            }

            @Override
            public void onFailure(Exception e) {
                ordersLiveData.postValue(ApiResponseState_Enum.error(e.getMessage(), null));
            }
        });
        Log.d("orderdetails ordersLiveData  :  ", ordersLiveData.toString());


        return ordersLiveData;

    }

    public MutableLiveData<ApiResponseState_Enum<String>> orderEditRequest( String orderid) {
        MutableLiveData<ApiResponseState_Enum<String>> ordersLiveData = new MutableLiveData<>();
        ordersLiveData.postValue(ApiResponseState_Enum.loading(null));

        firestoreService.EditRequest( orderid, new FirestoreService.FirestoreCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d("orderdetails response   :  ", result.toString());

                if (result.isEmpty()) {
                    ordersLiveData.postValue(ApiResponseState_Enum.error("No data available", result));
                } else {
                    ordersLiveData.postValue(ApiResponseState_Enum.success(result));
                }
            }

            @Override
            public void onFailure(Exception e) {
                ordersLiveData.postValue(ApiResponseState_Enum.error(e.getMessage(), null));
            }
        });
        Log.d("orderdetails ordersLiveData  :  ", ordersLiveData.toString());


        return ordersLiveData;

    }

    public void createOrder(OrderDetails_Model order, FirestoreService.FirestoreCallback<Void> callback) {
        firestoreService.createOrder(order, callback);
    }
}