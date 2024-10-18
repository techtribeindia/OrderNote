package com.project.ordernote.data.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.project.ordernote.data.model.VendorDetails_Model;
import com.project.ordernote.data.remote.FirestoreService;
import com.project.ordernote.utils.ApiResponseState_Enum;

public class VendorDetails_Repository {




    private final FirestoreService firestoreService;

    private final MutableLiveData<ApiResponseState_Enum<VendorDetails_Model>> vendorDetailsLiveData;

    public VendorDetails_Repository() {
        vendorDetailsLiveData = new MutableLiveData<>();
        firestoreService = new FirestoreService();

    }



    public MutableLiveData<ApiResponseState_Enum<VendorDetails_Model>> FetchVendorDetailsDataFromRepository(String vendorkey) {
        if (vendorDetailsLiveData.getValue() == null || vendorDetailsLiveData.getValue().status == ApiResponseState_Enum.Status.ERROR) {
            vendorDetailsLiveData.setValue(ApiResponseState_Enum.loading(null));

            firestoreService.fetchVendorDetails(vendorkey,  new FirestoreService.FirestoreCallback<VendorDetails_Model>() {
                @Override
                public void onSuccess(VendorDetails_Model result) {
                    if (result.getVendorkey().isEmpty()) {

                        Log.i("Repository Log: ", "vendor Details Succesfully Fetched but empty" );
                        vendorDetailsLiveData.setValue(ApiResponseState_Enum.error("No data available", result));
                    } else {
                        Log.i("Repository Log: ", "vendor Details Succesfully Fetched" );
                        vendorDetailsLiveData.setValue(ApiResponseState_Enum.success(result));

                    }
                }

                @Override
                public void onFailure(Exception e) {

                    Log.i("Repository Log: ", "Error in Fetching vendor Details" );
                    vendorDetailsLiveData.setValue(ApiResponseState_Enum.error(e.getMessage(), null));
                }
            });
        }
        return vendorDetailsLiveData;
    }



}
