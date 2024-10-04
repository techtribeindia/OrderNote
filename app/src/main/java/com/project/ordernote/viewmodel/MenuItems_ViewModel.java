package com.project.ordernote.viewmodel;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.project.ordernote.data.model.Buyers_Model;
import com.project.ordernote.data.model.MenuItems_Model;

import com.project.ordernote.data.model.OrderDetails_Model;
import com.project.ordernote.data.repository.MenuItems_Repository;
import com.project.ordernote.utils.ApiResponseState_Enum;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MenuItems_ViewModel  extends AndroidViewModel {

    private   MenuItems_Repository repository  = null;;
    private MutableLiveData<ApiResponseState_Enum<List<MenuItems_Model>>> menuItemsLiveData;
    private MutableLiveData<String> selectedmenuJson;
    private MutableLiveData<MenuItems_Model> selectedMenuItemModel;
    private List<MenuItems_Model> menuItemsLiveDataOriginal = new ArrayList<>();



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


    public void setOrderDetails(ApiResponseState_Enum<List<MenuItems_Model>> response) {
        if (response != null && response.data != null) {
            if (menuItemsLiveDataOriginal == null || menuItemsLiveDataOriginal.isEmpty()) {

                menuItemsLiveDataOriginal = new ArrayList<>(response.data);
            }
        }
    }

    public void clearOrderDetails() {
        menuItemsLiveDataOriginal = new ArrayList<>(new ArrayList<>());
    }

    public void filterOrderWithMenuName(String itemname) {

        List<MenuItems_Model> filteredOrders = new ArrayList<>();
        for (MenuItems_Model order : menuItemsLiveDataOriginal) {
            if (order.getItemname().toLowerCase().contains(itemname.toLowerCase()) || itemname == null || itemname.trim().isEmpty()) {
                filteredOrders.add(order);
            }
        }
        String message = "";
        if(filteredOrders.isEmpty())
        {
            message = "There is menus for the entered Menu name";
        }
        menuItemsLiveData.setValue(ApiResponseState_Enum.successwithmessage(filteredOrders,message));

    }

    public void FetchMenuItemByVendorKeyFromRepository(String vendorKey) {
        menuItemsLiveData = repository.fetchMenuItemsUsingVendorkey(vendorKey);

        Log.d("SplashScreen","Got menu item");
    }


    public void setMenuListinMutableLiveData(List<MenuItems_Model> menuItems_List) {
        menuItemsLiveData.setValue(ApiResponseState_Enum.successwithmessage(menuItems_List,""));

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

    public void clearSelecteMenuJson()
    {
        selectedmenuJson.setValue("");
    }
    public LiveData<String> getSelecteMenuJson() {
        return selectedmenuJson;
    }

    public MutableLiveData<ApiResponseState_Enum<String>> updateInsertUpdateMenu(MenuItems_Model menuItemsModel, String process) {
        MutableLiveData<ApiResponseState_Enum<String>> resultLiveData = repository.updateInsertUpdateMenu( menuItemsModel,process);
        resultLiveData.observeForever(result -> {
            if (result != null && result.status == ApiResponseState_Enum.Status.SUCCESS) {
                updateInsertUpdateMenuData(menuItemsModel,process);
            }
        });
        return resultLiveData;
    }

    public  void updateInsertUpdateMenuData(MenuItems_Model menuItemsModel, String process)
    {
        ApiResponseState_Enum<List<MenuItems_Model>> currentData = menuItemsLiveData.getValue();
        List<MenuItems_Model> originalData = menuItemsLiveDataOriginal;

        if(Objects.equals(process, "add"))
        {
            if (originalData != null) {

                originalData.add(menuItemsModel);
                menuItemsLiveDataOriginal = new ArrayList<>(originalData);
                    menuItemsLiveData.setValue(ApiResponseState_Enum.successwithmessage(originalData,""));


            }
            return;
        }
        if (originalData != null) {
            List<MenuItems_Model> updatedOrders = new ArrayList<>(menuItemsLiveDataOriginal);
            for (MenuItems_Model order : updatedOrders) {
                if (order.getItemkey().equals(menuItemsModel.getItemkey())) {
                    order.setItemname(menuItemsModel.getItemname());

                    break;
                }

            }
        }
        if (currentData != null && currentData.data != null) {
            List<MenuItems_Model> updatedOrders = new ArrayList<>(currentData.data);
            for (MenuItems_Model order : updatedOrders) {
                if (order.getItemkey().equals(menuItemsModel.getItemkey())) {
                    order.setItemname(menuItemsModel.getItemname());

                    break;
                }

            }

            menuItemsLiveData.setValue(ApiResponseState_Enum.successwithmessage(updatedOrders,""));
            //  orderDetailsLiveData.observeForever(ordersObserver);

            //orderDetailsLiveData.setValue(new ApiResponseState_Enum.Status.SUCCESS, updatedOrders, null));
        }
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
