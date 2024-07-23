package com.project.ordernote.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.project.ordernote.data.model.MenuItems_Model;
import com.project.ordernote.data.repository.MenuItems_Repository;
import com.project.ordernote.utils.ApiResponseState_Enum;

import java.util.List;

public class MenuItems_ViewModel  extends AndroidViewModel {

    private final MenuItems_Repository repository;
    private LiveData<ApiResponseState_Enum<List<MenuItems_Model>>> menuItemsLiveData;

    public MenuItems_ViewModel(@NonNull Application application) {
        super(application);
        repository = new MenuItems_Repository();
    }

    public void fetchMenuItemsByVendorKey(String vendorKey) {
        menuItemsLiveData = repository.fetchMenuItemsUsingVendorkey(vendorKey);
    }

    public LiveData<ApiResponseState_Enum<List<MenuItems_Model>>> getMenuItemsFromViewModel() {
        return menuItemsLiveData;
    }

}
