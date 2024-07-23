package com.project.ordernote.ui.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.project.ordernote.R;
import com.project.ordernote.databinding.FragmentOrdersBinding;
import com.project.ordernote.ui.adapter.OrdersListAdapter;
import com.project.ordernote.viewmodel.OrderDetails_ViewModel;

import java.util.ArrayList;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrdersListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrdersListFragment extends Fragment {
    private OrderDetails_ViewModel orderDetails_viewModel;
    private OrdersListAdapter ordersListAdapter;
    private FragmentOrdersBinding binding;

    public OrdersListFragment() {
        // Required empty public constructor
    }

    public static OrdersListFragment newInstance(String param1, String param2) {
        OrdersListFragment fragment = new OrdersListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        orderDetails_viewModel = new ViewModelProvider(this).get(OrderDetails_ViewModel.class);
        binding = FragmentOrdersBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        ordersListAdapter = new OrdersListAdapter();
        binding.ordersRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        binding.ordersRecyclerView.setAdapter(ordersListAdapter);

        setActiveButton(binding.pendingordersButton);

        orderDetails_viewModel.getOrdersByStatus("ORDERCREATED").observe(getViewLifecycleOwner(), ordersWithStatusResult -> {
            ordersListAdapter.setOrders(ordersWithStatusResult);
        });

        binding.pendingordersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setActiveButton(binding.pendingordersButton);
                orderDetails_viewModel.getOrdersByStatus("ORDERCREATED").observe(getViewLifecycleOwner(), ordersWithStatusResult -> {
                    ordersListAdapter.setOrders(ordersWithStatusResult);
                });
            }
        });

        binding.rejectedordersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setActiveButton(binding.rejectedordersButton);
                orderDetails_viewModel.getOrdersByStatus("ORDERREJECTED").observe(getViewLifecycleOwner(), ordersWithStatusResult -> {
                    ordersListAdapter.setOrders(ordersWithStatusResult);
                });
            }
        });

        return view;
    }

    private void setActiveButton(Button activeButton) {
        binding.pendingordersButton.setBackgroundResource(R.drawable.button_inactive);
        binding.rejectedordersButton.setBackgroundResource(R.drawable.button_inactive);

        binding.pendingordersButton.setTextColor(getResources().getColor(R.color.black));
        binding.rejectedordersButton.setTextColor(getResources().getColor(R.color.black));

        activeButton.setBackgroundResource(R.drawable.button_active);
        activeButton.setTextColor(getResources().getColor(R.color.white));
    }
}