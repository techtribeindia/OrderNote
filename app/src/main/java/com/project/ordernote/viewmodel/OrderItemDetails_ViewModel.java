package com.project.ordernote.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.google.firebase.Timestamp;
import com.project.ordernote.data.model.ItemDetails_Model;
import com.project.ordernote.data.model.OrderItemDetails_Model;
import com.project.ordernote.data.repository.OrderItemDetails_Repository;

import java.util.List;
import java.util.UUID;

public class OrderItemDetails_ViewModel extends AndroidViewModel {


    private OrderItemDetails_Repository repository = null;

    public OrderItemDetails_ViewModel(@NonNull Application application) {
        super(application);
        if(repository == null ){
            repository = new OrderItemDetails_Repository();

        }


    }


    public void addItemDetailsEntryInDBFromLOOP(List<ItemDetails_Model> itemDetailsModelList , String vendorkey , String vendorName , String orderid, Timestamp orderplaceddate){

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
            orderItemDetailsModel.setOrderplacedtime(orderplaceddate);


            repository.addOrderItemDetailsInDB(orderItemDetailsModel);

        }




    }

}
