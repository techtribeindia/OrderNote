package com.project.ordernote.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.project.ordernote.data.model.VendorDetails_Model;
import com.project.ordernote.data.repository.MenuItems_Repository;
import com.project.ordernote.data.repository.VendorDetails_Repository;
import com.project.ordernote.utils.ApiResponseState_Enum;

import java.util.List;

public class VendorDetails_ViewModel extends AndroidViewModel {



    private VendorDetails_Repository repository  = null;;
    private MutableLiveData<ApiResponseState_Enum<VendorDetails_Model>> vendorItemsLiveData;



    public VendorDetails_ViewModel(@NonNull Application application) {
        super(application);

        if(repository == null ){
            repository = new VendorDetails_Repository();

        }
        if(vendorItemsLiveData == null){
            vendorItemsLiveData = new MutableLiveData<>();

        }


    }



    public void FetchVendorItemByVendorKeyFromRepository(String vendorKey) {
        vendorItemsLiveData = repository.FetchVendorDetailsDataFromRepository(vendorKey);

        Log.d("SplashScreenActivity","Got menu item");
    }






    public LiveData<ApiResponseState_Enum<VendorDetails_Model>> getVendorItemsFromViewModel() {
        return vendorItemsLiveData;
    }







}
