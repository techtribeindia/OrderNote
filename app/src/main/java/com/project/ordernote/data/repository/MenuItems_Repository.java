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

    public MutableLiveData<ApiResponseState_Enum<List<MenuItems_Model>>> fetchMenuItemsUsingVendorkey(String vendorkey) {

        MutableLiveData<ApiResponseState_Enum<List<MenuItems_Model>>> menuItemLiveData = new MutableLiveData<>();
        menuItemLiveData.postValue(ApiResponseState_Enum.loading(null));

        firestoreService.fetchMenuItemsUsingVendorkey(vendorkey, new FirestoreService.FirestoreCallback<List<MenuItems_Model>>() {
            @Override
            public void onSuccess(List<MenuItems_Model> result) {
                if (result.isEmpty()) {


                    menuItemLiveData.postValue(ApiResponseState_Enum.successwithmessage(result,"Menu Items not available"));
                } else {


                    menuItemLiveData.postValue(ApiResponseState_Enum.successwithmessage(result,""));
                }
            }

            @Override
            public void onFailure(Exception e) {

                Log.i("Repository Log: ", "Error in fetching MenuItem " );

                menuItemLiveData.postValue(ApiResponseState_Enum.error(e.getMessage(), null));
            }
        });
        return menuItemLiveData;
    }
    public MutableLiveData<ApiResponseState_Enum<String>> updateInsertUpdateMenu( MenuItems_Model menuItemsModel, String process) {
        MutableLiveData<ApiResponseState_Enum<String>> ordersLiveData = new MutableLiveData<>();
        ordersLiveData.postValue(ApiResponseState_Enum.loading(null));

        firestoreService.updateInsertUpdateMenu( menuItemsModel,process, new FirestoreService.FirestoreCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d("orderdetails response   :  ", result.toString());

                if (result.isEmpty()) {
                    ordersLiveData.postValue(ApiResponseState_Enum.error("No data available", result));
                } else {
                    ordersLiveData.postValue(ApiResponseState_Enum.successwithmessage(result,""));
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
}
