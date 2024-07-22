package com.project.ordernote.ui.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.project.ordernote.data.model.OrderDetails_Model;
import com.project.ordernote.data.model.OrderItemDetails_Model;
import com.project.ordernote.data.remote.FirestoreService;
import com.project.ordernote.databinding.FragmentAddOrdersBinding;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAddOrdersBinding.inflate(inflater, container, false);



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
                buyersViewModel = new ViewModelProvider(this).get(Buyers_ViewModel.class);
                buyersViewModel.getBuyersListFromRepository("vendor_1");
            } catch (Exception e) {
                e.printStackTrace();
            }


            try {
                menuItemsViewModel = new ViewModelProvider(this).get(MenuItems_ViewModel.class);
                menuItemsViewModel.fetchMenuItemsByVendorKey("vendor_1");
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

         // Example of adding an order
        binding.buyerdetailsLabelTextview.setOnClickListener(v -> {
            List<OrderItemDetails_Model> cartItems = getCartItems(); // Implement this method
            double discountPercentage = getDiscountValue(); // Implement this method

            OrderDetails_Model order = new OrderDetails_Model(/* Initialize order details */);
            showProgressBar(true);
            ordersViewModel.addOrder(order, cartItems, discountPercentage, new FirestoreService.FirestoreCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    // Handle success
                    // Example: show success message or navigate back
                    showProgressBar(false);
                }

                @Override
                public void onFailure(Exception e) {
                    // Handle failure
                    // Example: show error message
                    showProgressBar(false);
                }
            });
        });





    }

    private void showProgressBar(boolean show) {
        if(show){
            binding.progressbar.playAnimation();
            binding.progressbar.setVisibility(View.VISIBLE);
            binding.progressbarBacklayout.setVisibility(View.VISIBLE);
        }
        else{
            binding.progressbar.cancelAnimation();
            binding.progressbar.setVisibility(View.GONE);
            binding.progressbarBacklayout.setVisibility(View.GONE);
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
