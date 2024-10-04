package com.project.ordernote.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.project.ordernote.data.local.LocalDataManager;
import com.project.ordernote.data.model.AppData_Model;
import com.project.ordernote.data.model.MenuItems_Model;
import com.project.ordernote.data.model.OrderDetails_Model;
import com.project.ordernote.data.repository.AppData_Repository;
import com.project.ordernote.utils.ApiResponseState_Enum;

import java.util.List;

public class AppData_ViewModel extends AndroidViewModel {
    private   MutableLiveData<ApiResponseState_Enum<AppData_Model>> appDataModelLiveData;
    private   AppData_Repository repository;
    private Observer<ApiResponseState_Enum<AppData_Model>> appData_ModelObserver;

    public AppData_ViewModel(@NonNull Application application) {
        super(application);
        if(appDataModelLiveData == null){
            appDataModelLiveData = new MutableLiveData<>();
        }
        repository = new AppData_Repository();
        initObserver();
    }

    private void initObserver() {
        appData_ModelObserver = state -> appDataModelLiveData.setValue(state);
    }


    public void FetchAppDataFromRepositoryAndSaveInLocalDataManager() {
        LiveData<ApiResponseState_Enum<AppData_Model>> source = repository.FetchAppDataFromRepository();
        if(source.getValue()!=null){
            if(source.getValue().data!=null){
                Log.d("SplashScreen","Got App Data");
                appDataModelLiveData.setValue(source.getValue());
                LocalDataManager.getInstance().setAppData_model(source.getValue().data);
            }
            else{

                appDataModelLiveData = new MutableLiveData<>();
                LocalDataManager.getInstance().setAppData_model(new AppData_Model());

            }
        }
        try{
            source.observeForever(appData_ModelObserver);
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }


    public LiveData<ApiResponseState_Enum<AppData_Model>> getAppModelDataFromLiveModel() {



        return appDataModelLiveData;
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        appDataModelLiveData.removeObserver(appData_ModelObserver);


    }
}