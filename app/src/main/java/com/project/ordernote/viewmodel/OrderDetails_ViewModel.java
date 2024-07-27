package com.project.ordernote.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.project.ordernote.data.model.Buyers_Model;
import com.project.ordernote.data.model.OrderDetails_Model;
import com.project.ordernote.data.model.OrderItemDetails_Model;
import com.project.ordernote.data.remote.FirestoreService;
import com.project.ordernote.data.repository.OrderDetails_Repository;
import com.project.ordernote.utils.ApiResponseState_Enum;
import com.project.ordernote.utils.calculations.OrderValueCalculator;

import java.util.List;


public class OrderDetails_ViewModel extends AndroidViewModel {
    private final OrderDetails_Repository repository;

    private MutableLiveData<ApiResponseState_Enum<List<OrderDetails_Model>>> orderDetailsLiveData;


    public OrderDetails_ViewModel(@NonNull Application application) {
        super(application);
        repository = new OrderDetails_Repository();
    }






    public LiveData<ApiResponseState_Enum<List<OrderDetails_Model>>> getOrdersListFromViewModel() {
        if(orderDetailsLiveData == null){
            orderDetailsLiveData = new MutableLiveData<>();
        }
        return orderDetailsLiveData;
    }


}