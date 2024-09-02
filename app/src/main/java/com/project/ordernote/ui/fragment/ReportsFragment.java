package com.project.ordernote.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.project.ordernote.data.model.ItemDetails_Model;
import com.project.ordernote.data.model.OrderDetails_Model;
import com.project.ordernote.data.model.OrderItemDetails_Model;
import com.project.ordernote.data.model.ReportsFilterDetails_Model;
import com.project.ordernote.databinding.FragmentReportsBinding;
import com.project.ordernote.utils.ApiResponseState_Enum;
import com.project.ordernote.utils.Constants;
import com.project.ordernote.viewmodel.OrderDetails_ViewModel;
import com.project.ordernote.viewmodel.OrderItemDetails_ViewModel;
import com.project.ordernote.viewmodel.Reports_ViewModel;

import java.util.List;
import java.util.Objects;


public class ReportsFragment extends Fragment {

    View bottomSheetView;
    FragmentReportsBinding binding;
    BottomSheetDialog bottomSheetDialog;
    Reports_ViewModel reportsViewModel;
    private OrderDetails_ViewModel ordersViewModel;
    private OrderItemDetails_ViewModel orderItemDetailsViewModel;
    private Observer<ApiResponseState_Enum<List<OrderItemDetails_Model>>> orderItemDetailsSalesDataObserver;
    private Observer<ApiResponseState_Enum<List<OrderDetails_Model>>> orderDetailsSalesDataObserver;

    boolean isOrderDetailsFetchedData = false , isOrderItemDetailsFetchedData = false;

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
        binding = FragmentReportsBinding.inflate(inflater, container, false);
        reportsViewModel = new ViewModelProvider(requireActivity()).get(Reports_ViewModel.class);
        setObserverForSalesData();
        try {

            ordersViewModel = new ViewModelProvider(requireActivity()).get(OrderDetails_ViewModel.class);
            ordersViewModel.clearAllLiveData();
            ordersViewModel.getOrdersListFromViewModel().observeForever(orderDetailsSalesDataObserver);

        } catch (Exception e) {
            e.printStackTrace();
        }


        try{
            orderItemDetailsViewModel = new ViewModelProvider(requireActivity()).get(OrderItemDetails_ViewModel.class);
            orderItemDetailsViewModel.getOrdersItemDetailsListFromViewModel().observeForever(orderItemDetailsSalesDataObserver);


        }
        catch (Exception e){
            e.printStackTrace();
        }


        return binding.getRoot();
       // return inflater.inflate(R.layout.fragment_reports, container, false);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

      //  bottomSheetView = LayoutInflater.from(requireActivity().getApplicationContext()).inflate(R.layout.filter_screen_bottom_sheet, (RelativeLayout) requireActivity().findViewById(R.id.dialogContent));
       // bottomSheetDialog = new BottomSheetDialog(requireContext(), R.style.DialogAnimation);
      //  bottomSheetDialog.setDismissWithAnimation(true);
      //  bottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(R.color.transparent));

        binding.customSalesReportCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showGenerateCustomSalesReportDialog();
            }
        });


       // setObserverForSalesData();
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
                                    Toast.makeText(requireActivity(), "Successfully fetched the order details", Toast.LENGTH_SHORT).show();
                                }

                                break;
                            case ERROR:
                                if(resource.message.equals(Constants.noDataAvailable)){
                                    isOrderDetailsFetchedData = true;
                                    Toast.makeText(requireActivity(), "No order Data for selected filter", Toast.LENGTH_SHORT).show();
                                    if(isOrderItemDetailsFetchedData){
                                        Toast.makeText(requireActivity(), "Successfully fetched the order details", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else{
                                    Toast.makeText(requireActivity(), "Error while fetching order details  ," + String.valueOf(resource.message), Toast.LENGTH_SHORT).show();

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
                                    Toast.makeText(requireActivity(), "Successfully fetched the order Item details", Toast.LENGTH_SHORT).show();
                                }

                                break;
                            case ERROR:
                                if(resource.message.equals(Constants.noDataAvailable)){
                                    isOrderItemDetailsFetchedData = true;
                                    Toast.makeText(requireActivity(), "No order Item Data for selected filter", Toast.LENGTH_SHORT).show();
                                    if(isOrderDetailsFetchedData){
                                        Toast.makeText(requireActivity(), "Successfully fetched the order Item details", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else{
                                    Toast.makeText(requireActivity(), "Error while fetching order Item details  ," + String.valueOf(resource.message), Toast.LENGTH_SHORT).show();

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
                        ordersViewModel.getOrdersByStatus_DateAndVendorKey(Constants.placed_status,reportData.getStartDate() , reportData.getEndDate() , reportData.getVendorkey());

                        orderItemDetailsViewModel.getOrderItemsByStatus_VendorAndDate(Constants.placed_status,reportData.getStartDate() , reportData.getEndDate() , reportData.getVendorkey());



                    }
                    else if(reportData.getSelectedFilterType().equals(Constants.yesterday_filter)){
                        ordersViewModel.getOrdersByStatus_DateAndVendorKey(Constants.placed_status,reportData.getStartDate() , reportData.getEndDate() , reportData.getVendorkey());

                        orderItemDetailsViewModel.getOrderItemsByStatus_VendorAndDate(Constants.placed_status,reportData.getStartDate() , reportData.getEndDate() , reportData.getVendorkey());

                    }
                    else if(reportData.getSelectedFilterType().equals(Constants.last7day_filter)){
                        ordersViewModel.getOrdersByStatus_DateAndVendorKey(Constants.placed_status,reportData.getStartDate() , reportData.getEndDate() , reportData.getVendorkey());

                        orderItemDetailsViewModel.getOrderItemsByStatus_VendorAndDate(Constants.placed_status,reportData.getStartDate() , reportData.getEndDate() , reportData.getVendorkey());

                    }
                    else if(reportData.getSelectedFilterType().equals(Constants.buyerwise_filter)){

                        ordersViewModel.getOrdersByBuyerKeyStatus_DateAndVendorKey(reportData.getSelectedBuyerKey(),Constants.placed_status,reportData.getStartDate() , reportData.getEndDate() , reportData.getVendorkey());

                        orderItemDetailsViewModel.getOrderItemsByBuyerKeyStatus_DateAndVendorKey(reportData.getSelectedBuyerKey(),Constants.placed_status,reportData.getStartDate() , reportData.getEndDate() , reportData.getVendorkey());

                    }
                    else if(reportData.getSelectedFilterType().equals(Constants.customdatewise_filter)){

                        ordersViewModel.getOrdersByStatus_DateAndVendorKey(Constants.placed_status,reportData.getStartDate() , reportData.getEndDate() , reportData.getVendorkey());

                        orderItemDetailsViewModel.getOrderItemsByStatus_VendorAndDate(Constants.placed_status,reportData.getStartDate() , reportData.getEndDate() , reportData.getVendorkey());

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



    @Override
    public void onDestroy() {
        super.onDestroy();

        ordersViewModel.getOrdersListFromViewModel().removeObserver(orderDetailsSalesDataObserver);
        orderItemDetailsViewModel.getOrdersItemDetailsListFromViewModel().removeObserver(orderItemDetailsSalesDataObserver);

    }
}