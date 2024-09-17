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

import com.project.ordernote.data.model.MenuItems_Model;

import com.project.ordernote.data.model.OrderDetails_Model;
import com.project.ordernote.data.remote.FirestoreService;
import com.project.ordernote.data.repository.OrderDetails_Repository;
import com.project.ordernote.utils.ApiResponseState_Enum;
import com.project.ordernote.utils.Constants;
import com.project.ordernote.utils.calculations.MenuItemValueCalculator;
import com.project.ordernote.utils.calculations.OrderValueCalculator;

import java.util.ArrayList;
import java.util.List;

import java.util.Objects;


public class OrderDetails_ViewModel extends AndroidViewModel {
    private final OrderDetails_Repository repository;
    public String vendorkey="";
    private MutableLiveData<ApiResponseState_Enum<List<OrderDetails_Model>>> orderDetailsLiveData;
    private MutableLiveData<ApiResponseState_Enum<List<OrderDetails_Model>>> orderDetailsForReportScreenData;

    private MutableLiveData<String> selectedOrderJson;

    private Observer<ApiResponseState_Enum<List<OrderDetails_Model>>> ordersObserver;
    private Observer<ApiResponseState_Enum<List<OrderDetails_Model>>> ordersforReportScreenObserver;

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

    public  void setUserDetails(String vendorkey)
    {
        this.vendorkey = vendorkey;
        repository.setUserDetails(vendorkey);
    }

    public void clearAllLiveData(){

        try {
            orderDetailsLiveData = new MutableLiveData<>();

            if (orderDetailsLiveData.getValue() != null) {
                orderDetailsLiveData.setValue(ApiResponseState_Enum.success(new ArrayList<>()));
            }


            if (selectedOrderJson.getValue() != null) {
                selectedOrderJson.setValue("");
            }
            if (ordersValueModel.getValue() != null) {
                ordersValueModel.setValue(new OrderDetails_Model());
            }

            if (discountLiveData.getValue() == null) {
                discountLiveData.setValue(0.00);
            }
            if (itemDetailsArrayListLiveData.getValue() != null) {
                itemDetailsArrayListLiveData.setValue(new ArrayList<>());
            }


            discountLiveData = new MutableLiveData<>();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

      private void initObserver() {
        if(orderDetailsLiveData == null){
            orderDetailsLiveData = new MutableLiveData<>();

        }
        ordersObserver = state -> orderDetailsLiveData.setValue(state);
        if(orderDetailsForReportScreenData == null){
            orderDetailsForReportScreenData = new MutableLiveData<>();
        }
        ordersforReportScreenObserver = reportScreenstate -> orderDetailsForReportScreenData.setValue(reportScreenstate);
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



    public LiveData<ApiResponseState_Enum<List<OrderDetails_Model>>> getOrdersListForReportDetailsFromViewModel() {
        if(orderDetailsForReportScreenData == null){
            orderDetailsForReportScreenData = new MutableLiveData<>();
        }
        return orderDetailsForReportScreenData;
    }



    public void clearFromViewModel()
    {
        List<OrderDetails_Model> updatedOrders = new ArrayList<>();
        orderDetailsLiveData.setValue(ApiResponseState_Enum.success(updatedOrders));
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
 


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

 
  
    public void getOrdersByStatus_DateAndVendorKey(String status, Timestamp startTimestamp, Timestamp endTimestamp , String vendorkey) {
        try {

            LiveData<ApiResponseState_Enum<List<OrderDetails_Model>>> source = repository.getOrdersByStatus_DateAndVendorKey(status, startTimestamp, endTimestamp, vendorkey);
            source.observeForever(ordersObserver);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setSelectedOrder(OrderDetails_Model order) {
        Gson gson = new Gson();
        String orderJson = gson.toJson(order);
        selectedOrderJson.setValue(orderJson);
    }

    public MutableLiveData<ApiResponseState_Enum<String>> acceptOrder(String orderId, String status) {
        MutableLiveData<ApiResponseState_Enum<String>> resultLiveData = repository.acceptOrder(orderId, status);
        resultLiveData.observeForever(result -> {
            if (result != null && result.status == ApiResponseState_Enum.Status.SUCCESS) {
                removeOrderFromLiveData(orderId);
            }
        });
        return resultLiveData;
    }

    public MutableLiveData<ApiResponseState_Enum<String>> rejectOrder(String orderId, String status) {
        return null;
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

    public MutableLiveData<ApiResponseState_Enum<String>> placeEditRequest(String orderId, String DispatchStatus) {
        MutableLiveData<ApiResponseState_Enum<String>> resultLiveData = repository.orderEditRequest( orderId,DispatchStatus);
        resultLiveData.observeForever(result -> {
            if (result != null && result.status == ApiResponseState_Enum.Status.SUCCESS) {
                editOrderDetails(orderId);
            }
        });
        return resultLiveData;
    }

    public MutableLiveData<ApiResponseState_Enum<String>> updateBatchDetails(String orderid,String transporName, String driverMobieno, String truckNo) {
        MutableLiveData<ApiResponseState_Enum<String>> resultLiveData = repository.updateBatchDetails( orderid,transporName,driverMobieno,truckNo);
        resultLiveData.observeForever(result -> {
            if (result != null && result.status == ApiResponseState_Enum.Status.SUCCESS) {
                updateBatchDetailsData(orderid,transporName,driverMobieno,truckNo);
            }
        });
        return resultLiveData;
    }

    public void  updateBatchDetailsData(String orderid,String transporName, String driverMobieno, String truckNo)
    {
        ApiResponseState_Enum<List<OrderDetails_Model>> currentData = orderDetailsLiveData.getValue();
        if (currentData != null && currentData.data != null) {
            List<OrderDetails_Model> updatedOrders = new ArrayList<>(currentData.data);
            for (OrderDetails_Model order : updatedOrders) {
                if (order.getOrderid().equals(orderid)) {
                    order.setDispatchstatus("DISPATCHED");
                    order.setTransportname(transporName);
                    order.setDrivermobileno(driverMobieno);
                    order.setTruckno(truckNo);
                    break;
                }
            }

            orderDetailsLiveData.setValue(ApiResponseState_Enum.success(updatedOrders));
            //  orderDetailsLiveData.observeForever(ordersObserver);

            //orderDetailsLiveData.setValue(new ApiResponseState_Enum.Status.SUCCESS, updatedOrders, null));
        }
    }
    public void editOrderDetails(String orderId)
    {
        ApiResponseState_Enum<List<OrderDetails_Model>> currentData = orderDetailsLiveData.getValue();
        if (currentData != null && currentData.data != null) {
            List<OrderDetails_Model> updatedOrders = new ArrayList<>(currentData.data);
            for (OrderDetails_Model order : updatedOrders) {
                if (order.getOrderid().equals(orderId)) {
                    order.setDispatchstatus("EDITREQUESTED");
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

    public void clearSelectedOrderJson()
    {
        selectedOrderJson.setValue("");
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

            try{
                if(!cartItemList.isEmpty()) {
                    boolean isSameItemHaveDifferentPrice = false;
                    boolean isSameItemWithDifferentPriceAdded = false;
                    for (int i = 0; i < cartItemList.size(); i++) {
                        ItemDetails_Model itemDetailsModelFromCart = cartItemList.get(i);

                        if (menuItemsModel.getItemkey().equals(itemDetailsModelFromCart.getMenuitemkey())) {
                            //item already added in cart

                            if(menuItemsModel.getItemtype().equals(Constants.priceperkg_pricetype)){
                                if((menuItemsModel.getPriceperkg() == itemDetailsModelFromCart.getMenuitemprice() ) && (menuItemsModel.getGrossweight() == itemDetailsModelFromCart.getGrossweight() ) )
                                {
                                    //handle for priceperkg
                                    double grossweightfromCart = itemDetailsModelFromCart.getGrossweight();
                                    double grossweightFromMenu = menuItemsModel.getGrossweight();
                                    int qtyfromCart = itemDetailsModelFromCart.getQuantity();
                                    int qtyFromMenu = (int) menuItemsModel.getQuantity();
                                    int quantity = qtyfromCart  + qtyFromMenu;
                                    itemDetailsModelFromCart.setQuantity(quantity);
                                    menuItemsModel.setQuantity((double) quantity);
                                    //double grossweight = grossweightFromMenu + grossweightfromCart;
                                    //itemDetailsModelFromCart.setGrossweight(grossweight);
                                    double itemtotalprice = MenuItemValueCalculator.calculateItemtotalPrice(menuItemsModel);
                                    itemDetailsModelFromCart.setTotalprice(itemtotalprice);

                                    cartItemList.set(i, itemDetailsModelFromCart);
                                    break;
                                }
                                else{


                                }
                            }
                            else{
                                //handle for unitprice
                                if(menuItemsModel.getUnitprice() == itemDetailsModelFromCart.getMenuitemprice()) {

                                    int quantityfromCart = itemDetailsModelFromCart.getQuantity();
                                    int quantityFromMenu = (int) menuItemsModel.getQuantity();
                                    int quantity = 0;

                                    quantity = quantityFromMenu + quantityfromCart;
                                    itemDetailsModelFromCart.setQuantity(quantity);

                                    double itemtotalprice = MenuItemValueCalculator.calculateItemtotalPrice(menuItemsModel);
                                    itemDetailsModelFromCart.setTotalprice(itemtotalprice);
                                    cartItemList.set(i, itemDetailsModelFromCart);

                                    break;
                                }
                                else{

                                }
                            }


                        }

                            //last item of loop
                        if ((cartItemList.size() - 1) == i) {
                                // this particular item is not added in cart
                                ItemDetails_Model itemDetailsModel = new ItemDetails_Model();
                                itemDetailsModel.setGrossweight((menuItemsModel.getGrossweight()));

                                itemDetailsModel.setItemname(menuItemsModel.getItemname());
                                itemDetailsModel.setQuantity((int) menuItemsModel.getQuantity());
                                itemDetailsModel.setNetweight(menuItemsModel.getNetweight());
                                itemDetailsModel.setMenutype(menuItemsModel.getItemtype());
                                itemDetailsModel.setMenuitemkey(menuItemsModel.getItemkey());
                                if (menuItemsModel.getItemtype().equals(Constants.priceperkg_pricetype)) {
                                    itemDetailsModel.setMenuitemprice(menuItemsModel.getPriceperkg());

                                } else if (menuItemsModel.getItemtype().equals(Constants.unitprice_pricetype)) {
                                    itemDetailsModel.setMenuitemprice(menuItemsModel.getUnitprice());
                                }


                            double itemprice = MenuItemValueCalculator.calculateItemPrice(menuItemsModel);
                            itemDetailsModel.setPrice(itemprice);
                            double itemtotalprice = MenuItemValueCalculator.calculateItemtotalPrice(menuItemsModel);
                                itemDetailsModel.setTotalprice(itemtotalprice);
                                cartItemList.add(itemDetailsModel);

                                break;
                            }


                    }
                }
                else{
                    //no item added in cart
                    ItemDetails_Model itemDetailsModel = new ItemDetails_Model();
                    itemDetailsModel.setGrossweight((menuItemsModel.getGrossweight()));

                    itemDetailsModel.setItemname(menuItemsModel.getItemname());
                    itemDetailsModel.setQuantity((int) menuItemsModel.getQuantity());
                    itemDetailsModel.setNetweight(menuItemsModel.getNetweight());
                    itemDetailsModel.setMenutype(menuItemsModel.getItemtype());
                    itemDetailsModel.setMenuitemkey(menuItemsModel.getItemkey());
                    if (menuItemsModel.getItemtype().equals(Constants.priceperkg_pricetype)) {
                        itemDetailsModel.setMenuitemprice(menuItemsModel.getPriceperkg());

                    } else if (menuItemsModel.getItemtype().equals(Constants.unitprice_pricetype)) {
                        itemDetailsModel.setMenuitemprice(menuItemsModel.getUnitprice());
                    }


                    double itemprice = MenuItemValueCalculator.calculateItemPrice(menuItemsModel);
                    itemDetailsModel.setPrice(itemprice);

                    double itemtotalprice = MenuItemValueCalculator.calculateItemtotalPrice(menuItemsModel);
                    itemDetailsModel.setTotalprice(itemtotalprice);
                    cartItemList.add(itemDetailsModel);

                }
            }
            catch (Exception e){
                e.printStackTrace();
            }


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

    public void createOrderInDb(OrderDetails_Model orderDetailsModel , FirestoreService.FirestoreCallback<Void> callback) {
        repository.createOrder(orderDetailsModel, callback);

    }

    public void removeItemFromCart(int position) {


        List<ItemDetails_Model> cartItemList = new ArrayList<>(Objects.requireNonNull(itemDetailsArrayListLiveData.getValue()));
        cartItemList.remove(position);
        itemDetailsArrayListLiveData.setValue(cartItemList);
        OrderDetails_Model orderDetailsModel = new OrderDetails_Model();
        orderDetailsModel = OrderValueCalculator.CalculateOrderDetailsValue(itemDetailsArrayListLiveData.getValue() , discountLiveData.getValue());
        ordersValueModel.setValue(orderDetailsModel);


    }

    public int getTotalQtyFromItemDetailsArrayList() {

        if(!itemDetailsArrayListLiveData.getValue().isEmpty()){
            int totalquantity = 0;
            for(int i = 0 ; i < itemDetailsArrayListLiveData.getValue().size(); i++){
                int quantity = itemDetailsArrayListLiveData.getValue().get(i).getQuantity();
                totalquantity  = totalquantity + quantity;


            }
            return totalquantity;
        }
        else{
            return  0;
        }


    }

    public void getOrdersByBuyerKeyStatus_DateAndVendorKey(String selectedBuyerKey, String status, Timestamp startTimestamp, Timestamp endTimestamp, String vendorkey) {

        LiveData<ApiResponseState_Enum<List<OrderDetails_Model>>> source = repository.getOrdersByBuyerKey_Status_DateAndVendorKey(selectedBuyerKey , status, startTimestamp, endTimestamp , vendorkey);
        source.observeForever(ordersObserver);
    }

    public void getOrdersByDateAndVendorKey(Timestamp startdatee, Timestamp endDatee, String vendorkey) {

        LiveData<ApiResponseState_Enum<List<OrderDetails_Model>>> source = repository.getOrdersByDateAndVendorKey(startdatee , endDatee, vendorkey);
        source.observeForever(ordersforReportScreenObserver);
    }
}