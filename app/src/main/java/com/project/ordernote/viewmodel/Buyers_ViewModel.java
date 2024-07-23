package com.project.ordernote.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.project.ordernote.data.model.Buyers_Model;
import com.project.ordernote.data.repository.Buyers_Repository;
import com.project.ordernote.utils.ApiResponseState_Enum;

import java.util.List;

public class Buyers_ViewModel extends AndroidViewModel {

    private final Buyers_Repository repository;
    private LiveData<ApiResponseState_Enum<List<Buyers_Model>>> buyersListLiveData;

    public Buyers_ViewModel(@NonNull Application application) {
        super(application);
        repository = new Buyers_Repository();
    }

    public void getBuyersListFromRepository(String vendorKey) {
        buyersListLiveData = repository.getBuyersList(vendorKey);
    }

    public LiveData<ApiResponseState_Enum<List<Buyers_Model>>> getBuyersListFromViewModel() {
        return buyersListLiveData;
    }

}

