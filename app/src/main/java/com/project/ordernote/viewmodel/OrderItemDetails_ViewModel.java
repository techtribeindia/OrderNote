package com.project.ordernote.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.google.firebase.Timestamp;
import com.project.ordernote.data.model.Buyers_Model;
import com.project.ordernote.data.model.ItemDetails_Model;
import com.project.ordernote.data.model.OrderItemDetails_Model;
import com.project.ordernote.data.repository.OrderItemDetails_Repository;
import com.project.ordernote.utils.ApiResponseState_Enum;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class OrderItemDetails_ViewModel extends AndroidViewModel {


    private OrderItemDetails_Repository repository = null;
    private Observer<ApiResponseState_Enum<List<OrderItemDetails_Model>>> ordersItemDetailsObserver;
     private MutableLiveData<ApiResponseState_Enum<List<OrderItemDetails_Model>>> orderItemDetailsModelMutableLiveData;

    private Observer<ApiResponseState_Enum<List<OrderItemDetails_Model>>> ordersItemDetailsReportScreenObserver;
    private MutableLiveData<ApiResponseState_Enum<List<OrderItemDetails_Model>>> orderItemDetailsReportScreenMutableLiveData;


    public OrderItemDetails_ViewModel(@NonNull Application application) {
        super(application);
        if(repository == null ){
            repository = new OrderItemDetails_Repository();

        }
        initObserver();

    }


    private void initObserver() {
        if(orderItemDetailsModelMutableLiveData == null){
            orderItemDetailsModelMutableLiveData = new MutableLiveData<>();
        }
        ordersItemDetailsObserver = state -> orderItemDetailsModelMutableLiveData.setValue(state);
        if(orderItemDetailsReportScreenMutableLiveData == null){
            orderItemDetailsReportScreenMutableLiveData = new MutableLiveData<>();
        }
        ordersItemDetailsReportScreenObserver = state -> orderItemDetailsReportScreenMutableLiveData.setValue(state);
    }


    public LiveData<ApiResponseState_Enum<List<OrderItemDetails_Model>>> getOrdersItemDetailsReportScreenFromViewModel() {
        if(orderItemDetailsReportScreenMutableLiveData == null){
            orderItemDetailsReportScreenMutableLiveData = new MutableLiveData<>();
        }
        return orderItemDetailsReportScreenMutableLiveData;
    }

    public LiveData<ApiResponseState_Enum<List<OrderItemDetails_Model>>> getOrdersItemDetailsListFromViewModel() {
        if(orderItemDetailsModelMutableLiveData == null){
            orderItemDetailsModelMutableLiveData = new MutableLiveData<>();
        }
        return orderItemDetailsModelMutableLiveData;
    }


    public void addItemDetailsEntryInDBFromLOOP(List<ItemDetails_Model> itemDetailsModelList , String vendorkey , String vendorName , String orderid, Timestamp orderplaceddate, Buyers_Model buyersModel){

        for(int i = 0 ; i < itemDetailsModelList.size(); i ++){
            OrderItemDetails_Model orderItemDetailsModel = new OrderItemDetails_Model();
            orderItemDetailsModel.setGrossweight(itemDetailsModelList.get(i).getGrossweight());
            orderItemDetailsModel.setPortionsize(itemDetailsModelList.get(i).getPortionsize());
            orderItemDetailsModel.setItemname(itemDetailsModelList.get(i).getItemname());
            orderItemDetailsModel.setMenuitemkey(itemDetailsModelList.get(i).getMenuitemkey());
            orderItemDetailsModel.setMenutype(itemDetailsModelList.get(i).getMenutype());
            orderItemDetailsModel.setNetweight(itemDetailsModelList.get(i).getNetweight());
            orderItemDetailsModel.setQuantity(itemDetailsModelList.get(i).getQuantity());
            orderItemDetailsModel.setPrice(itemDetailsModelList.get(i).getPrice());
            orderItemDetailsModel.setTotalprice(itemDetailsModelList.get(i).getTotalprice());
            orderItemDetailsModel.setOrderid(orderid);
            orderItemDetailsModel.setUnqiuekey(String.valueOf(UUID.randomUUID()));
            orderItemDetailsModel.setMenuitemprice(itemDetailsModelList.get(i).getMenuitemprice());

            orderItemDetailsModel.setVendorkey(vendorkey);
            orderItemDetailsModel.setVendorname(vendorName);
            orderItemDetailsModel.setOrderplaceddate(orderplaceddate);


            repository.addOrderItemDetailsInDB(orderItemDetailsModel);

        }




    }

    public void getOrderItemsByDateAndVendorKey( Timestamp startTimestamp, Timestamp endTimestamp , String vendorkey) {
        try {
            LiveData<ApiResponseState_Enum<List<OrderItemDetails_Model>>> source = repository.getOrderItemsByDateAndVendorKey( startTimestamp, endTimestamp, vendorkey);
            source.observeForever(ordersItemDetailsObserver);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public void getOrderItemsByDateAndVendorKey_ReportScreenObserver(Timestamp startdatee, Timestamp endDatee, String vendorkey) {

        if(orderItemDetailsReportScreenMutableLiveData!=null && orderItemDetailsReportScreenMutableLiveData .getValue() != null  && orderItemDetailsReportScreenMutableLiveData .getValue().data != null) {
            orderItemDetailsReportScreenMutableLiveData .getValue().data.clear();
        }


        LiveData<ApiResponseState_Enum<List<OrderItemDetails_Model>>> source = repository.getOrderItemsByDateAndVendorKey(startdatee , endDatee , vendorkey);
        source.observeForever(ordersItemDetailsReportScreenObserver);

    }

    public boolean checkIfReportScreenLiveDataHaveItemDetailsForOrderId(String orderid) {

         try {
             if (orderItemDetailsReportScreenMutableLiveData == null) {
                 orderItemDetailsReportScreenMutableLiveData = new MutableLiveData<>();
                 Log.d("itemdetailviewmodel", "checkIfReportScreenLiveDataHaveItemDetailsForOrderId - null return");

                 return false;
             }
             for (int iterator = 0; iterator < Objects.requireNonNull(orderItemDetailsReportScreenMutableLiveData.getValue()).data.size(); iterator++) {
                 if (orderid.equals(orderItemDetailsReportScreenMutableLiveData.getValue().data.get(iterator).getOrderid())) {

                     return true;
                 } else {
                     if ((Objects.requireNonNull(orderItemDetailsReportScreenMutableLiveData.getValue()).data.size() - 1) == iterator) {
                         Log.d("itemdetailviewmodel", "checkIfReportScreenLiveDataHaveItemDetailsForOrderId - inside else return");

                         return false;

                     }
                 }
             }
         }
         catch (Exception e){
             e.printStackTrace();
             Log.d("itemdetailviewmodel", "checkIfReportScreenLiveDataHaveItemDetailsForOrderId - catch");

             return false;
         }
        Log.d("itemdetailviewmodel", "checkIfReportScreenLiveDataHaveItemDetailsForOrderId - last return");

        return false;

    }
}
