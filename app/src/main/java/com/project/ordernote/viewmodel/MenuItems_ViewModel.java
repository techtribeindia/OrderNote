package com.project.ordernote.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.project.ordernote.data.model.MenuItems_Model;

import com.project.ordernote.data.model.OrderDetails_Model;
import com.project.ordernote.data.repository.MenuItems_Repository;
import com.project.ordernote.utils.ApiResponseState_Enum;

import java.util.List;
import java.util.Objects;

public class MenuItems_ViewModel  extends AndroidViewModel {

    private   MenuItems_Repository repository  = null;;
    private MutableLiveData<ApiResponseState_Enum<List<MenuItems_Model>>> menuItemsLiveData;
    private MutableLiveData<String> selectedmenuJson;
    private MutableLiveData<MenuItems_Model> selectedMenuItemModel;
 //   private MutableLiveData<String> selectedMenuItemPosition;



    public MenuItems_ViewModel(@NonNull Application application) {
        super(application);

        if(repository == null ){
            repository = new MenuItems_Repository();

        }
        if(menuItemsLiveData == null){
            menuItemsLiveData = new MutableLiveData<>();

        }
        if(selectedMenuItemModel == null){
            selectedMenuItemModel = new MutableLiveData<>();

        }

        if(selectedmenuJson == null){
            selectedmenuJson = new MutableLiveData<>();

        }
       /* if(selectedMenuItemPosition == null){
            selectedMenuItemPosition = new MutableLiveData<>();

        }

        */

    }

    public void FetchMenuItemByVendorKeyFromRepository(String vendorKey) {
        menuItemsLiveData = repository.fetchMenuItemsUsingVendorkey(vendorKey);
    }


    public void setMenuListinMutableLiveData(List<MenuItems_Model> menuItems_List) {
        menuItemsLiveData.setValue(ApiResponseState_Enum.success(menuItems_List));

    }



    public LiveData<ApiResponseState_Enum<List<MenuItems_Model>>> getMenuItemsFromViewModel() {
        return menuItemsLiveData;
    }
    public LiveData<MenuItems_Model> getSelectedMenuItemsFromViewModel() {
        return selectedMenuItemModel;
    }

    public void updateSelectedMenuItemModel(MenuItems_Model selectedMenuItemModel) {
        this.selectedMenuItemModel .setValue( selectedMenuItemModel);
    }
    public void setSelectedMenu(MenuItems_Model order) {
        Gson gson = new Gson();
        String orderJson = gson.toJson(order);
        selectedmenuJson.setValue(orderJson);
    }
 /*   public LiveData<String> getSelectedMenuItemPositionFromViewModel() {
        return selectedMenuItemPosition;
    }

    public void setSelectedMenuItemPosition(String selectedMenuItemPosition) {
        this.selectedMenuItemPosition.setValue(selectedMenuItemPosition);
    }

  */

    public LiveData<MenuItems_Model> getMenuItemForMenuItemKeyFromViewModel(String menuitemkey) {
       // selectedMenuItemModel. setValue(new MenuItems_Model());
       // selectedMenuItemPosition.setValue("");
        boolean isItemFound  = false;
        for(int i = 0; i < Objects.requireNonNull(menuItemsLiveData.getValue()).data.size(); i++){

            MenuItems_Model menuItemsModel = Objects.requireNonNull(menuItemsLiveData.getValue()).data.get(i);

            if(menuItemsModel.getItemkey().equals(menuitemkey)){

                isItemFound = true;
                //menuItemsModel.setQuantity(1);
                 //selectedMenuItemModel.setValue(menuItemsModel);

                // Create a deep copy of menuItemsModel using the copy constructor
                MenuItems_Model copiedMenuItem = new MenuItems_Model(menuItemsModel);
                copiedMenuItem.setQuantity(1);

                selectedMenuItemModel.setValue(copiedMenuItem);
             //    selectedMenuItemPosition.setValue(String.valueOf(i));
            }

            if((Objects.requireNonNull(menuItemsLiveData.getValue()).data.size() -1 )== i){
                if(!isItemFound){
                    selectedMenuItemModel. setValue(new MenuItems_Model());
                }
            }



        }
      return  selectedMenuItemModel;
    }
}
