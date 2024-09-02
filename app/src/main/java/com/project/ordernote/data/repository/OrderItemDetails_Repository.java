package com.project.ordernote.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.Timestamp;
import com.project.ordernote.data.model.OrderItemDetails_Model;
import com.project.ordernote.data.model.OrderItemDetails_Model;
import com.project.ordernote.data.remote.FirestoreService;
import com.project.ordernote.utils.ApiResponseState_Enum;
import com.project.ordernote.utils.Constants;

import java.util.List;

public class OrderItemDetails_Repository {

    private final FirestoreService firestoreService;

    public OrderItemDetails_Repository() {
        firestoreService = new FirestoreService();
    }

    public void addOrderItemDetailsInDB(OrderItemDetails_Model orderItemDetailsModel) {

        firestoreService.addOrderItemDetails(orderItemDetailsModel);

    }


    public MutableLiveData<ApiResponseState_Enum<List<OrderItemDetails_Model>>> getOrderItemsByStatus_VendorAndDate(String status, Timestamp startTimestamp, Timestamp endTimestamp, String vendorkey) {
        MutableLiveData<ApiResponseState_Enum<List<OrderItemDetails_Model>>> ordersItemLiveData = new MutableLiveData<>();
        ordersItemLiveData.postValue(ApiResponseState_Enum.loading(null));

        firestoreService.getOrderItemsByStatus_VendorAndDate(status,startTimestamp, endTimestamp,vendorkey , new FirestoreService.FirestoreCallback<List<OrderItemDetails_Model>>() {
            @Override
            public void onSuccess(List<OrderItemDetails_Model> result) {
                Log.d("orderItemdetails response   :  ", result.toString());

                if (result.isEmpty()) {
                    ordersItemLiveData.postValue(ApiResponseState_Enum.error(Constants.noDataAvailable, result));
                } else {
                    ordersItemLiveData.postValue(ApiResponseState_Enum.success(result));

                }
            }



            @Override
            public void onFailure(Exception e) {
                ordersItemLiveData.postValue(ApiResponseState_Enum.error(e.getMessage(), null));
            }
        });
        Log.d("orderdetails ordersLiveData  :  ", ordersItemLiveData.toString());


        return ordersItemLiveData;

    }


    public LiveData<ApiResponseState_Enum<List<OrderItemDetails_Model>>> getOrderItemsByBuyerKeyStatus_DateAndVendorKey(String selectedBuyerKey, String status, Timestamp startTimestamp, Timestamp endTimestamp, String vendorkey) {

        MutableLiveData<ApiResponseState_Enum<List<OrderItemDetails_Model>>> ordersItemLiveData = new MutableLiveData<>();
        ordersItemLiveData.postValue(ApiResponseState_Enum.loading(null));

        firestoreService.getOrderItemsByBuyerKeyStatus_DateAndVendorKey(selectedBuyerKey,status,startTimestamp, endTimestamp,vendorkey , new FirestoreService.FirestoreCallback<List<OrderItemDetails_Model>>() {
            @Override
            public void onSuccess(List<OrderItemDetails_Model> result) {
                Log.d("orderItemdetails response   :  ", result.toString());

                if (result.isEmpty()) {
                    ordersItemLiveData.postValue(ApiResponseState_Enum.error("No data available", result));
                } else {
                    ordersItemLiveData.postValue(ApiResponseState_Enum.success(result));

                }
            }



            @Override
            public void onFailure(Exception e) {
                ordersItemLiveData.postValue(ApiResponseState_Enum.error(e.getMessage(), null));
            }
        });
        Log.d("orderdetails ordersLiveData  :  ", ordersItemLiveData.toString());


        return ordersItemLiveData;


    }
}
