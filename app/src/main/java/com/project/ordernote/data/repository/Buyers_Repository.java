package com.project.ordernote.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.project.ordernote.data.model.Buyers_Model;
import com.project.ordernote.data.remote.FirestoreService;
import com.project.ordernote.utils.ApiResponseState_Enum;

import java.util.List;

public class Buyers_Repository {
    private final FirestoreService firestoreService;

    public Buyers_Repository() {
        firestoreService = new FirestoreService();
    }
    public LiveData<ApiResponseState_Enum<List<Buyers_Model>>> getBuyersList(String vendorKey)  {
            MutableLiveData<ApiResponseState_Enum<List<Buyers_Model>>> menuItemLiveData = new MutableLiveData<>();
            menuItemLiveData.postValue(ApiResponseState_Enum.loading(null));

            firestoreService.fetchBuyersListUsingVendorkey(vendorKey, new FirestoreService.FirestoreCallback<List<Buyers_Model>>() {
                @Override
                public void onSuccess(List<Buyers_Model> result) {
                    if (result.isEmpty()) {
                        menuItemLiveData.postValue(ApiResponseState_Enum.error("No data available", result));
                    } else {
                        menuItemLiveData.postValue(ApiResponseState_Enum.success(result));
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    menuItemLiveData.postValue(ApiResponseState_Enum.error(e.getMessage(), null));
                }
            });
            return menuItemLiveData;
        }

    }
