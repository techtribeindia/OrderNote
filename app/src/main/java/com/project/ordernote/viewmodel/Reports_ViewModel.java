package com.project.ordernote.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.project.ordernote.data.model.OrderDetails_Model;
import com.project.ordernote.data.model.ReportsFilterDetails_Model;
import com.project.ordernote.data.model.Users_Model;
import com.project.ordernote.data.repository.Login_Repository;
import com.project.ordernote.utils.ApiResponseState_Enum;

import java.util.List;

public class Reports_ViewModel extends AndroidViewModel {

    private MutableLiveData<ReportsFilterDetails_Model> filterDetailsModelMutableLiveData;
    private Observer<ReportsFilterDetails_Model> filterDetailsModelObserver;

    public Reports_ViewModel(@NonNull Application application) {
        super(application);
        initObserver();
    }
    private void initObserver() {
        filterDetailsModelObserver = state -> filterDetailsModelMutableLiveData.setValue(state);
    }


    public void applyFilter(ReportsFilterDetails_Model filterValue) {
        filterDetailsModelMutableLiveData = new MutableLiveData<>(new ReportsFilterDetails_Model());
        filterDetailsModelMutableLiveData.setValue(filterValue);

        LiveData<ReportsFilterDetails_Model> filterDetailsModel = filterDetailsModelMutableLiveData;
        filterDetailsModel = new MutableLiveData<>();
        filterDetailsModel.observeForever(filterDetailsModelObserver);

    }

    public LiveData<ReportsFilterDetails_Model> getFilteredReports() {

        if(filterDetailsModelMutableLiveData ==null){
            filterDetailsModelMutableLiveData = new MutableLiveData<>();
        }
        return filterDetailsModelMutableLiveData;

    }
}
