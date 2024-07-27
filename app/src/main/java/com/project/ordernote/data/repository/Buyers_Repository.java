package com.project.ordernote.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.project.ordernote.data.model.Buyers_Model;
import com.project.ordernote.data.remote.FirestoreService;
import com.project.ordernote.utils.ApiResponseState_Enum;

import java.util.List;

public class Buyers_Repository {
    private final FirestoreService firestoreService;
    private final MutableLiveData<ApiResponseState_Enum<List<Buyers_Model>>> buyersListLiveData;

    public Buyers_Repository() {
        buyersListLiveData = new MutableLiveData<>();
        firestoreService = new FirestoreService();
    }

    public MutableLiveData<ApiResponseState_Enum<List<Buyers_Model>>> getBuyersList(String vendorKey) {
        if (buyersListLiveData.getValue() == null || buyersListLiveData.getValue().status == ApiResponseState_Enum.Status.ERROR) {
            buyersListLiveData.setValue(ApiResponseState_Enum.loading(null));
            firestoreService.fetchBuyersListUsingVendorkey(vendorKey, new FirestoreService.FirestoreCallback<List<Buyers_Model>>() {
                @Override
                public void onSuccess(List<Buyers_Model> result) {
                    if (result.isEmpty()) {
                        Log.i("Repository Log: ", "buyer Details Succesfully Fetched but empty" );
                        buyersListLiveData.setValue(ApiResponseState_Enum.error("No data available", result));
                    } else {
                        Log.i("Repository Log: ", "buyer Details Succesfully Fetched" );
                        buyersListLiveData.setValue(ApiResponseState_Enum.success(result));
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Log.i("Repository Log: ", "Error in Fetching buyer Details" );
                    buyersListLiveData.setValue(ApiResponseState_Enum.error(e.getMessage(), null));
                }
            });
        }
        return buyersListLiveData;
    }
}
