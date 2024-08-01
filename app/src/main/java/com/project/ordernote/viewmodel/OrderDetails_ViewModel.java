package com.project.ordernote.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;


import com.google.firebase.Timestamp;
import com.google.gson.Gson;

 import com.project.ordernote.data.model.ItemDetails_Model;
 import com.google.gson.Gson;
 
import com.project.ordernote.data.model.MenuItems_Model;

import com.project.ordernote.data.model.OrderDetails_Model;
import com.project.ordernote.data.repository.OrderDetails_Repository;
import com.project.ordernote.utils.ApiResponseState_Enum;
import com.project.ordernote.utils.Constants;
import com.project.ordernote.utils.calculations.MenuItemValueCalculator;
import com.project.ordernote.utils.calculations.OrderValueCalculator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import java.util.Objects;


public class OrderDetails_ViewModel extends AndroidViewModel {
    private final OrderDetails_Repository repository;

    private MutableLiveData<ApiResponseState_Enum<List<OrderDetails_Model>>> orderDetailsLiveData;
    private MutableLiveData<String> selectedOrderJson;

    private Observer<ApiResponseState_Enum<List<OrderDetails_Model>>> ordersObserver;

    private MutableLiveData<List<ItemDetails_Model>> itemDetailsArrayListLiveData;
    private MutableLiveData<OrderDetails_Model> ordersValueModel;
    private MutableLiveData<Double> discountLiveData;




    public OrderDetails_ViewModel(@NonNull Application application) {
        super(application);
        repository = new OrderDetails_Repository();
        orderDetailsLiveData = new MutableLiveData<>();
        itemDetailsArrayListLiveData = new MutableLiveData<>();
        ordersValueModel = new MutableLiveData<>();
        selectedOrderJson = new MutableLiveData<>();
        discountLiveData = new MutableLiveData<>();
        initObserver();
    }



    private void initObserver() {
        ordersObserver = state -> orderDetailsLiveData.setValue(state);
    }

    public LiveData<ApiResponseState_Enum<List<OrderDetails_Model>>> getOrdersByStatusFromViewModel() {
        return orderDetailsLiveData;
    }
    public LiveData<ApiResponseState_Enum<List<OrderDetails_Model>>> getOrdersListFromViewModel() {
        if(orderDetailsLiveData == null){
            orderDetailsLiveData = new MutableLiveData<>();
        }
        return orderDetailsLiveData;
    }
    public LiveData<List<ItemDetails_Model>> getItemDetailsArraylistViewModel() {
        if(itemDetailsArrayListLiveData == null){
            itemDetailsArrayListLiveData = new MutableLiveData<>();
        }
        return itemDetailsArrayListLiveData;
    }

 
    public LiveData<OrderDetails_Model> getOrderDetailsCalculatedValueModel() {
        if(ordersValueModel == null){
            ordersValueModel = new MutableLiveData<>();
        }
        return ordersValueModel;
    }
 

    public void getOrdersByStatus(String status) {
        try {
       //     orderDetailsLiveData = repository.getOrdersByStatus(status);
         //   Log.d("orderdetails getOrdersByStatus  :  ", String.valueOf(Objects.requireNonNull(orderDetailsLiveData.getValue()).data));

        LiveData<ApiResponseState_Enum<List<OrderDetails_Model>>> source = repository.getOrdersByStatus(status);
        source.observeForever(ordersObserver);
 


    public LiveData<Double> getDiscountValue() {
        if(discountLiveData == null){
            discountLiveData = new MutableLiveData<>();
        }
        return discountLiveData;
    }

 
    public void setDisountValue(double discountAmount) {
        if(discountLiveData == null){
            discountLiveData = new MutableLiveData<>();
        }
        discountLiveData.setValue(discountAmount);
        OrderDetails_Model orderDetailsModel = new OrderDetails_Model();
        orderDetailsModel = OrderValueCalculator.CalculateOrderDetailsValue(itemDetailsArrayListLiveData.getValue() , discountAmount);
        ordersValueModel.setValue(orderDetailsModel);
    }

 
  
    public void getOrdersByStatusAndDate(String status, Timestamp startTimestamp, Timestamp endTimestamp) {

        LiveData<ApiResponseState_Enum<List<OrderDetails_Model>>> source = repository.getOrdersByStatusAndDate(status, startTimestamp, endTimestamp);
        source.observeForever(ordersObserver);
    }


    public LiveData<ApiResponseState_Enum<List<OrderDetails_Model>>> getOrdersListFromViewModel() {
        if(orderDetailsLiveData == null){
            orderDetailsLiveData = new MutableLiveData<>();
        }
        return orderDetailsLiveData;
    }







    public void setSelectedOrder(OrderDetails_Model order) {
        Gson gson = new Gson();
        String orderJson = gson.toJson(order);
        selectedOrderJson.setValue(orderJson);
    }

    public MutableLiveData<ApiResponseState_Enum<String>> acceptOrder(String transporName, String driverMobieno, String truckNo, String orderId, String status) {
        MutableLiveData<ApiResponseState_Enum<String>> resultLiveData = repository.acceptOrder(transporName, driverMobieno, truckNo, orderId, status);
        resultLiveData.observeForever(result -> {
            if (result != null && result.status == ApiResponseState_Enum.Status.SUCCESS) {
                removeOrderFromLiveData(orderId);
            }
        });
        return resultLiveData;
    }

    public MutableLiveData<ApiResponseState_Enum<String>> rejectOrder(String orderId,String status) {
        MutableLiveData<ApiResponseState_Enum<String>> resultLiveData = repository.rejectOrder( orderId,status);
        resultLiveData.observeForever(result -> {
            if (result != null && result.status == ApiResponseState_Enum.Status.SUCCESS) {
                removeOrderFromLiveData(orderId);
            }
        });
        return resultLiveData;
    }

    public MutableLiveData<ApiResponseState_Enum<String>> cancelOrder(String orderId,String status) {
        MutableLiveData<ApiResponseState_Enum<String>> resultLiveData = repository.cancelOrder( orderId,status);
        resultLiveData.observeForever(result -> {
            if (result != null && result.status == ApiResponseState_Enum.Status.SUCCESS) {
                removeOrderFromLiveData(orderId);
            }
        });
        return resultLiveData;
    }

    public MutableLiveData<ApiResponseState_Enum<String>> placeOrder(String orderId,String status) {
        MutableLiveData<ApiResponseState_Enum<String>> resultLiveData = repository.placeOrder( orderId,status);
        resultLiveData.observeForever(result -> {
            if (result != null && result.status == ApiResponseState_Enum.Status.SUCCESS) {
                removeOrderFromLiveData(orderId);
            }
        });
        return resultLiveData;
    }

    public MutableLiveData<ApiResponseState_Enum<String>> placeEditRequest(String orderId) {
        MutableLiveData<ApiResponseState_Enum<String>> resultLiveData = repository.orderEditRequest( orderId);
        resultLiveData.observeForever(result -> {
            if (result != null && result.status == ApiResponseState_Enum.Status.SUCCESS) {
                editOrderDetails(orderId);
            }
        });
        return resultLiveData;
    }

    public void editOrderDetails(String orderId)
    {
        ApiResponseState_Enum<List<OrderDetails_Model>> currentData = orderDetailsLiveData.getValue();
        if (currentData != null && currentData.data != null) {
            List<OrderDetails_Model> updatedOrders = new ArrayList<>(currentData.data);
            for (OrderDetails_Model order : updatedOrders) {
                if (order.getOrderid().equals(orderId)) {
                    order.setEditrequest(true);
                    break;
                }
            }

            orderDetailsLiveData.setValue(ApiResponseState_Enum.success(updatedOrders));
            //  orderDetailsLiveData.observeForever(ordersObserver);

            //orderDetailsLiveData.setValue(new ApiResponseState_Enum.Status.SUCCESS, updatedOrders, null));
        }
    }

    public void removeOrderFromLiveData(String orderId) {

  
 


        ApiResponseState_Enum<List<OrderDetails_Model>> currentData = orderDetailsLiveData.getValue();
        if (currentData != null && currentData.data != null) {
            List<OrderDetails_Model> updatedOrders = new ArrayList<>(currentData.data);
            for (OrderDetails_Model order : updatedOrders) {
                if (order.getOrderid().equals(orderId)) {
                    updatedOrders.remove(order);
                    break;
                }
            }

            orderDetailsLiveData.setValue(ApiResponseState_Enum.success(updatedOrders));
          //  orderDetailsLiveData.observeForever(ordersObserver);

            //orderDetailsLiveData.setValue(new ApiResponseState_Enum.Status.SUCCESS, updatedOrders, null));
        }
    }

    public LiveData<String> getSelectedOrderJson() {
        return selectedOrderJson;
    }

    @Override
    protected void onCleared() {
        super.onCleared();

        orderDetailsLiveData.removeObserver(ordersObserver);
    }

    public void addItemInCart(MenuItems_Model menuItemsModel) {
        try {
            if (itemDetailsArrayListLiveData == null) {
                itemDetailsArrayListLiveData = new MutableLiveData<>();
            }
            if (itemDetailsArrayListLiveData.getValue() == null) {
                itemDetailsArrayListLiveData.setValue(new ArrayList<>());
            }
            if(discountLiveData == null){
                discountLiveData = new MutableLiveData<>();

            }
            if (discountLiveData.getValue() == null) {
                discountLiveData.setValue(0.0);
            }


            List<ItemDetails_Model> cartItemList = new ArrayList<>(Objects.requireNonNull(itemDetailsArrayListLiveData.getValue()));
            ItemDetails_Model itemDetailsModel = new ItemDetails_Model();
            itemDetailsModel.setGrossweight(menuItemsModel.getGrossweight());
            itemDetailsModel.setItemname(menuItemsModel.getItemname());
            itemDetailsModel.setQuantity((int) menuItemsModel.getQuantity());
            itemDetailsModel.setNetweight(menuItemsModel.getNetweight());
            itemDetailsModel.setMenutype(menuItemsModel.getItemtype());
            itemDetailsModel.setMenuitemkey(menuItemsModel.getItemkey());
            if (menuItemsModel.getItemtype().equals(Constants.priceperkg_pricetype)) {
                itemDetailsModel.setPrice(menuItemsModel.getPriceperkg());

            } else if (menuItemsModel.getItemtype().equals(Constants.unitprice_pricetype)) {
                itemDetailsModel.setPrice(menuItemsModel.getUnitprice());
            }

            double itemtotalprice = MenuItemValueCalculator.calculateItemPrice(menuItemsModel);
            itemDetailsModel.setTotalprice(itemtotalprice);
            cartItemList.add(itemDetailsModel);
            itemDetailsArrayListLiveData.setValue(cartItemList);
            OrderDetails_Model orderDetailsModel = new OrderDetails_Model();
            orderDetailsModel = OrderValueCalculator.CalculateOrderDetailsValue(itemDetailsArrayListLiveData.getValue() , discountLiveData.getValue());
            ordersValueModel.setValue(orderDetailsModel);
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    public double getSubTotalPrice() {
        if(ordersValueModel == null){
            ordersValueModel = new MutableLiveData<>();

        }
        if (ordersValueModel.getValue() == null) {
            ordersValueModel.setValue(new OrderDetails_Model());
        }
        return ordersValueModel.getValue().getPrice();
    }

    public double getTotalPrice() {
        if(ordersValueModel == null){
            ordersValueModel = new MutableLiveData<>();

        }
        if (ordersValueModel.getValue() == null) {
            ordersValueModel.setValue(new OrderDetails_Model());
        }
        return ordersValueModel.getValue().getTotalprice();
    }
}