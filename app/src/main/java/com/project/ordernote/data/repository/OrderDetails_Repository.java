package com.project.ordernote.data.repository;


import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.project.ordernote.data.model.OrderDetails_Model;
import com.project.ordernote.data.remote.FirestoreService;
import com.project.ordernote.utils.ApiResponseState_Enum;
import com.project.ordernote.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrderDetails_Repository {

    private final FirestoreService firestoreService;
    public String vendorkey;
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

    public  void setUserDetails(String vendorkey)
    {
        this.vendorkey = vendorkey;
        firestoreService.setUserDetails(vendorkey);
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
    public LiveData<ApiResponseState_Enum<List<OrderDetails_Model>>> getOrdersByDateAndVendorKey(Timestamp startdatee, Timestamp endDatee, String vendorkey) {
        MutableLiveData<ApiResponseState_Enum<List<OrderDetails_Model>>> ordersLiveData = new MutableLiveData<>();
        ordersLiveData.postValue(ApiResponseState_Enum.loading(null));

        firestoreService.getOrdersByDateAndVendorKey( startdatee, endDatee,vendorkey, new FirestoreService.FirestoreCallback<List<OrderDetails_Model>>() {
            @Override
            public void onSuccess(List<OrderDetails_Model> result) {
                if (result.isEmpty()) {
                    ordersLiveData.postValue(ApiResponseState_Enum.error(Constants.noDataAvailable, result));
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

    public MutableLiveData<ApiResponseState_Enum<List<OrderDetails_Model>>> getOrdersByStatus_DateAndVendorKey(String status, Timestamp startTimestamp, Timestamp endTimestamp, String vendorkey) {
        MutableLiveData<ApiResponseState_Enum<List<OrderDetails_Model>>> ordersLiveData = new MutableLiveData<>();
        ordersLiveData.postValue(ApiResponseState_Enum.loading(null));

        firestoreService.getOrdersByStatus_DateAndVendorKey(status, startTimestamp, endTimestamp,vendorkey, new FirestoreService.FirestoreCallback<List<OrderDetails_Model>>() {
            @Override
            public void onSuccess(List<OrderDetails_Model> result) {
                if (result.isEmpty()) {
                    ordersLiveData.postValue(ApiResponseState_Enum.error(Constants.noDataAvailable, result));
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

    public MutableLiveData<ApiResponseState_Enum<List<OrderDetails_Model>>> getOrdersByBuyerKey_Status_DateAndVendorKey(String buyerkey, String status, Timestamp startTimestamp, Timestamp endTimestamp, String vendorkey) {
        MutableLiveData<ApiResponseState_Enum<List<OrderDetails_Model>>> ordersLiveData = new MutableLiveData<>();
        ordersLiveData.postValue(ApiResponseState_Enum.loading(null));

        firestoreService.getOrdersByBuyerKey_Status_DateAndVendorKey(buyerkey ,status, startTimestamp, endTimestamp,vendorkey, new FirestoreService.FirestoreCallback<List<OrderDetails_Model>>() {
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



    public MutableLiveData<ApiResponseState_Enum<String>> acceptOrder(String orderid, String status) {
        MutableLiveData<ApiResponseState_Enum<String>> ordersLiveData = new MutableLiveData<>();
        ordersLiveData.postValue(ApiResponseState_Enum.loading(null));

        firestoreService.acceptOrder(orderid,status, new FirestoreService.FirestoreCallback<String>() {
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

    public MutableLiveData<ApiResponseState_Enum<String>> orderEditRequest( String orderid, String DispatchStatus) {
        MutableLiveData<ApiResponseState_Enum<String>> ordersLiveData = new MutableLiveData<>();
        ordersLiveData.postValue(ApiResponseState_Enum.loading(null));

        firestoreService.EditRequest( orderid,DispatchStatus, new FirestoreService.FirestoreCallback<String>() {
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


    public MutableLiveData<ApiResponseState_Enum<String>> updateBatchDetails( String orderid,String transporName, String driverMobieno, String truckNo) {
        MutableLiveData<ApiResponseState_Enum<String>> ordersLiveData = new MutableLiveData<>();
        ordersLiveData.postValue(ApiResponseState_Enum.loading(null));

        firestoreService.updateBatchDetails( orderid,transporName,driverMobieno,truckNo, new FirestoreService.FirestoreCallback<String>() {
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