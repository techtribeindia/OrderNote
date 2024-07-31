package com.project.ordernote.ui.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.project.ordernote.data.local.LocalDataManager;
import com.project.ordernote.data.model.Buyers_Model;
import com.project.ordernote.data.model.ItemDetails_Model;
import com.project.ordernote.data.model.MenuItems_Model;
import com.project.ordernote.data.model.OrderDetails_Model;
import com.project.ordernote.data.model.OrderItemDetails_Model;

import com.project.ordernote.databinding.FragmentAddOrdersBinding;
import com.project.ordernote.ui.adapter.CreateOrderCartItemAdapter;
import com.project.ordernote.utils.ApiResponseState_Enum;
import com.project.ordernote.viewmodel.Buyers_ViewModel;
import com.project.ordernote.viewmodel.MenuItems_ViewModel;
import com.project.ordernote.viewmodel.OrderDetails_ViewModel;

import java.util.ArrayList;
import java.util.List;

public class CreateOrderFragment extends Fragment {
    private OrderDetails_ViewModel ordersViewModel;
    private MenuItems_ViewModel menuItemsViewModel;

    boolean menuItemFetchedSuccesfully = false , buyerDetailsFetchedSuccessfully = false;
    private Buyers_ViewModel buyersViewModel;

    private FragmentAddOrdersBinding binding;
    CreateOrderCartItemAdapter createOrderCartItemAdapter ;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAddOrdersBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            try {
                ordersViewModel = new ViewModelProvider(this).get(OrderDetails_ViewModel.class);
            } catch (Exception e) {
                e.printStackTrace();
            }


            try {
               // buyersViewModel = new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication()).create(Buyers_ViewModel.class);
                // Initialize ViewModels
                buyersViewModel = new ViewModelProvider(requireActivity()).get(Buyers_ViewModel.class);

                //     buyersViewModel = new ViewModelProvider(this).get(Buyers_ViewModel.class);
                List<Buyers_Model> buyersList = LocalDataManager.getInstance().getBuyers();

                if(buyersList.size() > 0){

                    buyersViewModel.setBuyersListinMutableLiveData(buyersList);
                }
                else{
                    buyersViewModel.getBuyersListFromRepository("vendor_1");
                }

                buyersViewModel.setSelectedBuyerLiveData(new Buyers_Model());

            } catch (Exception e) {
                e.printStackTrace();
            }


            try {
                menuItemsViewModel = new ViewModelProvider(requireActivity()).get(MenuItems_ViewModel.class);



                List<MenuItems_Model> menuItemsList = LocalDataManager.getInstance().getMenuItem();

                if(menuItemsList.size() > 0){
                    menuItemsViewModel.setMenuListinMutableLiveData(menuItemsList);
                }
                else{
                    menuItemsViewModel.fetchMenuItemsByVendorKey("vendor_1");
                }


               // menuItemsViewModel = new ViewModelProvider(this).get(MenuItems_ViewModel.class);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        catch (Exception e ){
            e.printStackTrace();
        }


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        binding.buyerselectionCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openBuyerSelectionDialog();
            }
        });


        binding.addItemCardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMenuitemSelectionDialog();
            }
        });

        menuItemsViewModel.getMenuItemsFromViewModel().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null) {
                switch (resource.status) {
                    case LOADING:
                        menuItemFetchedSuccesfully = false;
                        showProgressBar(true);
                        Toast.makeText(requireActivity(), "Loading Menu", Toast.LENGTH_SHORT).show();
                        break;
                    case SUCCESS:
                        menuItemFetchedSuccesfully = true;
                        if(buyerDetailsFetchedSuccessfully) {
                            showProgressBar(false);
                        }
                        Toast.makeText(requireActivity(), "Success in fetching menu", Toast.LENGTH_SHORT).show();

                        // adapter.setMenuItems(resource.data);
                        break;
                    case ERROR:
                        Toast.makeText(requireActivity(), "Error in fetching menu", Toast.LENGTH_SHORT).show();
                        menuItemFetchedSuccesfully = false;
                        showProgressBar(false);
                        break;
                }
            }
        });

        buyersViewModel.getBuyersListFromViewModel().observe(getViewLifecycleOwner(),  resource -> {
            if (resource != null) {
                switch (resource.status) {
                    case LOADING:
                        buyerDetailsFetchedSuccessfully = false;
                        showProgressBar(true);
                        Toast.makeText(requireActivity(), "Loading Buyer", Toast.LENGTH_SHORT).show();
                        break;
                    case SUCCESS:
                        buyerDetailsFetchedSuccessfully = true;
                        if(menuItemFetchedSuccesfully){
                            showProgressBar(false);
                        }

                        Toast.makeText(requireActivity(), "Success in fetching Buyer", Toast.LENGTH_SHORT).show();
                        break;
                    case ERROR:
                        buyerDetailsFetchedSuccessfully = false;
                        Toast.makeText(requireActivity(), "Error in fetching Buyer", Toast.LENGTH_SHORT).show();
                        showProgressBar(false);
                        break;
                }
            }
        });

        buyersViewModel.getSelectedBuyersDetailsFromViewModel().observe(getViewLifecycleOwner(), buyerData -> {
            if (buyerData != null) {

                if(!buyerData.getName().equals("")){
                    binding.selectedBuyerNameTextview.setText(String.valueOf(buyerData.getName()));
                    binding.selecedBuyerDetailsTextview.setText(String.valueOf(buyerData.getAddress1() +" , "+'\n'+buyerData.getAddress2()+" - "+""+buyerData.getPincode()+" . "+'\n'+"Ph:- +91"+buyerData.getMobileno()));
                }



            }
        });

        ordersViewModel.getItemDetailsArraylistViewModel().observe(getViewLifecycleOwner(),  data -> {
            if (data != null) {

                setAdapterForCartRecyclerView(data);

            }
        });



    }

    private void setAdapterForCartRecyclerView(List<ItemDetails_Model> data) {

        if(createOrderCartItemAdapter != null){
            createOrderCartItemAdapter.notifyDataSetChanged();
        }
        else{
            createOrderCartItemAdapter = new CreateOrderCartItemAdapter(data);
            binding.itemDetailsRecyclerview.setAdapter(createOrderCartItemAdapter);
            binding.itemDetailsRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));

        }





    }

    private void openMenuitemSelectionDialog() {
        try{
            AddMenuItem_InCart_Fragment dialogFragment2 = new AddMenuItem_InCart_Fragment();
            // dialogFragment.setBuyerSelectionListener(this); // Set the listener
            dialogFragment2.show(getParentFragmentManager(), "AddMenuItemInCartDialogFragment");

        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    private void openBuyerSelectionDialog() {

        try{
            BuyerSelectionDialogFragment dialogFragment = new BuyerSelectionDialogFragment();
           // dialogFragment.setBuyerSelectionListener(this); // Set the listener
            dialogFragment.show(getParentFragmentManager(), "BuyerSelectionDialogFragment");

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

    // Implement methods to get cart items and discount value
    private List<OrderItemDetails_Model> getCartItems() {
        return new ArrayList<>(); // Implement this method
    }

    private double getDiscountValue() {
        return 0; // Implement this method
    }
}
