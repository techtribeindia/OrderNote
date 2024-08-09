package com.project.ordernote.data.repository;

import android.util.Log;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.project.ordernote.data.local.LocalDataManager;
import com.project.ordernote.data.model.Buyers_Model;
import com.project.ordernote.data.remote.FirestoreService;
import com.project.ordernote.utils.ApiResponseState_Enum;
import com.project.ordernote.utils.Constants;

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
                        buyersListLiveData.setValue(ApiResponseState_Enum.error(Constants.noDataAvailable, result));
                        LocalDataManager.getInstance().setBuyers(result);
                    } else {
                        Log.i("Repository Log: ", "buyer Details Succesfully Fetched" );
                        buyersListLiveData.setValue(ApiResponseState_Enum.success(result));
                        LocalDataManager.getInstance().setBuyers(result);

                    }
                }

                @Override
                public void onFailure(Exception e) {
                    e.printStackTrace();
                    Log.i("Repository Log: ", "Error in Fetching buyer Details" );
                    buyersListLiveData.setValue(ApiResponseState_Enum.error(e.getMessage(), null));
                }
            });
        }
        return buyersListLiveData;
    }


    public void addBuyerInDB(Buyers_Model buyersModel, FirestoreService.FirestoreCallback<Void> callback) {

        firestoreService.addBuyerInDB(buyersModel , callback);

    }

    public void deleteBuyerDetails(String buyerkey, FirestoreService.FirestoreCallback<Void> firestoreCallback) {

        firestoreService.deleteBuyerDetails(buyerkey , firestoreCallback);

    }

    public void updateBuyerInDB(Buyers_Model buyersModel, FirestoreService.FirestoreCallback<Void> callback) {
        firestoreService.updateBuyerInDB(buyersModel , callback);

    }
}

