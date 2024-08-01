package com.project.ordernote.data.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.project.ordernote.data.model.AppData_Model;
import com.project.ordernote.data.remote.FirestoreService;
import com.project.ordernote.utils.ApiResponseState_Enum;

public class AppData_Repository {


    private final FirestoreService firestoreService;

    private final   MutableLiveData<ApiResponseState_Enum<AppData_Model>> appDataLiveData;

    public AppData_Repository() {
        appDataLiveData = new MutableLiveData<>();
        firestoreService = new FirestoreService();
    }



    public MutableLiveData<ApiResponseState_Enum<AppData_Model>> FetchAppDataFromRepository() {
        if (appDataLiveData.getValue() == null || appDataLiveData.getValue().status == ApiResponseState_Enum.Status.ERROR) {
            appDataLiveData.setValue(ApiResponseState_Enum.loading(null));

            firestoreService.fetchAppData( new FirestoreService.FirestoreCallback<AppData_Model>() {
                @Override
                public void onSuccess(AppData_Model result) {
                    if (result.getPaymentmode().isEmpty()) {

                        Log.i("Repository Log: ", "buyer Details Succesfully Fetched but empty" );
                        appDataLiveData.setValue(ApiResponseState_Enum.error("No data available", result));
                    } else {
                        Log.i("Repository Log: ", "buyer Details Succesfully Fetched" );
                        appDataLiveData.setValue(ApiResponseState_Enum.success(result));

                    }
                }

                @Override
                public void onFailure(Exception e) {

                    Log.i("Repository Log: ", "Error in Fetching buyer Details" );
                    appDataLiveData.setValue(ApiResponseState_Enum.error(e.getMessage(), null));
                }
            });
        }
        return appDataLiveData;
    }
}



