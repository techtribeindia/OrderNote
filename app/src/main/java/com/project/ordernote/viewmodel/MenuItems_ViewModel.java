package com.project.ordernote.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import androidx.lifecycle.MutableLiveData;

import com.project.ordernote.data.model.Buyers_Model;
import com.project.ordernote.data.model.MenuItems_Model;
import com.project.ordernote.data.repository.Buyers_Repository;

import com.project.ordernote.data.repository.MenuItems_Repository;
import com.project.ordernote.utils.ApiResponseState_Enum;

import java.util.List;

public class MenuItems_ViewModel  extends AndroidViewModel {

    private   MenuItems_Repository repository  = null;;
    private MutableLiveData<ApiResponseState_Enum<List<MenuItems_Model>>> menuItemsLiveData;

    public MenuItems_ViewModel(@NonNull Application application) {
        super(application);

        if(repository == null ){
            repository = new MenuItems_Repository();

        }
        if(menuItemsLiveData == null){
            menuItemsLiveData = new MutableLiveData<>();

        }

    }

    public void fetchMenuItemsByVendorKey(String vendorKey) {
        menuItemsLiveData = repository.fetchMenuItemsUsingVendorkey(vendorKey);
    }


    public void setMenuListinMutableLiveData(List<MenuItems_Model> menuItems_List) {
        menuItemsLiveData.setValue(ApiResponseState_Enum.success(menuItems_List));

    }



    public LiveData<ApiResponseState_Enum<List<MenuItems_Model>>> getMenuItemsFromViewModel() {
        return menuItemsLiveData;
    }

}
