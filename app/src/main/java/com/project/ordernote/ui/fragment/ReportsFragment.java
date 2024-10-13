package com.project.ordernote.ui.fragment;

import static android.os.Build.VERSION.SDK_INT;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.project.ordernote.R;
import com.project.ordernote.data.model.OrderDetails_Model;
import com.project.ordernote.data.model.OrderItemDetails_Model;
import com.project.ordernote.data.model.ReportsFilterDetails_Model;
import com.project.ordernote.databinding.FragmentReportsBinding;
import com.project.ordernote.ui.activity.Dashboard;
import com.project.ordernote.utils.AlertDialogUtil;
import com.project.ordernote.utils.ApiResponseState_Enum;
import com.project.ordernote.utils.CheckPermissionForManageStorage;
import com.project.ordernote.utils.CheckPermissionForManageStorageInterface;
import com.project.ordernote.utils.Constants;
import com.project.ordernote.utils.DateParserClass;
import com.project.ordernote.utils.PDFGeneratorListener;
import com.project.ordernote.utils.SessionManager;
import com.project.ordernote.viewmodel.OrderDetails_ViewModel;
import com.project.ordernote.viewmodel.OrderItemDetails_ViewModel;
import com.project.ordernote.viewmodel.Reports_ViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class ReportsFragment extends Fragment {

    View bottomSheetView;
    FragmentReportsBinding binding;
    BottomSheetDialog bottomSheetDialog;
    Reports_ViewModel reportsViewModel;
    private OrderDetails_ViewModel ordersViewModel;
    private OrderItemDetails_ViewModel orderItemDetailsViewModel;
    private Observer<ApiResponseState_Enum<List<OrderItemDetails_Model>>> orderItemDetailsSalesDataObserver;
    private Observer<ApiResponseState_Enum<List<OrderDetails_Model>>> orderDetailsSalesDataObserver;
    private Observer<ApiResponseState_Enum<List<OrderItemDetails_Model>>> reportScreenOrderItemDetailsSalesDataObserver;
    private Observer<ApiResponseState_Enum<List<OrderDetails_Model>>> reportScreenOrderDetailsSalesDataObserver;

    HashMap<String , List<OrderItemDetails_Model>> orderwiseOrderItemDetails = new HashMap<>();
    HashMap<String , List<OrderDetails_Model>>  statuswiseOrderDetails = new HashMap<>();

    HashMap<String , JSONObject> statuswisetotalcountdetailsjson = new HashMap<>();
    HashMap<String , List<String>> statuswiseOrderid = new HashMap<>();

    PDFGeneratorListener pdfGeneratorListener;
    boolean isOrderDetailsFetchedData = false , isOrderItemDetailsFetchedData = false , processOrderAndOrderItemArrayForReportScreenUIMethodCalled = false;
    boolean isOrderDetailsForReportScreenFetchedData = false , isOrderItemDetailsForReportScreenFetchedData = false;

    private SessionManager sessionManager;
    boolean isTodaySelectedInReportScreen = false;

    private static int REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION = 1;
    CheckPermissionForManageStorageInterface checkPermissionForManageStorageInterface;

    Share_Or_View_BottomSheetDialogFragment shareOrViewBottomSheetDialogFragment;

    public ReportsFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static ReportsFragment newInstance(String param1, String param2) {
        ReportsFragment fragment = new ReportsFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        sessionManager = new SessionManager(requireActivity(), Constants.USERPREF_NAME);

        binding = FragmentReportsBinding.inflate(inflater, container, false);
        reportsViewModel = new ViewModelProvider(requireActivity()).get(Reports_ViewModel.class);
        setObserverForSalesData();
        setObserverForReportScreenSalesData();

        try {

            ordersViewModel = new ViewModelProvider(requireActivity()).get(OrderDetails_ViewModel.class);
            ordersViewModel.clearAllLiveData();
            ordersViewModel.getOrdersListFromViewModel().observeForever(orderDetailsSalesDataObserver);
            ordersViewModel.getOrdersListForReportDetailsFromViewModel().observeForever(reportScreenOrderDetailsSalesDataObserver);

        } catch (Exception e) {
            e.printStackTrace();
        }


        try{
            orderItemDetailsViewModel = new ViewModelProvider(requireActivity()).get(OrderItemDetails_ViewModel.class);
            orderItemDetailsViewModel.getOrdersItemDetailsListFromViewModel().observeForever(orderItemDetailsSalesDataObserver);
            orderItemDetailsViewModel.getOrdersItemDetailsReportScreenFromViewModel().observeForever(reportScreenOrderItemDetailsSalesDataObserver);

        }
        catch (Exception e){
            e.printStackTrace();
        }


        return binding.getRoot();
       // return inflater.inflate(R.layout.fragment_reports, container, false);
    }




    @SuppressLint({"ResourceAsColor", "SetTextI18n"})
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.vendorNameTextview.setText(String.valueOf(sessionManager.getVendorname())+" , ");
        binding.dateTimeDetails.setText(String.valueOf(DateParserClass.getDateTimeInReadableFormat()));

        binding.todayCommonFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Drawable backgroundtoday = ContextCompat.getDrawable(requireActivity(), R.drawable.round_redbackground_withoutpadding);
                binding.todayCommonFilter.setBackground(backgroundtoday);
                binding.todayCommonFilter.setTextColor(getResources().getColor(R.color.white));

                Drawable backgroundweek = ContextCompat.getDrawable(requireActivity(), R.drawable.round_lightredbackground);
                binding.weekCommonFilter.setBackground(backgroundweek);
                binding.weekCommonFilter.setTextColor(getResources().getColor(R.color.black));
                isTodaySelectedInReportScreen = true;
               // Toast.makeText(requireActivity(), "Today", Toast.LENGTH_SHORT).show();


                Timestamp startdatee = (DateParserClass.convertGivenDateToTimeStamp(DateParserClass.getDateInStandardFormat()+" 00:00:00"));
                Timestamp endDatee = (DateParserClass.convertGivenDateToTimeStamp(DateParserClass.getDateInStandardFormat()+" 23:59:59"));
                processOrderAndOrderItemArrayForReportScreenUIMethodCalled = false;
                ordersViewModel.getOrdersByDateAndVendorKey( startdatee,endDatee ,sessionManager.getVendorkey());
                orderItemDetailsViewModel.getOrderItemsByDateAndVendorKey_ReportScreenObserver( startdatee, endDatee , sessionManager.getVendorkey());


            }
        });
        binding.weekCommonFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Drawable backgroundweek = ContextCompat.getDrawable(requireActivity(), R.drawable.round_redbackground_withoutpadding);
                binding.weekCommonFilter.setBackground(backgroundweek);
                binding.weekCommonFilter.setTextColor(getResources().getColor(R.color.white));

                Drawable backgroundtoday = ContextCompat.getDrawable(requireActivity(), R.drawable.round_lightredbackground);
                binding.todayCommonFilter.setBackground(backgroundtoday);
                binding.todayCommonFilter.setTextColor(getResources().getColor(R.color.black));
                //Toast.makeText(requireActivity(), "Week", Toast.LENGTH_SHORT).show();
                isTodaySelectedInReportScreen = false;
                processOrderAndOrderItemArrayForReportScreenUIMethodCalled = false;
                //Timestamp startdatee = (DateParserClass.convertGivenDateToTimeStamp(DateParserClass.getFirstDayOfWeek(DateParserClass.getDateInStandardFormat())+" 00:00:00"));
                Timestamp startdatee = (DateParserClass.convertGivenDateToTimeStamp("06/10/2024 00:00:00"));
                Timestamp endDatee = (DateParserClass.convertGivenDateToTimeStamp(DateParserClass.getDateInStandardFormat()+" 23:59:59"));
                ordersViewModel.getOrdersByDateAndVendorKey( startdatee,endDatee ,sessionManager.getVendorkey());
                orderItemDetailsViewModel.getOrderItemsByDateAndVendorKey_ReportScreenObserver( startdatee, endDatee , sessionManager.getVendorkey());

            }
        });
        binding.todayCommonFilter.performClick();
        binding.customSalesReportCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showGenerateCustomSalesReportDialog();
            }
        });

        binding.refreshLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.dateTimeDetails.setText(String.valueOf(DateParserClass.getDateTimeInReadableFormat()));

                if (isTodaySelectedInReportScreen) {
                    binding.todayCommonFilter.performClick();
                }
                else{
                    binding.weekCommonFilter.performClick();
                }

            }
        });

        binding.downloadReportScrenData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!statuswisetotalcountdetailsjson.isEmpty() && !orderwiseOrderItemDetails.isEmpty() && !statuswiseOrderid.isEmpty()){
                    if(isTodaySelectedInReportScreen){
                        setListenerAndGenerateReport(Constants.today_statuswise_pdf);

                    }
                    else{
                        setListenerAndGenerateReport(Constants.week_statuswise_pdf);
                    }

                }
                else{
                    showSnackbar(view,"There is no data to generate report");

                  //  Toast.makeText(requireActivity(), "There is no data to generate", Toast.LENGTH_SHORT).show();
                }

            }
        });


        checkPermissionForManageStorageInterface = new CheckPermissionForManageStorageInterface() {
            @Override
            public void onPermissionRejected(String result, boolean isJustCheckForPermission) {
                if(isJustCheckForPermission){
                    try {
                        // Inside an Activity or Fragment
                        AlertDialogUtil.showCustomDialogllowAccess(
                                requireActivity(),
                                "Grant All file access",
                                "Please allow app to manage files in device storage \n\nAll file access -> OrderNote -> TurnOn", "Open Allow Access ", "Not now","RED",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Handle positive button click


                                        CheckPermissionForManageStorage.checkPermissionForManageStorageFromFragment(requireContext() , requireActivity() , checkPermissionForManageStorageInterface);

                                       // Toast.makeText(requireActivity(), "Create", Toast.LENGTH_SHORT).show();
                                    }
                                },
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Handle negative button click
                                        CheckPermissionForManageStorage.justCheckForPermission(requireContext() , requireActivity() , checkPermissionForManageStorageInterface);

                                       // Toast.makeText(requireActivity(), "NotNow", Toast.LENGTH_SHORT).show();

                                    }
                                }
                        );





                    }
                    catch (Exception e){
                        e.printStackTrace();
                        CheckPermissionForManageStorage.justCheckForPermission(requireContext() , requireActivity() , checkPermissionForManageStorageInterface);

                    }


                }
            }

            @Override
            public void onPermissionGranted(String result, boolean isJustCheckForPermission) {
               // Toast.makeText(requireActivity(), "Permission Granted", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onPermissionRequired(String result, boolean isJustCheckForPermission) {
                if(isJustCheckForPermission){
                    try {
                        // Inside an Activity or Fragment
                        AlertDialogUtil.showCustomDialogllowAccess(
                                requireActivity(),
                                "Grant All file access",
                                "Please allow app to manage files in device storage \n\nAll file access -> OrderNote -> TurnOn", "Open Allow Access ", "Not now","RED",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Handle positive button click


                                        CheckPermissionForManageStorage.checkPermissionForManageStorageFromFragment(requireContext() , requireActivity() , checkPermissionForManageStorageInterface);

                                       // Toast.makeText(requireActivity(), "Create", Toast.LENGTH_SHORT).show();
                                    }
                                },
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Handle negative button click
                                        CheckPermissionForManageStorage.justCheckForPermission(requireContext() , requireActivity() , checkPermissionForManageStorageInterface);

                                      //  Toast.makeText(requireActivity(), "NotNow", Toast.LENGTH_SHORT).show();

                                    }
                                }
                        );





                    }
                    catch (Exception e){
                        e.printStackTrace();
                        CheckPermissionForManageStorage.justCheckForPermission(requireContext() , requireActivity() , checkPermissionForManageStorageInterface);

                    }


                }

              //  Toast.makeText(requireActivity(), result, Toast.LENGTH_SHORT).show();

            }

        };
        CheckPermissionForManageStorage.justCheckForPermission(requireContext() , requireActivity() , checkPermissionForManageStorageInterface);

       // setObserverForSalesData();
    }



    private void setObserverForReportScreenSalesData() {

        try {
            isOrderDetailsForReportScreenFetchedData = false ;
            isOrderItemDetailsForReportScreenFetchedData = false;


            reportScreenOrderDetailsSalesDataObserver = resource -> {
                // Update your UI or perform any actions based on the updated data
                try {
                    if (resource != null) {
                        switch (resource.status) {
                            case LOADING:
                                showProgressBar(true);
                                //  Toast.makeText(requireActivity(), "Loading Order Details", Toast.LENGTH_SHORT).show();
                                break;
                            case SUCCESS:
                                isOrderDetailsForReportScreenFetchedData = true;
                                if(isOrderItemDetailsForReportScreenFetchedData){

                                    processOrderAndOrderItemArrayForReportScreenUI();

                                }

                                break;
                            case ERROR:
                                if(resource.message.equals(Constants.noDataAvailable)){
                                    isOrderDetailsForReportScreenFetchedData = true;
                                    showSnackbar(getView() ,"No order Data for selected filter");
                                   // Toast.makeText(requireActivity(), "No order Data for selected filter", Toast.LENGTH_SHORT).show();
                                    if(isOrderItemDetailsForReportScreenFetchedData){
                                        setDataOnUI();
                                        showProgressBar(false);
                                      //  Toast.makeText(requireActivity(), "Successfully fetched the order details", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else{
                                   // Toast.makeText(requireActivity(), "Error while fetching order details  ," + String.valueOf(resource.message), Toast.LENGTH_SHORT).show();
                                    showSnackbar(getView() ,"Error while fetching order details ");

                                }

                                break;
                        }
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            };

            reportScreenOrderItemDetailsSalesDataObserver = resource -> {
                // Update your UI or perform any actions based on the updated data
                try {
                    if (resource != null) {
                        switch (resource.status) {
                            case LOADING:
                                showProgressBar(true);
                                //  Toast.makeText(requireActivity(), "Loading Order Item Details", Toast.LENGTH_SHORT).show();
                                break;
                            case SUCCESS:
                                isOrderItemDetailsForReportScreenFetchedData = true;
                                if(isOrderDetailsForReportScreenFetchedData){

                                    processOrderAndOrderItemArrayForReportScreenUI();
                                 }

                                break;
                            case ERROR:
                                if(resource.message.equals(Constants.noDataAvailable)){
                                    isOrderItemDetailsForReportScreenFetchedData = true;
                                    showSnackbar(getView() ,"No order Item Data for selected filter");
                                   // Toast.makeText(requireActivity(), "No order Item Data for selected filter", Toast.LENGTH_SHORT).show();
                                    if(isOrderDetailsForReportScreenFetchedData){
                                        setDataOnUI();
                                        showProgressBar(false);
                                      //  Toast.makeText(requireActivity(), "Successfully fetched the order Item details", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else{
                                   // Toast.makeText(requireActivity(), "Error while fetching order Item details  ," + String.valueOf(resource.message), Toast.LENGTH_SHORT).show();
                                    showSnackbar(getView() ,"Error while fetching order Item details ");
                                }

                                break;
                        }
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            };

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void processOrderAndOrderItemArrayForReportScreenUI() {


        statuswiseOrderDetails.clear();
        statuswisetotalcountdetailsjson.clear();
        orderwiseOrderItemDetails.clear();
        statuswiseOrderid.clear();

        if( ordersViewModel.getOrdersListForReportDetailsFromViewModel().getValue() != null && ordersViewModel.getOrdersListForReportDetailsFromViewModel().getValue().data != null  && orderItemDetailsViewModel.getOrdersItemDetailsReportScreenFromViewModel().getValue() != null && orderItemDetailsViewModel.getOrdersItemDetailsReportScreenFromViewModel().getValue().data != null && !Objects.requireNonNull(ordersViewModel.getOrdersListForReportDetailsFromViewModel().getValue()).data.isEmpty() && !Objects.requireNonNull(orderItemDetailsViewModel.getOrdersItemDetailsReportScreenFromViewModel().getValue()).data.isEmpty()) {
        for (int orderIterator = 0; orderIterator < Objects.requireNonNull(ordersViewModel.getOrdersListForReportDetailsFromViewModel().getValue()).data.size(); orderIterator++) {
            OrderDetails_Model orderDetailsModel = Objects.requireNonNull(ordersViewModel.getOrdersListForReportDetailsFromViewModel().getValue()).data.get(orderIterator);
            if(orderItemDetailsViewModel.checkIfReportScreenLiveDataHaveItemDetailsForOrderId(orderDetailsModel.getOrderid())) {
                if (statuswiseOrderDetails.containsKey(orderDetailsModel.getStatus())) {

                    List<String> orderIDList = new ArrayList<>(Objects.requireNonNull(statuswiseOrderid.get(orderDetailsModel.getStatus())));
                    if (!orderIDList.contains(orderDetailsModel.getOrderid())) {
                        orderIDList.add(String.valueOf(orderDetailsModel.getOrderid()));
                        statuswiseOrderid.put(orderDetailsModel.getStatus(), orderIDList);

                    }

                    List<OrderDetails_Model> orderDetailsModelList = new ArrayList<>(Objects.requireNonNull(statuswiseOrderDetails.get(orderDetailsModel.getStatus())));
                    orderDetailsModelList.add(orderDetailsModel);
                    statuswiseOrderDetails.replace(orderDetailsModel.getStatus(), orderDetailsModelList);
                } else {

                    List<String> orderIDList = new ArrayList<>();
                    List<OrderDetails_Model> orderDetailsModelList = new ArrayList<>();

                    orderDetailsModelList.add(orderDetailsModel);
                    statuswiseOrderDetails.put(orderDetailsModel.getStatus(), orderDetailsModelList);

                    orderIDList.add(String.valueOf(orderDetailsModel.getOrderid()));
                    statuswiseOrderid.put(orderDetailsModel.getStatus(), orderIDList);


                }


                double orderPrice = 0, orderDiscount = 0, finalPrice = 0;
                orderPrice = orderDetailsModel.getTotalprice();
                orderDiscount = orderDetailsModel.getDiscount();
                if (orderDiscount > 0) {
                    finalPrice = orderPrice + orderDiscount;
                } else {
                    finalPrice = orderPrice;
                }
                if (statuswisetotalcountdetailsjson.containsKey(orderDetailsModel.getStatus())) {
                    JSONObject jsonObject = statuswisetotalcountdetailsjson.get(orderDetailsModel.getStatus());
                    int ordersCountFromJson = 0;
                    double priceFromJson = 0, finalPriceFromJson = 0, discountPiceFromJson = 0;
                    try {
                        ordersCountFromJson = jsonObject.getInt("count");
                        priceFromJson = jsonObject.getDouble("price");
                        priceFromJson = orderPrice + priceFromJson;
                        discountPiceFromJson = jsonObject.getDouble("discount");
                        finalPriceFromJson = jsonObject.getDouble("pricebeforediscount");

                        finalPriceFromJson = finalPriceFromJson + finalPrice;
                        discountPiceFromJson = discountPiceFromJson + orderDiscount;

                        ordersCountFromJson = ordersCountFromJson + 1;
                        jsonObject.put("discount", discountPiceFromJson);
                        jsonObject.put("pricebeforediscount", finalPriceFromJson);

                        jsonObject.put("price", priceFromJson);
                        jsonObject.put("count", ordersCountFromJson);
                        statuswisetotalcountdetailsjson.replace(orderDetailsModel.getStatus(), jsonObject);

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                } else {

                    JSONObject jsonObject = new JSONObject();

                    try {
                        jsonObject.put("price", orderPrice);
                        jsonObject.put("count", 1);
                        jsonObject.put("discount", orderDiscount);
                        jsonObject.put("pricebeforediscount", finalPrice);
                        jsonObject.put("status", orderDetailsModel.getStatus());
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    statuswisetotalcountdetailsjson.put(orderDetailsModel.getStatus(), jsonObject);


                }
            }

            }
        for (int itemIterator = 0; itemIterator < Objects.requireNonNull(orderItemDetailsViewModel.getOrdersItemDetailsReportScreenFromViewModel().getValue()).data.size(); itemIterator++) {
                List<OrderItemDetails_Model> orderItemDetailsArrayList = new ArrayList<>();

                OrderItemDetails_Model orderItemDetailsModel = orderItemDetailsViewModel.getOrdersItemDetailsReportScreenFromViewModel().getValue().data.get(itemIterator);

                if (orderwiseOrderItemDetails.containsKey(orderItemDetailsModel.getOrderid())) {
                    orderItemDetailsArrayList = orderwiseOrderItemDetails.get(orderItemDetailsModel.getOrderid());
                    Objects.requireNonNull(orderItemDetailsArrayList).add(orderItemDetailsModel);
                    orderwiseOrderItemDetails.replace(orderItemDetailsModel.getOrderid(), orderItemDetailsArrayList);
                } else {

                    orderItemDetailsArrayList.add(orderItemDetailsModel);
                    orderwiseOrderItemDetails.put(orderItemDetailsModel.getOrderid(), orderItemDetailsArrayList);

                }

            }


        setDataOnUI();
        }
        else{

            if(processOrderAndOrderItemArrayForReportScreenUIMethodCalled){
                setDataOnUI();

                return;
            }
            processOrderAndOrderItemArrayForReportScreenUIMethodCalled = true;

            // Create a scheduled thread pool with 1 thread
            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

            // Create a Handler associated with the main thread's Looper
            Handler mainHandler = new Handler(Looper.getMainLooper());


            // Schedule the task to run after 6 seconds
            scheduler.schedule(new Runnable() {
                @Override
                public void run() {
                    // Code to execute after 6 seconds

                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            // UI operations go here
                            showProgressBar(false);
                        }
                    });


                    System.out.println("Thread executed after 6 seconds");
                    //showProgressBar(false);
                }
            }, 6, TimeUnit.SECONDS);

        }


    }


    @SuppressLint("SetTextI18n")
    private void setDataOnUI() {
          DecimalFormat df = new DecimalFormat(Constants.twoDecimalWithCommaPattern);

        try{
            if(statuswisetotalcountdetailsjson.containsKey(Constants.pending_status)) {
                //JSONObject statusBasedObject = statuswisetotalcountdetailsjson.get(Constants.created_status);
                binding.pendingOrdersCountTextview.setText(statuswisetotalcountdetailsjson.get(Constants.pending_status).getInt("count") + " Orders");
                binding.totalpendingorderPriceTextview.setText("₹ " + String.valueOf(df.format(statuswisetotalcountdetailsjson.get(Constants.pending_status).getDouble("price"))));

            }
            else{
                binding.pendingOrdersCountTextview.setText("0 Orders");
                binding.totalpendingorderPriceTextview.setText("₹ 00,000");

            }
            if(statuswisetotalcountdetailsjson.containsKey(Constants.accepted_status)) {
                binding.acceptedOrdersCountTextview.setText(statuswisetotalcountdetailsjson.get(Constants.accepted_status).getInt("count") + " Orders");
                binding.totalAcceptedOrdersPriceTextview.setText("₹ " + String.valueOf(df.format(statuswisetotalcountdetailsjson.get(Constants.accepted_status).getDouble("price"))));
            }
            else{
                binding.acceptedOrdersCountTextview.setText("0 Orders");
                binding.totalAcceptedOrdersPriceTextview.setText("₹ 00,000");

            }
            if(statuswisetotalcountdetailsjson.containsKey(Constants.rejected_status)) {

                binding.ordersRejectedCountTextview.setText(statuswisetotalcountdetailsjson.get(Constants.rejected_status).getInt("count") + " Orders");
                binding.totalRejectedPriceTextview.setText("₹ " + String.valueOf(df.format(statuswisetotalcountdetailsjson.get(Constants.rejected_status).getDouble("price"))));
            }
            else{
                binding.ordersRejectedCountTextview.setText("0 Orders");
                binding.totalRejectedPriceTextview.setText("₹ 00,000");

            }
            if(statuswisetotalcountdetailsjson.containsKey(Constants.cancelled_status)) {

                binding.ordersCancelledCountTextview.setText(statuswisetotalcountdetailsjson.get(Constants.cancelled_status).getInt("count") + " Orders");
                binding.totalCancelledPriceTextview.setText("₹ " + String.valueOf(df.format(statuswisetotalcountdetailsjson.get(Constants.cancelled_status).getDouble("price"))));
            }
            else{
                binding.ordersCancelledCountTextview.setText("0 Orders");
                binding.totalCancelledPriceTextview.setText("₹ 00,000");

            }
        }
        catch (Exception e){
            e.printStackTrace();
        }


        showProgressBar(false);
        //Toast.makeText(requireActivity(), "SetDatOnUI Called", Toast.LENGTH_SHORT).show();
    }

    private void setObserverForSalesData() {

        try {
            isOrderDetailsFetchedData = false ;
            isOrderItemDetailsFetchedData = false;


            orderDetailsSalesDataObserver = resource -> {
                // Update your UI or perform any actions based on the updated data
                try {
                    if (resource != null) {
                        switch (resource.status) {
                            case LOADING:
                                showProgressBar(true);
                                //  Toast.makeText(requireActivity(), "Loading Order Details", Toast.LENGTH_SHORT).show();
                                break;
                            case SUCCESS:
                                isOrderDetailsFetchedData = true;
                                if(isOrderItemDetailsFetchedData){

                                    //Toast.makeText(requireActivity(), Objects.requireNonNull(reportsViewModel.getFilteredReports().getValue()).getSelectedFileType(), Toast.LENGTH_SHORT).show();



                                    if(!resource.data.isEmpty()) {
                                        if (Objects.requireNonNull(reportsViewModel.getFilteredReports().getValue()).getSelectedFileType().equals(Constants.pdf_filetype)) {
                                            if (reportsViewModel.getFilteredReports().getValue().getSelectedBuyerKey().isEmpty()) {
                                                setListenerAndGenerateReport(Constants.datewiseConsolidatedPDF);
                                            } else {
                                                setListenerAndGenerateReport(Constants.buyerwiseConsolidatedPDF);

                                            }

                                        } else if (reportsViewModel.getFilteredReports().getValue().getSelectedFileType().equals(Constants.xls_filetype)) {

                                            if (reportsViewModel.getFilteredReports().getValue().getSelectedBuyerKey().isEmpty()) {
                                                setListenerAndGenerateReport(Constants.datewiseConsolidatedXLS);
                                            } else {
                                                setListenerAndGenerateReport(Constants.buyerwiseConsolidatedXLS);

                                            }

                                        }

                                    }
                                    else{
                                        showSnackbar(getView() ,"No Orders found (orderdetails)");
                                       // Toast.makeText(requireActivity(), "No Orders found (orderdetails)", Toast.LENGTH_SHORT).show();
                                    }

                                }

                                break;
                            case ERROR:
                                if(resource.message.equals(Constants.noDataAvailable)){
                                    isOrderDetailsFetchedData = true;
                                    showSnackbar(getView() ,"No order Data for selected filter");
                                   // Toast.makeText(requireActivity(), "No order Data for selected filter", Toast.LENGTH_SHORT).show();
                                    if(isOrderItemDetailsFetchedData){
                                      //  Toast.makeText(requireActivity(), "Successfully fetched the order details", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else{
                                    showProgressBar(false);
                                    showSnackbar(getView() ,"Error while fetching order details ,"+ String.valueOf(resource.message));
                                   // Toast.makeText(requireActivity(), "Error while fetching order details  ," + String.valueOf(resource.message), Toast.LENGTH_SHORT).show();

                                }
                                showProgressBar(false);
                                break;
                        }
                    }
                 }
                catch (Exception e){
                    e.printStackTrace();
                }
            };




            orderItemDetailsSalesDataObserver = resource -> {
                // Update your UI or perform any actions based on the updated data
                try {
                    if (resource != null) {
                        switch (resource.status) {
                            case LOADING:
                                showProgressBar(true);
                              //  Toast.makeText(requireActivity(), "Loading Order Item Details", Toast.LENGTH_SHORT).show();
                                break;
                            case SUCCESS:
                                isOrderItemDetailsFetchedData = true;
                                if(isOrderDetailsFetchedData){
                                  //  Toast.makeText(requireActivity(), "type :"+Constants.pdf_filetype+"!", Toast.LENGTH_SHORT).show();

                                  //  Toast.makeText(requireActivity(), "hii"+Objects.requireNonNull(reportsViewModel.getFilteredReports().getValue()).getSelectedFileType()+"090", Toast.LENGTH_SHORT).show();

                                    if(!resource.data.isEmpty()) {

                                        if(Objects.requireNonNull(reportsViewModel.getFilteredReports().getValue()).getSelectedFileType().equals(Constants.pdf_filetype)){
                                            if(reportsViewModel.getFilteredReports().getValue().getSelectedBuyerKey().isEmpty()){
                                                setListenerAndGenerateReport(Constants.datewiseConsolidatedPDF);
                                            }
                                            else{
                                                setListenerAndGenerateReport(Constants.buyerwiseConsolidatedPDF);

                                            }

                                        }
                                        else if(reportsViewModel.getFilteredReports().getValue().getSelectedFileType().equals(Constants.xls_filetype)) {

                                            if(reportsViewModel.getFilteredReports().getValue().getSelectedBuyerKey().isEmpty()){
                                                setListenerAndGenerateReport(Constants.datewiseConsolidatedXLS);
                                            }
                                            else{
                                                setListenerAndGenerateReport(Constants.buyerwiseConsolidatedXLS);

                                            }

                                        }

                                        //Toast.makeText(requireActivity(), "Successfully fetched the order Item details", Toast.LENGTH_SHORT).show();

                                    }
                                    else{
                                        showProgressBar(false);
                                        showSnackbar(getView() ," No Orders found (orderItemdetails) ");

                                      //  Toast.makeText(requireActivity(), "No Orders found (orderItemdetails)", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                break;
                            case ERROR:
                                if(resource.message.equals(Constants.noDataAvailable)){
                                    isOrderItemDetailsFetchedData = true;
                                    showSnackbar(getView() ," No Order Item Data for selected filter");

                                    //Toast.makeText(requireActivity(), "No order Item Data for selected filter", Toast.LENGTH_SHORT).show();
                                    if(isOrderDetailsFetchedData){
                                   //     Toast.makeText(requireActivity(), "Successfully fetched the order Item details", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else{
                                    //Toast.makeText(requireActivity(), "Error while fetching order Item details  ," + String.valueOf(resource.message), Toast.LENGTH_SHORT).show();
                                    showSnackbar(getView() ," Error while fetching order Item details  ," + String.valueOf(resource.message));

                                }
                                showProgressBar(false);
                                break;
                        }
                    }
                 }
                catch (Exception e){
                    e.printStackTrace();
                }
            };

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private List<OrderItemDetails_Model> processOrderItemDetails() {
         List<OrderItemDetails_Model> orderItemDetailsArrayList_Processed = new ArrayList<>();

        if(orderItemDetailsViewModel.getOrdersItemDetailsListFromViewModel().getValue().data.size() > 0) {
            for (int orderiterator = 0; orderiterator < ordersViewModel.getOrdersListFromViewModel().getValue().data.size(); orderiterator++) {
                OrderDetails_Model orderDetailsModel = ordersViewModel.getOrdersListFromViewModel().getValue().data.get(orderiterator);


                for (int itemIterator = 0; itemIterator < orderItemDetailsViewModel.getOrdersItemDetailsListFromViewModel().getValue().data.size(); itemIterator++) {
                    OrderItemDetails_Model orderItemDetailsModel = orderItemDetailsViewModel.getOrdersItemDetailsListFromViewModel().getValue().data.get(itemIterator);
                    if (orderItemDetailsModel.getOrderid().equals(orderDetailsModel.getOrderid())) {
                        orderItemDetailsArrayList_Processed.add(orderItemDetailsModel);
                    }


                }




            }
            return orderItemDetailsArrayList_Processed;

        }
        else{
            return new ArrayList<>();
        }

    }

    private void setListenerAndGenerateReport(String pdfType) {


        pdfGeneratorListener = new PDFGeneratorListener() {
            @Override
            public void onPDFGenerated(File pdfFile) {

                if(pdfType.equals(Constants.today_statuswise_pdf) || pdfType.equals(Constants.week_statuswise_pdf)){
                    showShare_Or_ViewFileDialog(true,pdfFile);

                }
                else {

                    showShare_Or_ViewFileDialog(false,pdfFile);

                    /*
                    Toast.makeText(requireActivity(), "PDF Generated", Toast.LENGTH_SHORT).show();
                    Uri uri = FileProvider.getUriForFile(requireContext(), requireContext().getPackageName() + ".provider", pdfFile);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(uri, "application/pdf");
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(intent);



                     */
                /*    Uri uri1 = FileProvider.getUriForFile(requireContext(), requireContext().getPackageName() + ".provider", pdfFile);
                    Intent intent1 = new Intent(Intent.ACTION_SEND);
                    intent1.setType("application/pdf");
                    intent1.putExtra(Intent.EXTRA_STREAM, uri1);
                    startActivity(Intent.createChooser(intent1, "Share PDF"));

                 */
                }
            }

            @Override
            public void onError(String message) {
              //  Toast.makeText(requireActivity(), "Error in generating PDF ", Toast.LENGTH_SHORT).show();
                showSnackbar(getView() ,"  Error in generating PDF ");
            }
        };


        if (SDK_INT < 30) {
            int writeExternalStoragePermission = ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (writeExternalStoragePermission == PackageManager.PERMISSION_GRANTED) {
                // Permission is already granted
                if(pdfType.equals(Constants.today_statuswise_pdf) || pdfType.equals(Constants.week_statuswise_pdf)){
                    reportsViewModel.generateOrderDetailsPDF(requireActivity(), requireContext(), pdfGeneratorListener, pdfType, sessionManager.getUserName(),statuswisetotalcountdetailsjson, orderwiseOrderItemDetails , statuswiseOrderid);

                }

                else {

                    if(!ProcessOrderDetails().isEmpty() && !processOrderItemDetails().isEmpty()){
                     reportsViewModel.generatePDF(requireActivity(), requireContext(), pdfGeneratorListener, pdfType,sessionManager.getUserName(), ProcessOrderDetails(), processOrderItemDetails());
                    }
                    else{
                        showSnackbar(getView(),"There is no data to generate report");
                        showProgressBar(false);
                    }

                }




            } else {
                // Permission is not granted
                CheckPermissionForManageStorage.justCheckForPermission(requireContext() , requireActivity() , checkPermissionForManageStorageInterface);


                Log.d("PermissionCheck", "Write External Storage permission NOT granted");
            }
        }
        else{
            if (Environment.isExternalStorageManager()) {
                // Permission is already granted
                Log.d("PermissionCheck", "Manage External Storage permission granted");
                if(pdfType.equals(Constants.today_statuswise_pdf) || pdfType.equals(Constants.week_statuswise_pdf)){
                    reportsViewModel.generateOrderDetailsPDF(requireActivity(), requireContext(), pdfGeneratorListener, pdfType,sessionManager.getUserName(), statuswisetotalcountdetailsjson, orderwiseOrderItemDetails , statuswiseOrderid) ;

                }

                else {

                    if(!ProcessOrderDetails().isEmpty() && !processOrderItemDetails().isEmpty()){
                        reportsViewModel.generatePDF(requireActivity(), requireContext(), pdfGeneratorListener, pdfType,sessionManager.getUserName(), ProcessOrderDetails(), processOrderItemDetails());
                    }
                    else{
                        showSnackbar(getView(),"There is no data to generate report");
                        showProgressBar(false);
                    }

                    //  reportsViewModel.generatePDF(requireActivity(), requireContext(), pdfGeneratorListener, pdfType,sessionManager.getVUserName(), ProcessOrderDetails(), processOrderItemDetails());
                }
            } else {
                // Permission is not granted
                Log.d("PermissionCheck", "Manage External Storage permission NOT granted");
                CheckPermissionForManageStorage.justCheckForPermission(requireContext() , requireActivity() , checkPermissionForManageStorageInterface);

            }
        }





    }

    private HashMap<String, OrderDetails_Model> ProcessOrderDetails() {
        HashMap<String, OrderDetails_Model> orderDetailsModelHashMap = new HashMap<>();

        for (int orderiterator = 0; orderiterator < ordersViewModel.getOrdersListFromViewModel().getValue().data.size(); orderiterator++) {
            OrderDetails_Model orderDetailsModel = ordersViewModel.getOrdersListFromViewModel().getValue().data.get(orderiterator);
            orderDetailsModelHashMap.put(orderDetailsModel.getOrderid() , orderDetailsModel);
        }
        return  orderDetailsModelHashMap;
    }

    private void showGenerateCustomSalesReportDialog() {


        try{
            FilterScreenBottomSheetFragment  bottomSheetFragment = new FilterScreenBottomSheetFragment();
            bottomSheetFragment.setFilterListener(filterValue -> {
                reportsViewModel.applyFilter(filterValue);
                setObserverForFilterData();
                // Apply filter and fetch data
            });
            bottomSheetFragment.show(getChildFragmentManager(), bottomSheetFragment.getTag());

        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    private void setObserverForFilterData() {

        try{
            reportsViewModel.getFilteredReports().observe(getViewLifecycleOwner(), new Observer<ReportsFilterDetails_Model>() {
                @Override
                public void onChanged(ReportsFilterDetails_Model reportData) {
                    // Update the UI with the filtered data

                    showProgressBar(true);
                    if(reportData.getSelectedFilterType().equals(Constants.today_filter)){
                        ordersViewModel.getOrdersByStatus_DateAndVendorKey(Constants.accepted_status,reportData.getStartDate() , reportData.getEndDate() , reportData.getVendorkey());

                        orderItemDetailsViewModel.getOrderItemsByDateAndVendorKey(reportData.getStartDate() , reportData.getEndDate() , reportData.getVendorkey());



                    }
                    else if(reportData.getSelectedFilterType().equals(Constants.yesterday_filter)){
                        ordersViewModel.getOrdersByStatus_DateAndVendorKey(Constants.accepted_status,reportData.getStartDate() , reportData.getEndDate() , reportData.getVendorkey());

                        orderItemDetailsViewModel.getOrderItemsByDateAndVendorKey(reportData.getStartDate() , reportData.getEndDate() , reportData.getVendorkey());

                    }
                    else if(reportData.getSelectedFilterType().equals(Constants.last7day_filter)){
                        ordersViewModel.getOrdersByStatus_DateAndVendorKey(Constants.accepted_status,reportData.getStartDate() , reportData.getEndDate() , reportData.getVendorkey());

                        orderItemDetailsViewModel.getOrderItemsByDateAndVendorKey(reportData.getStartDate() , reportData.getEndDate() , reportData.getVendorkey());

                    }
                    else if(reportData.getSelectedFilterType().equals(Constants.buyerwise_filter)){

                        ordersViewModel.getOrdersByBuyerKeyStatus_DateAndVendorKey(reportData.getSelectedBuyerKey(),Constants.accepted_status,reportData.getStartDate() , reportData.getEndDate() , reportData.getVendorkey());

                        orderItemDetailsViewModel.getOrderItemsByDateAndVendorKey(reportData.getStartDate() , reportData.getEndDate() , reportData.getVendorkey());

                    }
                    else if(reportData.getSelectedFilterType().equals(Constants.customdatewise_filter)){

                        ordersViewModel.getOrdersByStatus_DateAndVendorKey(Constants.accepted_status,reportData.getStartDate() , reportData.getEndDate() , reportData.getVendorkey());

                        orderItemDetailsViewModel.getOrderItemsByDateAndVendorKey(reportData.getStartDate() , reportData.getEndDate() , reportData.getVendorkey());

                    }

                   // Toast.makeText(requireActivity(), "Filtered data: - " + String.valueOf(reportData.getSelectedFilterType()), Toast.LENGTH_SHORT).show();
                }
            });

        }
        catch (Exception e){
            e.printStackTrace();
        }


    }


    private void showProgressBar(boolean show) {

        try {
            if (show) {
                binding.progressbar.playAnimation();
                binding.progressbar.setVisibility(View.VISIBLE);
                binding.progressbarBacklayout.setVisibility(View.VISIBLE);
            } else {
                binding.progressbar.cancelAnimation();
                binding.progressbar.setVisibility(View.GONE);
                binding.progressbarBacklayout.setVisibility(View.GONE);
            }
        }
        catch (Exception e){
            e.printStackTrace();

        }
    }


    private void showShare_Or_ViewFileDialog(boolean isOrderStatuswiseReport, File pdfFile) {


        try{

            Share_Or_View_BottomSheetDialogFragment  bottomSheetFragment = new Share_Or_View_BottomSheetDialogFragment();

            bottomSheetFragment.setShare_Or_View_Listener((selectedMode, selectedType) ->{
                if(isOrderStatuswiseReport){
                    if(selectedType.equals(Constants.pdf_filetype)){
                        if(selectedMode.equals(Constants.share_file)){
                            Uri uri1 = FileProvider.getUriForFile(requireContext(), requireContext().getPackageName() + ".provider", pdfFile);
                            Intent intent1 = new Intent(Intent.ACTION_SEND);
                            intent1.setType("application/pdf");
                            intent1.putExtra(Intent.EXTRA_STREAM, uri1);
                            startActivity(Intent.createChooser(intent1, "Share PDF"));

                        }
                        else{
                            showSnackbar(getView() ," PDF GENERATED ");
                           // Toast.makeText(requireActivity(), "PDF Generated", Toast.LENGTH_SHORT).show();
                            Uri uri = FileProvider.getUriForFile(requireContext(), requireContext().getPackageName() + ".provider", pdfFile);
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(uri, "application/pdf");
                            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                            //intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            startActivity(intent);



                        }
                    }
                    else if(selectedType.equals(Constants.xls_filetype)){
                        showSnackbar(getView() ," STATUS WISE XLS ");

                        // Toast.makeText(requireActivity(), "statuswise  xls", Toast.LENGTH_SHORT).show();
                    } else if (selectedType.equals("")) {
                        //showSnackbar(getView() ," ");
                        showProgressBar(false);
                    }
                }
                else{

                    if(Objects.requireNonNull(reportsViewModel.getFilteredReports().getValue()).getSelectedFileType().equals(Constants.pdf_filetype)){

                        if(selectedMode.equals(Constants.share_file)){
                            Uri uri1 = FileProvider.getUriForFile(requireContext(), requireContext().getPackageName() + ".provider", pdfFile);
                            Intent intent1 = new Intent(Intent.ACTION_SEND);
                            intent1.setType("application/pdf");
                            intent1.putExtra(Intent.EXTRA_STREAM, uri1);
                            startActivity(Intent.createChooser(intent1, "Share PDF"));

                        }
                        else if(selectedMode.equals(Constants.view_file)){
                            showSnackbar(getView() ," PDF GENERATED ");

                          //  Toast.makeText(requireActivity(), "PDF Generated", Toast.LENGTH_SHORT).show();
                            Uri uri = FileProvider.getUriForFile(requireContext(), requireContext().getPackageName() + ".provider", pdfFile);
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(uri, "application/pdf");
                            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                            //intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            startActivity(intent);



                        }
                        else if(selectedMode.equals("")){
                            showProgressBar(false);
                        }
                    }
                    else{
                        showSnackbar(getView() ," STATUS XLS ");

                        // Toast.makeText(requireActivity(), "statuswise  xls", Toast.LENGTH_SHORT).show();
                    }


                }

                showProgressBar(false);
            }
            );
            bottomSheetFragment.show(getChildFragmentManager(), bottomSheetFragment.getTag());

        }
        catch (Exception e){
            e.printStackTrace();
        }

    }



    @Override
    public void onDestroy() {
        super.onDestroy();

        ordersViewModel.getOrdersListFromViewModel().removeObserver(orderDetailsSalesDataObserver);
        orderItemDetailsViewModel.getOrdersItemDetailsListFromViewModel().removeObserver(orderItemDetailsSalesDataObserver);

        ordersViewModel.getOrdersListForReportDetailsFromViewModel().removeObserver(reportScreenOrderDetailsSalesDataObserver);
        orderItemDetailsViewModel.getOrdersItemDetailsReportScreenFromViewModel().removeObserver(reportScreenOrderItemDetailsSalesDataObserver);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2296) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    // Permission granted
                    checkPermissionForManageStorageInterface.onPermissionGranted(Constants.success,false);
                } else {
                    // Permission rejected
                    checkPermissionForManageStorageInterface.onPermissionRejected("Permission was rejected",false);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                checkPermissionForManageStorageInterface.onPermissionGranted(Constants.success ,false);
            } else {
                // Permission rejected
                checkPermissionForManageStorageInterface.onPermissionRejected("Permission was rejected" ,false);
            }
        }
    }

    private void showSnackbar(View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        Dashboard activity = (Dashboard) getActivity(); // Get reference to the activity
        if (activity != null) {
            FloatingActionButton fab = activity.getFabButton(); // Get FAB from the activity

            snackbar.setAnchorView(fab); // Set the FAB as the anchor view

        }

        snackbar.setAction("X", v -> snackbar.dismiss());
        snackbar.setActionTextColor(getResources().getColor(R.color.colorAccent)); // optional: set the action color

        // Get the Snackbar's layout view
        View snackbarView = snackbar.getView();

        // Check if the parent is CoordinatorLayout
        if (snackbarView.getLayoutParams() instanceof CoordinatorLayout.LayoutParams) {
            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) snackbarView.getLayoutParams();
            params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
            int marginInDp = (int) (30 * getResources().getDisplayMetrics().density); // Convert 30dp to pixels
            params.setMargins(0, marginInDp, 0, 0);
            snackbarView.setLayoutParams(params);
        } else if (snackbarView.getLayoutParams() instanceof FrameLayout.LayoutParams) {
            // If it's a FrameLayout, handle it like before
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbarView.getLayoutParams();
            params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
            int marginInDp = (int) (30 * getResources().getDisplayMetrics().density); // Convert 30dp to pixels
            params.setMargins(0, marginInDp, 0, 0);
            snackbarView.setLayoutParams(params);
        }

        snackbar.show();
    }


}