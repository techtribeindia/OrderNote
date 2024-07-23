package com.project.ordernote.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;


import com.project.ordernote.data.model.MenuItems_Model;
import com.project.ordernote.data.model.OrderDetails_Model;
import com.project.ordernote.data.remote.FirestoreService;
import com.project.ordernote.utils.ApiResponseState_Enum;

import java.util.List;

public class MenuItems_Repository {

    
    private final FirestoreService firestoreService;

    public MenuItems_Repository() {
        firestoreService = new FirestoreService();
    }

    public LiveData<ApiResponseState_Enum<List<MenuItems_Model>>> fetchMenuItemsUsingVendorkey(String vendorkey) {
        MutableLiveData<ApiResponseState_Enum<List<MenuItems_Model>>> menuItemLiveData = new MutableLiveData<>();
        menuItemLiveData.postValue(ApiResponseState_Enum.loading(null));

        firestoreService.fetchMenuItemsUsingVendorkey(vendorkey, new FirestoreService.FirestoreCallback<List<MenuItems_Model>>() {
            @Override
            public void onSuccess(List<MenuItems_Model> result) {
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
