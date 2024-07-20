package com.project.ordernote.ui.fragment;

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

import com.project.ordernote.data.model.MenuItems_Model;
import com.project.ordernote.data.model.OrderDetails_Model;
import com.project.ordernote.data.model.OrderItemDetails_Model;
import com.project.ordernote.data.remote.FirestoreService;
import com.project.ordernote.databinding.FragmentAddOrdersBinding;
import com.project.ordernote.viewmodel.MenuItems_ViewModel;
import com.project.ordernote.viewmodel.OrderDetails_ViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CreateOrderFragment extends Fragment {
    private OrderDetails_ViewModel ordersViewModel;
    private MenuItems_ViewModel menuItemsViewModel;

    private FragmentAddOrdersBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAddOrdersBinding.inflate(inflater, container, false);


        try {
            ordersViewModel = new ViewModelProvider(this).get(OrderDetails_ViewModel.class);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        try{
            menuItemsViewModel = new ViewModelProvider(this).get(MenuItems_ViewModel.class);
            menuItemsViewModel.fetchMenuItemsByVendorKey("vendor_1");
        }
        catch (Exception e){
            e.printStackTrace();
        }


/*
        menuItemsViewModel.fetchMenuItemsByVendorKey("vendor_1").observe(requireActivity(), new Observer<List<MenuItems_Model>>() {
            @Override
            public void onChanged(List<MenuItems_Model> value) {

                Toast.makeText(getContext(), "size 3 :-  "+ value.size(), Toast.LENGTH_SHORT).show();
            }
        });

 */


        menuItemsViewModel.getMenuItems().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null) {
                switch (resource.status) {
                    case LOADING:
                       // binding.progressBar.setVisibility(View.VISIBLE);
                      ///  binding.errorTextView.setVisibility(View.GONE);
                       // binding.recyclerView.setVisibility(View.GONE);
                        break;
                    case SUCCESS:
                       // binding.progressBar.setVisibility(View.GONE);
                       // binding.recyclerView.setVisibility(View.VISIBLE);
                      //  adapter.setMenuItems(resource.data);
                        break;
                    case ERROR:
                      //  binding.progressBar.setVisibility(View.GONE);
                      //  binding.errorTextView.setVisibility(View.VISIBLE);
                      //  binding.errorTextView.setText(resource.message);
                      //  binding.recyclerView.setVisibility(View.GONE);
                        break;
                }
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        menuItemsViewModel = new ViewModelProvider(this).get(MenuItems_ViewModel.class);
        String vendorKey = "vendor_1"; // Replace with actual vendor key
        menuItemsViewModel.fetchMenuItemsByVendorKey(vendorKey);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

         // Example of adding an order
        binding.buyerdetailsLabelTextview.setOnClickListener(v -> {
            List<OrderItemDetails_Model> cartItems = getCartItems(); // Implement this method
            double discountPercentage = getDiscountValue(); // Implement this method

            OrderDetails_Model order = new OrderDetails_Model(/* Initialize order details */);

            ordersViewModel.addOrder(order, cartItems, discountPercentage, new FirestoreService.FirestoreCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    // Handle success
                    // Example: show success message or navigate back
                }

                @Override
                public void onFailure(Exception e) {
                    // Handle failure
                    // Example: show error message
                }
            });
        });
        binding.lottieAnimationView.playAnimation();


    }

    // Implement methods to get cart items and discount value
    private List<OrderItemDetails_Model> getCartItems() {
        return new ArrayList<>(); // Implement this method
    }

    private double getDiscountValue() {
        return 0; // Implement this method
    }
}
