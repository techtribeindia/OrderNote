package com.project.ordernote.data.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.project.ordernote.data.model.Buyers_Model;
import com.project.ordernote.data.remote.FirestoreService;
import com.project.ordernote.utils.ApiResponseState_Enum;

import java.util.List;
import java.util.Objects;

public class Counter_Repository {


    private FirestoreService firestoreService;
    private MutableLiveData<ApiResponseState_Enum<Long>> orderNumberLiveData;

    public Counter_Repository() {
        firestoreService = new FirestoreService();
        orderNumberLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<ApiResponseState_Enum<Long>> incrementOrderCounter(String vendorkey) {


      //  if (orderNumberLiveData.getValue() == null || orderNumberLiveData.getValue().status == ApiResponseState_Enum.Status.ERROR) {
            orderNumberLiveData.setValue(ApiResponseState_Enum.loading(null));

            firestoreService.incrementOrderCounter(vendorkey , new FirestoreService.FirestoreCallback<Long>() {


                @Override
                public void onSuccess(Long result) {
                    orderNumberLiveData.postValue(ApiResponseState_Enum.success(result));

                }

                @Override
                public void onFailure(Exception e) {
                     orderNumberLiveData.postValue(ApiResponseState_Enum.error(e.getMessage(), null));

                }
            });
       // }
        return orderNumberLiveData;


    }

    public MutableLiveData<ApiResponseState_Enum<Long>> getOrderCounterAndIncrementLocally(String vendorkey) {

        Log.i("orderno loading : ", String.valueOf(Objects.requireNonNull(vendorkey)));

             orderNumberLiveData.setValue(ApiResponseState_Enum.loading(null));

            firestoreService.getOrderCounterAndIncrementLocally(vendorkey , new FirestoreService.FirestoreCallback<Long>() {


                @Override
                public void onSuccess(Long result) {
                    Log.i("orderno onSuccess : ", String.valueOf(Objects.requireNonNull(result)));

                    orderNumberLiveData.postValue(ApiResponseState_Enum.success(result));

                }

                @Override
                public void onFailure(Exception e) {
                    orderNumberLiveData.postValue(ApiResponseState_Enum.error(e.getMessage(), null));
                    Log.i("orderno Exception : ", String.valueOf(Objects.requireNonNull(e)));

                }
            });

        return orderNumberLiveData;

    }
}