package com.project.ordernote.ui.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.project.ordernote.R;
import com.project.ordernote.data.model.OrderDetails_Model;
import com.project.ordernote.data.model.OrderItemDetails_Model;
import com.project.ordernote.data.remote.FirestoreService;
import com.project.ordernote.databinding.FragmentAddOrdersBinding;
import com.project.ordernote.viewmodel.Dashboard_ViewModel;
import com.project.ordernote.viewmodel.OrderDetails_ViewModel;

import java.util.ArrayList;
import java.util.List;

public class AddOrdersFragment extends Fragment {
    private OrderDetails_ViewModel ordersViewModel;
    private FragmentAddOrdersBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAddOrdersBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            ordersViewModel = new ViewModelProvider(this).get(OrderDetails_ViewModel.class);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        //BY ARUNNN
        // Example of adding an order
        binding.btnAddOrder.setOnClickListener(v -> {
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
    }

    // Implement methods to get cart items and discount value
    private List<OrderItemDetails_Model> getCartItems() {
        return new ArrayList<>(); // Implement this method
    }

    private double getDiscountValue() {
        return 0; // Implement this method
    }
}
