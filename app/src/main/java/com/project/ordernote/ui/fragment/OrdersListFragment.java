package com.project.ordernote.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.project.ordernote.R;
import com.project.ordernote.databinding.FragmentOrdersBinding;
import com.project.ordernote.ui.adapter.OrdersListAdapter;
import com.project.ordernote.viewmodel.OrderDetails_ViewModel;

public class OrdersListFragment extends Fragment {
    private OrderDetails_ViewModel orderDetails_viewModel;
    private OrdersListAdapter ordersListAdapter;
    private FragmentOrdersBinding binding;
    private OrderListItemDescFragment orderListItemDescFragment;
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        orderDetails_viewModel = new ViewModelProvider(this).get(OrderDetails_ViewModel.class);
        binding = FragmentOrdersBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        orderListItemDescFragment = new OrderListItemDescFragment();
        ordersListAdapter = new OrdersListAdapter();
        ordersListAdapter.setmHandler(newHandler());
        binding.ordersRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        binding.ordersRecyclerView.setAdapter(ordersListAdapter);

        setActiveButton(binding.pendingordersButton);

        try {
            orderDetailViewModel("ORDERCREATED");
        }
        catch (Exception e ){
            e.printStackTrace();
        }

        binding.pendingordersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {

                setActiveButton(binding.pendingordersButton);
                orderDetailViewModel("ORDERCREATED");
                observeOrderDetails();
            }
        });

        binding.rejectedordersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {

                setActiveButton(binding.rejectedordersButton);
                orderDetailViewModel("ORDERREJECTED");
                observeOrderDetails();
            }
        });

        return view;
    }

    private Handler newHandler() {

        Handler.Callback callback = new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message message) {

                Bundle bundle = message.getData();
                Toast.makeText(requireActivity(), bundle.getString("fromadapter"), Toast.LENGTH_SHORT).show();
                Toast.makeText(requireActivity(), String.valueOf(bundle.getInt("position")), Toast.LENGTH_SHORT).show();

                orderListItemDescFragment.show(getParentFragmentManager(),"orderListItemDescFragment");

                return false;
            }
        };

        return new Handler(callback);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        observeOrderDetails();

    }

    private void observeOrderDetails() {
        orderDetails_viewModel.getOrdersByStatusFromViewModel().observe(getViewLifecycleOwner(), resource -> {
            ordersListAdapter.clearOrders();
            if (resource != null) {
                switch (resource.status) {
                    case LOADING:
                        Toast.makeText(requireActivity(), "Loading Orders", Toast.LENGTH_SHORT).show();
                        break;
                    case SUCCESS:
                        Toast.makeText(requireActivity(), "Success in fetching orders", Toast.LENGTH_SHORT).show();
                        ordersListAdapter.setOrders(resource.data);
                        break;
                    case ERROR:
                        Toast.makeText(requireActivity(), "Error in fetching orders", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    private void orderDetailViewModel(String status) {
        orderDetails_viewModel.getOrdersByStatus(status);
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
